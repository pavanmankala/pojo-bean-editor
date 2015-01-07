package org.apache.pojo.beaneditor.model.outline;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pojo.beaneditor.PojoBeanCreator;
import org.apache.pojo.beaneditor.model.PBENodeValueMutator;

public class PBEONodeTyp_Map extends PBEOExtendableNode {
    private final Map<Integer, PBEOKeyValuePair> backingMap;
    private final Class<?> valueType;

    public PBEONodeTyp_Map(String elemName, PojoBeanCreator creator, PBENodeValueMutator mutator, Object ctx) {
        super(elemName, creator, mutator, ctx);
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

        backingMap = new HashMap<Integer, PBEOKeyValuePair>();
    }

    @Override
    public Object addNewBranchNode(final int index) {
        MapEntry<Object> newEntry = new MapEntry<Object>();
        Object newValueObj = extensionElementCreator.createPojoBean(valueType);

        newEntry.setKey("/** TODO: Insert Key here**/");
        newEntry.setValue(newValueObj);
        PBEOMapEntryNode keyElem = new PBEOMapEntryNode("Key", null, newEntry), valueElem = new PBEOMapEntryNode("Key",
                null, newEntry);
        PBEOKeyValuePair pair = new PBEOKeyValuePair(keyElem, valueElem);

        PBEOKeyValuePair existingValue = backingMap.remove(index);

        if (existingValue != null) {
            int nextIndex = index;

            backingMap.put(nextIndex++, pair);

            while (existingValue != null) {
                existingValue = backingMap.put(nextIndex++, existingValue);
            }

            return newEntry;
        }

        if (index == backingMap.size()) {
            backingMap.put(index, pair);
        } else {
            throw new RuntimeException("Illegal index specified: " + index);
        }

        return newEntry;
    }

    @Override
    public Object removeBranchNode(final int index) {
        PBEOKeyValuePair existingValue = backingMap.get(index), lastValue = backingMap.get(backingMap.size() - 1);

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
    public Object getNodeValue() {
        return backingMap;
    }

    @Override
    public Object setNodeValue(Object element) {
        if (element == null) {
            mutator.setObject(context, element);
            return null;
        }

        if (element != null && !(element instanceof Map)) {
            throw new RuntimeException("element is not a Map element");
        }

        for (Entry<?, ?> e : ((Map<?, ?>) element).entrySet()) {
            if (e.getKey() instanceof String && valueType.isInstance(e.getValue())) {
                MapEntry<Object> mapObject = new MapEntry<Object>();
                mapObject.setKey(e.getKey().toString());
                mapObject.setValue(e.getValue());

                // new PBEOMapEntryElement("MapKeyValuePair", this, null,
                // mapObject);
                //
            } else {
                throw new RuntimeException("Illegal object type found in map; has to be of "
                        + valueType.getSimpleName());
            }
        }

        return null;
    }

    public static class PBEOKeyValuePair {
        public final PBEOMapEntryNode keyElem, valueElem;

        public PBEOKeyValuePair(PBEOMapEntryNode key, PBEOMapEntryNode val) {
            keyElem = key;
            valueElem = val;
        }
    }

    @Override
    public void visit(PBEOVisitor visitor, int step) {
        // TODO Auto-generated method stub

    }
}
