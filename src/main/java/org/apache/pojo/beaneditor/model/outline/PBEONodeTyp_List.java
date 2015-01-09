package org.apache.pojo.beaneditor.model.outline;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.pojo.beaneditor.PojoBeanCreator;
import org.apache.pojo.beaneditor.model.PBEBeanParser;
import org.apache.pojo.beaneditor.model.PBENodeValueMutator;

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
        Object existingValue = getNodeValue();

        if (existingValue != null && existingValue instanceof List) {
            if (PBEUtils.isRepresentedAsLeaf(listElemType)) {
                for (Object branch : ((List<?>) existingValue)) {
                    int index = backingList.size();
                    Object primObj = ContainerLeafNode.createLeafType(listElemType);
                    addNodeIntern(index, primObj);
                    PBEONode addedNode = backingList.get(index);
                    addedNode.setNodeValue(branch);
                }
            } else {
                for (Object branch : ((List<?>) existingValue)) {
                    addNodeIntern(backingList.size(), branch);
                }
            }
        }
    }

    private Object addNodeIntern(int index, Object obj) {
        int idx = Math.min(Math.abs(index), backingList.size());
        String nodeName = listElemType.getSimpleName() + "[" + idx + "]";

        PBEOAggregatedNode typElemNode = new PBEOAggregatedNode(nodeName, null, null);
        PBEBeanParser.parseBeanIntoNode(typElemNode, extensionElementCreator, obj);
        backingList.add(idx, typElemNode);
        return obj;
    }

    @Override
    public Object addNewBranchNode(int index) {
        Object retElem;

        if (!PBEUtils.isRepresentedAsLeaf(listElemType)) {
            retElem = extensionElementCreator.createPojoBean(listElemType);
        } else {
            retElem = ContainerLeafNode.createLeafType(listElemType);
        }

        return addNodeIntern(index, retElem);
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
