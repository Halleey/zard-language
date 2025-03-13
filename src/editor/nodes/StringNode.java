package editor.nodes;

import nodeVariables.TypedValue;

import java.util.Map;

public class StringNode extends ASTNode {
    private final String value;

    public StringNode(String value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Map<String, TypedValue> variables) {
        return new TypedValue("string", value);
    }
}

