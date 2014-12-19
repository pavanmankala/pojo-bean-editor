package org.apache.pojo.beaneditor.model;

import org.apache.pojo.beaneditor.PBEElementMutator;
import org.apache.pojo.beaneditor.PojoBeanCreator;

public class PBEMapElement extends PBEExtendableElement {
    public PBEMapElement(String elemName, PojoBeanCreator creator, PBEModelElement parent, PBEElementMutator mutator,
            Object obj) {
        super(elemName, creator, parent, mutator, obj);
    }
}
