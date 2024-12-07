package editor.translate;

import editor.*;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser extends  GlobalClass {
    private final List<Token> tokens;
    private int pos;
    private Token currentToken;
    private final Map<String, Object> variableValues;
    private boolean mainFound;
    private int whilePosition;
    private JTextArea logArea;

    public Parser(List<Token> tokens, JTextArea logArea) {
        this.tokens = tokens;
        this.pos = 0;
        this.currentToken = tokens.get(pos);
        this.variableValues = new HashMap<>();
        this.mainFound = false;
        this.logArea = logArea;

    }

    public void log(String message) {
        if (logArea != null) {
            logArea.append(message + "\n");
        }
    }
    public void setCurrentToken(Token currentToken) {
        this.currentToken = currentToken;
    }

    public void backToWhile() {
        while (pos >= 0) {
            if (currentToken.getType() == Token.TokenType.KEYWORD &&
                    "while".equals(currentToken.getValue())) {
                whilePosition = pos;
                log("Encontrado o token 'while': " + currentToken);
                return;
            }
            back();
        }

        log("Token 'while' não encontrado. Chegou ao início.");
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.currentToken = tokens.get(pos);
        this.variableValues = new HashMap<>();
        this.mainFound = false;
    }

    public Token getCurrentToken() {

        return currentToken;

    }

    public Map<String, Object> getVariableValues() {
        return variableValues;
    }

    public Token peekNextToken() {
        int nextPos = pos + 1;
        if (nextPos < tokens.size()) {
            return tokens.get(nextPos);
        }
        return null; // Retorna null se não houver próximo token
    }

    public void advance() {
        pos++;
        if (pos < tokens.size()) {
            // System.out.println("Token current for debug "+ getCurrentToken().getValue());
            currentToken = tokens.get(pos);
        } else {
            currentToken = new Token(Token.TokenType.EOF, "");
        }
    }

    public void back() {
        pos--;
        if (pos >= 0) {
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
        log("Parsing block start");
        while (currentToken.getType() != Token.TokenType.DELIMITER || !currentToken.getValue().equals("}")) {
            if(currentToken.getValue().equals("return")) {
                setFoundReturn(true);
                System.out.println("condition is " + isFoundReturn());
                return;
            }
            log("Inside block, current token: " + currentToken);
            statement();
            if (currentToken.getType() == Token.TokenType.EOF) {
                System.out.println("LAST CHAR" + getCurrentToken());
                throw new RuntimeException("Erro de sintaxe: bloco não terminado");
            }
        }
        log("End of block");
        eat(Token.TokenType.DELIMITER); // Consome '}'
        log("Post-block token: " + currentToken);
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
                return switch (operator) {
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
                return switch (operator) {
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

    public Object factor() {
        Token token = currentToken;
        switch (token.getType()) {
            case IDENTIFIER:
                advance();
                return variableValues.get(token.getValue());
            case NUMBER:
                advance();
                if (token.getValue().contains(".")) {
                    return Double.valueOf(token.getValue());
                } else {
                    return Integer.valueOf(token.getValue());
                }
            case STRING:
                advance();
                return token.getValue();
            case DELIMITER:
                if (token.getValue().equals("(")) {
                    advance();
                    Object result = expression();
                    if (!currentToken.getValue().equals(")")) {
                        throw new RuntimeException("Erro de sintaxe: esperado ')' mas encontrado " + currentToken);
                    }
                    advance();
                    return result;
                }
                break;
            case BOOLEAN:
                advance();
                return Boolean.valueOf(token.getValue());
            default:
                throw new RuntimeException("Erro de sintaxe: esperado IDENTIFIER, NUMBER, STRING ou '(' mas encontrado " + token.getType());
        }
        return null;
    }

    public Object calc() {
        Object result = term();
        while (currentToken.getType() == Token.TokenType.OPERATOR &&
                (currentToken.getValue().equals("+") || currentToken.getValue().equals("-"))) {
            String operator = currentToken.getValue();
            eat(Token.TokenType.OPERATOR);
            Object right = term();
            if (result instanceof Number && right instanceof Number) {
                if (result instanceof Double || right instanceof Double) {
                    if (operator.equals("+")) {
                        result = ((Number) result).doubleValue() + ((Number) right).doubleValue();
                    } else if (operator.equals("-")) {
                        result = ((Number) result).doubleValue() - ((Number) right).doubleValue();
                    }
                } else {
                    if (operator.equals("+")) {
                        result = ((Integer) result) + ((Integer) right);
                    } else if (operator.equals("-")) {
                        result = ((Integer) result) - ((Integer) right);
                    }
                }
            } else {
                throw new RuntimeException("Erro de sintaxe: operações aritméticas suportadas apenas para números");
            }
        }
        return result;
    }

    public void statement() {
       // System.out.println("Current token in statement: " + currentToken);

        if (currentToken.getType() == Token.TokenType.KEYWORD) {
            switch (currentToken.getValue()) {
                case "print":
                    log("Executing print statement.");
                    System.out.println("Executing print statement.");
                    new PrintStatement(this).execute();
                    break;
                case "int":
                case "double":
                case "string":
                case "boolean":
                    log("Executing variable statement.");
                    new VariableStatement(this).execute();
                    break;
                case "main":
                    log("Executing main statement.");
                    new MainStatement(this).execute();
                    mainFound = true;
                    break;
                case "if":
                case "else if":
                case "else":
                    log("Executing if/else statement.");
                    new IfStatement(this).execute();
                    break;
                case "input":
                    System.out.println("Executing input statement.");
                    new InputStatement(this).execute();
                    break;
                case "while":
                    log("Executing while statement.");
                    new WhileStatement(this).execute();
                    break;
                case "function":
                    log("Defining function.");
                    new FunctionStatement(this).definirFuncao();
                    break;
                case "call":
                    advance();
                    String functionName = getCurrentToken().getValue(); // Captura o nome da função
                    log("INVOC FUNCTION: " + functionName);
                    advance();
                    if (!getCurrentToken().getValue().equals("(")) {
                        throw new RuntimeException("Erro de sintaxe: esperado '(' mas encontrado " + getCurrentToken().getValue());
                    }
                    eat(Token.TokenType.DELIMITER);
                    List<Object> argumentos = new ArrayList<>();
                    while (!(getCurrentToken().getType() == Token.TokenType.DELIMITER && getCurrentToken().getValue().equals(")"))) {
                        if (getCurrentToken().getType() == Token.TokenType.DELIMITER && getCurrentToken().getValue().equals(",")) {
                            advance(); // Ignora a vírgula e passa para o próximo token
                            continue;
                        }
                        if (getCurrentToken().getType() == Token.TokenType.STRING || getCurrentToken().getType() == Token.TokenType.IDENTIFIER) {
                            argumentos.add(getCurrentToken().getValue());
                        } else {
                            System.out.println("Token inesperado ao processar argumentos: " + getCurrentToken());
                        }
                        advance();
                    }

                    eat(Token.TokenType.DELIMITER); // Consome ')'
                    FunctionStatement func = FunctionStatement.getFunction(functionName);
                    log(" Função encontrada: " + functionName);
                    if (func != null) {
                        func.consumir(argumentos);
                    } else {
                        throw new RuntimeException("Função não encontrada: " + functionName);
                    }
                    advance();
                    break;
                case "return":
                    log("Processing return statement.");
                    break;
                case "}":
                case ";":
                    log("Advancing through delimiter: " + currentToken.getValue());
                    advance();
                    break;
                default:
                    throw new RuntimeException("Erro de sintaxe: declaração inesperada " + currentToken);
            }
        }

        else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            // Processa identificadores e atribuições
            String variableName = currentToken.getValue();
            log("Processing identifier: " + variableName);
            advance();
            System.out.println("Processing : " + getCurrentToken());
            if (currentToken.getType() == Token.TokenType.OPERATOR &&
                    currentToken.getValue().equals("=")) {
                new VariableStatement(this).atribuir(variableName);
            } else if (currentToken.getType() == Token.TokenType.OPERATOR &&
                    (currentToken.getValue().equals("++") || currentToken.getValue().equals("--"))) {
                // Processa operadores de incremento/decremento
                String operator = currentToken.getValue();
                log("Processing operator: " + operator);
                advance();

                if (variableValues.containsKey(variableName)) {
                    Number value = (Number) variableValues.get(variableName);
                    if (operator.equals("++")) {
                        value = (value instanceof Double) ? value.doubleValue() + 1.0 : value.intValue() + 1;
                    } else if (operator.equals("--")) {
                        value = (value instanceof Double) ? value.doubleValue() - 1.0 : value.intValue() - 1;
                    }
                    variableValues.put(variableName, value);
                } else {
                    throw new RuntimeException("Variável não declarada: " + variableName);
                }
            } else {
                // Atribui valor padrão se não for um operador
                System.out.println("Assigning value to variable.");
                new VariableStatement(this).assignValue();
            }
        } else if (currentToken.getType() == Token.TokenType.DELIMITER &&
                (currentToken.getValue().equals("}") || currentToken.getValue().equals(";"))) {
            log("Advancing through delimiter: " + currentToken.getValue());
            advance();
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado KEYWORD ou IDENTIFIER mas encontrado " + currentToken.getType() + " " + currentToken.getValue());
        }
    }

    public void parse() {

        while (currentToken.getType() != Token.TokenType.EOF) {
            if (currentToken.getValue().equals("main")) {
                System.out.println(getCurrentToken());

                statement();
            } else {
                advance();
            }
        }
        if (!mainFound) {
            log("main method not definied");
            throw new RuntimeException("Main method not defined");
        }
    }

    public static void main(String[] args) {
        try {
            String input = new String(Files.readAllBytes(Paths.get("src/editor/test.zd")));
            Lexer lexer = new Lexer(input);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);

            parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
