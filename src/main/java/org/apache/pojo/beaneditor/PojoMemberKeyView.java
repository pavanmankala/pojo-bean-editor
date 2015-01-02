package org.apache.pojo.beaneditor;

import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

public class PojoMemberKeyView extends View {
    public PojoMemberKeyView(Element elem) {
        super(elem);
    }

    @Override
    public float getPreferredSpan(int axis) {
        return 0;
    }

    @Override
    public void paint(Graphics g, Shape allocation) {
    }

    @Override
    public Shape modelToView(int pos, Shape a, Bias b) throws BadLocationException {
        return null;
    }

    @Override
    public int viewToModel(float x, float y, Shape a, Bias[] biasReturn) {
        return 0;
    }
}
