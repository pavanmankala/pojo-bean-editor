package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.PBEElementMutator;
import org.apache.pojo.beaneditor.PojoBeanCreator;

public abstract class PBEOExtendableElement extends PBEOModelElement {
    protected final PojoBeanCreator extensionElementCreator;

    public PBEOExtendableElement(String elemName, PojoBeanCreator extensionElementCreator, PBEOModelElement parent,
            PBEElementMutator mutator, Object ctx) {
        super(elemName, parent, mutator, ctx);
        this.extensionElementCreator = extensionElementCreator;
    }

    public abstract Object addNewBranch(int index);

    @Override
    public final boolean isLeaf() {
        return false;
    }
}
