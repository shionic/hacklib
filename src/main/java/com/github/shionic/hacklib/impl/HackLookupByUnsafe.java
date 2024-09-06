package com.github.shionic.hacklib.impl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class HackLookupByUnsafe {
    private static final Class<?> unsafeClazz;

    static {
        try {
            unsafeClazz = ClassLoader.getPlatformClassLoader().loadClass("sun.misc.Unsafe");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodHandles.Lookup makeLookup() throws Exception {
        Field field = unsafeClazz.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Object unsafe = field.get(null);
        return makeLookup(unsafe);
    }

    @SuppressWarnings("deprecation")
    public static MethodHandles.Lookup makeLookup(Object unsafe) throws Exception {
        var field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
        Method staticFieldOffsetMethod = unsafeClazz.getDeclaredMethod("staticFieldOffset", Field.class);
        Method staticFieldBase = unsafeClazz.getDeclaredMethod("staticFieldBase", Field.class);
        Method getObject = unsafeClazz.getDeclaredMethod("getObject", Object.class, long.class);
        var offset = (long) staticFieldOffsetMethod.invoke(unsafe, field);
        var base = staticFieldBase.invoke(unsafe, field);
        return (MethodHandles.Lookup) getObject.invoke(unsafe, base, offset);
    }
}
