package org.apache.pojo.beaneditor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import javax.swing.text.Caret;
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
import org.apache.pojo.beaneditor.views.PojoMemberKeyView;
import org.apache.pojo.beaneditor.views.PojoMemberView;

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
    protected Caret createCaret() {
        return new PojoBeanEditorCaret();
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
        editor.getCaret().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                caretUpdated();
            }
        });

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isCursorOnFold(e)) {
                    e.getComponent().setCursor(Cursor.getDefaultCursor());
                } else {
                    e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    if (isCursorOnFold(e) && currentPmkv != null) {
                        currentPmkv.toggleFold();
                        editor.repaint();
                    }
                }
            }
        };

        // editor.addMouseListener(ma);
        // editor.addMouseMotionListener(ma);
        super.installListeners();
    }

    private PojoMemberKeyView currentPmkv;

    protected boolean isCursorOnFold(MouseEvent e) {
        Rectangle keyBounds = new Rectangle();
        currentPmkv = getPojoMemberKeyViewAt(e, keyBounds);

        if (currentPmkv == null || currentPmkv.getNode().isLeaf()) {
            return false;
        } else {
            keyBounds.x += currentPmkv.getStepNo() * 10;
            keyBounds.width = 16;

            return keyBounds.contains(e.getX(), e.getY());
        }
    }

    protected PojoMemberKeyView getPojoMemberKeyViewAt(MouseEvent e, Rectangle keyBounds) {
        Shape currentAllocation = getRootView(editor).getChildAllocation(0, new Rectangle(editor.getSize()));
        View v = getRootView(editor);

        while (!(v instanceof PojoMemberView)) {
            int viewIndex = v.getViewIndex(e.getX(), e.getY(), currentAllocation);

            if (viewIndex == -1) {
                return null;
            } else {
                currentAllocation = v.getChildAllocation(viewIndex, currentAllocation);
            }

            v = v.getView(viewIndex);
        }

        currentAllocation = v.getChildAllocation(0, currentAllocation);
        keyBounds.setBounds((Rectangle) currentAllocation);
        return (PojoMemberKeyView) v.getView(0);
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

                editor.getDocument().getText(p0, p1-p0);
                Rectangle s = (Rectangle) getRootView(editor).modelToView(valueLeafElem.getStartOffset(), bounds,
                        Bias.Forward);
                g.fillRect(s.x, s.y, visibleRect.width, s.height);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    class PojoBeanEditorCaret extends BasicCaret {
        @Override
        protected void positionCaret(MouseEvent e) {
            if (!isCursorOnFold(e)) {
                super.positionCaret(e);
            }
        }

        @Override
        protected void moveCaret(MouseEvent e) {
            super.moveCaret(e);
        }
    }
}
