package com.github.shionic.hacklib.impl;

import java.lang.invoke.MethodHandles;

public class HackLookupByNativeLibrary {

    static {
        try {
            System.loadLibrary("hacklibaccess");
        } catch (Throwable e) {
        }
    }

    public native static MethodHandles.Lookup makeLookup();
}
