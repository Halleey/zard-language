public class VariableStatement {
    private Parser parser;

    public VariableStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        String type = parser.getCurrentToken().getValue();
        parser.eat(Token.TokenType.KEYWORD);
        String variableName = parser.getCurrentToken().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);

        if (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                parser.getCurrentToken().getValue().equals("=")) {
            parser.eat(Token.TokenType.OPERATOR);
            Object value = parser.calc();
            parser.getVariableValues().put(variableName, value);

        } else if (parser.getCurrentToken().getType() == Token.TokenType.IDENTIFIER) {
            System.out.println("log atual: " + parser.getCurrentToken());
            atribuir(variableName);  // Passa o nome da variável para o método atribuir
        } else {
            parser.getVariableValues().put(variableName, getDefault(type));
        }

        parser.eat(Token.TokenType.DELIMITER);
        System.out.println("Declaração de variável: Tipo " + type + ", Nome: " + variableName);
    }

    private Object getDefault(String type) {
        switch (type) {
            case "int":
                return 0;
            case "double":
                return 0.0;
            case "string":
                return "";
            case "boolean":
                return false;
            default:
                throw new RuntimeException("Tipo desconhecido: " + type);
        }
    }

    public void atribuir(String variableName) {
        // Verificar e consumir o operador de atribuição '='
        if (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                parser.getCurrentToken().getValue().equals("=")) {
            parser.eat(Token.TokenType.OPERATOR);

            // Calcular o novo valor da variável
            Object value = parser.calc();

            // Verificar se a variável já foi declarada
            if (parser.getVariableValues().containsKey(variableName)) {
                // Atualizar o valor da variável existente
                parser.getVariableValues().put(variableName, value);
            } else {
                // Se a variável não foi declarada, lançar uma exceção
                throw new RuntimeException("Variável não declarada: " + variableName);
            }

            // Consumir o delimitador que encerra a atribuição
            if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER) {
                parser.eat(Token.TokenType.DELIMITER);
            } else {
                throw new RuntimeException("Erro de sintaxe: esperado delimitador após atribuição mas encontrado " + parser.getCurrentToken().getValue());
            }
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado operador de atribuição '=' mas encontrado " + parser.getCurrentToken().getValue());
        }
    }



    public void assignValue() {
        String variableName = parser.getCurrentToken().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);
        parser.eat(Token.TokenType.OPERATOR);

        Object value = parser.calc();
        parser.getVariableValues().put(variableName, value);
        parser.eat(Token.TokenType.DELIMITER);
    }
}
