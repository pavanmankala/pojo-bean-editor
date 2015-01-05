package org.apache.pojo.beaneditor;

import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;

public class PojoMemberView extends BoxView {
    public PojoMemberView(Element elem) {
        super(elem, X_AXIS);
    }

@Override
public float getAlignment(int axis) {
    return 0.0f;
}
}