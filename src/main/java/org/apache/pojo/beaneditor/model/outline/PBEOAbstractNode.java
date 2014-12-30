package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.PBENodeValueMutator;

public abstract class PBEOAbstractNode extends PBEONode {
    public PBEOAbstractNode(String elemName, PBENodeValueMutator mutator, Object ctx) {
        super(elemName, mutator, ctx);
    }

    @Override
    public Object getNodeValue() {
        return mutator.getObject(context);
    }

    @Override
    public Object setNodeValue(Object element) {
        Object existing = mutator.getObject(context);

        if (existing != null && element != null && !existing.equals(element)) {
            mutator.setObject(context, element);
        }

        return existing;
    }
}
