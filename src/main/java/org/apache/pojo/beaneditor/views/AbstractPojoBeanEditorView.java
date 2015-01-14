package org.apache.pojo.beaneditor.views;

import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;

public class AbstractPojoBeanEditorView extends BoxView {
    public AbstractPojoBeanEditorView(Element elem, int axis) {
        super(elem, axis);
    }

    @Override
    public float getAlignment(int axis) {
        return 0.0f;
    }

    @Override
    public void setParent(View parent) {
        super.setParent(parent);
        if (parent != null) {
            for (int i = 0; i < getViewCount(); i++) {
                getView(i).setParent(this);
            }
        }
    }
}
