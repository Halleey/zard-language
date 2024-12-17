package editor.whiles;

import editor.Token;
import editor.translate.Parser;

public class CheckCondition {
    private final Parser parser;

    public CheckCondition(Parser parser) {
        this.parser = parser;
    }

    public Object checkWhileCondition() {
        parser.backToWhile();
        parser.eat(Token.TokenType.KEYWORD); // Consome 'while'
        parser.eat(Token.TokenType.DELIMITER); // Consome '('

        Object condition = parser.expression();
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição de 'while' deve ser um valor booleano.");
        }

        parser.eat(Token.TokenType.DELIMITER); // Consome ')'
        parser.eat(Token.TokenType.DELIMITER); // Consome '{'
        return condition;
    }

}
