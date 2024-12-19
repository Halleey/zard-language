package editor.ifStatement;
import editor.translate.Token;
import editor.translate.Parser;

public class IfStatement  {
    private final Parser parser;

    public IfStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        parser.eat(Token.TokenType.KEYWORD);
        parser.eat(Token.TokenType.DELIMITER);

        Object condition = parser.expression();
        System.out.println("Condition evaluated: " + condition);

        System.out.println("Consuming ')'");
        parser.eat(Token.TokenType.DELIMITER);

        System.out.println("Consuming '{'");
        parser.eat(Token.TokenType.DELIMITER);

        boolean conditionActivation = (boolean) condition;
        System.out.println("Condition activation: " + conditionActivation);

        if (conditionActivation) {
//            if(parser.getCurrentToken().getValue().equals("return")) {
//                System.out.println("TENTEI POR AQUI)");
//                setFoundReturn(true);
//                System.out.println("condition is"+ isFoundReturn());
//                return;
//            }
            parser.parseBlock();
            skipElseBlock();
//            setFoundReturn(true);
//            System.out.println("condition is"+ isFoundReturn());
            return;
        } else {
            while (!parser.getCurrentToken().getValue().equals("}")) {
                parser.advance();
            }
            parser.eat(Token.TokenType.DELIMITER); // Consome '}'
        }

        while (parser.getCurrentToken().getType() == Token.TokenType.KEYWORD) {
            String value = parser.getCurrentToken().getValue();

            if (value.equals("else")) {
                if (parser.peekNextToken().getValue().equals("if")) {
                    // Se for 'else if', processa
                    parser.eat(Token.TokenType.KEYWORD);
                    parser.eat(Token.TokenType.KEYWORD);
                    parser.eat(Token.TokenType.DELIMITER);

                    Object elseIfCondition = parser.expression();

                    parser.eat(Token.TokenType.DELIMITER);
                    parser.eat(Token.TokenType.DELIMITER);

                    boolean elseIfConditionActivation = (boolean) elseIfCondition;
                    if (elseIfConditionActivation) {
                        if(parser.getCurrentToken().getValue().equals("return")) {
                            System.out.println("TENTEI POR AQUI)");
                            return;
                        }
                        parser.parseBlock();
                        skipElseBlock();
                        return;
                    } else {
                        while (!parser.getCurrentToken().getValue().equals("}")) {
                            parser.advance();
                        }
                        parser.eat(Token.TokenType.DELIMITER);
                    }
                } else {
                    parser.eat(Token.TokenType.KEYWORD); // Consome 'else'

                    if (parser.getCurrentToken().getValue().equals("{")) {
                        parser.eat(Token.TokenType.DELIMITER);
                        parser.parseBlock();
                        return;
                    } else {
                        throw new RuntimeException("Erro de sintaxe: esperado '{' após 'else' mas encontrado " + parser.getCurrentToken());
                    }
                }
            }
        }
    }

    public void skipElseBlock() {
        while (parser.getCurrentToken().getValue().equals("else")) {
            parser.eat(Token.TokenType.KEYWORD); // Consome 'else'

            if (parser.getCurrentToken().getType() == Token.TokenType.KEYWORD &&
                    parser.getCurrentToken().getValue().equals("if")) {

                parser.eat(Token.TokenType.KEYWORD);
                parser.eat(Token.TokenType.DELIMITER);

                Object elseIfCondition = parser.expression();
                parser.eat(Token.TokenType.DELIMITER);
                parser.eat(Token.TokenType.DELIMITER);

                boolean elseIfConditionActivation = (boolean) elseIfCondition;
                if (elseIfConditionActivation) {
                    parser.parseBlock();
                    return;
                } else {
                    while (!parser.getCurrentToken().getValue().equals("}")) {
                        parser.advance();
                    }

                    parser.eat(Token.TokenType.DELIMITER); // Consome '}'
                }
            } else if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                    parser.getCurrentToken().getValue().equals("{")) {

                parser.eat(Token.TokenType.DELIMITER);

                while (parser.getCurrentToken().getType() != Token.TokenType.DELIMITER ||
                        !parser.getCurrentToken().getValue().equals("}")) {
                    parser.advance();
                }

                parser.eat(Token.TokenType.DELIMITER);
                return;
            } else {
                throw new RuntimeException("Erro de sintaxe: esperado '{' ou 'if' após 'else' mas encontrado " + parser.getCurrentToken());
            }
        }
    }
}