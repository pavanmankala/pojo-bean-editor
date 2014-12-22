package org.apache.pojo.beaneditor.model.outline;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pojo.beaneditor.PBEElementMutator;
import org.apache.pojo.beaneditor.PojoBeanCreator;

public class PBEOMapElement extends PBEOExtendableElement {
    private final Map<Integer, PBEOMapEntry<Object>> backingMap;
    private final Class<?> valueType;

    public PBEOMapElement(String elemName, PojoBeanCreator creator, PBEOElement parent, PBEElementMutator mutator,
            Object ctx) {
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

        backingMap = new HashMap<Integer, PBEOMapEntry<Object>>();
    }

    @Override
    public Object addNewBranch(final int index) {
        PBEOMapEntry<Object> newEntry = new PBEOMapEntry<Object>();
        Object newValueObj = extensionElementCreator.createPojoBean(valueType);

        newEntry.setKey("/** TODO: Insert Key here**/");
        newEntry.setValue(newValueObj);

        PBEOMapEntry<Object> existingValue = backingMap.remove(index);

        if (existingValue != null) {
            int nextIndex = index;

            backingMap.put(nextIndex++, newEntry);

            while (existingValue != null) {
                existingValue = backingMap.put(nextIndex++, existingValue);
            }

            return newEntry;
        }

        if (index == backingMap.size()) {
            backingMap.put(index, newEntry);
        } else {
            throw new RuntimeException("Illegal index specified: " + index);
        }

        return newEntry;
    }

    @Override
    public Object removeBranchElement(final int index) {
        PBEOMapEntry<Object> existingValue = backingMap.get(index), lastValue = backingMap.get(backingMap.size() - 1);

        for (int itIndex = backingMap.size() - 2; itIndex >= index; itIndex--) {
            lastValue = backingMap.put(itIndex, lastValue);
        }

        if (lastValue == existingValue) {
            return lastValue;
        } else {
            throw new RuntimeException("No element found at index: " + index);
        }
    }

    @Override
    public Object getElement() {
        return backingMap;
    }

    @Override
    public Object setElement(Object element) {
        if (element == null) {
            mutator.setObject(context, element);
            return null;
        }

        if (element != null && !(element instanceof Map)) {
            throw new RuntimeException("element is not a Map element");
        }

        for (Entry<?, ?> e : ((Map<?, ?>) element).entrySet()) {
            if (e.getKey() instanceof String && valueType.isInstance(e.getValue())) {
                PBEOMapEntry<Object> mapObject = new PBEOMapEntry<Object>();
                mapObject.setKey(e.getKey().toString());
                mapObject.setValue(e.getValue());

                new PBEOMapEntryElement("MapKeyValuePair", this, null, mapObject);
            } else {
                throw new RuntimeException("Illegal object type found in map; has to be of "
                        + valueType.getSimpleName());
            }
        }

        return null;
    }



}
