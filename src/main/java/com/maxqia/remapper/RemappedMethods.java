package com.maxqia.remapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static com.maxqia.remapper.Utils.*;

class RemappedMethods {

    // Classes
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return forName(className, true, RemapUtils.getCallerClassloader());
    } // Class.forName(String) grabs the caller's classloader, we replicate that

    public static Class<?> forName(String className, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        if (!className.startsWith("net.minecraft.server.v1_12_R1")) {
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
        return inst.getField(Transformer.remapper.mapFieldName(reverseMap(inst), name, null));
    }

    public static Field getDeclaredField(Class<?> inst, String name) throws NoSuchFieldException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getDeclaredField(name);
        }
        return inst.getDeclaredField(Transformer.remapper.mapFieldName(reverseMap(inst), name, null));
    }

    // Get Methods
    public static Method getMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getMethod(name, parameterTypes);
        }
        return inst.getMethod(mapMethod(inst, name, parameterTypes), parameterTypes);
    }

    public static Method getDeclaredMethod(Class<?> inst, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getDeclaredMethod(name, parameterTypes);
        }
        return inst.getDeclaredMethod(mapMethod(inst, name, parameterTypes), parameterTypes);
    }

    // getName
    public static String getName(Field field) {
        if (!field.getDeclaringClass().getName().startsWith("net.minecraft.")) {
            return field.getName();
        }
        String name = field.getName();
        String match = reverseMap(field.getDeclaringClass());
        Map<String, String> map = Transformer.jarMapping.fields;
        for (String value : map.keySet()) {
            if (map.get(name).startsWith(match)) {
                String[] matched = value.split("/");
                return matched[matched.length - 1];
            }
        }

        return name;
    }

    public static String getName(Method method) {
        if (!method.getDeclaringClass().getName().startsWith("net.minecraft.")) {
            return method.getName();
        }
        String name = method.getName();
        String match = reverseMap(method.getDeclaringClass());
        Map<String, String> map = Transformer.jarMapping.methods;
        for (String value : map.keySet()) {
            if (map.get(name).startsWith(match)) {
                String[] matched = value.split("\\s+")[0].split("/");
                String rtr = matched[matched.length - 1];
                return rtr;
            }
        }

        return name;
    }

    // getSimpleName
    public static String getSimpleName(Class<?> inst) {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getSimpleName();
        }
        String[] name = getName(inst).split("\\.");
        return name[name.length - 1];
    }

    public static String getName(Class<?> inst) {
        if (!inst.getName().startsWith("net.minecraft.")) {
            return inst.getName();
        }
        return reverseMapExternal(inst);
    }
}
