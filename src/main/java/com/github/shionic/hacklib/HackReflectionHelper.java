package com.github.shionic.hacklib;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class HackReflectionHelper {
    private static final MethodHandle METHOD_SET_ACCESSIBLE0;
    static {
        try {
            METHOD_SET_ACCESSIBLE0 = HackLookupHolder.getLookup().findVirtual(AccessibleObject.class, "setAccessible0", MethodType.methodType(boolean.class, boolean.class));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void setAccessible(AccessibleObject f, boolean value) {
        try {
            METHOD_SET_ACCESSIBLE0.invoke(f, value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static<T> T newInstance(Class<T> clazz, Object... objects) {
        Class<?>[] classes = new Class[objects.length];
        for(int i=0;i< objects.length;++i) {
            classes[i] = objects.getClass();
        }
        final MethodHandle c;
        try {
            c = HackLookupHolder.getLookup().findConstructor(clazz, MethodType.methodType(void.class, classes));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        try {
            return (T) c.invoke(objects);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static<T> T newInstance(Constructor<T> constructor, Object... objects) {
        final MethodHandle c;
        try {
            c = HackLookupHolder.getLookup().unreflectConstructor(constructor);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        try {
            return (T) c.invoke(objects);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static<T> T newInstance(Class<T> clazz) {
        final MethodHandle c;
        try {
            c = HackLookupHolder.getLookup().findConstructor(clazz, MethodType.methodType(void.class));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        try {
            return (T) c.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static<T> T getStaticField(Class<?> clazz, String name, Class<T> fieldClazz) {
        try {
            return (T) HackLookupHolder.getLookup().findStaticVarHandle(clazz, name, fieldClazz).get();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static<T> void setStaticField(Class<?> clazz, String name, Class<T> fieldClazz, T value) {
        try {
            HackLookupHolder.getLookup().findStaticVarHandle(clazz, name, fieldClazz).set(value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static<T> T getField(Object obj, String name, Class<T> fieldClazz) {
        try {
            Class<?> clazz = obj.getClass();
            return (T) HackLookupHolder.getLookup().findVarHandle(clazz, name, fieldClazz).get(clazz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static<T> void setField(Object obj, String name, Class<T> fieldClazz, T value) {
        try {
            Class<?> clazz = obj.getClass();
            HackLookupHolder.getLookup().findVarHandle(clazz, name, fieldClazz).set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
