package org.apache.pojo.beaneditor.model.outline;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.pojo.beaneditor.PBEElementMutator;
import org.apache.pojo.beaneditor.PojoBeanCreator;

public class PBEOCollectionElement extends PBEOExtendableElement {
    private final Class<?> valueType;

    public PBEOCollectionElement(String elemName, PojoBeanCreator creator, PBEOModelElement parent,
            PBEElementMutator mutator, Object ctx) {
        super(elemName, creator, parent, mutator, ctx);
        Type retType = mutator.getElemGetter().getGenericReturnType();
        if (retType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) retType;
            Class<?> keyType = (Class<?>) pType.getActualTypeArguments()[0];

            if (keyType != String.class) {
                throw new RuntimeException("non string key types not supported");
            }

            valueType = (Class<?>) pType.getActualTypeArguments()[1];
        } else {
            throw new RuntimeException("no value type defined");
        }
    }

    @Override
    public Object addNewBranch(int index) {
        Object obj = extensionElementCreator.createPojoBean(valueType);
        return null;
    }

    @Override
    public Object removeBranchElement(int index) {
        return null;
    }

    @Override
    public Object getElement() {
        return null;
    }

    @Override
    public Object setElement(Object element) {
        return null;
    }

    public static class MapEntry<T> {
        private String key;
        private T value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }
}
