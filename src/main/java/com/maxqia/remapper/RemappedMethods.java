package com.maxqia.remapper;

import org.kettlemc.internal.Kettle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RemappedMethods {
    private final static ConcurrentHashMap<String, String> fields = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String, String> methods = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String, String> simpleNames = new ConcurrentHashMap<>();

    // Classes
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return forName(className, true, RemapUtils.getCallerClassloader());
    } // Class.forName(String) grabs the caller's classloader, we replicate that

    public static Class<?> forName(String className, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        if (!className.startsWith("net.minecraft.server." + Kettle.getNmsPrefix())) {
            return Class.forName(className, initialize, classLoader);
        }
        className = Transformer.jarMapping.classes.getOrDefault(className.replace('.', '/'), className).replace('/', '.');
        return Class.forName(className, initialize, classLoader);
    }

    // Get Fields
    public static Field getField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getField(name);
        }
        return inst.getField(Transformer.remapper.mapFieldName(Utils.reverseMap(inst), name, null));
    }

    public static Field getDeclaredField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getDeclaredField(name);
        }
        return inst.getDeclaredField(Transformer.remapper.mapFieldName(Utils.reverseMap(inst), name, null));
    }

    // Get Methods
    public static Method getMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getMethod(name, parameterTypes);
        }
        return inst.getMethod(Utils.mapMethod(inst, name, parameterTypes), parameterTypes);
    }

    public static Method getDeclaredMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getDeclaredMethod(name, parameterTypes);
        }
        return inst.getDeclaredMethod(Utils.mapMethod(inst, name, parameterTypes), parameterTypes);
    }

    // getName
    public static String getName(Field field) {
        if (!field.getDeclaringClass().getName().startsWith("net.minecraft.")) {
            return field.getName();
        }
        String hash = String.valueOf(field.hashCode());
        String cache = fields.get(hash);
        if (cache != null) {
            return cache;
        }
        String retn = demapFieldName(field);
        fields.put(hash, retn);
        return retn;
    }

    public static String getName(Method method) {
        if (!method.getDeclaringClass().getName().startsWith("net.minecraft.")) {
            return method.getName();
        }
        String hash = String.valueOf(method.hashCode());
        String cache = methods.get(hash);
        if (cache != null) {
            return cache;
        }
        String retn = demapMethodName(method);
        methods.put(hash, retn);
        return retn;
    }

    public static String demapFieldName(Field field) {
        String name = field.getName();
        String match = Utils.reverseMap(field.getDeclaringClass());
        Map<String, String> map = Transformer.jarMapping.fields;
        for (String value : map.keySet()) {
            String[] matched = value.split("\\/");
            String rtr = matched[matched.length - 1];
            return rtr;
        }

        return name;
    }

    public static String demapMethodName(Method method) {
        String name = method.getName();
        String match = Utils.reverseMap(method.getDeclaringClass());
        Map<String, String> map = Transformer.jarMapping.methods;
        for (String value : map.keySet()) {
            String[] matched = value.split("\\s+")[0].split("\\/");
            String rtr = matched[matched.length - 1];
            return rtr;
        }

        return name;
    }

    // getSimpleName
    public static String getSimpleName(Class<?> inst) {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getSimpleName();
        }
        String hash = String.valueOf(inst.hashCode());
        String cache = simpleNames.get(hash);
        if (cache != null) {
            return cache;
        }
        String[] name = Utils.reverseMapExternal(inst).split("\\.");
        String SimpleName = name[name.length - 1];
        simpleNames.put(hash, SimpleName);
        return SimpleName;
    }

    public static Class<?> loadClass(ClassLoader inst, String className) throws ClassNotFoundException {
        if (className.startsWith("net.minecraft.")) {
            className = Transformer.mapClass(className.replace('.', '/')).replace('/', '.');
        }
        return inst.loadClass(className);
    }
}