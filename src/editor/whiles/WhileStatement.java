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
        parser.eat(Token.TokenType.KEYWORD);
        parser.eat(Token.TokenType.DELIMITER);

        Object condition = parser.expression();
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição de 'while' deve ser um valor booleano.");
        }

        parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER);

        System.out.println("Iniciando execução do while com condição: " + condition);

        while ((boolean) condition) {

            //cada iteração dentro
            System.out.println("Dentro do loop. Condição: " + condition);
            System.out.println("TOKEN CURRENT para looping " + parser.getCurrentToken().getValue() );

            if (isFoundReturn()) {
                skipWhile.skipToEndOfFunction();
                setFoundReturn(false);
                setFunctionWhile(false);
                break;
            }

            try {
                processWhileBlock();
                condition = checkWhileCondition();
                if (!(condition instanceof Boolean) || !(boolean) condition) {
                    System.out.println("Saindo do loop. Condição não mais verdadeira.");
                    setFunctionWhile(false);
                    System.out.println("definied ?" + isFunctionWhile());
                    break;
                }

            } catch (RuntimeException e) {
                System.out.println("Erro encontrado no token: " + parser.getCurrentToken().getType());
                break;
            }
        }

        // Verificação final, após o loop
        System.out.println("Execução do while finalizada.");
        if (parser.getCurrentToken().getType() != Token.TokenType.EOF) {
            skipWhile.skipToEndOfFunction();
            setFunctionWhile(false);
            parser.advance();
            System.out.println("TOKEN CURRENT final looping" + parser.getCurrentToken().getValue());
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
        parser.eat(Token.TokenType.DELIMITER);
    }

    public Object checkWhileCondition() {
        parser.backToWhile();
        parser.eat(Token.TokenType.KEYWORD);
        parser.eat(Token.TokenType.DELIMITER);

        Object condition = parser.expression();
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição de 'while' deve ser um valor booleano.");
        }

        parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER);
        return condition;
    }
}
