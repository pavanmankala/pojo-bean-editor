package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.model.PBENodeValueMutator;

public abstract class PBEONode {
    protected final PBENodeValueMutator mutator;
    protected final Object context;
    private final String nodeName;
    private final boolean isLeaf;

    public PBEONode(String nodeName, PBENodeValueMutator mutator, Object ctx) {
        this.mutator = mutator;
        this.nodeName = nodeName;
        this.context = ctx;
        if (mutator != null) {
            this.isLeaf = PBEUtils.isRepresentedAsLeaf(mutator.getElemGetter().getReturnType());
        } else {
            this.isLeaf = false;
        }
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public String getNodeName() {
        return nodeName;
    }

    public PBENodeValueMutator getMutator() {
        return mutator;
    }

    public abstract Object getNodeValue();

    public abstract Object setNodeValue(Object element);
}
