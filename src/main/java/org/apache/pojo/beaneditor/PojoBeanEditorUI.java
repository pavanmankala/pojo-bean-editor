package org.apache.pojo.beaneditor;

import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.PlainView;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

public class PojoBeanEditorUI extends BasicTextAreaUI {
    private final PojoBeanEditor editor;

    public static ComponentUI createUI(JComponent ta) {
        return new PojoBeanEditorUI((PojoBeanEditor) ta);
    }

    public PojoBeanEditorUI(PojoBeanEditor pojoBeanEditor) {
        super();
        this.editor = pojoBeanEditor;
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
        }

        return null;
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
                    case PBEDocument.VALUE_ELEM:
                        currentRow.append(new PlainView(element));
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
