package org.apache.pojo.beaneditor.views;

import javax.swing.text.Element;

import org.apache.pojo.beaneditor.model.PBEDocument;

public class PojoMemberKeyView extends AbstractPojoBeanEditorView {
    private final short leftInset;

    public PojoMemberKeyView(Element elem) {
        super(elem, X_AXIS);
        leftInset = (short) (((int) getElement().getAttributes().getAttribute(PBEDocument.ATTRIB_STEP_NO)) * 10);
    }
    @Override
    protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
        super.layoutMinorAxis(targetSpan, axis, offsets, spans);
        offsets[0] = 0;
    }

    @Override
    protected int getViewIndexAtPosition(int pos) {
        return 0;
    }

    @Override
    protected short getLeftInset() {
        return leftInset;
    }
}
