package editor.whiles;

import editor.translate.Parser;
import editor.translate.Token;

public class SkipWhile {

    private final Parser parser;

    public SkipWhile(Parser parser) {
        this.parser = parser;
    }

    public void skipToEndOfFunction() {
        int braceLevel = 0;
        while (parser.getCurrentToken().getType() != Token.TokenType.EOF) {
            if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER) {
                if ("{".equals(parser.getCurrentToken().getValue())) {
                    braceLevel++;
                } else if ("}".equals(parser.getCurrentToken().getValue())) {
                    if (braceLevel == 0) {
                        return;
                    } else {
                        braceLevel--;
                    }
                }
            }
            parser.advance();
        }
    }
}
