
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String input; // Armazena a entrada do código fonte
    private int pos = 0; // Posição atual no input
    private char currentChar; // Caractere atual sendo analisado

    // Construtor do Lexer
    public Lexer(String input) {
        this.input = input;
        this.currentChar = input.charAt(pos); // Inicializa o caractere atual
    }


    private void error() {
        if (pos < input.length()) {
            throw new RuntimeException("Error parsing input at position " + pos + ": " + input.charAt(pos));
        } else {
            throw new RuntimeException("Error parsing input at position " + pos + ": EOF");
        }
    }

    // Método para avançar para o próximo caractere
    private void advance() {
        pos++; // Avança para o próximo caractere
        if (pos > input.length() - 1) { // Se a posição atual for maior que o último índice da entrada
            currentChar = '\0'; // Define o caractere atual como '\0' para indicar o fim da entrada
        } else {
            currentChar = input.charAt(pos); // Atualiza o caractere atual para o próximo caractere na entrada
        }
    }

    // Método para ignorar espaços em branco
    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance(); // Avança até o próximo caractere não branco
        }
    }

    // Método para ler identificadores e palavras-chave
    private Token readIdentifier() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            result.append(currentChar);
            advance();
        }
        String identifier = result.toString();
        switch (identifier) {
            case "int", "string", "double", "print", "if", "else","else if","input" :
                return new Token(Token.TokenType.KEYWORD, identifier);
            default:
                return new Token(Token.TokenType.IDENTIFIER, identifier);
        }
    }

    private Token readOperator() {
        StringBuilder result = new StringBuilder();
        result.append(currentChar);
        advance();


        if ((result.toString().equals("=") && currentChar == '=') ||
                (result.toString().equals("<") && (currentChar == '=' || currentChar == '>')) ||
                (result.toString().equals(">") && currentChar == '=')) {
            result.append(currentChar);
            advance();
        }
        return new Token(Token.TokenType.OPERATOR, result.toString());
    }

    private Token readNumber() {
        StringBuilder result = new StringBuilder();
        boolean hasDecimalPoint = false;

        while (currentChar != '\0' && (Character.isDigit(currentChar) || currentChar == '.')) {
            if (currentChar == '.') {
                if (hasDecimalPoint) {
                    throw new RuntimeException("Número inválido com mais de um ponto decimal");
                }
                hasDecimalPoint = true;
            }
            result.append(currentChar);
            advance();
        }
        return new Token(Token.TokenType.NUMBER, result.toString());
    }

    private Token readString() {
        StringBuilder result = new StringBuilder();
        advance();
        while (currentChar != '\0' && currentChar != '"') {
            result.append(currentChar);
            advance();
        }
        advance();
        return new Token(Token.TokenType.STRING, result.toString());
    }

    // Método principal para tokenizar a entrada
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }
            if (currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}' || currentChar == ';') {
                tokens.add(new Token(Token.TokenType.DELIMITER, Character.toString(currentChar)));
                advance();
                continue;
            }
            if (currentChar == '"') {
                tokens.add(readString());
                continue;
            }
            if (Character.isLetter(currentChar)) {
                tokens.add(readIdentifier());
                continue;
            }
            if (Character.isDigit(currentChar)) {
                tokens.add(readNumber());
                continue;
            }
            if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/') {
                tokens.add(readOperator());
                continue;
            }
            if (currentChar == '=' || currentChar == '>' || currentChar == '<') {
                tokens.add(readOperator());
                continue;
            }
            error(); // Lança um erro se o caractere não for reconhecido
        }
        tokens.add(new Token(Token.TokenType.EOF, ""));
        return tokens;
    }


    public static void main(String[] args) {
        String input = "int a = 2; if (3 > 2) { print(true); }";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
    }
}