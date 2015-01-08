package org.apache.pojo.beaneditor.model;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;
import org.fife.ui.rsyntaxtextarea.modes.JavaTokenMaker;

public class PBECodeTokenMaker extends JavaTokenMaker {
    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
        Token token = super.getTokenList(text, initialTokenType, startOffset);

        PBEToken root = new PBEToken(token);
        TokenImpl cloneToken = root, origToken = (TokenImpl) token.getNextToken();

        while (origToken != null) {
            PBEToken nextClone = new PBEToken(origToken);
            cloneToken.setNextToken(nextClone);
            cloneToken = nextClone;
            origToken = (TokenImpl) origToken.getNextToken();
        }

        return root;
    }

    public static class PBEToken extends TokenImpl {
        public PBEToken() {
            super();
        }

        public PBEToken(Token t) {
            super(t);
        }

        @Override
        public Token getLastPaintableToken() {
            Token t = this;

            while (t.isPaintable()) {
                Token next = t.getNextToken();
                if (next == null || !next.isPaintable()) {
                    return t;
                }
                t = next;
            }

            return t;
        }
    }
}
