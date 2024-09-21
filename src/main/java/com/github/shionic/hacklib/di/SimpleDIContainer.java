package com.github.shionic.hacklib.di;

import com.github.shionic.hacklib.HackReflectionHelper;

import java.lang.reflect.Constructor;
import java.util.*;

public class SimpleDIContainer {
    private final Map<Class<?>, ObjectDefinition> definitionMap = new HashMap<>();
    private final Map<Class<?>, List<Object>> objectMap = new HashMap<>();
    private final List<ObjectPostProcessor> postProcessors = new ArrayList<>();
    private <T> ObjectDefinition makeDefinition(Class<T> clazz) {
        Constructor<?> constructor = null;
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            if(constructor == null) {
                constructor = c;
                continue;
            }
            var annotation = c.getAnnotation(SimpleAutoinject.class);
            if(annotation != null) {
                constructor = c;
                break;
            }
        }
        if(constructor == null) {
            throw new RuntimeException(String.format("No one constructors found in %s", clazz.getName()));
        }

        List<Class<?>> dependencies = new ArrayList<>(List.of(constructor.getParameterTypes()));

        return new ObjectDefinition(clazz, constructor, dependencies);
    }

    public SimpleDIContainer() {
    }

    public SimpleDIContainer(List<ObjectPostProcessor> postProcessors) {
        this.postProcessors.addAll(postProcessors);
    }

    public void addPostProcessor(ObjectPostProcessor postProcessor) {
        postProcessors.add(postProcessor);
    }

    @SuppressWarnings("unchecked")
    public<T> T getCachedInstanceExact(Class<T> clazz) {
        List<Object> list = objectMap.get(clazz);
        return (T) getObjectFromList(list, clazz);
    }
    
    @SuppressWarnings("unchecked")
    public<T> T getCachedInstance(Class<T> clazz) {
        var result = getCachedInstanceExact(clazz);
        if(result != null) {
            return result;
        }
        for(var i : clazz.getInterfaces()) {
            result = (T) getCachedInstance(i);
            if(result != null) {
                return result;
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if(superclass != null && superclass != Object.class) {
            result = (T) getCachedInstance(superclass);
        }
        return result;
    }
    
    private Object getObjectFromList(List<Object> list, Class<?> clazz) {
        if(list != null && !list.isEmpty()) {
            if(list.size() == 1) {
                return list.getFirst();
            } else {
                throw new RuntimeException(String.format("Class %s had more than one variants", clazz.getName()));
            }
        }
        return null;
    }
    
    private void putObjectToMap(Class<?> clazz, Object object) {
        List<Object> e = objectMap.computeIfAbsent(clazz, x -> new ArrayList<>(1));
        e.add(object);
        for(var i : clazz.getInterfaces()) {
            putObjectToMap(i, object);
        }
        Class<?> superclass = clazz.getSuperclass();
        if(superclass != null && superclass != Object.class) {
            putObjectToMap(superclass, object);
        }
    }

    @SuppressWarnings("unchecked")
    public<T> T getInstance(Class<T> clazz) {
        var obj = getCachedInstance(clazz);
        if(obj != null) {
            return obj;
        }
        var def = definitionMap.computeIfAbsent(clazz, this::makeDefinition);
        List<Object> objects = new ArrayList<>(def.dependencies().size());
        for(var e : def.dependencies()) {
            objects.add(getInstance(e));
        }
        obj = (T) HackReflectionHelper.newInstance(def.constructor(), objects);
        for(var e : postProcessors) {
            e.postProcess(clazz, obj, def);
        }
        putObjectToMap(clazz, obj);
        return obj;
    }

    public record ObjectDefinition(Class<?> clazz, Constructor<?> constructor, List<Class<?>> dependencies) {

    }

    public interface ObjectPostProcessor {
        void postProcess(Class<?> clazz, Object obj, ObjectDefinition definition);
    }
}
