package editor.variables;

import java.util.Map;

public class VariableManager {
    private Map<String, Object> variableValues;

    public VariableManager(Map<String, Object> variableValues) {
        this.variableValues = variableValues;
    }

    public void handleIncrementDecrement(String identifier, String operator) {
        if (variableValues.containsKey(identifier)) {
            Object value = variableValues.get(identifier);

            if (value instanceof Integer) {
                int intValue = (int) value;
                if (operator.equals("++")) {
                    intValue++; // Incrementa
                } else if (operator.equals("--")) {
                    intValue--; // Decrementa
                }
                variableValues.put(identifier, intValue);
                System.out.println("[DEBUG] Variável " + identifier + " atualizada para: " + intValue);
            } else if (value instanceof Double) {
                double doubleValue = (double) value;
                if (operator.equals("++")) {
                    doubleValue += 1.0; // Incrementa
                } else if (operator.equals("--")) {
                    doubleValue -= 1.0; // Decrementa
                }
                variableValues.put(identifier, doubleValue);
                System.out.println("[DEBUG] Variável " + identifier + " atualizada para: " + doubleValue);
            } else {
                throw new RuntimeException("Tipo de variável incompatível para incremento/decremento: " + identifier);
            }
        } else {
            throw new RuntimeException("Variável não declarada: " + identifier);
        }
    }
}
