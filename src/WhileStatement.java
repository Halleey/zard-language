public class WhileStatement {
    private final Parser parser;

    public WhileStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {

        parser.eat(Token.TokenType.KEYWORD);
        parser.eat(Token.TokenType.DELIMITER);


        Object condition = parser.expression();
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição de 'while' deve ser um valor booleano.");
        }

        parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER);

        while ((boolean) condition) {
            processWhileBlock();

            condition = checkWhileCondition();
            if (!(boolean) condition) {
                while (!parser.getCurrentToken().getValue().equals("}")) {
                    parser.advance();
                    System.out.println("skiped "+ parser.getCurrentToken());
                }
            }
        }
        parser.eat(Token.TokenType.DELIMITER);
        parser.advance();
    }


    private void processWhileBlock() {
        while (!(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                "}".equals(parser.getCurrentToken().getValue()))) {
            parser.statement();
        }
    }

    private Object checkWhileCondition() {
        parser.backToWhile();
        parser.eat(Token.TokenType.KEYWORD);
        parser.eat(Token.TokenType.DELIMITER);

        Object condition = parser.expression();
        parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER);
        return condition;
    }

}
