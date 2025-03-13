package editor.nodes;

import nodeVariables.TypedValue;

import java.util.Map;

public abstract class ASTNode {
    public abstract Object evaluate(Map<String, TypedValue> variables);
}
