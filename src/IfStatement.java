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
                System.out.println("Token atual sendo ignorado: " + parser.getCurrentToken());
                parser.advance();
            }
            System.out.println("Consuming '}' for 'if'");
            parser.eat(Token.TokenType.DELIMITER); // Consome '}'
        }

        System.out.println("Checking for 'else if' or 'else'");
        // Verifica se há 'else if' ou 'else'
        while (parser.getCurrentToken().getType() == Token.TokenType.KEYWORD) {
            String value = parser.getCurrentToken().getValue();

            if (value.equals("else")) {
                if (parser.peekNextToken().getValue().equals("if")) {
                    // Se for 'else if', processa
                    System.out.println("Processing 'else if'");
                    parser.eat(Token.TokenType.KEYWORD); // Consome 'else'
                    parser.eat(Token.TokenType.KEYWORD); // Consome 'if'
                    parser.eat(Token.TokenType.DELIMITER); // Consome '('
                    System.out.println("Evaluating 'else if' condition");
                    Object elseIfCondition = parser.expression(); // Avalia a condição
                    System.out.println("'else if' condition evaluated: " + elseIfCondition);
                    parser.eat(Token.TokenType.DELIMITER); // Consome ')'
                    parser.eat(Token.TokenType.DELIMITER); // Consome '{'

                    boolean elseIfConditionActivation = (boolean) elseIfCondition;
                    System.out.println("'else if' condition activation: " + elseIfConditionActivation);
                    if (elseIfConditionActivation) {
                        System.out.println("Executing block for 'else if'");
                        parser.parseBlock(); // Executa o bloco do 'else if'
                        // Ignora o bloco 'else' se o bloco 'else if' foi executado
                        skipElseBlock();
                        return; // Retorna após executar o bloco do 'else if'
                    } else {
                        // Avança até o final do bloco 'else if'
                        System.out.println("Skipping block for 'else if'");
                        while (!parser.getCurrentToken().getValue().equals("}")) {
                            System.out.println("Current token while skipping 'else if': " + parser.getCurrentToken());
                            parser.advance();
                        }
                        System.out.println("Consuming '}' for 'else if'");
                        parser.eat(Token.TokenType.DELIMITER); // Consome '}'
                    }
                } else {
                    // Se for 'else', processa
                    System.out.println("Processing 'else'");
                    parser.eat(Token.TokenType.KEYWORD); // Consome 'else'

                    // Verifica se o próximo token é '{'
                    if (parser.getCurrentToken().getValue().equals("{")) {
                        System.out.println("Consuming '{' for 'else'");
                        parser.eat(Token.TokenType.DELIMITER); // Consome '{'
                        System.out.println("Executing block for 'else'");
                        parser.parseBlock(); // Executa o bloco do 'else'
                        return; // Retorna após executar o bloco do 'else'
                    } else {
                        throw new RuntimeException("Erro de sintaxe: esperado '{' após 'else' mas encontrado " + parser.getCurrentToken());
                    }
                }
            }
        }
    }

    private void skipElseBlock() {
        // Verifica se há um bloco 'else' a ser ignorado
        while (parser.getCurrentToken().getValue().equals("else")) {
            System.out.println("Skipping block for 'else'");
            parser.eat(Token.TokenType.KEYWORD); // Consome 'else'

            // Verifica se o próximo token é 'if'
            if (parser.getCurrentToken().getType() == Token.TokenType.KEYWORD &&
                    parser.getCurrentToken().getValue().equals("if")) {
                System.out.println("Skipping 'else if'");
                // Processa 'else if'
                parser.eat(Token.TokenType.KEYWORD); // Consome 'if'
                parser.eat(Token.TokenType.DELIMITER); // Consome '('
                System.out.println("Evaluating 'else if' condition");
                Object elseIfCondition = parser.expression(); // Avalia a condição
                parser.eat(Token.TokenType.DELIMITER); // Consome ')'
                parser.eat(Token.TokenType.DELIMITER); // Consome '{'

                boolean elseIfConditionActivation = (boolean) elseIfCondition;
                System.out.println("'else if' condition activation: " + elseIfConditionActivation);
                if (elseIfConditionActivation) {
                    System.out.println("Executing block for 'else if'");
                    parser.parseBlock(); // Executa o bloco do 'else if'
                    return; // Retorna após executar o bloco do 'else if'
                } else {
                    // Avança até o final do bloco 'else if'
                    while (!parser.getCurrentToken().getValue().equals("}")) {
                        System.out.println("Current token while skipping 'else if': " + parser.getCurrentToken());
                        parser.advance();
                    }

                    parser.eat(Token.TokenType.DELIMITER); // Consome '}'
                }
            } else if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                    parser.getCurrentToken().getValue().equals("{")) {
                // Se o próximo token é '{', processa o bloco 'else'

                parser.eat(Token.TokenType.DELIMITER); // Consome '{'
                // Avança até o final do bloco 'else'
                while (parser.getCurrentToken().getType() != Token.TokenType.DELIMITER ||
                        !parser.getCurrentToken().getValue().equals("}")) {
                    parser.advance();
                }

                parser.eat(Token.TokenType.DELIMITER); // Consome '}'
                return; // Retorna após executar o bloco do 'else'
            } else {
                throw new RuntimeException("Erro de sintaxe: esperado '{' ou 'if' após 'else' mas encontrado " + parser.getCurrentToken());
            }
        }
    }
}