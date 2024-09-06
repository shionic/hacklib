package com.github.shionic.hacklib;

import com.github.shionic.hacklib.impl.HackLookupByReflection;
import com.github.shionic.hacklib.impl.HackLookupByUnsafe;

import java.lang.invoke.MethodHandles;

public final class HackLookupHolder {
    private static final MethodHandles.Lookup lookup;

    static {
        MethodHandles.Lookup tmp = null;
        try {
            tmp = HackLookupByReflection.makeLookup();
        } catch (Throwable e) {

        }
        if(tmp == null) {
            try {
                tmp = HackLookupByUnsafe.makeLookup();
            } catch (Throwable e) {

            }
        }
        if(tmp == null) {
            throw new InternalError("Failed to get full power lookup");
        }
        lookup = tmp;
    }

    public static MethodHandles.Lookup getLookup() {
        return lookup;
    }
}
