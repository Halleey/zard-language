package editor.process;
import editor.translate.Parser;
import editor.translate.Token;
import editor.variables.VariableStatement;

import java.util.Map;

public class IdentifierProcessor {
    private final Parser parser;
    private final Map<String, Object> variableValues;

    public IdentifierProcessor(Parser parser, Map<String, Object> variableValues) {
        this.parser = parser;
        this.variableValues = variableValues;
    }

    public void processIdentifier() {
        System.out.println("[DEBUG] Processando identificador: " + parser.getCurrentToken().getValue());

        String variableName = parser.getCurrentToken().getValue();
        parser.advance(); // Avança para o próximo token

        if (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                parser.getCurrentToken().getValue().equals("=")) {
            // Atribuição de variável
            new VariableStatement(parser).atribuir(variableName);
        } else if (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                (parser.getCurrentToken().getValue().equals("++") || parser.getCurrentToken().getValue().equals("--"))) {
            // Incremento ou decremento
            String operator = parser.getCurrentToken().getValue();
            parser.advance(); // Avança para o próximo token
            if (variableValues.containsKey(variableName)) {
                Object value = variableValues.get(variableName);
                if (value instanceof Integer) {
                    int intValue = (int) value;
                    if (operator.equals("++")) {
                        intValue++;
                    } else if (operator.equals("--")) {
                        intValue--;
                    }
                    variableValues.put(variableName, intValue);
                } else if (value instanceof Double) {
                    double doubleValue = (double) value;
                    if (operator.equals("++")) {
                        doubleValue += 1.0;
                    } else if (operator.equals("--")) {
                        doubleValue -= 1.0;
                    }
                    variableValues.put(variableName, doubleValue);
                } else {
                    throw new RuntimeException("Tipo de variável incompatível para incremento/decremento: " + variableName);
                }
            } else {
                throw new RuntimeException("Variável não declarada: " + variableName);
            }
        } else {
            // Atribui valor padrão
            System.out.println("[DEBUG] Atribuindo valor à variável.");
            new VariableStatement(parser).assignValue();
        }
    }
}
