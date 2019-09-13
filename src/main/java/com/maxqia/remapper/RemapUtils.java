package com.maxqia.remapper;

/**
 * Based on Apache's ReflectionUtil
 *
 * @author Maxqia
 */
public class RemapUtils {

    private static SecurityManager securityManager = new SecurityManager();

    public static Class<?> getCallerClass(int skip) {
        return securityManager.getCallerClass(skip);
    }

    public static ClassLoader getCallerClassloader() {
        return getCallerClass(3).getClassLoader();
    }

    static class SecurityManager extends java.lang.SecurityManager {
        public Class<?> getCallerClass(int skip) {
            return getClassContext()[skip + 1];
        }
    }
}