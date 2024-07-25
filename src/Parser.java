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

    private void advance() {
        pos++;
        if (pos < tokens.size()) {
            currentToken = tokens.get(pos);
        } else {
            currentToken = new Token(Token.TokenType.EOF, "");
        }
    }

    private void eat(Token.TokenType type) {
        if (currentToken.getType() == type) {
            advance();
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado " + type + " mas encontrado " + currentToken.getType());
        }
    }

    private void ifStatement() {
        eat(Token.TokenType.KEYWORD); // Consome 'if'
        eat(Token.TokenType.DELIMITER); // Consome '('
        Object condition = expression(); // Processa a condição
        eat(Token.TokenType.DELIMITER); // Consome ')'
        eat(Token.TokenType.DELIMITER); // Consome '{'

        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Erro de sintaxe: a condição do if deve ser um valor booleano");
        }

        boolean conditionResult = (Boolean) condition;
        if (conditionResult) {
            parseBlock(); // Processa o bloco do if
        } else {
            skipBlock(); // Pula o bloco do if
        }

        if (currentToken.getType() == Token.TokenType.KEYWORD && "else".equals(currentToken.getValue())) {
            eat(Token.TokenType.KEYWORD); // Consome 'else'
            parseBlock(); // Processa o bloco do else
        }
    }

    private void parseBlock() {
        while (currentToken.getType() != Token.TokenType.DELIMITER || !currentToken.getValue().equals("}")) {
            statement();
        }
        eat(Token.TokenType.DELIMITER); // Consome '}'
    }

    private void skipBlock() {
        int openBraces = 0;
        while (currentToken.getType() != Token.TokenType.DELIMITER || !currentToken.getValue().equals("}")) {
            if (currentToken.getType() == Token.TokenType.DELIMITER && currentToken.getValue().equals("{")) {
                openBraces++;
            }
            if (currentToken.getType() == Token.TokenType.DELIMITER && currentToken.getValue().equals("}")) {
                if (openBraces == 0) {
                    break;
                }
                openBraces--;
            }
            advance();
        }
        eat(Token.TokenType.DELIMITER); // Consome '}'
    }

    private Object expression() {
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
            eat(Token.TokenType.DELIMITER); // Consome '('
            Object result = expression(); // Avalia a expressão entre parênteses
            eat(Token.TokenType.DELIMITER); // Consome ')'
            return result;
        }
        throw new RuntimeException("Erro de sintaxe: esperado IDENTIFIER, NUMBER, STRING ou '(' mas encontrado " + currentToken.getType());
    }

    private Object calc() {
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

    private void statement() {
        if (currentToken.getType() == Token.TokenType.KEYWORD) {
            switch (currentToken.getValue()) {
                case "print":
                    printStatement();
                    break;
                case "int":
                case "double":
                case "string":
                    variableDeclaration();
                    break;
                case "if":
                    ifStatement();
                    break;
                default:
                    throw new RuntimeException("Erro de sintaxe: declaração inesperada " + currentToken);
            }
        } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            atribuirValor();
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

    private void variableDeclaration() {
        String type = currentToken.getValue();
        eat(Token.TokenType.KEYWORD);
        String variableName = currentToken.getValue();
        eat(Token.TokenType.IDENTIFIER);

        if (currentToken.getType() == Token.TokenType.OPERATOR && currentToken.getValue().equals("=")) {
            eat(Token.TokenType.OPERATOR);
            Object value = calc(); // Processa a expressão para obter o valor da variável
            variableValues.put(variableName, value);
        } else {
            variableValues.put(variableName, getDefault(type));
        }

        eat(Token.TokenType.DELIMITER);
        System.out.println("Declaração de variável: Tipo " + type + ", Nome: " + variableName);
    }

    private void atribuirValor() {
        String variableName = currentToken.getValue();
        eat(Token.TokenType.IDENTIFIER);
        eat(Token.TokenType.OPERATOR);

        Object value = calc(); // Processa a expressão para atribuição
        variableValues.put(variableName, value);
        eat(Token.TokenType.DELIMITER);
    }

    private Object getDefault(String type) {
        switch (type) {
            case "int":
                return 0;
            case "double":
                return 0.0;
            case "string":
                return "";
            default:
                throw new RuntimeException("Tipo desconhecido: " + type);
        }
    }

    public void parse() {
        while (currentToken.getType() != Token.TokenType.EOF) {
            statement();
        }
    }

    public static void main(String[] args) {
        String filePath = "src/test.zd";
        Lexer lexer = new Lexer(readFile(filePath));
        List<Token> tokens = lexer.tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
        Parser parser = new Parser(tokens);
        parser.parse();
        System.out.println("Parsing completed successfully.");
    }

    private static String readFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
    }
}