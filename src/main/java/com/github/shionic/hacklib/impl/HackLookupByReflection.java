package com.github.shionic.hacklib.impl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public final class HackLookupByReflection {
    public static MethodHandles.Lookup makeLookup() throws Exception {
        Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
        field.setAccessible(true);
        return (MethodHandles.Lookup) field.get(null);
    }
}
