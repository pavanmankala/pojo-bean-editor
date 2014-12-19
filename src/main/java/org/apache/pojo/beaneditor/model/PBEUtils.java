package org.apache.pojo.beaneditor.model;

public class PBEUtils {
    public static boolean isRepresentedAsLeaf(Class<?> classType) {
        return classType.isPrimitive() || classType == String.class || classType.isEnum() || classType == Boolean.class
                || classType == Character.class || classType == Byte.class || classType == Short.class
                || classType == Integer.class || classType == Long.class || classType == Float.class
                || classType == Double.class;
    }
}
