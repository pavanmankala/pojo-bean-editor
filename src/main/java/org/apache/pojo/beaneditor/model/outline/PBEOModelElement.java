package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.PBEElementMutator;

public abstract class PBEOModelElement {
    protected final PBEOModelElement parentElem;
    protected final PBEElementMutator mutator;
    protected final Object context;
    private final String elemName;
    private final boolean isLeaf;

    public PBEOModelElement(String elemName, PBEOModelElement parent, PBEElementMutator mutator, Object ctx) {
        this.parentElem = parent;
        this.mutator = mutator;
        this.elemName = elemName;
        this.context = ctx;
        this.isLeaf = PBEUtils.isRepresentedAsLeaf(mutator.getElemGetter().getReturnType());
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public String getElemName() {
        return elemName;
    }

    public PBEElementMutator getMutator() {
        return mutator;
    }

    public PBEOModelElement getParentElem() {
        return parentElem;
    }

    public abstract Object getElement();

    public abstract Object setElement(Object element);
}
