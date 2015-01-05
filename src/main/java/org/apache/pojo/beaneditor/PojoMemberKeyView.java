package org.apache.pojo.beaneditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.text.View;

public class PojoMemberKeyView extends BoxView {
    public PojoMemberKeyView(Element elem) {
        super(elem, X_AXIS);
    }

    @Override
    protected short getLeftInset() {
        return (short) (((int) getElement().getAttributes().getAttribute(PBEDocument.ATTRIB_STEP_NO)) * 10);
    }

    @Override
    public float getAlignment(int axis) {
        return 0.0f;
    }

    public static class KeyView extends PlainView {
        private static Color keyColor = new Color(127, 0, 85);
        Segment seg = new Segment();

        public KeyView(Element elem) {
            super(elem);
        }

        @Override
        public void paint(Graphics g, Shape a) {
            super.paint(g, a);
        }

        protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
            g.setFont(g.getFont().deriveFont(Font.BOLD));
            g.setColor(keyColor);
            Document doc = getDocument();
            doc.getText(p0, p1 - p0, seg);
            int ret = Utilities.drawTabbedText(seg, x, y, g, this, p0);
            return ret;
        }

        protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
            g.setFont(g.getFont().deriveFont(Font.BOLD));
            g.setColor(keyColor);
            Document doc = getDocument();
            doc.getText(p0, p1 - p0, seg);
            int ret = Utilities.drawTabbedText(seg, x, y, g, this, p0);
            return ret;
        }
    }
}
