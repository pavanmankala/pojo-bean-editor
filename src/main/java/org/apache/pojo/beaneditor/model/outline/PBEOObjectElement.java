package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.PBEElementMutator;

public abstract class PBEOObjectElement extends PBEOElement {
    public PBEOObjectElement(String elemName, PBEOElement parent, PBEElementMutator mutator, Object ctx) {
        super(elemName, parent, mutator, ctx);
    }
}
