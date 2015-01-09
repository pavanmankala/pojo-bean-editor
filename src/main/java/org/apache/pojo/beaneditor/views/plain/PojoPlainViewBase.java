package org.apache.pojo.beaneditor.views.plain;

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

import org.apache.pojo.beaneditor.model.PBEDocument;
import org.apache.pojo.beaneditor.model.outline.PBEONode;

public class PojoPlainViewBase extends PlainView {
    protected final Segment tempSeg = new Segment();
    protected final PBEONode node;

    public PojoPlainViewBase(Element elem) {
        super(elem);
        node = (PBEONode) elem.getAttributes().getAttribute(PBEDocument.ATTRIB_NODE_OBJ);
    }

    @Override
    public final int viewToModel(float fx, float fy, Shape a, Bias[] bias) {
        bias[0] = Position.Bias.Forward;

        Rectangle alloc = a.getBounds();
        Document doc = getDocument();
        int x = (int) fx;
        int y = (int) fy;
        if (y < alloc.y) {
            return getStartOffset();
        } else if (y > alloc.y + alloc.height) {
            return getEndOffset() - 1;
        } else {
            Element map = getElement();
            int fontHeight = metrics.getHeight();
            int lineIndex = (fontHeight > 0 ? Math.abs((y - alloc.y) / fontHeight) : map.getElementCount() - 1);
            if (lineIndex >= map.getElementCount()) {
                return getEndOffset() - 1;
            }
            Element line = map.getElement(lineIndex);
            if (x < alloc.x) {
                return line.getStartOffset();
            } else if (x > alloc.x + alloc.width) {
                return line.getEndOffset() - 1;
            } else {
                try {
                    int p0 = line.getStartOffset();
                    int p1 = line.getEndOffset() - 1;
                    doc.getText(p0, p1 - p0, tempSeg);
                    // tabBase = alloc.x;
                    int offs = p0 + Utilities.getTabbedTextOffset(tempSeg, metrics, alloc.x, x, this, p0);
                    return offs;
                } catch (BadLocationException e) {
                    // should not happen
                    return -1;
                }
            }
        }
    }
}
