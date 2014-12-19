package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.PBEElementMutator;

public class PBEOLeafElement extends PBEOModelElement {
    public PBEOLeafElement(String elemName, PBEOModelElement parent, PBEElementMutator mutator, Object ctx) {
        super(elemName, parent, mutator, ctx);
    }

    @Override
    public Object getElement() {
        return mutator.getObject(context);
    }

    @Override
    public Object setElement(Object element) {
        Object existing = mutator.getObject(context);

        if (existing != null && element != null && !existing.equals(element)) {
            mutator.setObject(context, element);
        }

        return existing;
    }
}
