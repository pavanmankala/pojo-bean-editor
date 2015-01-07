package org.apache.pojo.beaneditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Caret;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

import org.apache.pojo.beaneditor.model.PBEDocument;
import org.apache.pojo.beaneditor.model.outline.PBEONode;
import org.apache.pojo.beaneditor.views.PojoBeanView;
import org.apache.pojo.beaneditor.views.PojoMemberKeyView;
import org.apache.pojo.beaneditor.views.PojoMemberValueView;
import org.apache.pojo.beaneditor.views.PojoMemberView;
import org.apache.pojo.beaneditor.views.plain.KeyPlainView;
import org.apache.pojo.beaneditor.views.plain.ValuePlainView;

public class PojoBeanEditorUI extends BasicTextAreaUI {
    private static final PBEEditorKit defaultKit = new PBEEditorKit();
    private final PojoBeanEditor editor;

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

        PBEDocument pbeDoc = (PBEDocument) elem.getDocument();

        switch (elem.getName()) {
            case PBEDocument.KEY_ELEM:
                break;
            case PBEDocument.VALUE_ELEM:
                break;
            case PBEDocument.ROOT_ELEM:
                return new PojoTableView(elem);
            case PBEDocument.MEMBER_ELEM:
                break;
            case AbstractDocument.ContentElementName:
                return new ValuePlainView(elem.getParentElement());
        }

        return null;
    }

    @Override
    protected void paintBackground(Graphics g) {
        super.paintBackground(g);
        paintCurrentLineHighlight(g, editor.getVisibleRect());
    }

    protected void paintCurrentLineHighlight(Graphics g, Rectangle visibleRect) {
        if (visibleRect != null) {
            return;
        }

        try {
            Caret caret = editor.getCaret();

            Color highlight = new Color(255, 255, 170);

            g.setColor(highlight);

            View parentContainedView = null, containedView = getRootView(editor);
            int index = 0;

            while (containedView.getElement().getName() != PBEDocument.KEY_ELEM
                    && containedView.getElement().getName() != PBEDocument.VALUE_ELEM) {
                index = containedView.getViewIndex(caret.getDot(), Bias.Forward);
                parentContainedView = containedView;
                containedView = containedView.getView(index);
            }
            Rectangle viewShape = (Rectangle) parentContainedView.getChildAllocation(index, new Rectangle());
            Rectangle lineShape = (Rectangle) getRootView(editor).modelToView(caret.getDot(), new Rectangle(),
                    Bias.Forward);
            g.fillRect(viewShape.x, lineShape.y, visibleRect.width, lineShape.height);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

    }

    class PojoTableView extends PojoBeanView {
        public PojoTableView(Element elem) {
            super(elem);
            ElementIterator iterator = new ElementIterator(elem);

            Element element = iterator.next(); // root element
            BoxView currentRow = null;

            while ((element = iterator.next()) != null) {
                switch (element.getName()) {
                    case PBEDocument.KEY_ELEM:
                        PojoMemberKeyView keyView = new PojoMemberKeyView(element);
                        keyView.append(new KeyPlainView(element));
                        currentRow.append(keyView);
                        break;
                    case PBEDocument.VALUE_ELEM:
                        PBEONode node = (PBEONode) element.getAttributes().getAttribute(PBEDocument.ATTRIB_NODE_OBJ);
                        if (node.isLeaf()) {
                            PojoMemberValueView valueView = new PojoMemberValueView(element);
                            valueView.append(new ValuePlainView(element));
                            currentRow.append(valueView);
                        }
                        break;
                    case PBEDocument.MEMBER_ELEM:
                        currentRow = new PojoMemberView(element);
                        append(currentRow);
                        break;
                }
            }
        }
    }
}
