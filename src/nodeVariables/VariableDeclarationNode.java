package nodeVariables;

import editor.nodes.ASTNode;

import java.util.Map;

public class VariableDeclarationNode extends ASTNode {
    private String type;
    private String name;
    private ASTNode value;

    public VariableDeclarationNode(String type, String name, ASTNode value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    public Object evaluate(Map<String, TypedValue> variables) {
        Object evaluatedValue = (value != null) ? value.evaluate(variables) : getDefaultValue(type);
        if (!isValidType(type, evaluatedValue)) {
            throw new RuntimeException("Erro de tipo: esperado '" + type + "', mas recebeu '" + evaluatedValue + "'");
        }
        variables.put(name, new TypedValue(type, evaluatedValue));
        return evaluatedValue;
    }

    private static Object getDefaultValue(String type) {
        switch (type) {
            case "int": return 0;
            case "double": return 0.0;
            case "string": return "";
            case "boolean": return false;
            default: throw new RuntimeException("Tipo desconhecido: " + type);
        }
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
