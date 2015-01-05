package org.apache.pojo.beaneditor.model.outline;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.pojo.beaneditor.PBEBeanParser;
import org.apache.pojo.beaneditor.PBENodeValueMutator;
import org.apache.pojo.beaneditor.PojoBeanCreator;

public class PBEONodeTyp_List extends PBEOExtendableNode {
    private List<PBEONode> backingList;
    private final Class<?> listElemType;

    public PBEONodeTyp_List(String elemName, PojoBeanCreator creator, PBENodeValueMutator mutator, Object ctx) {
        super(elemName, creator, mutator, ctx);
        Type elemTyp = mutator.getElemGetter().getGenericReturnType();

        if (elemTyp instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) elemTyp;
            listElemType = (Class<?>) pType.getActualTypeArguments()[0];
        } else {
            throw new RuntimeException("no value type defined");
        }

        backingList = new ArrayList<PBEONode>();
        addNewBranchNode(0);
    }

    @Override
    public Object addNewBranchNode(int index) {
        if (!PBEUtils.isRepresentedAsLeaf(listElemType)) {
            Object retElem = extensionElementCreator.createPojoBean(listElemType);
            int idx = Math.min(index, backingList.size());
            PBEOAggregatedNode typElemNode = new PBEOAggregatedNode(listElemType.getSimpleName() + "[" + idx + "]",
                    null, null);
            PBEBeanParser.parseBeanIntoNode(typElemNode, extensionElementCreator, retElem);
            backingList.add(idx, typElemNode);
            return retElem;
        }

        return null;
    }

    @Override
    public Object removeBranchNode(int index) {
        return null;
    }

    @Override
    public void visit(PBEOVisitor visitor, int step) {
        for (PBEONode node : backingList) {
            visitor.node(node, step + 1);

            if (!node.isLeaf() && node instanceof Visitable) {
                ((Visitable) node).visit(visitor, step + 2);
            }
        }
    }
}
