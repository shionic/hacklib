package com.github.shionic.hacklib;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.net.URL;

public class HackClassLoaderHelper {
    private static final VarHandle GET_UCP_FROM_CLASSLOADER;
    private static final MethodHandle ADD_URL_TO_UCP;
    private static final MethodHandle FIND_BOOTSTRAP_CLASS_OR_NULL;
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
    }

    public static void addUrlToBuildinClassLoader(ClassLoader classLoader, URL url) {
        var ucp = GET_UCP_FROM_CLASSLOADER.get(classLoader);
        try {
            ADD_URL_TO_UCP.invoke(ucp, url);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void addUrlToSystemClassLoader(URL url) {
        addUrlToBuildinClassLoader(SYSTEM_CLASS_LOADER, url);
    }

    public static void addUrlToPlatformClassLoader(URL url) {
        addUrlToBuildinClassLoader(PLATFORM_CLASS_LOADER, url);
    }

    public static Class<?> findBootstrapClassOrNull(String name) {
        try {
            return (Class<?>) FIND_BOOTSTRAP_CLASS_OR_NULL.invoke(name);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
