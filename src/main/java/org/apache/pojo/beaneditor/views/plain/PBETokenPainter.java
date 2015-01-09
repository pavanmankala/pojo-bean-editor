package org.apache.pojo.beaneditor.views.plain;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.text.TabExpander;

import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;

class PBETokenPainter {
    private Rectangle2D.Float bgRect;
    private SyntaxScheme scheme = new SyntaxScheme(true);

    public PBETokenPainter() {
        bgRect = new Rectangle2D.Float();
    }

    public final float paint(Token token, Graphics2D g, float x, float y, TabExpander e) {
        return paint(token, g, x, y, e, 0);
    }

    public float paint(Token token, Graphics2D g, float x, float y, TabExpander e, float clipStart) {
        return paintImpl(token, g, x, y, e, clipStart, false);
    }

    protected void paintBackground(float x, float y, float width, float height, Graphics2D g, int fontAscent,
            Color color, boolean xor) {
        // RSyntaxTextArea's bg can be null, so we must check for this.
        Color temp = Color.white;// host.getBackground();
        if (xor) { // XOR painting is pretty slow on Windows
            g.setXORMode(temp != null ? temp : Color.WHITE);
        }
        g.setColor(color);
        bgRect.setRect(x, y - fontAscent, width, height);
        // g.fill(bgRect);
        g.fillRect((int) x, (int) (y - fontAscent), (int) width, (int) height);
        if (xor) {
            g.setPaintMode();
        }
    }

    public Color getForegroundForTokenType(Token token) {
        Color fg = scheme.getStyle(token.getType()).foreground;
        return fg != null ? fg : Color.black;
    }

    public Color getBackgroundForTokenType(Token token) {
        Color fg = scheme.getStyle(token.getType()).background;
        return fg != null ? fg : Color.white;
    }

    public Font getFontForTokenType(Token token) {
        return scheme.getStyle(token.getType()).font;
    }

    protected float paintImpl(Token token, Graphics2D g, float x, float y, TabExpander e, float clipStart,
            boolean selected) {
        int origX = (int) x;
        int textOffs = token.getTextOffset();
        char[] text = token.getTextArray();
        int end = textOffs + token.length();
        float nextX = x;
        int flushLen = 0;
        int flushIndex = textOffs;
        Color fg, bg;
        if (selected) {
            fg = Color.black;
            bg = Color.gray;
        } else {
            fg = getForegroundForTokenType(token);
            bg = null;
        }
        FontMetrics fm = g.getFontMetrics();

        for (int i = textOffs; i < end; i++) {
            switch (text[i]) {
                case '\t':
                    nextX = e.nextTabStop(x + fm.charsWidth(text, flushIndex, flushLen), 0);
                    if (bg != null) {
                        paintBackground(x, y, nextX - x, fm.getHeight(), g, fm.getAscent(), bg, !selected);
                    }
                    if (flushLen > 0) {
                        g.setColor(fg);
                        g.drawChars(text, flushIndex, flushLen, (int) x, (int) y);
                        flushLen = 0;
                    }
                    flushIndex = i + 1;
                    x = nextX;
                    break;
                default:
                    flushLen += 1;
                    break;
            }
        }

        nextX = x + fm.charsWidth(text, flushIndex, flushLen);

        if (flushLen > 0 && nextX >= clipStart) {
            if (bg != null) {
                paintBackground(x, y, nextX - x, fm.getHeight(), g, fm.getAscent(), bg, !selected);
            }
            g.setColor(fg);
            g.drawChars(text, flushIndex, flushLen, (int) x, (int) y);
        }

        // if (host.getUnderlineForToken(token)) {
        // g.setColor(fg);
        // int y2 = (int) (y + 1);
        // g.drawLine(origX, y2, (int) nextX, y2);
        // }

        // Don't check if it's whitespace - some TokenMakers may return types
        // other than Token.WHITESPACE for spaces (such as Token.IDENTIFIER).
        // This also allows us to paint tab lines for MLC's.
        // if (host.getPaintTabLines() && origX==host.getMargin().left) {// &&
        // isWhitespace()) {
        // paintTabLines(token, origX, (int)y, (int)nextX, g, e, host);
        // }

        return nextX;

    }

    /**
     * {@inheritDoc}
     */
    public float paintSelected(Token token, Graphics2D g, float x, float y, TabExpander e) {
        return paintSelected(token, g, x, y, e, 0);
    }

    /**
     * {@inheritDoc}
     */
    public float paintSelected(Token token, Graphics2D g, float x, float y, TabExpander e, float clipStart) {
        return paintImpl(token, g, x, y, e, clipStart, true);
    }

}