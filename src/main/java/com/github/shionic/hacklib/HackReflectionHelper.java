package com.github.shionic.hacklib;

import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;

public class HackReflectionHelper {
    private static final MethodHandle METHOD_SET_ACCESSIBLE0;
    static {
        try {
            METHOD_SET_ACCESSIBLE0 = HackLookupHolder.getLookup().findVirtual(AccessibleObject.class, "setAccessible0", MethodType.methodType(boolean.class, boolean.class));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static void setAccessible(AccessibleObject f, boolean value) {
        METHOD_SET_ACCESSIBLE0.invoke(f, value);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static<T> T newInstance(Class<T> clazz, Object... objects) {
        Class<?>[] classes = new Class[objects.length];
        for(int i=0;i< objects.length;++i) {
            classes[i] = objects.getClass();
        }
        final MethodHandle c;
        c = HackLookupHolder.getLookup().findConstructor(clazz, MethodType.methodType(void.class, classes));
        return (T) c.invoke(objects);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static<T> T newInstance(Constructor<T> constructor, Object... objects) {
        final MethodHandle c;
        c = HackLookupHolder.getLookup().unreflectConstructor(constructor);
        return (T) c.invoke(objects);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static<T> T newInstance(Class<T> clazz) {
        final MethodHandle c;
        c = HackLookupHolder.getLookup().findConstructor(clazz, MethodType.methodType(void.class));
        return (T) c.invoke();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static<T> T getStaticField(Class<?> clazz, String name, Class<T> fieldClazz) {
        return (T) HackLookupHolder.getLookup().findStaticVarHandle(clazz, name, fieldClazz).get();
    }

    @SneakyThrows
    public static<T> void setStaticField(Class<?> clazz, String name, Class<T> fieldClazz, T value) {
        HackLookupHolder.getLookup().findStaticVarHandle(clazz, name, fieldClazz).set(value);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static<T> T getField(Object obj, String name, Class<T> fieldClazz) {
        Class<?> clazz = obj.getClass();
        return (T) HackLookupHolder.getLookup().findVarHandle(clazz, name, fieldClazz).get(clazz);
    }

    @SneakyThrows
    public static<T> void setField(Object obj, String name, Class<T> fieldClazz, T value) {
        Class<?> clazz = obj.getClass();
        HackLookupHolder.getLookup().findVarHandle(clazz, name, fieldClazz).set(obj, value);
    }
}
