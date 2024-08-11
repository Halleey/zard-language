public class WhileStatement {
    private final Parser parser;

    public WhileStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        parser.eat(Token.TokenType.KEYWORD); // Consume 'while'
        parser.eat(Token.TokenType.DELIMITER); // Consume '('

        // Avalia a condição
        Object condition = parser.expression();
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição de 'while' deve ser um valor booleano.");
        }

        parser.eat(Token.TokenType.DELIMITER); // Consume ')'
        parser.eat(Token.TokenType.DELIMITER); // Consume '{'

        while ((boolean) condition) {
            processWhileBlock();

            // Reavalie a condição
            condition = checkWhileCondition();
            if (!(boolean) condition) {
                // Pular até o final do bloco da função
                skipToEndOfFunction();
                break;
            }
        }

        // Avançar para o próximo token após o final do bloco do while
        if (parser.getCurrentToken().getType() != Token.TokenType.EOF) {
            parser.advance();
        }
    }

    private void processWhileBlock() {
        int braceLevel = 0;
        while (!(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                "}".equals(parser.getCurrentToken().getValue()) && braceLevel == 0)) {
            if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER) {
                if ("{".equals(parser.getCurrentToken().getValue())) {
                    braceLevel++;
                } else if ("}".equals(parser.getCurrentToken().getValue())) {
                    braceLevel--;
                }
            }
            parser.statement();
            if (parser.getCurrentToken().getType() == Token.TokenType.EOF) {
                throw new RuntimeException("Erro de sintaxe: bloco não terminado");
            }
        }
    }

    private Object checkWhileCondition() {
        parser.backToWhile();
        parser.eat(Token.TokenType.KEYWORD); // Consume 'while'
        parser.eat(Token.TokenType.DELIMITER); // Consume '('

        Object condition = parser.expression();
        parser.eat(Token.TokenType.DELIMITER); // Consume ')'
        parser.eat(Token.TokenType.DELIMITER); // Consume '{'
        return condition;
    }

    private void skipToEndOfFunction() {
        int braceLevel = 0;
        while (!(parser.getCurrentToken().getType() == Token.TokenType.EOF)) {
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
