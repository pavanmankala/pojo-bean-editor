package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.PBEElementMutator;
import org.apache.pojo.beaneditor.PojoBeanCreator;

public class PBEOCollectionElement extends PBEOExtendableElement {
    public PBEOCollectionElement(String elemName, PojoBeanCreator creator, PBEOElement parent,
            PBEElementMutator mutator, Object ctx) {
        super(elemName, creator, parent, mutator, ctx);
    }

    @Override
    public Object addNewBranch(int index) {
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
}
