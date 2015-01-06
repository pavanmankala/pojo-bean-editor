package org.apache.pojo.beaneditor.views;

import java.awt.Shape;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ViewFactory;
import javax.swing.text.Position.Bias;

public class PojoMemberValueView extends AbstractPojoBeanEditorView {
    public PojoMemberValueView(Element elem) {
        super(elem, X_AXIS);
    }

    @Override
    protected short getLeftInset() {
        return 15;
    }

    @Override
    protected int getViewIndexAtPosition(int pos) {
        return 0;
    }

    @Override
    public int getNextVisualPositionFrom(int pos, Bias b, Shape a, int direction, Bias[] biasRet)
            throws BadLocationException {
        return super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);
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
