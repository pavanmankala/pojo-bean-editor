package org.apache.pojo.beaneditor.model;

import org.apache.pojo.beaneditor.PBEElementMutator;
import org.apache.pojo.beaneditor.PojoBeanCreator;

public class PBECollectionElement extends PBEExtendableElement {
    public PBECollectionElement(String elemName, PojoBeanCreator creator, PBEModelElement parent,
            PBEElementMutator mutator, Object obj) {
        super(elemName, creator, parent, mutator, obj);
    }
}
