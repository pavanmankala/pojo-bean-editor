package org.apache.pojo.beaneditor;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
        c.add(new PojoBeanEditor(new PojoBeanCreator() {
            @Override
            public Object createPojoBean(Class<?> pojoTypeClazz) {
                if (pojoTypeClazz == TestBean.class) {
                    return new TestBean();
                } else if (pojoTypeClazz == ChildBean.class) {
                    return new ChildBean();
                }
                return null;
            }
        }, TestBean.class));

        frame.pack();
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
