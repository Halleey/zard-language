public class IfStatement {
    private final Parser parser;

    public IfStatement(Parser parser) {
        this.parser = parser;
    }
    public void execute() {
        parser.eat(Token.TokenType.KEYWORD);
        parser.eat(Token.TokenType.DELIMITER);

        Object condition = parser.expression();
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição de 'if' deve ser um valor booleano.");
        }

        parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER);

        if ((Boolean) condition) {
            parser.parseBlock();
        } else {
            skipBlock();
        }

        while (parser.getCurrentToken().getType() == Token.TokenType.KEYWORD &&
                "else".equals(parser.getCurrentToken().getValue())) {

            parser.eat(Token.TokenType.KEYWORD);

            if (parser.getCurrentToken().getType() == Token.TokenType.KEYWORD &&
                    "if".equals(parser.getCurrentToken().getValue())) {

                parser.eat(Token.TokenType.KEYWORD);
                parser.eat(Token.TokenType.DELIMITER);

                condition = parser.expression();
                if (!(condition instanceof Boolean)) {
                    throw new RuntimeException("Condição de 'else if' deve ser um valor booleano.");
                }

                parser.eat(Token.TokenType.DELIMITER);
                parser.eat(Token.TokenType.DELIMITER);

                if ((Boolean) condition) {
                    parser.parseBlock();
                } else {
                    skipBlock();
                }
            } else {
                // 'else' bloco
                if (parser.getCurrentToken().getValue().equals("{")) {
                    parser.eat(Token.TokenType.DELIMITER);
                    parser.parseBlock();
                }
                break;
            }
        }
    }

    private void skipBlock() {
        int braceLevel = 0;
        while (parser.getCurrentToken().getType() != Token.TokenType.EOF &&
                !(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                        "}".equals(parser.getCurrentToken().getValue()) && braceLevel == 0)) {
            if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER) {
                if ("{".equals(parser.getCurrentToken().getValue())) {
                    braceLevel++;
                } else if ("}".equals(parser.getCurrentToken().getValue())) {
                    braceLevel--;
                }
            }
            parser.advance();
        }
        if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                "}".equals(parser.getCurrentToken().getValue())) {
            parser.eat(Token.TokenType.DELIMITER);
        }
    }
}
