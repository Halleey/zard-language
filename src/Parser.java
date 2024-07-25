import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
public class Parser {
    private List<Token> tokens;
    private int pos;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.currentToken = tokens.get(pos);
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
        System.out.println("Parsing statement: " + currentToken);
        if (currentToken.getType() == Token.TokenType.KEYWORD) {
            if (currentToken.getValue().equals("print")) {
                eat(Token.TokenType.KEYWORD);
                eat(Token.TokenType.DELIMITER); // (
                expression(); // Processa a expressão dentro dos parênteses
                eat(Token.TokenType.DELIMITER); // )
                eat(Token.TokenType.DELIMITER); // ;
                System.out.println(); // Adiciona uma nova linha após a impressão
            } else if (currentToken.getValue().equals("int") ||
                    currentToken.getValue().equals("double") ||
                    currentToken.getValue().equals("string")) {
                variableDeclaration();
            } else {
                throw new RuntimeException("Erro de sintaxe: declaração inesperada " + currentToken);
            }
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado KEYWORD mas encontrado " + currentToken.getType());
        }
    }

    private void variableDeclaration() {
        String type = currentToken.getValue();
        eat(Token.TokenType.KEYWORD); // Consome o tipo
        String variableName = currentToken.getValue();
        eat(Token.TokenType.IDENTIFIER); // Consome o identificador
        eat(Token.TokenType.DELIMITER); // Consome o delimitador ';'

        // Print para verificar a declaração
        System.out.println("Declaração de variável: Tipo=" + type + ", Nome=" + variableName);
    }

    private void expression() {
        if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            System.out.print(currentToken.getValue()); // Imprime o valor do identificador
            eat(Token.TokenType.IDENTIFIER); // Consome o identificador
        } else if (currentToken.getType() == Token.TokenType.NUMBER) {
            System.out.print(currentToken.getValue()); // Imprime o valor numérico
            eat(Token.TokenType.NUMBER); // Consome o número
        } else if (currentToken.getType() == Token.TokenType.STRING) {
            System.out.print("\"" + currentToken.getValue() + "\""); // Imprime o valor da string
            eat(Token.TokenType.STRING); // Consome a string
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado IDENTIFIER, NUMBER ou STRING mas encontrado " + currentToken.getType());
        }

        while (currentToken.getType() == Token.TokenType.IDENTIFIER ||
                currentToken.getType() == Token.TokenType.NUMBER ||
                currentToken.getType() == Token.TokenType.STRING) {
            System.out.print(" " + currentToken.getValue()); // Imprime o valor de identificadores, números ou strings adicionais
            if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
                eat(Token.TokenType.IDENTIFIER); // Consome identificadores adicionais
            } else if (currentToken.getType() == Token.TokenType.NUMBER) {
                eat(Token.TokenType.NUMBER); // Consome números adicionais
            } else if (currentToken.getType() == Token.TokenType.STRING) {
                eat(Token.TokenType.STRING); // Consome strings adicionais
            }
        }
    }

    public void parse() {
        while (currentToken.getType() != Token.TokenType.EOF) {
            statement(); // Processa instruções enquanto não for EOF
        }
    }

    public static void main(String[] args) {
        String filePath = "src/test.zd"; // Caminho para o arquivo de código fonte
        Lexer lexer = new Lexer(readFile(filePath)); // Cria o lexer com o conteúdo do arquivo
        List<Token> tokens = lexer.tokenize(); // Tokeniza o conteúdo
        for (Token token : tokens) {
            System.out.println(token); // Imprime tokens para depuração
        }
        Parser parser = new Parser(tokens); // Cria o parser com os tokens
        parser.parse(); // Analisa os tokens
        System.out.println("Parsing completed successfully."); // Mensagem de conclusão
    }

    private static String readFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath)); // Lê o arquivo como string
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage()); // Lança um erro se não conseguir ler o arquivo
        }
    }
}

