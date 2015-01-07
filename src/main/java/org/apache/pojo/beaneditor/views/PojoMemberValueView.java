package org.apache.pojo.beaneditor.views;

import java.awt.Shape;

import javax.swing.event.DocumentEvent;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;
import javax.swing.text.ViewFactory;

public class PojoMemberValueView extends AbstractPojoBeanEditorView {
    public PojoMemberValueView(Element elem) {
        super(elem, X_AXIS);
    }

    @Override
    protected short getLeftInset() {
        return 10;
    }

    @Override
    protected int getViewIndexAtPosition(int pos) {
        return 0;
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

    @Override
    public int viewToModel(float x, float y, Shape a, Bias[] bias) {
        a = getChildAllocation(0, a);
        return getView(0).viewToModel(x, y, a, bias);
    }
}
