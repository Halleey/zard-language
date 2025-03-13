package nodeVariables;

import editor.nodes.ASTNode;

import java.util.Map;

public class IncrementDecrementNode extends ASTNode {
    private String name;
    private String operator;

    public IncrementDecrementNode(String name, String operator) {
        this.name = name;
        this.operator = operator;
    }
    @Override
    public TypedValue evaluate(Map<String, TypedValue> variables) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variável não declarada: " + name);
        }

        TypedValue typedValue = variables.get(name);

        if (typedValue.getType().equals("int")) {
            int newValue = (operator.equals("++")) ? (int) typedValue.getValue() + 1 : (int) typedValue.getValue() - 1;
            TypedValue updatedValue = new TypedValue("int", newValue);
            variables.put(name, updatedValue);
            return updatedValue;
        } else if (typedValue.getType().equals("double")) {
            double newValue = (operator.equals("++")) ? (double) typedValue.getValue() + 1.0 : (double) typedValue.getValue() - 1.0;
            TypedValue updatedValue = new TypedValue("double", newValue);
            variables.put(name, updatedValue);
            return updatedValue;
        } else {
            throw new RuntimeException("Tipo incompatível para incremento/decremento: " + name);
        }
    }
}
