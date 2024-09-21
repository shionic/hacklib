package com.github.shionic.hacklib;

import com.github.shionic.hacklib.impl.HackLookupByNativeLibrary;
import com.github.shionic.hacklib.impl.HackLookupByReflection;
import com.github.shionic.hacklib.impl.HackLookupByUnsafe;

import java.lang.invoke.MethodHandles;

public final class HackLookupHolder {
    private static final MethodHandles.Lookup lookup;

    public enum AccessMethod {
        NATIVE, REFLECTION, UNSAFE, BASIC
    }

    private static final AccessMethod usingAccessMethod;

    static {
        MethodHandles.Lookup tmp = null;
        AccessMethod accessMethod = null;
        try {
            tmp = HackLookupByReflection.makeLookup();
            accessMethod = AccessMethod.REFLECTION;
        } catch (Throwable e) {

        }
        if(tmp == null) {
            try {
                tmp = HackLookupByNativeLibrary.makeLookup();
                accessMethod = AccessMethod.NATIVE;
            } catch (Throwable e) {
            }
        }
        if(tmp == null) {
            try {
                tmp = HackLookupByUnsafe.makeLookup();
                accessMethod = AccessMethod.UNSAFE;
            } catch (Throwable e) {

            }
        }
        if(tmp == null) {
            tmp = MethodHandles.lookup();
            accessMethod = AccessMethod.BASIC;
        }
        lookup = tmp;
        usingAccessMethod = accessMethod;
    }

    public static MethodHandles.Lookup getLookup() {
        return lookup;
    }

    public static AccessMethod getAccessMethod() {
        return usingAccessMethod;
    }
}
