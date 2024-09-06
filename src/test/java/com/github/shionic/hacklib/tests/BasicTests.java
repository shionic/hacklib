package com.github.shionic.hacklib.tests;

import com.github.shionic.hacklib.HackClassLoaderHelper;
import com.github.shionic.hacklib.HackLookupHolder;
import com.github.shionic.hacklib.HackModuleHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BasicTests {
    @Test
    public void getLookup() {
        var lookup = HackLookupHolder.getLookup();
        Assertions.assertNotNull(lookup);
    }

    @Test
    public void getAllUnnamedModule() {
        var module = HackModuleHelper.getAllUnnamedModule();
        Assertions.assertNotNull(module);
    }

    @Test
    public void getBaseModuleController() {
        var layer = String.class.getModule().getLayer();
        var controller = HackModuleHelper.getController(layer);
        Assertions.assertNotNull(controller);
        Assertions.assertEquals(layer, controller.layer());
    }

    @Test
    public void getBootstrapClassOrNull() {
        var clazz = HackClassLoaderHelper.findBootstrapClassOrNull("java.lang.String");
        Assertions.assertEquals(String.class, clazz);
    }

    @Test
    public void myTest() {
        var module = HackClassLoaderHelper.class.getModule();
        HackClassLoaderHelper.addUrlToSystemClassLoader(null);
    }
}
