package org.apache.pojo.beaneditor;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
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
            case AbstractDocument.ContentElementName:
                return new ValuePlainView(elem.getParentElement());
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
