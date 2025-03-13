package editor.nodes;

import nodeVariables.TypedValue;

import java.util.Map;
public class VariableNode extends ASTNode {
    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public Object evaluate(Map<String, TypedValue> variables) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variável não definida: " + name);
        }
        return variables.get(name);
    }
}
