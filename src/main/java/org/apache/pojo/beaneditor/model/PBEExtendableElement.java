package org.apache.pojo.beaneditor.model;

import org.apache.pojo.beaneditor.PBEElementMutator;
import org.apache.pojo.beaneditor.PojoBeanCreator;

public abstract class PBEExtendableElement extends PBEModelElement {
    protected final PojoBeanCreator creator;

    public PBEExtendableElement(String elemName, PojoBeanCreator creator, PBEModelElement parent,
            PBEElementMutator mutator, Object obj) {
        super(elemName, parent, mutator, obj);
        this.creator = creator;
    }

    @Override
    public final boolean isLeaf() {
        return false;
    }
}
