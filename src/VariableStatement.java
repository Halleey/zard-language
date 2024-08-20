import java.util.HashMap;
import java.util.Map;

public class VariableStatement {
    private Parser parser;
    private Map<String, String> variableTypes; // Armazena o tipo das variáveis

    public VariableStatement(Parser parser) {
        this.parser = parser;
        this.variableTypes = new HashMap<>();
    }

    public void execute() {
        String type = parser.getCurrentToken().getValue();
        parser.eat(Token.TokenType.KEYWORD);


        String variableName = parser.getCurrentToken().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);


        variableTypes.put(variableName, type);

        if (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                parser.getCurrentToken().getValue().equals("=")) {
            parser.eat(Token.TokenType.OPERATOR);

            Object value = parser.calc();

            validateType(type, value);
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

    public void validateType(String type, Object value) {
        if (type.equals("int")) {
            if (!(value instanceof Integer)) {
                throw new RuntimeException("Tipo de variável int não pode receber valor: " + value);
            }
        } else if (type.equals("double")) {
            if (!(value instanceof Double)) {
                throw new RuntimeException("Tipo de variável double não pode receber valor: " + value);
            }
        } else if (type.equals("boolean")) {
            if (!(value instanceof Boolean)) {
                throw new RuntimeException("Tipo de variável boolean não pode receber valor: " + value);
            }
        } else if (type.equals("string")) {
            if (!(value instanceof String)) {
                throw new RuntimeException("Tipo de variável string não pode receber valor: " + value);
            }
        } else {
            throw new RuntimeException("Tipo desconhecido: " + type);
        }
    }

    public void assignValue() {

        String variableName = parser.getCurrentToken().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);
        parser.eat(Token.TokenType.OPERATOR);

        Object value = parser.calc();

        String type = getVariableType(variableName);
        validateType(type, value);
        parser.getVariableValues().put(variableName, value);
        parser.eat(Token.TokenType.DELIMITER);
    }

    private String getVariableType(String variableName) {
        String type = variableTypes.get(variableName);
        if (type == null) {
            throw new RuntimeException("Erro: Variável não declarada: " + variableName);
        }
        return type;
    }
}
