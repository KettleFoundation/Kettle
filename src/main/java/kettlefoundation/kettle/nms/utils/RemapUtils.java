package kettlefoundation.kettle.nms.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import kettlefoundation.kettle.nms.reflections.ReflectionTransformer;
import net.md_5.specialsource.JarRemapper;
import org.objectweb.asm.Type;

/**
 * RemapUtils
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 08/05/2019 - 08:27 AM
 */
public class RemapUtils {

  public static final String NMS_PREFIX = "net/minecraft/server";
  public static final String NMS_VERSION = "v1_12_R1";

  public static String reverseMapExternal(Class<?> name) {
    return reverseMap(name).replace('/', '.').replace('$', '.');
  }

  public static String reverseMap(Class<?> name) {
    return reverseMap(Type.getInternalName(name));
  }

  public static String reverseMap(String check) {
    return ReflectionTransformer.classDeMappings.getOrDefault(check, check);
  }

  public static String mapMethod(Class<?> clazz, String name, Class<?>... parameterType) {
    String result = mapMethodInternal(clazz, name, parameterType);
    if (result != null) {
      return result;
    }
    return name;
  }

  public static String mapClass(String className) {
    String tRemapped = JarRemapper.mapTypeName(className, ReflectionTransformer.jarMapping.packages, ReflectionTransformer.jarMapping.classes, className);
    if (tRemapped.equals(className) && className.startsWith(NMS_PREFIX) && !className.contains(NMS_VERSION)) {
      String tNewClassStr = NMS_PREFIX + NMS_VERSION + "/" + className.substring(NMS_PREFIX.length());
      return JarRemapper.mapTypeName(tNewClassStr, ReflectionTransformer.jarMapping.packages, ReflectionTransformer.jarMapping.classes, className);
    }
    return tRemapped;
  }

  private static String mapMethodInternal(Class<?> clazz, String name, Class<?>... parameterType) {
    String match = reverseMap(clazz) + "/" + name;
    Collection<String> collection = ReflectionTransformer.methodFastMapping.get(match);
    for (String value : collection) {
      String[] str = value.split("\\s+");
      int i = 0;
      for (Type type : Type.getArgumentTypes(str[1])) {
        String typename = (type.getSort() == Type.ARRAY ? type.getInternalName() : type.getClassName());
        if (i >= parameterType.length || !typename.equals(reverseMapExternal(parameterType[i]))) {
          i = -1;
          break;
        }
        i++;

      }
      if (i >= parameterType.length) {
        return ReflectionTransformer.jarMapping.methods.get(value);
      }
    }

    Class superClass = clazz.getSuperclass();
    if (superClass != null) {
      String superMethodName = mapMethodInternal(superClass, name, parameterType);
      return superMethodName;
    }
    return null;
  }

  public static String demapFieldName(Field field) {
    String name = field.getName();
    String match = reverseMap(field.getDeclaringClass());

    Collection<String> collection = ReflectionTransformer.fieldDeMapping.get(name);
    for (String value : collection) {
      if (value.startsWith(match)) {
        String[] matched = value.split("\\/");
        return matched[matched.length - 1];
      }
    }
    return name;
  }

  public static String demapMethodName(Method method) {
    String name = method.getName();
    String match = reverseMap(method.getDeclaringClass());

    Collection<String> colls = ReflectionTransformer.methodDeMapping.get(name);

    for (String value : colls) {
      if (value.startsWith(match)) {
        String[] matched = value.split("\\s+")[0].split("\\/");
        return matched[matched.length - 1];
      }
    }

    return name;
  }

}
