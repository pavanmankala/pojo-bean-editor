package org.apache.pojo.beaneditor.model.outline;

public interface Visitable {
    public static interface PBEOVisitor{
        void node(PBEONode node, int step);
    }

    public void visit(PBEOVisitor visitor, int step);
}
