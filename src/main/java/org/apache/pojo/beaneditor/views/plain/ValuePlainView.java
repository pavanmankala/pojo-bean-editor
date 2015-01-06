package org.apache.pojo.beaneditor.views.plain;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

public class ValuePlainView extends PlainView {
    Segment seg = new Segment();

    public ValuePlainView(Element elem) {
        super(elem);
    }

    @Override
    public void paint(Graphics g, Shape a) {
        super.paint(g, a);
    }

    @Override
    public int viewToModel(float fx, float fy, Shape a, Bias[] bias) {
        // PENDING(prinz) properly calculate bias
        bias[0] = Position.Bias.Forward;

        Rectangle alloc = a.getBounds();
        Document doc = getDocument();
        int x = (int) fx;
        int y = (int) fy;
        if (y < alloc.y) {
            // above the area covered by this icon, so the the position
            // is assumed to be the start of the coverage for this view.
            return getStartOffset();
        } else if (y > alloc.y + alloc.height) {
            // below the area covered by this icon, so the the position
            // is assumed to be the end of the coverage for this view.
            return getEndOffset() - 1;
        } else {
            // positioned within the coverage of this view vertically,
            // so we figure out which line the point corresponds to.
            // if the line is greater than the number of lines contained,
            // then
            // simply use the last line as it represents the last possible
            // place
            // we can position to.
            Element map = getElement();
            int fontHeight = metrics.getHeight();
            int lineIndex = (fontHeight > 0 ? Math.abs((y - alloc.y) / fontHeight) : map.getElementCount() - 1);
            if (lineIndex >= map.getElementCount()) {
                return getEndOffset() - 1;
            }
            Element line = map.getElement(lineIndex);
            int dx = 0;
            if (lineIndex == 0) {
                // alloc.x += firstLineOffset;
                // alloc.width -= firstLineOffset;
            }
            if (x < alloc.x) {
                // point is to the left of the line
                return line.getStartOffset();
            } else if (x > alloc.x + alloc.width) {
                // point is to the right of the line
                return line.getEndOffset() - 1;
            } else {
                // Determine the offset into the text
                try {
                    int p0 = line.getStartOffset();
                    int p1 = line.getEndOffset();
                    doc.getText(p0, p1 - p0, seg);
                    // tabBase = alloc.x;
                    int offs = p0 + Utilities.getTabbedTextOffset(seg, metrics, alloc.x, x, this, p0);
                    return offs;
                } catch (BadLocationException e) {
                    // should not happen
                    return -1;
                }
            }
        }
    }
}