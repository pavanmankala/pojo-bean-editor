package org.apache.pojo.beaneditor;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.rtsffm.tango.xmlprotocol.Rule;

public class TestBeanEditor {
    public void testOpenBeanEditor() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                openFrame();
            }
        });
    }

    private void openFrame() {
        JFrame frame = new JFrame("Test Rule Bean Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = frame.getContentPane();
        c.add(new JScrollPane(new PojoBeanEditor(new PojoBeanCreator() {
            @Override
            public Object createPojoBean(Class<?> pojoTypeClazz) {
                try {
                    return pojoTypeClazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new BeanValueTransformer() {
            @Override
            public String transform(Object beanMemberValue) {
                return null;
            }

            @Override
            public Object transform(String stringRep) {
                return null;
            }
        }, Rule.class)));

        frame.pack();
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new TestBeanEditor().testOpenBeanEditor();
    }
}
