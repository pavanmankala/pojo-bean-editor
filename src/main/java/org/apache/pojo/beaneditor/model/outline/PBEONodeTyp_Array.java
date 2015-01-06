package org.apache.pojo.beaneditor.model.outline;

import org.apache.pojo.beaneditor.PojoBeanCreator;
import org.apache.pojo.beaneditor.model.PBENodeValueMutator;

public class PBEONodeTyp_Array extends PBEOExtendableNode {
    public PBEONodeTyp_Array(String elemName, PojoBeanCreator creator, PBENodeValueMutator mutator, Object ctx) {
        super(elemName, creator, mutator, ctx);
    }

    @Override
    public Object getNodeValue() {
        return null;
    }

    @Override
    public Object setNodeValue(Object element) {
        return null;
    }

    @Override
    public Object addNewBranchNode(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object removeBranchNode(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void visit(PBEOVisitor visitor, int step) {
        // TODO Auto-generated method stub
        
    }
}
