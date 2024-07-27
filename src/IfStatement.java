public class IfStatement {
    private final Parser parser;

    public IfStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        System.out.println("Executing IfStatement, current token: " + parser.getCurrentToken());

        // Consome 'if'
        System.out.println("Consuming 'if'");
        parser.eat(Token.TokenType.KEYWORD);

        // Consome '('
        System.out.println("Consuming '('");
        parser.eat(Token.TokenType.DELIMITER);

        // Avalia a condição
        System.out.println("Evaluating condition");
        Object condition = parser.expression();
        System.out.println("Condition evaluated: " + condition);

        // Consome ')'
        System.out.println("Consuming ')'");
        parser.eat(Token.TokenType.DELIMITER);

        // Consome '{'
        System.out.println("Consuming '{'");
        parser.eat(Token.TokenType.DELIMITER);

        boolean conditionActivation = (boolean) condition;
        System.out.println("Condition activation: " + conditionActivation);

        if (conditionActivation) {
            // Executa o bloco se a condição for verdadeira
            System.out.println("Executing block for 'if'");
            parser.parseBlock();
        } else {
            // Avança até o final do bloco 'if'
            System.out.println("Skipping block for 'if'");
            while (parser.getCurrentToken().getType() != Token.TokenType.DELIMITER ||
                    !parser.getCurrentToken().getValue().equals("}")) {
                System.out.println("Current token while skipping: " + parser.getCurrentToken());
                parser.advance();
            }
            System.out.println("Consuming '}' for 'if'");
            parser.eat(Token.TokenType.DELIMITER); // Consome '}'
        }

        System.out.println("Checking for 'else if' or 'else'");
        // Verifica se há 'else if' ou 'else'
        while (parser.getCurrentToken().getType() == Token.TokenType.KEYWORD &&
                (parser.getCurrentToken().getValue().equals("else") ||
                        (parser.getCurrentToken().getValue().equals("else") && parser.peekNextToken().getValue().equals("if")))) {

            if (parser.getCurrentToken().getValue().equals("else") && parser.peekNextToken().getValue().equals("if")) {
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
                    return; // Retorna após executar o bloco 'else if'
                } else {
                    // Avança até o final do bloco 'else if'
                    System.out.println("Skipping block for 'else if'");
                    while (parser.getCurrentToken().getType() != Token.TokenType.DELIMITER ||
                            !parser.getCurrentToken().getValue().equals("}")) {
                        System.out.println("Current token while skipping 'else if': " + parser.getCurrentToken());
                        parser.advance();
                    }
                    System.out.println("Consuming '}' for 'else if'");
                    parser.eat(Token.TokenType.DELIMITER); // Consome '}'
                }
            } else if (parser.getCurrentToken().getValue().equals("else")) {
                System.out.println("Processing 'else'");
                parser.eat(Token.TokenType.KEYWORD); // Consome 'else'

                // Verifica se o próximo token é '{'
                if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                        parser.getCurrentToken().getValue().equals("{")) {
                    System.out.println("Consuming '{' for 'else'");
                    parser.eat(Token.TokenType.DELIMITER); // Consome '{'
                    System.out.println("Executing block for 'else'");
                    parser.parseBlock(); // Executa o bloco do 'else'
                    return; // Retorna após executar o bloco 'else'
                } else {
                    throw new RuntimeException("Erro de sintaxe: esperado '{' após 'else' mas encontrado " + parser.getCurrentToken());
                }
            }
        }
    }
}
