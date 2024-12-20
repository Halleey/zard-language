package editor.whiles;
import editor.globals.GlobalClass;
import editor.translate.Token;
import editor.translate.Parser;

public class WhileStatement extends GlobalClass {
    private final Parser parser;
    private final SkipWhile skipWhile;
    public WhileStatement(Parser parser) {
        this.parser = parser;
        this.skipWhile = new SkipWhile(parser);
    }

    public void execute() {
        parser.eat(Token.TokenType.KEYWORD); // Consome 'while'
        parser.eat(Token.TokenType.DELIMITER); // Consome '('

        Object condition = parser.expression();
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição de 'while' deve ser um valor booleano.");
        }

        parser.eat(Token.TokenType.DELIMITER); // Consome ')'
        parser.eat(Token.TokenType.DELIMITER); // Consome '{'

        while ((boolean) condition) {
            if (isFoundReturn()) {
                skipWhile.skipToEndOfFunction();
                setFoundReturn(false);
                break;
            }

            try {
                processWhileBlock();
                condition = checkWhileCondition();
                if (!(condition instanceof Boolean) || !(boolean) condition) {
                    break;
                }

            } catch (RuntimeException e) {
                System.out.println(parser.getCurrentToken().getType() + " token para debugar");
                break; // Interrompe o loop, mas não encerra o programa.
            }
        }

        if (parser.getCurrentToken().getType() != Token.TokenType.EOF) {
            skipWhile.skipToEndOfFunction();
            parser.advance();
        }
    }

    private void processWhileBlock() {
        int braceLevel = 0;
        while (!(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                "}".equals(parser.getCurrentToken().getValue()) && braceLevel == 0)) {

            if (parser.getCurrentToken().getValue().equals("return")) {
                setFoundReturn(true);
                skipWhile.skipToEndOfFunction();
                return;
            }

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
        parser.eat(Token.TokenType.DELIMITER); // Consome '}'
    }

    private Object checkWhileCondition() {
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

