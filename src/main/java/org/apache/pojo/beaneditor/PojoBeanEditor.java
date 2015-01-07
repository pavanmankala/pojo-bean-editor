package org.apache.pojo.beaneditor;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextArea;
import javax.swing.text.StyleContext;
import javax.swing.undo.UndoManager;

import org.apache.pojo.beaneditor.model.PBEBeanParser;
import org.apache.pojo.beaneditor.model.PBEDocument;

public class PojoBeanEditor extends JTextArea {
    private final UndoManager undoManager = new UndoManager();

    public PojoBeanEditor(PojoBeanCreator creator, BeanValueTransformer bvt, Class<?> objType) {
        this(creator, bvt, creator.createPojoBean(objType));
    }

    public PojoBeanEditor(PojoBeanCreator creator, BeanValueTransformer bvt, Object obj) {
        super(new PBEDocument(bvt, PBEBeanParser.parseBean(creator, obj)));
        init();
    }

    protected void init() {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        // Consolas added in Vista, used by VS2010+.
        Font font = sc.getFont("Consolas", Font.PLAIN, 13);
        if (!"Consolas".equals(font.getFamily())) {
            font = sc.getFont("Monospaced", Font.PLAIN, 13);
        }
        setFont(font);

        // getCaret().setBlinkRate(0);
        getDocument().addUndoableEditListener(undoManager);
    }

    public void updateUI() {
        setUI(new PojoBeanEditorUI(this));
        invalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g);
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }
}
