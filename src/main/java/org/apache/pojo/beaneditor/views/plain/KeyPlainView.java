package org.apache.pojo.beaneditor.views.plain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;
import javax.swing.text.Utilities;

public class KeyPlainView extends PojoPlainViewBase {
    private static final Color keyColor = new Color(186, 145, 145);
    private static final Color keyLeafColor = new Color(120, 0, 85), keyBackground = new Color(250, 255, 250),
            leafKeyBackground = new Color(245, 255, 245);
    private static final Map<Font, Font> boldFontMap = new HashMap<Font, Font>();

    public KeyPlainView(Element elem) {
        super(elem);
    }

    @Override
    public void paint(Graphics g, Shape a) {
        Graphics2D g2d = (Graphics2D) g;
        Color resetColor = g.getColor();

        g2d.setColor(node.isLeaf() ? leafKeyBackground : keyBackground);
        g2d.fill(a);
        // g2d.setColor(g2d.getColor().darker());
        // Rectangle r = (Rectangle) a;
        // g2d.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height);
        g2d.setColor(resetColor);

        super.paint(g, a);
    }

    protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
        Font boldFont, origFont;

        if ((boldFont = boldFontMap.get((origFont = g.getFont()))) == null) {
            boldFont = origFont.deriveFont(Font.BOLD);
            boldFontMap.put(origFont, boldFont);
        }

        g.setFont(boldFont);
        g.setColor(node.isLeaf() ? keyLeafColor : keyColor);
        Document doc = getDocument();
        doc.getText(p0, p1 - p0, tempSeg);
        int ret = Utilities.drawTabbedText(tempSeg, x, y, g, this, p0);
        return ret;
    }

    protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
        return drawUnselectedText(g, x, y, p0, p1);
    }

    @Override
    public Shape modelToView(int pos, Shape a, Bias b) throws BadLocationException {
        return super.modelToView(pos, a, b);
    }
}