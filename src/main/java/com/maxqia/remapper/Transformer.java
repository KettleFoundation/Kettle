package com.maxqia.remapper;

import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.provider.JointProvider;
import net.md_5.specialsource.transformer.MavenShade;
import org.kettlemc.internal.Kettle;
import org.kettlemc.remapper.KettleRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Transformer {

    public static JarMapping jarMapping;
    public static JarRemapper remapper;

    public static void init() {
        jarMapping = new JarMapping();
        try {
            jarMapping.packages.put("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil", "it/unimi/dsi/fastutil");
            jarMapping.packages.put("org/bukkit/craftbukkit/libs/jline", "jline");
            jarMapping.packages.put("org/bukkit/craftbukkit/libs/joptsimple", "joptsimple");
            jarMapping.methods.put("org/bukkit/Bukkit/getOnlinePlayers ()[Lorg/bukkit/entity/Player;", "_INVALID_getOnlinePlayers");

            Map<String, String> relocations = new HashMap<>();
            relocations.put("net.minecraft.server", "net.minecraft.server.v1_12_R1");

            jarMapping.loadMappings(
                    new BufferedReader(new InputStreamReader(Transformer.class.getClassLoader().getResourceAsStream("mappings/NMSMappings.srg"))),
                    new MavenShade(relocations),
                    null, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JointProvider provider = new JointProvider();
        provider.add(new ClassInheritanceProvider());
        jarMapping.setFallbackInheritanceProvider(provider);
        remapper = new KettleRemapper(jarMapping);
    }

    public static String mapClass(String className) {
        String tRemapped = JarRemapper.mapTypeName(className, jarMapping.packages, jarMapping.classes, className);
        if (tRemapped.equals(className) && className.startsWith(Kettle.getNmsPrefix()) && !className.contains(Kettle.getNativeVersion())) {
            String tNewClassStr = Kettle.getNmsPrefix() + Kettle.getNativeVersion() + "/" + className.substring(Kettle.getNmsPrefix().length());
            return JarRemapper.mapTypeName(tNewClassStr, jarMapping.packages, jarMapping.classes, className);
        }
        return tRemapped;
    }

    /**
     * Convert code from using Class.X methods to our remapped versions
     */
    public static byte[] transform(byte[] code) {
        ClassReader reader = new ClassReader(code); // Turn from bytes into visitor
        ClassNode node = new ClassNode();
        reader.accept(node, 0); // Visit using ClassNode

        for (MethodNode method : node.methods) { // Taken from SpecialSource
            ListIterator<AbstractInsnNode> insnIterator = method.instructions.iterator();
            while (insnIterator.hasNext()) {
                AbstractInsnNode insn = insnIterator.next();
                if (!(insn instanceof MethodInsnNode)) {
                    continue;
                }
                MethodInsnNode mi = (MethodInsnNode) insn;
                switch (mi.getOpcode()) {
                    case Opcodes.INVOKEVIRTUAL:
                        remapVirtual(mi);
                        break;
                    case Opcodes.INVOKESTATIC:
                        remapForName(mi);
                        break;
                }
            }
        }

        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }

    public static void remapForName(AbstractInsnNode insn) {
        MethodInsnNode method = (MethodInsnNode) insn;
        if (!"java/lang/Class".equals(method.owner) || !"forName".equals(method.name)) {
            return;
        }
        method.owner = Type.getInternalName(RemappedMethods.class);
    }

    public static void remapVirtual(AbstractInsnNode insn) {
        MethodInsnNode method = (MethodInsnNode) insn;

        if (!(("java/lang/Class".equals(method.owner) && ("getField".equals(method.name)
                || "getDeclaredField".equals(method.name)
                || "getMethod".equals(method.name)
                || "getDeclaredMethod".equals(method.name)
                || "getSimpleName".equals(method.name))
        )
                || ("getName".equals(method.name) && ("java/lang/reflect/Field".equals(method.owner)
                || "java/lang/reflect/Method".equals(method.owner)))
                || ("java/lang/ClassLoader".equals(method.owner) && "loadClass".equals(method.name)))) {
            return;
        }

        Type returnType = Type.getReturnType(method.desc);

        ArrayList<Type> args = new ArrayList<>();
        args.add(Type.getObjectType(method.owner));
        args.addAll(Arrays.asList(Type.getArgumentTypes(method.desc)));

        method.setOpcode(Opcodes.INVOKESTATIC);
        method.owner = Type.getInternalName(RemappedMethods.class);
        method.desc = Type.getMethodDescriptor(returnType, args.toArray(new Type[args.size()]));
    }
}