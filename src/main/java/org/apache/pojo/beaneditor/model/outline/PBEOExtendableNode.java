package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.PojoBeanCreator;
import org.apache.pojo.beaneditor.model.PBENodeValueMutator;

public abstract class PBEOExtendableNode extends PBEOAbstractNode implements Visitable {
    protected final PojoBeanCreator extensionElementCreator;

    public PBEOExtendableNode(String elemName, PojoBeanCreator extensionElementCreator,
            PBENodeValueMutator mutator, Object ctx) {
        super(elemName, mutator, ctx);
        this.extensionElementCreator = extensionElementCreator;
    }

    public abstract Object addNewBranchNode(int index);

    public abstract Object removeBranchNode(int index);

    @Override
    public final boolean isLeaf() {
        return false;
    }
}
