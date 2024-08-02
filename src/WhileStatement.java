
//need fixed bugs
public class WhileStatement {
    private final Parser parser;

    public WhileStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        // Consome o keyword 'while'
        parser.eat(Token.TokenType.KEYWORD);

        // Consome o delimitador '('
        parser.eat(Token.TokenType.DELIMITER);

        // Avalia a condição do loop
        Object condition = parser.expression();
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição de 'while' deve ser um valor booleano.");
        }

        // Consome o delimitador ')'
        parser.eat(Token.TokenType.DELIMITER);

        // Consome o delimitador '{'
        parser.eat(Token.TokenType.DELIMITER);

        // Executa o bloco de código enquanto a condição for verdadeira
        while ((boolean) condition) {
            parser.parseBlock(); // Executa o bloco de código

            // Avalia novamente a condição do loop após a execução do bloco
            parser.eat(Token.TokenType.KEYWORD); // Consome o keyword 'while'
            parser.eat(Token.TokenType.DELIMITER); // Consome o delimitador '('
            condition = parser.expression();
            if (!(condition instanceof Boolean)) {
                throw new RuntimeException("Condição de 'while' deve ser um valor booleano.");
            }
            parser.eat(Token.TokenType.DELIMITER); // Consome o delimitador ')'
        }

        // Consome o delimitador '}' após o bloco de código
        parser.eat(Token.TokenType.DELIMITER);
    }
}
