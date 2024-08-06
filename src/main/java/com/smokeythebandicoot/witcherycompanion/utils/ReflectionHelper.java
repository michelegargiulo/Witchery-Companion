package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReflectionHelper {

    private static final HashMap<ClassProp, Method> methodCache = new HashMap<>();
    private static final HashMap<ClassProp, Field> fieldCache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object obj, String methodName, Class<?>[] types, Class<?> returnType, boolean bypassCache, Object... params) {
        Class<?> clazz = obj.getClass();
        ClassProp cm = new ClassProp(clazz, methodName, types);
        try {

            Method target;
            if (methodCache.containsKey(cm) && !bypassCache) {
                target = methodCache.get(cm);
            } else {
                target = getMethodRecursive(clazz, methodName, types, returnType);
                if (target == null) {
                    WitcheryCompanion.logger.warn(String.format("Could not find method %s in class %s and its superclasses", methodName, clazz.getName()));
                    return null;
                }
                target.setAccessible(true);
                methodCache.put(cm, target);
            }

            return (T)target.invoke(obj, params);

        } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
            WitcheryCompanion.logger.error(e.getStackTrace());
            return null;
        }

    }

    public static <T> T invokeMethod(Object obj, String methodName, Class<?>[] types, boolean bypassCache, Object... params) {
        return invokeMethod(obj, methodName, types, null, bypassCache, params);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Object obj, String fieldName, boolean bypassCache) {
        Class<?> clazz = obj.getClass();
        ClassProp cm = new ClassProp(clazz, fieldName, null);
        try {

            Field target;
            if (fieldCache.containsKey(cm) && !bypassCache) {
                target = fieldCache.get(cm);
            } else {
                target = getFieldRecursive(clazz, fieldName);
                if (target == null) {
                    WitcheryCompanion.logger.warn(String.format("Could not find field %s in class %s and its superclasses", fieldName, clazz.getName()));
                    return null;
                }
                target.setAccessible(true);
                fieldCache.put(cm, target);
            }

            return (T)target.get(obj);

        } catch (IllegalAccessException e) {
            WitcheryCompanion.logger.error(e.getStackTrace());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getStaticField(Class<?> clazz, String fieldName, boolean bypassCache) {
        ClassProp cm = new ClassProp(clazz, fieldName, null);
        try {

            Field target;
            if (fieldCache.containsKey(cm) && !bypassCache) {
                target = fieldCache.get(cm);
            } else {
                target = getFieldRecursive(clazz, fieldName);
                if (target == null) {
                    WitcheryCompanion.logger.warn(String.format("Could not find field %s in class %s and its superclasses", fieldName, clazz.getName()));
                    return null;
                }
                target.setAccessible(true);
                fieldCache.put(cm, target);
            }

            return (T)target.get(null);

        } catch (IllegalAccessException e) {
            WitcheryCompanion.logger.error(e.getStackTrace());
        }

        return null;
    }

    public static void setField(Object obj, String fieldName, boolean bypassCache, Object setValue) {
        Class<?> clazz = obj.getClass();
        ClassProp cm = new ClassProp(clazz, fieldName, null);
        try {

            Field target;
            if (fieldCache.containsKey(cm) && !bypassCache) {
                target = fieldCache.get(cm);
            } else {
                target = getFieldRecursive(clazz, fieldName);
                if (target == null) {
                    WitcheryCompanion.logger.warn(String.format("Could not find field %s in class %s and its superclasses", fieldName, clazz.getName()));
                    return;
                }
                target.setAccessible(true);
                fieldCache.put(cm, target);
            }

            target.set(obj, setValue);

        } catch ( IllegalAccessException e) {
            WitcheryCompanion.logger.error(e.getStackTrace());
        }

    }

    public static void setStaticField(Class<?> clazz, String fieldName, boolean bypassCache, Object setValue) {
        ClassProp cm = new ClassProp(clazz, fieldName, null);
        try {

            Field target;
            if (fieldCache.containsKey(cm) && !bypassCache) {
                target = fieldCache.get(cm);
            } else {
                target = getFieldRecursive(clazz, fieldName);
                if (target == null) {
                    WitcheryCompanion.logger.warn(String.format("Could not find field %s in class %s and its superclasses", fieldName, clazz.getName()));
                    return;
                }
                fieldCache.put(cm, target);
            }

            target.setAccessible(true);
            target.set(null, setValue);

        } catch (IllegalAccessException e) {
            WitcheryCompanion.logger.error(e.getStackTrace());
        }

    }

    private static Field getFieldRecursive(Class<?> clazz, String fieldName) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Field[] cFields = c.getDeclaredFields();
            List<Field> fields = Arrays.stream(cFields).filter(field -> field.getName().equals(fieldName)).collect(Collectors.toList());
            if (!fields.isEmpty()) return fields.get(0);
        }
        return null;
    }

    /** This method looks for all declared methods from the passed class to all superclasses. A method must match all paramTypes.
     * Since JVM supports return type overloading (but the Java language does not), it is also possible to specify a return value
     * to further narrow the target method. Use null for any return type. No guarantees about which method is returned if
     * return type overloading has been used and null returnType is passed */
    private static Method getMethodRecursive(Class<?> clazz, String methodName, Class<?>[] paramTypes, Class<?> returnType) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Method[] cMethods = c.getDeclaredMethods();
            List<Method> methods = Arrays.stream(cMethods)
                    .filter(method -> method.getName().equals(methodName) && matchTypes(method, paramTypes, returnType))
                    .collect(Collectors.toList());
            if (!methods.isEmpty()) return methods.get(0);
        }
        return null;
    }

    /** Returns true if the target method has the same paramTypes and return type. Return type
     * match is ignored if returnType null is passed. Use 'Void.TYPE' to check for void return value */
    private static boolean matchTypes(Method method, Class<?>[] types, Class<?> returnType) {
        Class<?>[] methodTypes = method.getParameterTypes();

        if (methodTypes.length != types.length) return false;

        for (int i = 0; i < methodTypes.length; i++) {
            if (!methodTypes[i].equals(types[i]))
                return false;
        }

        if (returnType != null) {
            return method.getReturnType().equals(returnType);
        }

        return true;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassProp classProp = (ClassProp) o;
            return Objects.equals(aClass, classProp.aClass) && Objects.equals(methodName, classProp.methodName) && Objects.deepEquals(paramTypes, classProp.paramTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aClass, methodName, Arrays.hashCode(paramTypes));
        }
    }

}
