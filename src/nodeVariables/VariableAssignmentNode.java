package nodeVariables;

import editor.nodes.ASTNode;

import java.util.Map;

public class VariableAssignmentNode extends ASTNode {
    private String name;
    private ASTNode value;

    public VariableAssignmentNode(String name, ASTNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Object evaluate(Map<String, TypedValue> variables) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Erro: variável '" + name + "' não declarada.");
        }

        Object evaluatedValue = value.evaluate(variables);
        TypedValue var = variables.get(name);

        if (!isValidType(var.getType(), evaluatedValue)) {
            throw new RuntimeException("Erro de tipo: esperado '" + var.getType() + "', mas recebeu '" + evaluatedValue + "'");
        }

        var.setValue(evaluatedValue);
        return evaluatedValue;
    }

    private boolean isValidType(String type, Object value) {
        switch (type) {
            case "int": return value instanceof Integer;
            case "double": return value instanceof Double;
            case "string": return value instanceof String;
            case "boolean": return value instanceof Boolean;
            default: return false;
        }
    }
}

