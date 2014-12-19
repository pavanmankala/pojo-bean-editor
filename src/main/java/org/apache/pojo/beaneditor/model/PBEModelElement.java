package org.apache.pojo.beaneditor.model;

import org.apache.pojo.beaneditor.PBEElementMutator;

public class PBEModelElement {
    private final PBEModelElement parentElem;
    private final String elemName;
    private final PBEElementMutator mutator;
    private final boolean isLeaf;

    public PBEModelElement(String elemName, PBEModelElement parent, PBEElementMutator mutator, Object obj) {
        this.parentElem = parent;
        this.mutator = mutator;
        this.elemName = elemName;
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

    public PBEModelElement getParentElem() {
        return parentElem;
    }
}
