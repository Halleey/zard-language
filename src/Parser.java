import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Parser {
    private List<Token> tokens;
    private int pos;
    private Token currentToken;
    private Map<String, Object> variableValues;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.currentToken = tokens.get(pos);
        this.variableValues = new HashMap<>();
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public Map<String, Object> getVariableValues() {
        return variableValues;
    }

    public void advance() {
        pos++;
        if (pos < tokens.size()) {
            currentToken = tokens.get(pos);
        } else {
            currentToken = new Token(Token.TokenType.EOF, "");
        }
    }

    public void eat(Token.TokenType type) {
        if (currentToken.getType() == type) {
            advance();
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado " + type + " mas encontrado " + currentToken.getType());
        }
    }

    public void parseBlock() {
        System.out.println("Parsing block start");
        while (currentToken.getType() != Token.TokenType.DELIMITER || !currentToken.getValue().equals("}")) {
            System.out.println("Inside block, current token: " + currentToken);
            statement();
            if (currentToken.getType() == Token.TokenType.EOF) {
                throw new RuntimeException("Erro de sintaxe: bloco não terminado");
            }
        }
        System.out.println("End of block");
        eat(Token.TokenType.DELIMITER); // Consome '}'
        System.out.println("Post-block token: " + currentToken);
    }

    public Object expression() {
        Object result = term();

        while (currentToken.getType() == Token.TokenType.OPERATOR &&
                (currentToken.getValue().equals("==") ||
                        currentToken.getValue().equals("!=") ||
                        currentToken.getValue().equals("<") ||
                        currentToken.getValue().equals(">") ||
                        currentToken.getValue().equals("<=") ||
                        currentToken.getValue().equals(">="))) {
            String operator = currentToken.getValue();
            eat(Token.TokenType.OPERATOR);
            Object right = term();

            if (result instanceof Number && right instanceof Number) {
                double leftValue = ((Number) result).doubleValue();
                double rightValue = ((Number) right).doubleValue();
                switch (operator) {
                    case "==":
                        return leftValue == rightValue;
                    case "!=":
                        return leftValue != rightValue;
                    case "<":
                        return leftValue < rightValue;
                    case ">":
                        return leftValue > rightValue;
                    case "<=":
                        return leftValue <= rightValue;
                    case ">=":
                        return leftValue >= rightValue;
                }
            } else {
                throw new RuntimeException("Erro de sintaxe: operadores de comparação são suportados apenas para números");
            }
        }
        return result;
    }

    private Object term() {
        Object result = factor();

        while (currentToken.getType() == Token.TokenType.OPERATOR &&
                (currentToken.getValue().equals("*") || currentToken.getValue().equals("/"))) {
            String operator = currentToken.getValue();
            eat(Token.TokenType.OPERATOR);
            Object right = factor();

            if (operator.equals("*")) {
                result = ((Number) result).doubleValue() * ((Number) right).doubleValue();
            } else if (operator.equals("/")) {
                result = ((Number) result).doubleValue() / ((Number) right).doubleValue();
            }
        }
        return result;
    }

    private Object factor() {
        if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            String identifier = currentToken.getValue();
            eat(Token.TokenType.IDENTIFIER);
            return variableValues.getOrDefault(identifier, identifier);
        } else if (currentToken.getType() == Token.TokenType.NUMBER) {
            String number = currentToken.getValue();
            eat(Token.TokenType.NUMBER);
            return number.contains(".") ? Double.parseDouble(number) : Integer.parseInt(number);
        } else if (currentToken.getType() == Token.TokenType.STRING) {
            String str = currentToken.getValue();
            eat(Token.TokenType.STRING);
            return str;
        } else if (currentToken.getType() == Token.TokenType.DELIMITER && currentToken.getValue().equals("(")) {
            eat(Token.TokenType.DELIMITER);
            Object result = expression();
            eat(Token.TokenType.DELIMITER);
            return result;
        }
        throw new RuntimeException("Erro de sintaxe: esperado IDENTIFIER, NUMBER, STRING ou '(' mas encontrado " + currentToken.getType());
    }

    public Object calc() {
        Object result = term();

        while (currentToken.getType() == Token.TokenType.OPERATOR &&
                (currentToken.getValue().equals("+") || currentToken.getValue().equals("-"))) {
            String operator = currentToken.getValue();
            eat(Token.TokenType.OPERATOR);
            Object right = term();

            if (operator.equals("+")) {
                result = ((Number) result).doubleValue() + ((Number) right).doubleValue();
            } else if (operator.equals("-")) {
                result = ((Number) result).doubleValue() - ((Number) right).doubleValue();
            }
        }
        return result;
    }

    void statement() {
        if (currentToken.getType() == Token.TokenType.KEYWORD) {
            switch (currentToken.getValue()) {
                case "print":
                    printStatement();
                    break;
                case "int":
                case "double":
                case "string":
                    new VariableStatement(this).execute();
                    break;
                case "if":
                    new IfStatement(this).execute();
                    break;
                case "input":
                    new  InputStatement(this).execute();
                    break;
                case "}":
                    advance();
                case ";":
                    advance();
                default:
                    throw new RuntimeException("Erro de sintaxe: declaração inesperada " + currentToken);
            }
        } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            new VariableStatement(this).assignValue();
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado KEYWORD ou IDENTIFIER mas encontrado " + currentToken.getType());
        }
    }

    private void printStatement() {
        eat(Token.TokenType.KEYWORD); // Consome 'print'
        eat(Token.TokenType.DELIMITER); // Consome '('

        Object result = printExpression(); // Processa a expressão de impressão
        System.out.println(result);

        eat(Token.TokenType.DELIMITER); // Consome ')'
        eat(Token.TokenType.DELIMITER); // Consome ';'
    }

    private Object printExpression() {
        StringBuilder sb = new StringBuilder();

        while (currentToken.getType() != Token.TokenType.DELIMITER || !currentToken.getValue().equals(")")) {
            if (currentToken.getType() == Token.TokenType.STRING) {
                sb.append(currentToken.getValue());
                eat(Token.TokenType.STRING);
            } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
                String identifier = currentToken.getValue();
                sb.append(variableValues.getOrDefault(identifier, identifier));
                eat(Token.TokenType.IDENTIFIER);
            } else if (currentToken.getType() == Token.TokenType.NUMBER) {
                String number = currentToken.getValue();
                sb.append(number);
                eat(Token.TokenType.NUMBER);
            } else if (currentToken.getType() == Token.TokenType.OPERATOR && currentToken.getValue().equals("+")) {
                sb.append(" ");
                eat(Token.TokenType.OPERATOR);
            } else {
                throw new RuntimeException("Erro de sintaxe: esperado STRING, IDENTIFIER, NUMBER ou OPERATOR mas encontrado " + currentToken.getType());
            }

            if (currentToken.getType() == Token.TokenType.DELIMITER && currentToken.getValue().equals(")")) {
                break;
            }
        }

        return sb.toString();
    }

    public void parse() {
        while (currentToken.getType() != Token.TokenType.EOF) {
            statement();
        }
    }

    public static void main(String[] args) {
        try {
            String input = new String(Files.readAllBytes(Paths.get("src/test.zd")));
            Lexer lexer = new Lexer(input);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}