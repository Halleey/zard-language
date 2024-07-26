public class IfStatement {
    private final Parser parser;

    public IfStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        System.out.println("Executing IfStatement, current token: " + parser.getCurrentToken());
        parser.eat(Token.TokenType.KEYWORD); // Consome 'if'
        parser.eat(Token.TokenType.DELIMITER); // Consome '('
        Object condition = parser.expression(); // Avalia a condição
        parser.eat(Token.TokenType.DELIMITER); // Consome ')'
        parser.eat(Token.TokenType.DELIMITER); // Consome '{'

        boolean conditionActivation = (boolean) condition;
        if (conditionActivation) {
            parser.parseBlock();
        } else {
            // Avançar até o final do bloco 'if'
            while (parser.getCurrentToken().getType() != Token.TokenType.DELIMITER ||
                    !parser.getCurrentToken().getValue().equals("}")) {
                parser.advance();
            }
            parser.eat(Token.TokenType.DELIMITER); // Consome '}'
        }
        while (parser.getCurrentToken().getType() != Token.TokenType.EOF) {
            parser.statement();
        }
    }
}