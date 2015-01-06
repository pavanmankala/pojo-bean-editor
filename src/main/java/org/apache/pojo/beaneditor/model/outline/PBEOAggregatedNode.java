package org.apache.pojo.beaneditor.model.outline;

import java.util.ArrayList;
import java.util.List;

import org.apache.pojo.beaneditor.model.PBENodeValueMutator;

public class PBEOAggregatedNode extends PBEOAbstractNode implements Visitable {
    private final List<PBEONode> elements = new ArrayList<PBEONode>(5);

    public PBEOAggregatedNode(String elemName, PBENodeValueMutator mutator, Object ctx) {
        super(elemName, mutator, ctx);
    }

    public void addElement(PBEONode element) {
        elements.add(element);
    }

    public boolean removeElement(PBEONode element) {
        return elements.remove(element);
    }

    @Override
    public void visit(PBEOVisitor visitor, int step) {
        for (PBEONode node : elements) {
            visitor.node(node, step);

            if (!node.isLeaf() && node instanceof Visitable) {
                ((Visitable) node).visit(visitor, step + 1);
            }
        }
    }
}
