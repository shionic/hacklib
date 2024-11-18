package com.github.shionic.hacklib;

import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.net.URL;
import java.security.ProtectionDomain;

public class HackClassLoaderHelper {
    private static final VarHandle GET_UCP_FROM_CLASSLOADER;
    private static final MethodHandle ADD_URL_TO_UCP;
    private static final MethodHandle FIND_BOOTSTRAP_CLASS_OR_NULL;
    private static final MethodHandle DEFINE_CLASS;
    private static final MethodHandle DEFINE_PACKAGE;
    public static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
    public static final ClassLoader PLATFORM_CLASS_LOADER = ClassLoader.getPlatformClassLoader();

    static {
        try {
            @SuppressWarnings("Java9ReflectionClassVisibility")
            var clazz = SYSTEM_CLASS_LOADER.loadClass("jdk.internal.loader.BuiltinClassLoader");
            @SuppressWarnings("Java9ReflectionClassVisibility")
            var ucpClazz = SYSTEM_CLASS_LOADER.loadClass("jdk.internal.loader.URLClassPath");
            GET_UCP_FROM_CLASSLOADER = HackLookupHolder.getLookup().findVarHandle(clazz, "ucp", ucpClazz);
            ADD_URL_TO_UCP = HackLookupHolder.getLookup().findVirtual(ucpClazz, "addURL", MethodType.methodType(void.class, URL.class));
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            FIND_BOOTSTRAP_CLASS_OR_NULL = HackLookupHolder.getLookup().findStatic(ClassLoader.class, "findBootstrapClassOrNull", MethodType.methodType(Class.class, String.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            DEFINE_CLASS = HackLookupHolder.getLookup().findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class));
            DEFINE_PACKAGE = HackLookupHolder.getLookup().findVirtual(ClassLoader.class, "definePackage", MethodType.methodType(Package.class, String.class, Module.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static void addUrlToBuildinClassLoader(ClassLoader classLoader, URL url) {
        var ucp = GET_UCP_FROM_CLASSLOADER.get(classLoader);
        ADD_URL_TO_UCP.invoke(ucp, url);
    }

    public static void addUrlToSystemClassLoader(URL url) {
        addUrlToBuildinClassLoader(SYSTEM_CLASS_LOADER, url);
    }

    public static void addUrlToPlatformClassLoader(URL url) {
        addUrlToBuildinClassLoader(PLATFORM_CLASS_LOADER, url);
    }

    @SneakyThrows
    public static Class<?> findBootstrapClassOrNull(String name) {
        return (Class<?>) FIND_BOOTSTRAP_CLASS_OR_NULL.invoke(name);
    }

    @SneakyThrows
    public static Class<?> defineClass(ClassLoader classLoader, String name, byte[] bytes, int offset, int length, ProtectionDomain domain) {
        return (Class<?>) DEFINE_CLASS.invoke(classLoader, name, bytes, offset, length, domain);
    }

    public static Class<?> defineClass(ClassLoader classLoader, String name, byte[] bytes, int offset, int length) {
        return defineClass(classLoader, name, bytes, offset, length, null);
    }

    public static Class<?> defineClass(ClassLoader classLoader, String name, byte[] bytes) {
        return defineClass(classLoader, name, bytes, 0, bytes.length, null);
    }

    @SneakyThrows
    public static Package definePackage(ClassLoader classLoader, String name, Module module) {
        return (Package) DEFINE_PACKAGE.invoke(classLoader, name, module);
    }
}
