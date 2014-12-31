package org.apache.pojo.beaneditor;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.StyleConstants;
import javax.swing.text.TableView;
import javax.swing.text.View;
import javax.swing.text.html.HTML;

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
            return super.create(elem);
        }

        PBEDocument pbeDoc = (PBEDocument) elem.getDocument();
        String kind = elem.getName();
        PojoTableView rootTableView;
        ElementIterator iterator = new ElementIterator(elem);

        switch (kind) {
            case "key":
                break;
            case "value":
                break;
            case "root":
                rootTableView = new PojoTableView(elem);
                Element element;
                while ((element = iterator.next()) != null) {
                    AttributeSet attributes = element.getAttributes();
                    Object name = attributes.getAttribute(StyleConstants.NameAttribute);
                    if ((name instanceof HTML.Tag)
                            && ((name == HTML.Tag.H1) || (name == HTML.Tag.H2) || (name == HTML.Tag.H3))) {
                        // Build up content text as it may be within multiple
                        // elements
                        StringBuffer text = new StringBuffer();
                        int count = element.getElementCount();
                        for (int i = 0; i < count; i++) {
                            Element child = element.getElement(i);
                            AttributeSet childAttributes = child.getAttributes();
                            if (childAttributes.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.CONTENT) {
                                int startOffset = child.getStartOffset();
                                int endOffset = child.getEndOffset();
                                int length = endOffset - startOffset;
                                //text.append(htmlDoc.getText(startOffset, length));
                            }
                        }
                        System.out.println(name + ": " + text.toString());
                    }
                }

                return rootTableView;
            case "member":
                break;
        }
        return null;
    }

    class PojoTableView extends TableView {
        public PojoTableView(Element elem) {
            super(elem);
        }
    }
}
