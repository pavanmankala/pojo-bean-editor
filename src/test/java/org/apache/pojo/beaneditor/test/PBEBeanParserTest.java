package org.apache.pojo.beaneditor.test;

import junit.framework.Assert;

import org.apache.pojo.beaneditor.BeanValueTransformer;
import org.apache.pojo.beaneditor.PojoBeanCreator;
import org.apache.pojo.beaneditor.model.PBEBeanParser;
import org.apache.pojo.beaneditor.model.PBEDocument;
import org.apache.pojo.beaneditor.model.outline.PBEOAggregatedNode;
import org.junit.Test;

public class PBEBeanParserTest {
    @Test
    public void testParser() {
        PBEOAggregatedNode node = PBEBeanParser.parseBean(new PojoBeanCreator() {
            @Override
            public Object createPojoBean(Class<?> pojoTypeClazz) {
                if (pojoTypeClazz == TestBean.class) {
                    return new TestBean();
                } else if (pojoTypeClazz == ChildBean.class) {
                    return new ChildBean();
                }
                return null;
            }
        }, new TestBean());

        Assert.assertNotNull(node);
    }

    @Test
    public void testPBEDocumentInit() {
        PBEOAggregatedNode node = PBEBeanParser.parseBean(new PojoBeanCreator() {
            @Override
            public Object createPojoBean(Class<?> pojoTypeClazz) {
                if (pojoTypeClazz == TestBean.class) {
                    return new TestBean();
                } else if (pojoTypeClazz == ChildBean.class) {
                    return new ChildBean();
                }
                return null;
            }
        }, new TestBean());
        PBEDocument doc = new PBEDocument(new BeanValueTransformer() {

            @Override
            public String transform(Object beanMemberValue) {
                return null;
            }

            @Override
            public Object transform(String stringRep) {
                return null;
            }
        }, node);
        System.out.println(doc);
    }
}
