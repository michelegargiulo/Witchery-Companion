package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ReflectionHelper {

    private static final HashMap<ClassProp, Method> methodCache = new HashMap<>();
    private static final HashMap<ClassProp, Field> fieldCache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object obj, String methodName, Class<?>[] types, boolean bypassCache, Object... params) {
        Class<?> clazz = obj.getClass();
        ClassProp cm = new ClassProp(clazz, methodName, types);
        try {

            Method target;
            if (methodCache.containsKey(cm) && !bypassCache) {
                target = methodCache.get(cm);
            } else {
                target = clazz.getDeclaredMethod(methodName, types);
                methodCache.put(cm, target);
            }

            target.setAccessible(true);
            return (T)target.invoke(obj, params);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
            WitcheryCompanion.logger.error(e.getStackTrace());
            return null;
        }

    }


    public static void setField(Object obj, String fieldName, boolean bypassCache, Object setValue) {
        Class<?> clazz = obj.getClass();
        ClassProp cm = new ClassProp(clazz, fieldName, null);
        try {

            Field target;
            if (fieldCache.containsKey(cm) && !bypassCache) {
                target = fieldCache.get(cm);
            } else {
                target = clazz.getDeclaredField(fieldName);
                fieldCache.put(cm, target);
            }

            target.setAccessible(true);
            target.set(obj, setValue);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            WitcheryCompanion.logger.error(e.getStackTrace());
        }

    }

    private static class ClassProp {
        public final Class<?> aClass;
        public final String methodName;
        public final Class<?>[] paramTypes;

        public ClassProp(Class<?> aClass, String methodName, Class<?>[] paramTypes) {
            this.aClass = aClass;
            this.methodName = methodName;
            this.paramTypes = paramTypes;
        }
    }

}
