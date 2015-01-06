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
        return super.modelToView(pos, a, b);
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

    @Override
    public int getViewIndex(int pos, Position.Bias b) {
        if(b == Position.Bias.Backward) {
            pos -= 1;
        }
        if ((pos >= getStartOffset()) && (pos <= getEndOffset())) {
            return getViewIndexAtPosition(Math.min(pos, getEndOffset() - 1));
        }
        return -1;
    }
}