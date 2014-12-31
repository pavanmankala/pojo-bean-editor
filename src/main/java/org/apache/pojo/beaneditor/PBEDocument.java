package org.apache.pojo.beaneditor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleContext;

import org.apache.pojo.beaneditor.model.outline.PBEOAggregatedNode;
import org.apache.pojo.beaneditor.model.outline.PBEONode;
import org.apache.pojo.beaneditor.model.outline.Visitable.PBEOVisitor;

public class PBEDocument extends AbstractDocument {
    private final PBEOAggregatedNode aggregatedNode;
    private final AbstractElement defaultRoot;

    protected PBEDocument(PBEOAggregatedNode aggNode) {
        super(new GapContent(10000));
        aggregatedNode = aggNode;
        defaultRoot = new RootElement();
        initContent();
    }

    private void initContent() {
        writeLock();
        try {
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

                    MemberBranchElement mbe = new MemberBranchElement(null);
                    branches.add(mbe);
                    mbe.addAttribute("step", step);

                    StyleContext ctx = StyleContext.getDefaultStyleContext();
                    AttributeSet set = ctx.addAttribute(SimpleAttributeSet.EMPTY, "PBE_Node", node);
                    set = ctx.addAttribute(SimpleAttributeSet.EMPTY, "PBE_Node_Step", step);

                    BranchElement keyBranch = new KeyBranchElement(mbe, set), valueBranch = new ValueBranchElement(mbe,
                            set);

                    Element[] keylines = new Element[1];
                    keylines[0] = createLeafElement(keyBranch, null, offset, offset + nodeName.length());
                    keyBranch.replace(0, 0, keylines);
                    offset = offset + nodeName.length();

                    Element[] valuelines = new Element[1];
                    valuelines[0] = createLeafElement(valueBranch, null, offset, offset + valName.length());
                    valueBranch.replace(0, 0, valuelines);
                    offset = offset + valName.length();

                    Element[] keyValueElems = new Element[2];
                    keyValueElems[0] = keyBranch;
                    keyValueElems[1] = valueBranch;
                    mbe.replace(0, 0, keyValueElems);
                }
            }, 0);
            BranchElement root = (BranchElement) getDefaultRootElement();
            root.replace(0, 0, branches.toArray(new Element[0]));
        } finally {
            writeUnlock();
        }
    }

    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        super.insertUpdate(chng, attr);
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
    }

    @Override
    public Element getDefaultRootElement() {
        return defaultRoot;
    }

    @Override
    public Element getParagraphElement(int pos) {
        return null;
    }

    public class RootElement extends BranchElement {
        public RootElement() {
            super(null, null);
        }

        @Override
        public String getName() {
            return "root";
        }
    }

    public class MemberBranchElement extends BranchElement {
        public MemberBranchElement(AttributeSet set) {
            super(defaultRoot, set);
        }

        @Override
        public String getName() {
            return "member";
        }
    }

    public class KeyBranchElement extends BranchElement {
        public KeyBranchElement(MemberBranchElement parent, AttributeSet a) {
            super(parent, a);
        }

        @Override
        public String getName() {
            return "key";
        }
    }

    public class ValueBranchElement extends BranchElement {
        public ValueBranchElement(MemberBranchElement parent, AttributeSet a) {
            super(parent, a);
        }

        @Override
        public String getName() {
            return "value";
        }
    }
}
