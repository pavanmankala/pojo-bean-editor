package org.apache.pojo.beaneditor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

import org.apache.pojo.beaneditor.model.outline.PBEOAggregatedNode;
import org.apache.pojo.beaneditor.model.outline.PBEONode;
import org.apache.pojo.beaneditor.model.outline.Visitable.PBEOVisitor;

public class PBEDocument extends PlainDocument {
    private final PBEOAggregatedNode aggregatedNode;
    private final AbstractElement defaultRoot;
    private transient Segment s;

    protected PBEDocument(PBEOAggregatedNode aggNode) {
        aggregatedNode = aggNode;
        defaultRoot = createDefaultRoot();
        initContent();
    }

    private void initContent() {
        final Content content = getContent();
        final List<BranchElement> branches = new ArrayList<BranchElement>();
        aggregatedNode.visit(new PBEOVisitor() {
            int offset = 0;

            @Override
            public void node(PBEONode node, int step) {
                String nodeName = node.getNodeName(), valName = "VAL";

                try {
                    content.insertString(offset, nodeName);
                    content.insertString(nodeName.length() + offset, valName);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                offset += nodeName.length() + valName.length();
            }
        }, 0);

        aggregatedNode.visit(new PBEOVisitor() {
            int offset = 0;

            @Override
            public void node(PBEONode node, int step) {
                String nodeName = node.getNodeName(), valName = "VAL";

                BranchElement currentBranch = (BranchElement) createBranchElement(defaultRoot, null);
                branches.add(currentBranch);

                BranchElement keyBranch = (BranchElement) createBranchElement(currentBranch, null), valueBranch = (BranchElement) createBranchElement(
                        currentBranch, null);

                Element[] keylines = new Element[1];
                keylines[0] = createLeafElement(keyBranch, null, offset, offset + nodeName.length());
                keyBranch.replace(0, 0, keylines);
                offset = offset + nodeName.length();

                Element[] valuelines = new Element[1];
                valuelines[0] = createLeafElement(valueBranch, null, offset, offset + valName.length());
                valueBranch.replace(0, 0, valuelines);
                offset = offset + valName.length();

                Element[] currBranchLines = new Element[2];
                currBranchLines[0] = keyBranch;
                currBranchLines[1] = valueBranch;
                currentBranch.replace(0, 0, currBranchLines);
            }
        }, 0);
        BranchElement root = (BranchElement) getDefaultRootElement();
        root.replace(0, 0, branches.toArray(new Element[0]));
        System.out.println(branches);
    }

    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        super.insertUpdate(chng, attr);
    }

    protected AbstractElement createDefaultRoot() {
        BranchElement map = (BranchElement) createBranchElement(null, null);
        return map;
    }

    @Override
    public Element getDefaultRootElement() {
        return defaultRoot;
    }

    @Override
    public Element getParagraphElement(int pos) {
        return null;
    }
}
