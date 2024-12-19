package editor.whiles;
import editor.globals.GlobalClass;
import editor.translate.Token;
import editor.translate.Parser;

public class WhileStatement extends GlobalClass {
    private final Parser parser;
    private ProcessWhileBlock processWhileBlock;
    private CheckCondition checkCondition;



    public WhileStatement(Parser parser) {
        this.parser = parser;
        this.processWhileBlock = new ProcessWhileBlock(parser);
        this.checkCondition = new CheckCondition(parser);

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
                processWhileBlock.skipToEndOfFunction();
                setFoundReturn(false);
                break;
            }
            try {
                processWhileBlock.processBlock();
                condition = checkCondition.checkWhileCondition();
                if (!(condition instanceof Boolean) || !(boolean) condition) {
                    break;
                }

            } catch (RuntimeException e) {
                System.out.println(parser.getCurrentToken().getType() + " token para debugar");
                break; // Interrompe o loop, mas não encerra o programa.
            }
        }

        if (parser.getCurrentToken().getType() != Token.TokenType.EOF) {
            processWhileBlock.skipToEndOfFunction();
            parser.advance();
        }
    }

}
