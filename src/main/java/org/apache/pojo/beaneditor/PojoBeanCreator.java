package org.apache.pojo.beaneditor;

public interface PojoBeanCreator {
    <T> T createPojoBean(Class<T> pojoTypeClazz);
}
