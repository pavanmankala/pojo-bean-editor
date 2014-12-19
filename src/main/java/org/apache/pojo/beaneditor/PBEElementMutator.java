package org.apache.pojo.beaneditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PBEElementMutator {
    private final Method elemSetter, elemGetter;

    public PBEElementMutator(Method setter, Method getter) {
        elemSetter = setter;
        elemGetter = getter;
    }

    public Object getObject(Object context) {
        try {
            return elemGetter.invoke(context, new Object[0]);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return null;
        }
    }

    public void setObject(Object context, Object toSet) {
        try {
            elemSetter.invoke(context, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Method getElemGetter() {
        return elemGetter;
    }

    public Method getElemSetter() {
        return elemSetter;
    }
}
