package org.apache.pojo.beaneditor.views.plain;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.plaf.basic.BasicTextUI.BasicHighlighter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

import org.apache.pojo.beaneditor.PojoBeanEditor;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;
import org.fife.ui.rsyntaxtextarea.modes.JavaTokenMaker;

public class ValuePlainView extends PlainView {
    Segment seg = new Segment();
    JavaTokenMaker tm = new JavaTokenMaker();
    PBETokenPainter tp = new PBETokenPainter();
    private int tabSize;
    private int tabBase;
    private int lineHeight = 0;
    private int ascent;
    private int clipStart;
    private int clipEnd;
    PojoBeanEditor host;
    private TokenImpl tempToken = new TokenImpl();
    Font font;

    public ValuePlainView(Element elem) {
        super(elem);
    }

    @Override
    public void paint(Graphics g, Shape a) {
        Graphics2D g2d = (Graphics2D) g;
        g.setFont(g.getFont().deriveFont(Font.PLAIN));
        Rectangle alloc = a.getBounds();

        tabBase = alloc.x;
        host = (PojoBeanEditor) getContainer();

        Rectangle clip = g.getClipBounds();
        // An attempt to speed things up for files with long lines. Note that
        // this will actually slow things down a bit for the common case of
        // regular-length lines, but it doesn't make a perceivable difference.
        clipStart = clip.x;
        clipEnd = clipStart + clip.width;

        lineHeight = g.getFontMetrics().getHeight();
        ascent = g.getFontMetrics().getAscent();// metrics.getAscent();
        int heightAbove = clip.y - alloc.y;
        int linesAbove = Math.max(0, heightAbove / lineHeight);

        Rectangle lineArea = lineToRect(a, linesAbove);
        int y = lineArea.y + ascent;
        int x = lineArea.x;
        Element map = getElement();
        int lineCount = map.getElementCount();

        // Whether token styles should always be painted, even in selections
        int selStart = host.getSelectionStart();
        int selEnd = host.getSelectionEnd();

        BasicHighlighter h = (BasicHighlighter) host.getHighlighter();

        Token token;
        // System.err.println("Painting lines: " + linesAbove + " to " +
        // (endLine-1));

        PBETokenPainter painter = tp;
        int line = linesAbove;
        // int count = 0;
        while (y < clip.y + clip.height + ascent && line < lineCount) {

            Element lineElement = map.getElement(line);
            int startOffset = lineElement.getStartOffset();
            // int endOffset = (line==lineCount ? lineElement.getEndOffset()-1 :
            // lineElement.getEndOffset()-1);
            int endOffset = lineElement.getEndOffset() - 1; // Why always "-1"?
            h.paintLayeredHighlights(g2d, startOffset, endOffset, a, host, this);

            // Paint a line of text.
            try {
                getDocument().getText(startOffset, endOffset - startOffset, seg);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            token = tm.getTokenList(seg, Token.NULL, 0);
            if (selStart == selEnd || (startOffset >= selEnd || endOffset < selStart)) {
                drawLine(token, g2d, x, y);
            } else {
                // System.out.println("Drawing line with selection: " + line);
                drawLineWithSelection(token, g2d, x, y, selStart, selEnd);
            }

            y += lineHeight;
            line++;
            // count++;

        }
        // System.out.println("SyntaxView: lines painted=" + count);
    }

    private float drawLine(Token token, Graphics2D g, float x, float y) {
        float nextX = x; // The x-value at the end of our text.

        while (token != null && token.isPaintable() && nextX < clipEnd) {
            nextX = tp.paint(token, g, nextX, y, this, clipStart);
            token = token.getNextToken();
        }

        // Return the x-coordinate at the end of the painted text.
        return nextX;

    }

    private float drawLineWithSelection(Token token, Graphics2D g, float x, float y, int selStart, int selEnd) {

        float nextX = x; // The x-value at the end of our text.

        while (token != null && token.isPaintable() && nextX < clipEnd) {

            // Selection starts in this token
            if (token.containsPosition(selStart)) {

                if (selStart > token.getOffset()) {
                    tempToken.copyFrom(token);
                    tempToken.textCount = selStart - tempToken.getOffset();
                    nextX = tp.paint(tempToken, g, nextX, y, this, clipStart);
                    tempToken.textCount = token.length();
                    tempToken.makeStartAt(selStart);
                    // Clone required since token and tempToken must be
                    // different tokens for else statement below
                    token = new TokenImpl(tempToken);
                }

                int tokenLen = token.length();
                int selCount = Math.min(tokenLen, selEnd - token.getOffset());
                if (selCount == tokenLen) {
                    nextX = tp.paintSelected(token, g, nextX, y, this, clipStart);
                } else {
                    tempToken.copyFrom(token);
                    tempToken.textCount = selCount;
                    nextX = tp.paintSelected(tempToken, g, nextX, y, this, clipStart);
                    tempToken.textCount = token.length();
                    tempToken.makeStartAt(token.getOffset() + selCount);
                    token = tempToken;
                    nextX = tp.paint(token, g, nextX, y, this, clipStart);
                }

            }

            // Selection ends in this token
            else if (token.containsPosition(selEnd)) {
                tempToken.copyFrom(token);
                tempToken.textCount = selEnd - tempToken.getOffset();
                nextX = tp.paintSelected(tempToken, g, nextX, y, this, clipStart);
                tempToken.textCount = token.length();
                tempToken.makeStartAt(selEnd);
                token = tempToken;
                nextX = tp.paint(token, g, nextX, y, this, clipStart);
            }

            // This token is entirely selected
            else if (token.getOffset() >= selStart && token.getEndOffset() <= selEnd) {
                nextX = tp.paintSelected(token, g, nextX, y, this, clipStart);
            }

            // This token is entirely unselected
            else {
                nextX = tp.paint(token, g, nextX, y, this, clipStart);
            }

            token = token.getNextToken();

        }

        // Return the x-coordinate at the end of the painted text.
        return nextX;

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