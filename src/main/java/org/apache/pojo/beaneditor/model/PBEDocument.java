package org.apache.pojo.beaneditor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleContext;

import org.apache.pojo.beaneditor.BeanValueTransformer;
import org.apache.pojo.beaneditor.model.outline.PBEOAggregatedNode;
import org.apache.pojo.beaneditor.model.outline.PBEONode;
import org.apache.pojo.beaneditor.model.outline.Visitable.PBEOVisitor;

public class PBEDocument extends AbstractDocument {
    public static final String KEY_ELEM = "key", VALUE_ELEM = "value", MEMBER_ELEM = "member", ROOT_ELEM = "root";
    public static final String ATTRIB_STEP_NO = "PBE_STEP", ATTRIB_NODE_OBJ = "PBE_NODE_OBJ";

    private final PBEOAggregatedNode aggregatedNode;
    private final BranchElement defaultRoot;
    private BranchElement editingKeyOrValElem;
    private Vector<Element> added = new Vector<Element>();
    private Vector<Element> removed = new Vector<Element>();
    private Segment s = new Segment();
    private BeanValueTransformer valueTransformer;

    public PBEDocument(BeanValueTransformer bvt, PBEOAggregatedNode aggNode) {
        super(new GapContent());
        aggregatedNode = aggNode;
        defaultRoot = new RootElement();
        this.valueTransformer = bvt;
        initContent();
    }

    private void initContent() {
        writeLock();

        try {
            ElementGenerator generator = new ElementGenerator();
            aggregatedNode.visit(new ContentFiller(getContent(), valueTransformer), 0);
            aggregatedNode.visit(generator, 0);

            ((BranchElement) getDefaultRootElement()).replace(0, 0, generator.getBranches());
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

        keyElem.replace(0, 1, keys);

        // if the edit is LowerBoundaryEdit
        if (chng.getOffset() == keyElem.getEndOffset()) {
            Element newLeaf0 = createLeafElement(valueElem, null, keyElem.getStartOffset()
                    + node.getNodeName().length(), valueElem.getElement(0).getEndOffset());
            valueElem.replace(0, 1, new Element[] { newLeaf0 });
        }

        insertUpdatePlain(valueElem, chng, attr);
    }

    private void insertUpdatePlain(ValueBranchElement base, DefaultDocumentEvent chng, AttributeSet attr) {
        removed.removeAllElements();
        added.removeAllElements();
        BranchElement lineMap = base;
        int offset = chng.getOffset();
        int length = chng.getLength();
        if (offset > 0) {
            offset -= 1;
            length += 1;
        }
        int index = lineMap.getElementIndex(offset);
        Element rmCandidate = lineMap.getElement(index);
        int rmOffs0 = rmCandidate.getStartOffset();
        int rmOffs1 = rmCandidate.getEndOffset();
        int lastOffset = rmOffs0;
        try {
            getContent().getChars(offset, length, s);
            boolean hasBreaks = false;
            for (int i = 0; i < length; i++) {
                char c = s.array[s.offset + i];
                if (c == '\n') {
                    int breakOffset = offset + i + 1;
                    added.addElement(createLeafElement(lineMap, null, lastOffset, breakOffset));
                    lastOffset = breakOffset;
                    hasBreaks = true;
                }
            }
            if (hasBreaks) {
                removed.addElement(rmCandidate);
                if ((offset + length == rmOffs1) && (lastOffset != rmOffs1)
                        && ((index + 1) < lineMap.getElementCount())) {
                    Element e = lineMap.getElement(index + 1);
                    removed.addElement(e);
                    rmOffs1 = e.getEndOffset();
                }
                if (lastOffset < rmOffs1) {
                    added.addElement(createLeafElement(lineMap, null, lastOffset, rmOffs1));
                }

                Element[] aelems = new Element[added.size()];
                added.copyInto(aelems);
                Element[] relems = new Element[removed.size()];
                removed.copyInto(relems);
                ElementEdit ee = new ElementEdit(lineMap, index, relems, aelems);
                chng.addEdit(ee);
                lineMap.replace(index, relems.length, aelems);
            }
        } catch (BadLocationException e) {
            throw new Error("Internal error: " + e.toString());
        }
    }

    protected void removeUpdate(DefaultDocumentEvent chng) {
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

        removed.removeAllElements();
        BranchElement map = valueElem;
        int offset = chng.getOffset();
        int length = chng.getLength();
        int line0 = map.getElementIndex(offset);
        int line1 = map.getElementIndex(offset + length);
        if (line0 != line1) {
            // a line was removed
            for (int i = line0; i <= line1; i++) {
                removed.addElement(map.getElement(i));
            }
            int p0 = map.getElement(line0).getStartOffset();
            int p1 = map.getElement(line1).getEndOffset();
            Element[] aelems = new Element[1];
            aelems[0] = createLeafElement(map, null, p0, p1);
            Element[] relems = new Element[removed.size()];
            removed.copyInto(relems);
            ElementEdit ee = new ElementEdit(map, line0, relems, aelems);
            chng.addEdit(ee);
            map.replace(line0, relems.length, aelems);
        }

        super.removeUpdate(chng);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (offs == 0) {
            return;
        }

        editingKeyOrValElem = getElementAt(offs);
        String name = editingKeyOrValElem.getName();

        if (name == KEY_ELEM) {
            if (offs > editingKeyOrValElem.getStartOffset() && offs < editingKeyOrValElem.getEndOffset()) {
                // Key is being edited -> Not allowed
                return;
            }

            // Check if the element before the editing element is not a leaf
            // node
            MemberBranchElement memberElem = (MemberBranchElement) editingKeyOrValElem.getParentElement();
            int beforeMembElemIndex = defaultRoot.getElementIndex(memberElem.getStartOffset() - 1);
            if (beforeMembElemIndex != -1) {
                MemberBranchElement beforeMember = (MemberBranchElement) defaultRoot.getElement(beforeMembElemIndex);
                if (beforeMember != null) {
                    PBEONode node = (PBEONode) beforeMember.getElement(0).getAttributes().getAttribute(ATTRIB_NODE_OBJ);
                    if (!node.isLeaf()) {
                        return;
                    } else {
                        // adjust for editing element
                        editingKeyOrValElem = (BranchElement) beforeMember.getElement(1);
                    }
                }
            }
        }

        super.insertString(offs, str, a);
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        if (offs == 0) {
            return;
        }

        editingKeyOrValElem = getElementAt(offs);
        String name = editingKeyOrValElem.getName();

        if (name == KEY_ELEM) {
            return;
        }

        if (name == VALUE_ELEM) {
            BranchElement endElem = getElementAt(offs + len);

            if (endElem != editingKeyOrValElem) {
                return;
            }
        }

        super.remove(offs, len);
    }

    BranchElement getElementAt(int offs) {
        BranchElement elem = defaultRoot;
        String name = elem.getName();

        // find the currently editing key or value element
        while (name != KEY_ELEM && name != VALUE_ELEM) {
            elem = (BranchElement) elem.getElement(elem.getElementIndex(offs));
            name = elem.getName();
        }

        return elem;
    }

    @Override
    public Element getDefaultRootElement() {
        return defaultRoot;
    }

    @Override
    public Element getParagraphElement(int pos) {
        Element lineMap = getDefaultRootElement();
        return lineMap.getElement(lineMap.getElementIndex(pos));
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

    static abstract class OffsetMaintainedVisitor implements PBEOVisitor {
        private int offset;

        public int getOffset() {
            return offset;
        }

        protected void addToOffset(int val) {
            offset += val;
        }
    }

    static class ContentFiller extends OffsetMaintainedVisitor {
        private final Content docContent;
        private final BeanValueTransformer transformer;

        public ContentFiller(Content content, BeanValueTransformer valueTransformer) {
            docContent = content;
            transformer = valueTransformer;
        }

        @Override
        public void node(PBEONode node, int step) {
            Object nodeValue;

            try {
                nodeValue = node.getNodeValue();
            } catch (RuntimeException e) {
                nodeValue = null;
            }

            String nodeName = node.getNodeName(), nodeValStr = nodeValue == null ? " " : transformer
                    .transform(nodeValue);

            try {
                docContent.insertString(getOffset(), nodeName);
                addToOffset(nodeName.length());

                if (node.isLeaf()) {
                    docContent.insertString(getOffset(), nodeValStr);
                    addToOffset(nodeValStr.length());
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    class ElementGenerator extends OffsetMaintainedVisitor {
        private final List<BranchElement> branches = new ArrayList<BranchElement>();

        @Override
        public void node(PBEONode node, int step) {
            Object nodeValue;

            try {
                nodeValue = node.getNodeValue();
            } catch (RuntimeException e) {
                nodeValue = null;
            }

            String nodeName = node.getNodeName(), nodeValStr = nodeValue == null ? " " : valueTransformer
                    .transform(nodeValue);

            // prepare member branch element
            MemberBranchElement mbe = new MemberBranchElement(null);
            branches.add(mbe);
            mbe.addAttribute(ATTRIB_STEP_NO, step);
            mbe.addAttribute(ATTRIB_NODE_OBJ, node);

            // prepare attribute set for keyBranch and valueBranch
            // element
            StyleContext ctx = StyleContext.getDefaultStyleContext();
            AttributeSet set = ctx.addAttribute(SimpleAttributeSet.EMPTY, ATTRIB_NODE_OBJ, node);
            set = ctx.addAttribute(set, ATTRIB_STEP_NO, step);

            // init key and value branch elements
            BranchElement keyBranch = new KeyBranchElement(mbe, set), valueBranch = new ValueBranchElement(mbe, set);
            Element[] keyValueElems = new Element[node.isLeaf() ? 2 : 1];

            Element[] keylines = new Element[1];
            keylines[0] = createLeafElement(keyBranch, null, getOffset(), getOffset() + nodeName.length());
            keyBranch.replace(0, 0, keylines);
            addToOffset(nodeName.length());
            keyValueElems[0] = keyBranch;

            if (node.isLeaf()) {
                Element[] valuelines = new Element[1];
                valuelines[0] = createLeafElement(valueBranch, null, getOffset(), getOffset() + nodeValStr.length());
                valueBranch.replace(0, 0, valuelines);
                addToOffset(nodeValStr.length());
                keyValueElems[1] = valueBranch;
            }

            mbe.replace(0, 0, keyValueElems);
        }

        public Element[] getBranches() {
            return branches.toArray(new Element[0]);
        }
    }
}
