package editor;

import editor.translate.Parser;

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
            if (!(condition instanceof Boolean) || !(boolean) condition) {
                break;
            }
        }

        if (parser.getCurrentToken().getType() != Token.TokenType.EOF) {
            skipToEndOfFunction();
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
        parser.eat(Token.TokenType.DELIMITER);
    }

    private Object checkWhileCondition() {
        parser.backToWhile();
        parser.eat(Token.TokenType.KEYWORD);
        parser.eat(Token.TokenType.DELIMITER);

        Object condition = parser.expression();
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição de 'while' deve ser um valor booleano.");
        }

        parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER); // Consome '{'
        return condition;
    }

    private void skipToEndOfFunction() {
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
