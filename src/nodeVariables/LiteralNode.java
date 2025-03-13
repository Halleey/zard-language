package nodeVariables;

import editor.nodes.ASTNode;

import java.util.Map;

public class LiteralNode extends ASTNode {
    private Object value;

    public LiteralNode(Object value) {
        this.value = value;
    }
    @Override
    public Object evaluate(Map<String, TypedValue> variables) {
        return value;
    }
}
