package org.apache.pojo.beaneditor;

public interface BeanValueTransformer {
    Object transform(String stringRep);

    String transform(Object beanMemberValue);
}
