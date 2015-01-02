package org.apache.pojo.beaneditor;

import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;

public class PojoBeanView extends BoxView {

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

}