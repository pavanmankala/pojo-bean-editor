package org.apache.pojo.beaneditor;

import javax.swing.text.AbstractDocument;
import javax.swing.text.Element;

public class PBEDocument extends AbstractDocument {
    protected PBEDocument() {
        super(null);
    }

    @Override
    public Element getDefaultRootElement() {
        return null;
    }

    @Override
    public Element getParagraphElement(int pos) {
        return null;
    }
}
