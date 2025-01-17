package editor.expressions;

import editor.translate.Token;
import editor.translate.Parser;
import java.util.HashMap;
import java.util.Map;

public class ExpressionStatement {
    private final Parser parser;

    public ExpressionStatement(Parser parser) {
        this.parser = parser;
    }

    public Object expression() {
        Object result = term();

        while (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                (parser.getCurrentToken().getValue().equals("==") ||
                        parser.getCurrentToken().getValue().equals("!=") ||
                        parser.getCurrentToken().getValue().equals("<") ||
                        parser.getCurrentToken().getValue().equals(">") ||
                        parser.getCurrentToken().getValue().equals("<=") ||
                        parser.getCurrentToken().getValue().equals(">="))) {

            String operator = parser.getCurrentToken().getValue();
            parser.eat(Token.TokenType.OPERATOR);
            Object right = term();

            if (result instanceof Number && right instanceof Number) {
                double leftValue = ((Number) result).doubleValue();
                double rightValue = ((Number) right).doubleValue();
                result = switch (operator) {
                    case "==" -> leftValue == rightValue;
                    case "!=" -> leftValue != rightValue;
                    case "<" -> leftValue < rightValue;
                    case ">" -> leftValue > rightValue;
                    case "<=" -> leftValue <= rightValue;
                    case ">=" -> leftValue >= rightValue;
                    default -> throw new RuntimeException("Operador de comparação desconhecido: " + operator);
                };
            } else if (result instanceof Boolean && right instanceof Boolean) {
                boolean leftValue = (Boolean) result;
                boolean rightValue = (Boolean) right;
                result = switch (operator) {
                    case "==" -> leftValue == rightValue;
                    case "!=" -> leftValue != rightValue;
                    default -> throw new RuntimeException("Operadores de comparação suportados para booleanos: ==, !=");
                };
            } else {
                throw new RuntimeException("Erro de sintaxe: operadores de comparação são suportados apenas para números e booleanos");
            }
        }
        return result;
    }

    public Object term() {
        Object result = factor();

        while (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                (parser.getCurrentToken().getValue().equals("*") || parser.getCurrentToken().getValue().equals("/"))) {
            String operator = parser.getCurrentToken().getValue();
            parser.eat(Token.TokenType.OPERATOR);
            Object right = factor();

            if (operator.equals("*")) {
                result = ((Number) result).doubleValue() * ((Number) right).doubleValue();
            } else if (operator.equals("/")) {
                result = ((Number) result).doubleValue() / ((Number) right).doubleValue();
            }
        }
        return result;
    }

    public Object factor() {
        Token token = parser.getCurrentToken();
        switch (token.getType()) {
            case IDENTIFIER:
                parser.advance();
                // Suporte para acessar elementos de um mapa (ex.: mapa[chave])
                if (parser.getCurrentToken().getValue().equals("[")) {
                    parser.advance(); // Avança o '['
                    Object key = expression(); // Resolve a chave
                    if (!parser.getCurrentToken().getValue().equals("]")) {
                        throw new RuntimeException("Erro de sintaxe: esperado ']' mas encontrado " + parser.getCurrentToken());
                    }
                    parser.advance(); // Avança o ']'
                    Map<?, ?> map = (Map<?, ?>) parser.getVariableValues().get(token.getValue());
                    if (map == null) {
                        throw new RuntimeException("Erro: mapa '" + token.getValue() + "' não encontrado.");
                    }
                    return map.get(key);
                }
                return parser.getVariableValues().get(token.getValue());
            case NUMBER:
                parser.advance();
                if (token.getValue().contains(".")) {
                    return Double.valueOf(token.getValue());
                } else {
                    return Integer.valueOf(token.getValue());
                }
            case STRING:
                parser.advance();
                return token.getValue();
            case DELIMITER:
                if (token.getValue().equals("(")) {
                    parser.advance();
                    Object result = expression();
                    if (!parser.getCurrentToken().getValue().equals(")")) {
                        throw new RuntimeException("Erro de sintaxe: esperado ')' mas encontrado " + parser.getCurrentToken());
                    }
                    parser.advance();
                    return result;
                }
                // Suporte para inicialização de mapas
                if (token.getValue().equals("{")) {
                    parser.advance();
                    Map<Object, Object> map = new HashMap<>();
                    while (!parser.getCurrentToken().getValue().equals("}")) {
                        Object key = expression();
                        if (!parser.getCurrentToken().getValue().equals(":")) {
                            throw new RuntimeException("Erro de sintaxe: esperado ':' após a chave.");
                        }
                        parser.advance(); // Avança o ':'
                        Object value = expression();
                        map.put(key, value);

                        if (!parser.getCurrentToken().getValue().equals("}")) {
                            if (parser.getCurrentToken().getValue().equals(",")) {
                                parser.advance(); // Avança a vírgula
                            } else {
                                throw new RuntimeException("Erro de sintaxe: esperado ',' ou '}' mas encontrado " + parser.getCurrentToken());
                            }
                        }
                    }
                    parser.advance(); // Avança o '}'
                    return map;
                }
                break;
            case BOOLEAN:
                parser.advance();
                return Boolean.valueOf(token.getValue());
            default:
                throw new RuntimeException("Erro de sintaxe: esperado IDENTIFIER, NUMBER, STRING ou '(' mas encontrado " + token.getType());
        }
        return null;
    }

    public Object calc() {
        Object result = term();
        while (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR &&
                (parser.getCurrentToken().getValue().equals("+") || parser.getCurrentToken().getValue().equals("-"))) {
            String operator = parser.getCurrentToken().getValue();
            parser.eat(Token.TokenType.OPERATOR);
            Object right = term();

            if (result instanceof Integer && right instanceof Integer) {
                if (operator.equals("+")) {
                    result = ((Integer) result) + ((Integer) right);
                } else if (operator.equals("-")) {
                    result = ((Integer) result) - ((Integer) right);
                }
            } else if (result instanceof Double || right instanceof Double) {
                if (operator.equals("+")) {
                    result = ((Number) result).doubleValue() + ((Number) right).doubleValue();
                } else if (operator.equals("-")) {
                    result = ((Number) result).doubleValue() - ((Number) right).doubleValue();
                }
            } else {
                throw new RuntimeException("Erro de sintaxe: operações aritméticas suportadas apenas para números");
            }
        }
        return result;
    }
}
