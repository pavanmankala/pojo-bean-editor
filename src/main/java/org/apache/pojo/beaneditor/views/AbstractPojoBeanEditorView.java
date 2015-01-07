package org.apache.pojo.beaneditor.views;

import javax.swing.text.BoxView;
import javax.swing.text.Element;

public class AbstractPojoBeanEditorView extends BoxView {
    public AbstractPojoBeanEditorView(Element elem, int axis) {
        super(elem, axis);
    }

    @Override
    public float getAlignment(int axis) {
        return 0.0f;
    }
}
