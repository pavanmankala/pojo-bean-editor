package org.apache.pojo.beaneditor;

import javax.swing.JComponent;

public class PojoBeanEditor<T> extends JComponent {
    public PojoBeanEditor(PojoBeanCreator creator, Class<T> baseClass) {
        Object obj = creator.createPojoBean(baseClass);
    }
}
