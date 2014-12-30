package org.apache.pojo.beaneditor.model.outline;

import javax.swing.text.Element;
import javax.swing.text.TableView;

public class MapEntry<T> {
    private String key;
    private T value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
