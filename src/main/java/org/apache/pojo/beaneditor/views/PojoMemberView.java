package org.apache.pojo.beaneditor.views;

import javax.swing.text.Element;

public class PojoMemberView extends AbstractPojoBeanEditorView {
    public PojoMemberView(Element elem) {
        super(elem, X_AXIS);
    }

    @Override
    public float getAlignment(int axis) {
        return 0.0f;
    }

    @Override
    protected short getTopInset() {
        return 3;
    }

    public boolean isFolded() {
        return ((PojoMemberKeyView) getView(0)).isFolded();
    }
}