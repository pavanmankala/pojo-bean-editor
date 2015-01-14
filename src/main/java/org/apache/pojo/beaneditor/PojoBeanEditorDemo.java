package org.apache.pojo.beaneditor;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.rtsffm.tango.xmlprotocol.EventScriptDefinition;
import com.rtsffm.tango.xmlprotocol.OrderAgentDefinition;
import com.rtsffm.tango.xmlprotocol.PublishedExpression;
import com.rtsffm.tango.xmlprotocol.Rule;
import com.rtsffm.tango.xmlprotocol.RuleDefinition;
import com.rtsffm.tango.xmlprotocol.RuleInterface;
import com.rtsffm.tango.xmlprotocol.RuleParameter;

public class PojoBeanEditorDemo {
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
        Rule myRule = new Rule();

        {
            RuleInterface ri = new RuleInterface();
            RuleDefinition rd = new RuleDefinition();

            myRule.setDefinition(rd);
            myRule.setInterface(ri);

            ri.setComment("Here goes my rule comment");
            {
                RuleParameter fixParam = new RuleParameter();
                fixParam.setComment("Comment for Fix Param 1");
                fixParam.setType("Number");
                fixParam.setName("FixParam1");
                ri.getFixParam().add(fixParam);
            }

            {
                RuleParameter chgParam = new RuleParameter();
                chgParam.setComment("Comment for Chg Param 1");
                chgParam.setType("Boolean");
                chgParam.setName("ChgParam1");
                ri.getChgParam().add(chgParam);
            }
            {
                PublishedExpression overviewPE = new PublishedExpression();
                overviewPE.setComment("Comment for Overview PE 1");
                overviewPE.setValue("ValueOfOverviewPE1");
                overviewPE.setName("OverviewPE1");
                ri.getOverviewPE().add(overviewPE);
            }
            {
                PublishedExpression detailPE = new PublishedExpression();
                detailPE.setComment("Comment for Detail PE 1");
                detailPE.setValue("ValueOfDetailPE1");
                detailPE.setName("DetailPE1");
                ri.getDetailedPE().add(detailPE);
            }

            rd.setCode("package org.apache.pojo.beaneditor.test;\n" + "\n" + "public class ChildBean {\n"
                    + "    private String myName;\n" + "    private String myValue;\n" + "\n"
                    + "    public String getMyValue() {\n" + "        return myValue;\n" + "    }\n" + "\n"
                    + "    public void setMyValue(String myValue) {\n" + "        this.myValue = myValue;\n"
                    + "    }\n" + "\n" + "    public String getMyName() {\n" + "        return myName;\n" + "    }\n"
                    + "\n" + "    public void setMyName(String myName) {\n" + "        this.myName = myName;\n"
                    + "    }\n" + "}\n" + "");
            rd.setOffCondition("false");
            {
                EventScriptDefinition esd = new EventScriptDefinition();
                esd.setCode("package org.apache.pojo.beaneditor.test;\n" + "\n" + "public class ChildBean {\n"
                        + "    private String myName;\n" + "    private String myValue;\n" + "\n"
                        + "    public String getMyValue() {\n" + "        return myValue;\n" + "    }\n" + "\n"
                        + "    public void setMyValue(String myValue) {\n" + "        this.myValue = myValue;\n"
                        + "    }\n" + "\n" + "    public String getMyName() {\n" + "        return myName;\n"
                        + "    }\n" + "\n" + "    public void setMyName(String myName) {\n"
                        + "        this.myName = myName;\n" + "    }\n" + "}\n" + "");
                esd.setEventExpr("fp;cp;7");
                esd.setName("Event script Definition 1");
                rd.getEventScript().add(esd);
            }
            {
                OrderAgentDefinition oad = new OrderAgentDefinition();
                oad.setName("OrderAgent1");
                oad.setCode("package org.apache.pojo.beaneditor.test;\n" + "\n" + "public class ChildBean {\n"
                        + "    private String myName;\n" + "    private String myValue;\n" + "\n"
                        + "    public String getMyValue() {\n" + "        return myValue;\n" + "    }\n" + "\n"
                        + "    public void setMyValue(String myValue) {\n" + "        this.myValue = myValue;\n"
                        + "    }\n" + "\n" + "    public String getMyName() {\n" + "        return myName;\n"
                        + "    }\n" + "\n" + "    public void setMyName(String myName) {\n"
                        + "        this.myName = myName;\n" + "    }\n" + "}\n" + "");
                oad.setCnd("b");
                oad.setTrd("'XTR ALV'");
                oad.setAcc("\"ACC\"");
                oad.setBuy("false");
                oad.setLmt("10.7");
                rd.getOrderAgent().add(oad);
            }
            rd.setPeCode("String foo = \"bar\";");
        }
        myRule.setName("My Rule Name");

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
                return beanMemberValue != null ? beanMemberValue.toString() : "";
            }

            @Override
            public Object transform(String stringRep) {
                return null;
            }
        }, myRule)));

        frame.pack();
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new PojoBeanEditorDemo().testOpenBeanEditor();
    }
}
