package org.apache.pojo.beaneditor;

import javax.swing.JTextArea;

public class PojoBeanEditor extends JTextArea {
    public PojoBeanEditor(PojoBeanCreator creator, Object obj) {
        super(new PBEDocument(PBEBeanParser.parseBean(creator, obj)));
    }

    public PojoBeanEditor(PojoBeanCreator creator, Class<?> objType) {
        super(new PBEDocument(PBEBeanParser.parseBean(creator, creator.createPojoBean(objType))));
    }

    public void updateUI() {
        setUI(new PojoBeanEditorUI(this));
        invalidate();
    }
}
