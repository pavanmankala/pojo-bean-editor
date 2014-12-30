package org.apache.pojo.beaneditor;

import junit.framework.Assert;

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
}
