package org.apache.pojo.beaneditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position.Bias;
import javax.swing.text.StyleContext;
import javax.swing.text.View;

import org.apache.pojo.beaneditor.model.PBEDocument;
import org.apache.pojo.beaneditor.model.PBEDocument.MemberBranchElement;
import org.apache.pojo.beaneditor.model.PBEDocument.ValueBranchElement;
import org.apache.pojo.beaneditor.views.PojoBeanView;

public class PojoBeanEditorUI extends BasicTextAreaUI {
    private static final PBEEditorKit defaultKit = new PBEEditorKit();
    private final PojoBeanEditor editor;
    private final HighlightPainter editableLineHighlighter = new EditableAreaHighlighter();

    private MemberBranchElement highlightBranchElement;
    private Object previousHighlightTag;

    public static ComponentUI createUI(JComponent ta) {
        return new PojoBeanEditorUI((PojoBeanEditor) ta);
    }

    public PojoBeanEditorUI(PojoBeanEditor pojoBeanEditor) {
        super();
        this.editor = pojoBeanEditor;
    }

    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();

        InputMap im = editor.getInputMap();
        ActionMap am = editor.getActionMap();

        for (Action pbeAction : defaultKit.getPbeactions()) {
            KeyStroke accel = (KeyStroke) pbeAction.getValue(Action.ACCELERATOR_KEY);
            Object name = pbeAction.getValue(Action.NAME);
            im.put(accel, name);
            am.put(name, pbeAction);
        }
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        Font font = sc.getFont("Consolas", Font.PLAIN, 13);

        if (!"Consolas".equals(font.getFamily())) {
            font = sc.getFont("Monospaced", Font.PLAIN, 13);
        }

        editor.setFont(font);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        editor.getCaret().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                caretUpdated();
            }
        });
    }

    protected void caretUpdated() {
        PBEDocument doc = (PBEDocument) editor.getDocument();
        BranchElement be = (BranchElement) doc.getDefaultRootElement();
        Highlighter highlighter = editor.getHighlighter();

        if (be != null && be.getName() != PBEDocument.MEMBER_ELEM) {
            int index = be.getElementIndex(editor.getCaretPosition());
            be = (BranchElement) be.getElement(index);
        }

        if (previousHighlightTag != null) {
            highlighter.removeHighlight(previousHighlightTag);
            previousHighlightTag = null;
        }

        if (be == null) {
            return;
        }

        highlightBranchElement = (MemberBranchElement) be;

        try {
            previousHighlightTag = highlighter.addHighlight(editor.getCaretPosition(), be.getEndOffset(),
                    editableLineHighlighter);
        } catch (BadLocationException e1) {
            throw new RuntimeException(e1);
        }

        editor.repaint();
    }

    @Override
    public EditorKit getEditorKit(JTextComponent tc) {
        if (tc == editor) {
            return defaultKit;
        }

        return super.getEditorKit(tc);
    }

    public View create(Element elem) {
        if (!(elem.getDocument() instanceof PBEDocument)) {
            return new BoxView(elem, BoxView.X_AXIS);
        }

        caretUpdated();

        if (elem.getName() == PBEDocument.ROOT_ELEM) {
            return new PojoBeanView(elem);
        }

        return null;
    }

    class EditableAreaHighlighter implements HighlightPainter {
        private final Color highlightColor = new Color(255, 255, 170);

        @Override
        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            Rectangle visibleRect = (Rectangle) bounds;
            try {
                if (highlightBranchElement == null || highlightBranchElement.getElementCount() != 2) {
                    return;
                }

                g.setColor(highlightColor);

                ValueBranchElement vbe = (ValueBranchElement) highlightBranchElement.getElement(1);
                int valueLine = vbe.getElementIndex(Math.max(vbe.getStartOffset(), editor.getCaretPosition()));
                Element valueLeafElem = vbe.getElement(valueLine);

                Rectangle s = (Rectangle) getRootView(editor).modelToView(valueLeafElem.getStartOffset(), bounds,
                        Bias.Forward);
                g.fillRect(s.x, s.y, visibleRect.width, s.height);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

    }
}
