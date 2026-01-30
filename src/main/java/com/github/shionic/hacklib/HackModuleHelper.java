package com.github.shionic.hacklib;

import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class HackModuleHelper {
    private static final MethodHandle MODULE_CONTROLLER_CONSTRUCTOR;
    public static final Module ALL_UNNAMED_MODULE;
    public static final Module EVERYONE_MODULE;
    static {
        try {
            MODULE_CONTROLLER_CONSTRUCTOR = HackLookupHolder.getLookup().findConstructor(ModuleLayer.Controller.class, MethodType.methodType(void.class, ModuleLayer.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            ALL_UNNAMED_MODULE = (Module) HackLookupHolder.getLookup().findStaticVarHandle(Module.class, "ALL_UNNAMED_MODULE", Module.class).get();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Module everyoneModule;
        try {
            everyoneModule = (Module) HackLookupHolder.getLookup().findStaticVarHandle(Module.class, "EVERYONE_MODULE", Module.class).get();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            everyoneModule = null;
        }
        EVERYONE_MODULE = everyoneModule;
    }
    @SneakyThrows
    public static ModuleLayer.Controller getController(ModuleLayer layer) {
        return (ModuleLayer.Controller) MODULE_CONTROLLER_CONSTRUCTOR.invoke(layer);
    }

    public static Module getAllUnnamedModule() {
        return ALL_UNNAMED_MODULE;
    }

    public static Module getEveryoneModule() {
        return EVERYONE_MODULE;
    }
}
