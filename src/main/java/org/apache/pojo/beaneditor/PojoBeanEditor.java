package org.apache.pojo.beaneditor;

import java.awt.Font;

import javax.swing.JTextArea;

import org.apache.pojo.beaneditor.model.PBEBeanParser;
import org.apache.pojo.beaneditor.model.PBEDocument;

public class PojoBeanEditor extends JTextArea {
    public PojoBeanEditor(PojoBeanCreator creator, Object obj) {
        super(new PBEDocument(PBEBeanParser.parseBean(creator, obj)));
    }

    public PojoBeanEditor(PojoBeanCreator creator, Class<?> objType) {
        super(new PBEDocument(PBEBeanParser.parseBean(creator, creator.createPojoBean(objType))));
    }

    {
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        getCaret().setBlinkRate(0);
    }

    public void updateUI() {
        setUI(new PojoBeanEditorUI(this));
        invalidate();
    }
}
