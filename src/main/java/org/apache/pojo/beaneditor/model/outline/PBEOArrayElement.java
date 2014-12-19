package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.PBEElementMutator;
import org.apache.pojo.beaneditor.PojoBeanCreator;

public class PBEOArrayElement extends PBEOExtendableElement {
    public PBEOArrayElement(String elemName, PojoBeanCreator creator, PBEOModelElement parent,
            PBEElementMutator mutator, Object ctx) {
        super(elemName, creator, parent, mutator, ctx);
    }

    @Override
    public Object getElement() {
        return null;
    }

    @Override
    public Object setElement(Object element) {
        return null;
    }

    @Override
    public Object addNewBranch(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object removeBranchElement(int index) {
        // TODO Auto-generated method stub
        return null;
    }
}
