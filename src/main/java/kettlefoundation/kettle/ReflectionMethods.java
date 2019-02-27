package kettlefoundation.kettle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionMethods {

    private final static ConcurrentHashMap<String, String> fieldGetNameCache = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String, String> methodGetNameCache = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String, String> simpleNameGetNameCache = new ConcurrentHashMap<>();

    // Class.forName
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return forName(className, true, ReflectionUtils.getCallerClassloader());
    }

    public static Class<?> forName(String className, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        if (!className.startsWith("net.minecraft.server." + RemapUtils.NMS_VERSION))
            return Class.forName(className, initialize, classLoader);
        className = ReflectionTransformer.jarMapping.classes.getOrDefault(className.replace('.', '/'), className).replace('/', '.');
        return Class.forName(className, initialize, classLoader);
    }

    // Get Fields
    public static Field getField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) return inst.getField(name);
        return inst.getField(ReflectionTransformer.remapper.mapFieldName(RemapUtils.reverseMap(inst), name, null));
    }

    public static Field getDeclaredField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) return inst.getDeclaredField(name);
        return inst.getDeclaredField(ReflectionTransformer.remapper.mapFieldName(RemapUtils.reverseMap(inst), name, null));
    }

    // Get Methods
    public static Method getMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) return inst.getMethod(name, parameterTypes);
        return inst.getMethod(RemapUtils.mapMethod(inst, name, parameterTypes), parameterTypes);
    }

    public static Method getDeclaredMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) return inst.getDeclaredMethod(name, parameterTypes);
        return inst.getDeclaredMethod(RemapUtils.mapMethod(inst, name, parameterTypes), parameterTypes);
    }

    // getName
    public static String getName(Field field) {
        if (!field.getDeclaringClass().getName().startsWith("net.minecraft.")) return field.getName();
        String hash = String.valueOf(field.hashCode());
        String cache = fieldGetNameCache.get(hash);
        if (cache != null) return cache;
        String retn = RemapUtils.demapFieldName(field);
        fieldGetNameCache.put(hash, retn);
        return retn;
    }

    public static String getName(Method method) {
        if (!method.getDeclaringClass().getName().startsWith("net.minecraft.")) return method.getName();
        String hash = String.valueOf(method.hashCode());
        String cache = methodGetNameCache.get(hash);
        if (cache != null) return cache;
        String retn = RemapUtils.demapMethodName(method);
        methodGetNameCache.put(hash, retn);
        return retn;
    }

    // getSimpleName
    public static String getSimpleName(Class<?> inst) {
        if (!inst.getName().startsWith("net.minecraft.")) return inst.getSimpleName();
        String hash = String.valueOf(inst.hashCode());
        String cache = simpleNameGetNameCache.get(hash);
        if (cache != null) return cache;
        String[] name = RemapUtils.reverseMapExternal(inst).split("\\.");
        String retn = name[name.length - 1];
        simpleNameGetNameCache.put(hash, retn);
        return retn;
    }

    // ClassLoader.loadClass
    public static Class<?> loadClass(ClassLoader inst, String className) throws ClassNotFoundException {
        if (className.startsWith("net.minecraft."))
            className = RemapUtils.mapClass(className.replace('.', '/')).replace('/', '.');
        return inst.loadClass(className);
    }
}
