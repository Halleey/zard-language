package editor.nodes;

import nodeVariables.TypedValue;

import java.util.Map;
public class BinaryOpNode extends ASTNode {
    private final ASTNode left;
    private final String operator;
    private final ASTNode right;

    public BinaryOpNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object evaluate(Map<String, TypedValue> variables) {
        Object leftVal = unwrap(left.evaluate(variables));
        Object rightVal = unwrap(right.evaluate(variables));

        if (leftVal instanceof Number && rightVal instanceof Number) {
            double l = ((Number) leftVal).doubleValue();
            double r = ((Number) rightVal).doubleValue();
            return switch (operator) {
                case "+" -> l + r;
                case "-" -> l - r;
                case "*" -> l * r;
                case "/" -> l / r;
                case "==" -> l == r;
                case "!=" -> l != r;
                case "<" -> l < r;
                case ">" -> l > r;
                case "<=" -> l <= r;
                case ">=" -> l >= r;
                default -> throw new RuntimeException("Operador desconhecido: " + operator);
            };
        } else if (operator.equals("+") && (leftVal instanceof String || rightVal instanceof String)) {
            return leftVal.toString() + rightVal.toString();
        }

        throw new RuntimeException("Operação inválida entre tipos diferentes");
    }

    private Object unwrap(Object value) {
        return (value instanceof TypedValue) ? ((TypedValue) value).getValue() : value;
    }
}
