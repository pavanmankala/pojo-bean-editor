package org.apache.pojo.beaneditor.test;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.pojo.beaneditor.BeanValueTransformer;
import org.apache.pojo.beaneditor.PojoBeanCreator;
import org.apache.pojo.beaneditor.PojoBeanEditor;
import org.junit.Test;

public class TestBeanEditor {
    @Test
    public void testOpenBeanEditor() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                openFrame();
            }
        });

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void openFrame() {
        JFrame frame = new JFrame("Test Bean Editor");
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
                return beanMemberValue.toString();
            }

            @Override
            public Object transform(String stringRep) {
                return null;
            }
        }, TestBean.class)));

        frame.pack();
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
