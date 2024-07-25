
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

    // Método para lançar uma exceção quando ocorrer um erro de análise
    private void error() {
        throw new RuntimeException("Error parsing input at position " + pos);
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
            result.append(currentChar); // Adiciona o caractere ao resultado
            advance(); // Avança para o próximo caractere
        }
        String identifier = result.toString();
        switch (identifier) {
            case "int", "string", "double", "print":
                return new Token(Token.TokenType.KEYWORD, identifier);
            default:
                return new Token(Token.TokenType.IDENTIFIER, identifier);
        }
    }

    private Token readOperator() {
        StringBuilder result = new StringBuilder();
        result.append(currentChar);
        advance();

        if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' ||currentChar == '=') {
            result.append(currentChar);
            advance();
        }
        return new Token(Token.TokenType.OPERATOR, result.toString());
    }

    // Método para ler números
    private Token readNumber() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            result.append(currentChar);
            advance();
        }
        return new Token(Token.TokenType.NUMBER, result.toString());
    }

    private Token readString() {
        StringBuilder result = new StringBuilder();
        advance(); // Pular o caractere de abertura "
        while (currentChar != '\0' && currentChar != '"') {
            result.append(currentChar);
            advance();
        }
        advance(); // Pular o caractere de fechamento "
        return new Token(Token.TokenType.STRING, result.toString());
    }

    // Método principal para tokenizar a entrada
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace(); // Ignora espaços em branco
                continue;
            }
            if (currentChar == '(' || currentChar == ')' || currentChar == ';') {
                tokens.add(new Token(Token.TokenType.DELIMITER, Character.toString(currentChar))); // Adiciona delimitadores
                advance();
                continue;
            }
            if (currentChar == '"') {
                tokens.add(readString()); // Lê uma string
                continue;
            }
            if (Character.isLetter(currentChar)) {
                tokens.add(readIdentifier()); // Lê um identificador ou palavra-chave
                continue;
            }
            if(currentChar == '=') {
                tokens.add(readOperator());
                continue;
            }
            if (Character.isDigit(currentChar)) {
                tokens.add(readNumber()); // Lê um número
                continue;
            }
            error(); // Lança um erro se o caractere não for reconhecido
        }
        tokens.add(new Token(Token.TokenType.EOF, "")); // Adiciona um token de fim de arquivo
        return tokens;
    }
}
