package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ReflectionHelper {

    private static final HashMap<ClassProp, Method> methodCache = new HashMap<>();
    private static final HashMap<ClassProp, Field> fieldCache = new HashMap<>();

    public static boolean invokeMethod(Object obj, String methodName, boolean bypassCache, Object... params) {
        Class<?> clazz = obj.getClass();
        ClassProp cm = new ClassProp(clazz, methodName);
        try {

            Method target;
            if (methodCache.containsKey(cm) && !bypassCache) {
                target = methodCache.get(cm);
            } else {
                target = clazz.getMethod(methodName);
                methodCache.put(cm, target);
            }

            target.setAccessible(true);
            target.invoke(obj, params);
            return true;

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            WitcheryCompanion.logger.error(e.getStackTrace());
            return false;
        }

    }

    public static boolean setField(Object obj, String fieldName, boolean bypassCache, Object setValue) {
        Class<?> clazz = obj.getClass();
        ClassProp cm = new ClassProp(clazz, fieldName);
        try {

            Field target;
            if (fieldCache.containsKey(cm) && !bypassCache) {
                target = fieldCache.get(cm);
            } else {
                target = clazz.getField(fieldName);
                fieldCache.put(cm, target);
            }

            target.setAccessible(true);
            target.set(obj, setValue);
            return true;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            WitcheryCompanion.logger.error(e.getStackTrace());
            return false;
        }

    }

    private static class ClassProp {
        public final Class<?> aClass;
        public final String methodName;

        public ClassProp(Class<?> aClass, String methodName) {
            this.aClass = aClass;
            this.methodName = methodName;
        }
    }

}
