package org.apache.pojo.beaneditor;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.undo.UndoManager;

import org.apache.pojo.beaneditor.model.PBEBeanParser;
import org.apache.pojo.beaneditor.model.PBEDocument;

public class PojoBeanEditor extends JTextArea {
    private final UndoManager undoManager = new UndoManager();
    private Map<?, ?> aaHints;

    public PojoBeanEditor(PojoBeanCreator creator, BeanValueTransformer bvt, Class<?> objType) {
        this(creator, bvt, creator.createPojoBean(objType));
    }

    public PojoBeanEditor(PojoBeanCreator creator, BeanValueTransformer bvt, Object obj) {
        super(new PBEDocument(bvt, PBEBeanParser.parseBean(creator, obj)));
        init();
    }

    protected void init() {
        getCaret().setBlinkRate(0);
        getDocument().addUndoableEditListener(undoManager);
        calculateRenderingHints();
    }

    public void updateUI() {
        setUI(new PojoBeanEditorUI(this));
        invalidate();
    }

    private final Graphics2D getGraphics2D(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (aaHints != null) {
            g2d.addRenderingHints(aaHints);
        }

        return g2d;
    }

    private final void calculateRenderingHints() {
        aaHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (aaHints == null) {
            Map<RenderingHints.Key, Object> temp = new HashMap<RenderingHints.Key, Object>();
            JLabel label = new JLabel();
            FontMetrics fm = label.getFontMetrics(label.getFont());
            Object hint = null;
            try {
                Method m = FontMetrics.class.getMethod("getFontRenderContext");
                FontRenderContext frc = (FontRenderContext) m.invoke(fm);
                m = FontRenderContext.class.getMethod("getAntiAliasingHint");
                hint = m.invoke(frc);
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
            }

            if (hint == null) {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("windows")) {
                    hint = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
                } else {
                    hint = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
                }
            }
            temp.put(RenderingHints.KEY_TEXT_ANTIALIASING, hint);

            aaHints = temp;

        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(getGraphics2D(g));
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }
}
