package org.apache.pojo.beaneditor;

import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;

public class PojoMemberView extends BoxView {
    public PojoMemberView(Element elem) {
        super(elem, X_AXIS);
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
}