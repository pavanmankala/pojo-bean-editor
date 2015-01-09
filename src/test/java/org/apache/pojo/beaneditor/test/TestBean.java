package org.apache.pojo.beaneditor.test;

import java.util.List;

public class TestBean {
    private String myName = "Test Bean";
    private String myValue1;
    private ChildBean myChild;
    private List<String> myStringList;
    private String myValue2;

    public String getMyValue1() {
        return myValue1;
    }

    public ChildBean getMyChild() {
        return myChild;
    }

    public String getMyName() {
        return myName;
    }

    public List<String> getMyStringList() {
        return myStringList;
    }

    public String getMyValue2() {
        return myValue2;
    }

    public void setMyValue1(String myValue1) {
        this.myValue1 = myValue1;
    }

    public void setMyChild(ChildBean myChild) {
        this.myChild = myChild;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public void setMyValue2(String myValue2) {
        this.myValue2 = myValue2;
    }

    public void setMyStringList(List<String> myStringList) {
        this.myStringList = myStringList;
    }
}
