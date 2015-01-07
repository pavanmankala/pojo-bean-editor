package org.apache.pojo.beaneditor.test;

import javax.swing.text.Segment;

import junit.framework.Assert;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.junit.Test;

public class TokenExtractionTest {
    @Test
    public void testExtraction() {
        TokenMaker tm = TokenMakerFactory.getDefaultInstance().getTokenMaker(SyntaxConstants.SYNTAX_STYLE_JAVA);
        String javString = "package org.apache.pojo.beaneditor.test;\r\n"
                + "\r\n"
                + "import org.fife.ui.rsyntaxtextarea.SyntaxConstants;\r\n"
                + "import org.fife.ui.rsyntaxtextarea.TokenMaker;\r\n"
                + "import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;\r\n"
                + "import org.junit.Test;\r\n"
                + "\r\n"
                + "public class TokenExtractionTest {\r\n"
                + "    @Test public void testExtraction() {\r\n"
                + "        TokenMaker tm = TokenMakerFactory.getDefaultInstance().getTokenMaker(SyntaxConstants.SYNTAX_STYLE_JAVA);\r\n"
                + "        String javString = \"\";\r\n" + "    }\r\n" + "}";
        Segment seg = new Segment(javString.toCharArray(), 0, javString.length());
        Token parsedTokens = tm.getTokenList(seg, TokenTypes.NULL, 0);
        Assert.assertNotNull(parsedTokens);
    }
}
