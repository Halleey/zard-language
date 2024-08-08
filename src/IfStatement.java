public class IfStatement {
    private final Parser parser;

    public IfStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        System.out.println("Executing IfStatement, current token: " + parser.getCurrentToken());

        System.out.println("Consuming 'if'");
        parser.eat(Token.TokenType.KEYWORD);


        System.out.println("Consuming '('");
        parser.eat(Token.TokenType.DELIMITER);


        System.out.println("Evaluating condition");
        Object condition = parser.expression();
        System.out.println("Condition evaluated: " + condition);

        System.out.println("Consuming ')'");
        parser.eat(Token.TokenType.DELIMITER);


        System.out.println("Consuming '{'");
        parser.eat(Token.TokenType.DELIMITER);

        boolean conditionActivation = (boolean) condition;
        System.out.println("Condition activation: " + conditionActivation);

        if (conditionActivation) {
            System.out.println("Executing block for 'if'");
            parser.parseBlock();
            skipElseBlock();
            return;
        } else {
            System.out.println("Skipping block for 'if'");
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
                        parser.parseBlock();

                        skipElseBlock();
                        return;
                    } else {

                        while (!parser.getCurrentToken().getValue().equals("}")) {
                            System.out.println("Current token while skipping 'else if': " + parser.getCurrentToken());
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

    private void skipElseBlock() {

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
                System.out.println("'else if' condition activation: " + elseIfConditionActivation);
                if (elseIfConditionActivation) {
                    System.out.println("Executing block for 'else if'");
                    parser.parseBlock();
                    return;
                } else {
                    // Avança até o final do bloco 'else if'
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