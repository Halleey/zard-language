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
                default:
                    throw new RuntimeException("Erro de sintaxe: declaração inesperada " + currentToken);
            }
        } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            atribuirValor(); // Processa atribuições
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado KEYWORD ou IDENTIFIER mas encontrado " + currentToken.getType());
        }
    }

    private void printStatement() {
        eat(Token.TokenType.KEYWORD); // Consome 'print'
        eat(Token.TokenType.DELIMITER); // Consome '('

        StringBuilder result = new StringBuilder();
        while (currentToken.getType() != Token.TokenType.DELIMITER) { // Enquanto não encontrar ')'
            if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
                String identifier = currentToken.getValue();
                eat(Token.TokenType.IDENTIFIER);
                result.append(variableValues.getOrDefault(identifier, identifier));
            } else if (currentToken.getType() == Token.TokenType.STRING) {
                String str = currentToken.getValue();
                eat(Token.TokenType.STRING);
                result.append(str);
            } else {
                throw new RuntimeException("Erro de sintaxe: esperado IDENTIFIER ou STRING mas encontrado " + currentToken.getType());
            }
            if (currentToken.getType() == Token.TokenType.OPERATOR) {
                String operator = currentToken.getValue();
                eat(Token.TokenType.OPERATOR);
                if (!operator.equals("+")) {
                    throw new RuntimeException("Erro de sintaxe: esperado operador '+' mas encontrado " + operator);
                }
            }
        }

        eat(Token.TokenType.DELIMITER); // Consome ')'
        eat(Token.TokenType.DELIMITER); // Consome ';'

        System.out.println(result.toString()); // Imprime o valor concatenado
    }

    private void variableDeclaration() {
        String type = currentToken.getValue();
        eat(Token.TokenType.KEYWORD);
        String variableName = currentToken.getValue();
        eat(Token.TokenType.IDENTIFIER);

        // Se houver um operador de atribuição, processa o valor
        if (currentToken.getType() == Token.TokenType.OPERATOR && currentToken.getValue().equals("=")) {
            eat(Token.TokenType.OPERATOR);
            Object value = expression();
            variableValues.put(variableName, value);
        } else {
            variableValues.put(variableName, getDefault(type));
        }

        // Consome o delimitador ';' após a declaração
        eat(Token.TokenType.DELIMITER);

        System.out.println("Declaração de variável: Tipo " + type + ", Nome: " + variableName);
    }

    private void atribuirValor() {
        String variableName = currentToken.getValue(); // Nome da variável
        eat(Token.TokenType.IDENTIFIER); // Consome o nome da variável
        eat(Token.TokenType.OPERATOR); // Consome o operador '='

        Object value = expression(); // Processa a expressão à direita da atribuição
        variableValues.put(variableName, value); // Armazena o valor da variável
        eat(Token.TokenType.DELIMITER); // Consome o delimitador ';'
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

    private Object expression() {
        if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            String identifier = currentToken.getValue();
            eat(Token.TokenType.IDENTIFIER);
            return variableValues.getOrDefault(identifier, identifier); // Retorna o valor da variável ou o identificador
        } else if (currentToken.getType() == Token.TokenType.NUMBER) {
            String number = currentToken.getValue();
            eat(Token.TokenType.NUMBER);
            return Integer.parseInt(number); // Converte para inteiro
        } else if (currentToken.getType() == Token.TokenType.STRING) {
            String str = currentToken.getValue();
            eat(Token.TokenType.STRING);
            return str; // Retorna a string
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado IDENTIFIER, NUMBER ou STRING mas encontrado " + currentToken.getType());
        }
    }

    public void parse() {
        while (currentToken.getType() != Token.TokenType.EOF) {
            statement(); // Processa instruções enquanto não for EOF
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