package org.apache.pojo.beaneditor;

import java.awt.Shape;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class PojoMemberValueView extends BoxView {
    public PojoMemberValueView(Element elem) {
        super(elem, X_AXIS);
    }
    @Override
    public float getAlignment(int axis) {
        return 0.0f;
    }
    @Override
    protected short getLeftInset() {
        return 15;
    }

    @Override
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        getView(0).changedUpdate(e, a, f);
    }

    @Override
    public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        getView(0).insertUpdate(e, a, f);
    }

    @Override
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        getView(0).removeUpdate(e, a, f);
    }
}
