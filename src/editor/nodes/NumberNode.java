package editor.nodes;

import nodeVariables.TypedValue;

import java.util.Map;
public class NumberNode extends ASTNode {
    private final double value;

    public NumberNode(double value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Map<String, TypedValue> variables) {
        return new TypedValue("double", value);
    }
}
