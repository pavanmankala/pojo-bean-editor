package org.apache.pojo.beaneditor.views;

import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

public class PojoBeanView extends AbstractPojoBeanEditorView {

    public PojoBeanView(Element elem) {
        super(elem, Y_AXIS);
    }

    @Override
    protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
        int n = getViewCount();
        for (int i = 0; i < n; i++) {
            View v = getView(i);
            spans[i] = (int) v.getPreferredSpan(axis);
        }

        // make the adjustments
        int totalOffset = 0;
        for (int i = 0; i < n; i++) {
            offsets[i] = totalOffset;
            totalOffset = (int) Math.min((long) totalOffset + (long) spans[i], Integer.MAX_VALUE);
        }
    }

    @Override
    protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
        int n = getViewCount();
        for (int i = 0; i < n; i++) {
            View v = getView(i);
            int min = (int) v.getMinimumSpan(axis);
            offsets[i] = 0;
            spans[i] = Math.max(min, targetSpan);
        }
    }

    @Override
    public Shape modelToView(int pos, Shape a, Bias b) throws BadLocationException {
        boolean isBackward = (b == Position.Bias.Backward);
        int testPos = (isBackward) ? Math.max(0, pos - 1) : pos;
        if (isBackward && testPos < getStartOffset()) {
            return null;
        }
        int vIndex = getViewIndexAtPosition(testPos);
        if ((vIndex != -1) && (vIndex < getViewCount())) {
            View v = getView(vIndex);
            if (v != null
                    && ((testPos >= v.getStartOffset() && testPos < v.getEndOffset()) || testPos == v.getEndOffset())) {
                pos = testPos == v.getEndOffset() ? pos - 1 : pos;
                Shape childShape = getChildAllocation(vIndex, a);
                if (childShape == null) {
                    // We are likely invalid, fail.
                    return null;
                }
                Shape retShape = v.modelToView(pos, childShape, b);
                if (retShape == null && v.getEndOffset() == pos) {
                    if (++vIndex < getViewCount()) {
                        v = getView(vIndex);
                        retShape = v.modelToView(pos, getChildAllocation(vIndex, a), b);
                    }
                }
                return retShape;
            }
        }

        return null;
    }

    @Override
    public int getNextVisualPositionFrom(int pos, Bias b, Shape a, int direction, Bias[] biasRet)
            throws BadLocationException {
        try {
            return super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);
        } catch (Exception e) {
            return 0;
        }
    }
}