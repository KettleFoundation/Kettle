package kettlefoundation.kettle.nms.reflections;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import kettlefoundation.kettle.nms.ClassInheritanceProvider;
import kettlefoundation.kettle.nms.MappingLoader;
import kettlefoundation.kettle.nms.utils.ReflectionUtils;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.provider.JointProvider;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * ReflectionTransformer
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 08/05/2019 - 08:33 AM
 */
public class ReflectionTransformer {

  public static final String REFLECTION_METHODS = Type.getInternalName(ReflectionMethods.class);
  public static final HashMap<String, String> classDeMappings = Maps.newHashMap();
  public static final Multimap<String, String> methodDeMapping = ArrayListMultimap.create();
  public static final Multimap<String, String> fieldDeMapping = ArrayListMultimap.create();
  public static final Multimap<String, String> methodFastMapping = ArrayListMultimap.create();
  public static JarMapping jarMapping;
  public static JarRemapper remapper;
  private static boolean disable = false;

  static {
    try {
      ReflectionUtils.getCallerClassLoader();
    } catch (Throwable e) {
      new RuntimeException("Unsupported Java version, disabled reflection remap!", e).printStackTrace();
      disable = true;
    }

    jarMapping = MappingLoader.loadMappings();
    JointProvider provider = new JointProvider();
    provider.add(new ClassInheritanceProvider());
    jarMapping.setFallbackInheritanceProvider(provider);
    remapper = new JarRemapper(jarMapping);

    jarMapping.classes.forEach(classDeMappings::put);
    jarMapping.methods.forEach(methodDeMapping::put);
    jarMapping.fields.forEach(fieldDeMapping::put);
    jarMapping.methods.forEach((s, s1) -> methodFastMapping.put(s.split("\\s+")[0], s));
  }

  public static byte[] transform(byte[] bytes) {
    ClassReader classReader = new ClassReader(bytes);
    ClassNode classNode = new ClassNode();
    classReader.accept(classNode, 0);
    AtomicBoolean remapClassLoader = new AtomicBoolean(false);
    if (classNode.superName.equals("java/net/URLClassLoader")) {
      classNode.superName = "kettlefoundation/kettle/nms/NMSURLClassLoader";
      remapClassLoader.set(true);
    }

    classNode.methods.forEach(methodNode -> {
      for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
        if (insnNode instanceof TypeInsnNode) {
          TypeInsnNode insn = (TypeInsnNode) insnNode;
          if (insn.getOpcode() == Opcodes.NEW && insn.desc.equals("java/net/URLClassLoader")) {
            insn.desc = "kettlefoundation/kettle/nms/NMSURLClassLoader";
            remapClassLoader.set(true);
          }
        }

        if (!(insnNode instanceof MethodInsnNode)) {
          continue;
        }
        MethodInsnNode insn = (MethodInsnNode) insnNode;
        switch (insn.getOpcode()) {
          case Opcodes.INVOKEVIRTUAL:
            remapVirtual(insn);
            break;
          case Opcodes.INVOKESTATIC:
            rempapForName(insn);
            break;
          case Opcodes.INVOKESPECIAL:
            if (remapClassLoader.get()) {
              remapURLClassLoader(insn);
            }
            break;
        }

        if (insn.owner.equals("javax/script/ScriptEngineManager") && insn.desc.equals("()V") && insn.name.equals("<init>")) {
          insn.desc = "(Ljava/langClassLoader;)V";
          methodNode.instructions.insertBefore(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/ClassLoader", "getSystemClassLoader", "()Ljava/lang/ClassLoader;"));
          methodNode.maxStack++;
        }
      }
    });

    ClassWriter writer = new ClassWriter(0);
    classNode.accept(writer);
    return writer.toByteArray();
  }

  private static void remapURLClassLoader(MethodInsnNode method) {
    if (!method.owner.equals("java/net/URLClassLoader") || !method.name.equals("<init>")) {
      return;
    }
    method.owner = "kettlefoundation/kettle/nms/NMSURLClassLoader";
  }

  private static void rempapForName(AbstractInsnNode insn) {
    MethodInsnNode method = (MethodInsnNode) insn;
    if (disable || !method.owner.equals("java/lang/Class") || !method.name.equals("forName")) {
      return;
    }
    method.owner = REFLECTION_METHODS;
  }

  private static void remapVirtual(AbstractInsnNode insn) {
    MethodInsnNode method = (MethodInsnNode) insn;
    if (!((method.owner.equals("java/lang/Class") && (
        method.name.equals("getField") ||
            method.name.equals("getDeclaredField") ||
            method.name.equals("getMethod") ||
            method.name.equals("getDeclaredMethod") ||
            method.name.equals("getSimpleName"))) ||
        (method.name.equals("getName") && (
            method.owner.equals("java/lang/reflect/Field") ||
                method.owner.equals("java/lang/reflect/Method"))) ||
        (method.owner.equals("java/lang/ClassLoader") &&
            method.name.equals("loadClass"))
    )) {
      return;
    }

    Type returnType = Type.getReturnType(method.desc);

    ArrayList<Type> args = new ArrayList<>();
    args.add(Type.getObjectType(method.owner));
    args.addAll(Arrays.asList(Type.getArgumentTypes(method.desc)));

    method.setOpcode(Opcodes.INVOKESTATIC);
    method.owner = REFLECTION_METHODS;
    method.desc = Type.getMethodDescriptor(returnType, args.toArray(new Type[args.size()]));
  }
}
