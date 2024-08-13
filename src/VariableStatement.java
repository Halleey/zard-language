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

        if (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR && parser.getCurrentToken().getValue().equals("=")) {
            parser.eat(Token.TokenType.OPERATOR);
            Object value = parser.calc();
            parser.getVariableValues().put(variableName, value);
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

    public void assignValue() {
        String variableName = parser.getCurrentToken().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);
        parser.eat(Token.TokenType.OPERATOR);

        Object value = parser.calc();
        parser.getVariableValues().put(variableName, value);
        parser.eat(Token.TokenType.DELIMITER);
    }
}
