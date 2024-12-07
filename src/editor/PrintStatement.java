package editor;

import editor.translate.Parser;

public class PrintStatement {

    private final Parser parser;

    public PrintStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        parser.eat(Token.TokenType.KEYWORD); // Consome "print"
        parser.eat(Token.TokenType.DELIMITER); // Consome "("

        Object result = printExpression(); // Avalia a expressão dentro do print
        System.out.println(result);

        parser.eat(Token.TokenType.DELIMITER); // Consome ")"
    }

    public Object printExpression() {
        StringBuilder sb = new StringBuilder();

        while (!parser.getCurrentToken().getValue().equals(")")) { // Processa até encontrar o ")"
            Token currentToken = parser.getCurrentToken();

            switch (currentToken.getType()) {
                case STRING:
                    sb.append(currentToken.getValue()); // Adiciona o texto da string
                    parser.eat(Token.TokenType.STRING);
                    break;

                case IDENTIFIER:
                    String identifier = currentToken.getValue();
                    if (parser.getVariableValues().containsKey(identifier)) {
                        sb.append(parser.getVariableValues().get(identifier)); // Substitui pelo valor da variável
                    } else {
                        throw new RuntimeException("Erro: Variável '" + identifier + "' não definida.");
                    }
                    parser.eat(Token.TokenType.IDENTIFIER);
                    break;

                case NUMBER:
                    sb.append(currentToken.getValue()); // Adiciona o número como string
                    parser.eat(Token.TokenType.NUMBER);
                    break;

                case OPERATOR:
                    if (currentToken.getValue().equals("+")) {
                        sb.append(" "); // Adiciona espaço em vez de operador
                        parser.eat(Token.TokenType.OPERATOR);
                    } else {
                        throw new RuntimeException("Erro de sintaxe: operador inválido dentro do print.");
                    }
                    break;

                default:
                    throw new RuntimeException("Erro de sintaxe: '' não encontrada ");
            }
        }
        return sb.toString();
    }
}
