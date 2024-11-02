package editor;

import editor.translate.Parser;
import java.util.HashMap;
import java.util.Map;

public class VariableStatement {
    private Parser parser;
    private static final Map<String, String> variableTypes = new HashMap<>(); // Mapeia variáveis para seus tipos

    public VariableStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        String type = parser.getCurrentToken().getValue();
        parser.eat(Token.TokenType.KEYWORD);
        String variableName = parser.getCurrentToken().getValue();
        parser.eat(Token.TokenType.IDENTIFIER);

        variableTypes.put(variableName, type); // Armazena o tipo da variável

        if (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                parser.getCurrentToken().getValue().equals("=")) {
            parser.eat(Token.TokenType.OPERATOR);
            Object value = parser.calc();
            validateValueType(variableName, value); // Valida o tipo do valor
            parser.getVariableValues().put(variableName, value);

        } else if (parser.getCurrentToken().getType() == Token.TokenType.IDENTIFIER) {
            atribuir(variableName);  // Passa o nome da variável para o método atribuir
        } else {
            parser.getVariableValues().put(variableName, getDefault(type));
        }

        parser.eat(Token.TokenType.DELIMITER);
        parser.log("Declaração de variável: Tipo " + type + ", Nome: " + variableName);
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

    private void validateValueType(String variableName, Object value) {
        String variableType = variableTypes.get(variableName);

        if ("int".equals(variableType) && !(value instanceof Integer)) {
            parser.log("Erro de tipo: Esperado int mas encontrado " + value.getClass().getSimpleName());
            throw new RuntimeException("Erro de tipo: Esperado int mas encontrado " + value.getClass().getSimpleName());
        }
        else if ("double".equals(variableType) && !(value instanceof Double)) {
            parser.log("Erro de tipo: Esperado double mas encontrado " + value.getClass().getSimpleName());
            throw new RuntimeException("Erro de tipo: Esperado double mas encontrado " + value.getClass().getSimpleName());
        }
        else if ("string".equals(variableType) && !(value instanceof String)) {
            parser.log("Erro de tipo: Esperado string mas encontrado " + value.getClass().getSimpleName());
            throw new RuntimeException("Erro de tipo: Esperado string mas encontrado " + value.getClass().getSimpleName());
        }
        else if ("boolean".equals(variableType) && !(value instanceof Boolean)) {
            parser.log("Erro de tipo: Esperado boolean mas encontrado " + value.getClass().getSimpleName());
            throw new RuntimeException("Erro de tipo: Esperado boolean mas encontrado " + value.getClass().getSimpleName());
        }
    }

    public void atribuir(String variableName) {
        if (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                parser.getCurrentToken().getValue().equals("=")) {
            parser.eat(Token.TokenType.OPERATOR);

            Object value = parser.calc();
            validateValueType(variableName, value); // Valida o tipo do valor

            if (parser.getVariableValues().containsKey(variableName)) {
                parser.getVariableValues().put(variableName, value);
            } else {
                throw new RuntimeException("Variável não declarada: " + variableName);
            }

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
