package org.apache.pojo.beaneditor;

public class TestBean {
    private String myName;
    private String myValue;
    private ChildBean myChild;

    public String getMyValue() {
        return myValue;
    }

    public void setMyValue(String myValue) {
        this.myValue = myValue;
    }

    public void setMyChild(ChildBean myChild) {
        this.myChild = myChild;
    }

    public ChildBean getMyChild() {
        return myChild;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }
}
