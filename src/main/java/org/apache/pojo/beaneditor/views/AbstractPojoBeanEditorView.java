package org.apache.pojo.beaneditor.views;

import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;

public class AbstractPojoBeanEditorView extends BoxView {
    public AbstractPojoBeanEditorView(Element elem, int axis) {
        super(elem, axis);
    }

    @Override
    public int getNextVisualPositionFrom(int pos, Bias b, Shape a, int direction, Bias[] biasRet)
            throws BadLocationException {
        return super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);
    }

    @Override
    public float getAlignment(int axis) {
        return 0.0f;
    }
}
