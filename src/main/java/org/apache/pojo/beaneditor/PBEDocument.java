package org.apache.pojo.beaneditor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleContext;

import org.apache.pojo.beaneditor.model.outline.PBEOAggregatedNode;
import org.apache.pojo.beaneditor.model.outline.PBEONode;
import org.apache.pojo.beaneditor.model.outline.Visitable.PBEOVisitor;

public class PBEDocument extends AbstractDocument {
    public static final String KEY_ELEM = "key", VALUE_ELEM = "value", MEMBER_ELEM = "member", ROOT_ELEM = "root";
    public static final String ATTRIB_STEP_NO = "PBE_STEP", ATTRIB_NODE_OBJ = "PBE_NODE_OBJ";

    private final PBEOAggregatedNode aggregatedNode;
    private final BranchElement defaultRoot;
    private BranchElement editingKeyOrValElem;

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
                    mbe.addAttribute(ATTRIB_STEP_NO, step);

                    StyleContext ctx = StyleContext.getDefaultStyleContext();
                    AttributeSet set = ctx.addAttribute(SimpleAttributeSet.EMPTY, ATTRIB_NODE_OBJ, node);
                    set = ctx.addAttribute(set, ATTRIB_STEP_NO, step);

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
        BranchElement prevMemberElem = null, currMemberElem = (BranchElement) editingKeyOrValElem.getParentElement(), nextMemberElem = null;
        BranchElement membParent = (BranchElement) currMemberElem.getParentElement();
        int memberIndex;

        for (memberIndex = 0; memberIndex < membParent.getChildCount(); memberIndex++) {
            if (membParent.getElement(memberIndex) == currMemberElem) {
                break;
            }
        }

        if (memberIndex > 0) {
            prevMemberElem = (BranchElement) membParent.getElement(memberIndex - 1);
        }

        if (memberIndex < membParent.getChildCount() - 1) {
            nextMemberElem = (BranchElement) membParent.getElement(memberIndex + 1);
        }

        KeyBranchElement keyElem = (KeyBranchElement) currMemberElem.getElement(0);
        ValueBranchElement valueElem = (ValueBranchElement) currMemberElem.getElement(1);

        PBEONode node = (PBEONode) keyElem.getAttribute(ATTRIB_NODE_OBJ);

        Element[] keys = new Element[1];
        keys[0] = createLeafElement(keyElem, null, keyElem.getStartOffset(), keyElem.getStartOffset()
                + node.getNodeName().length());

        Element[] values = new Element[1];
        values[0] = createLeafElement(valueElem, null, keyElem.getStartOffset() + node.getNodeName().length(),
                valueElem.getEndOffset());

        keyElem.replace(0, 1, keys);
        valueElem.replace(0, 1, values);

        super.insertUpdate(chng, attr);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (offs == 0) {
            return;
        }

        editingKeyOrValElem = defaultRoot;
        String name = editingKeyOrValElem.getName();

        // find the currently editing key or value element
        while (name != KEY_ELEM && name != VALUE_ELEM) {
            editingKeyOrValElem = (BranchElement) editingKeyOrValElem.getElement(editingKeyOrValElem
                    .getElementIndex(offs));
            name = editingKeyOrValElem.getName();
        }

        if (name == KEY_ELEM) {
            KeyBranchElement keyElem = (KeyBranchElement) editingKeyOrValElem;

            if (offs > keyElem.getStartOffset() && offs < keyElem.getEndOffset()) {
                // Key is being edited -> Not allowed
                return;
            }
        }

        super.insertString(offs, str, a);
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
            return ROOT_ELEM;
        }
    }

    public class MemberBranchElement extends BranchElement {
        public MemberBranchElement(AttributeSet set) {
            super(defaultRoot, set);
        }

        @Override
        public String getName() {
            return MEMBER_ELEM;
        }
    }

    public class KeyBranchElement extends BranchElement {
        public KeyBranchElement(MemberBranchElement parent, AttributeSet a) {
            super(parent, a);
        }

        @Override
        public String getName() {
            return KEY_ELEM;
        }
    }

    public class ValueBranchElement extends BranchElement {
        public ValueBranchElement(MemberBranchElement parent, AttributeSet a) {
            super(parent, a);
        }

        @Override
        public String getName() {
            return VALUE_ELEM;
        }
    }
}
