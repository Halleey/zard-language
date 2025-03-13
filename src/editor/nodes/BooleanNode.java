package editor.nodes;

import nodeVariables.TypedValue;

import java.util.Map;
public class BooleanNode extends ASTNode {
    private final boolean value;

    public BooleanNode(boolean value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Map<String, TypedValue> variables) {
        return new TypedValue("boolean", value);
    }
}
