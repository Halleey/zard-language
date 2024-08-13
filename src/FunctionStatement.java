import java.util.*;
import java.util.function.Consumer;
public class FunctionStatement {
    private final Parser parser;
    private String nome;
    private List<String> parametros;
    private Consumer<List<Object>> corpo;
    private static final Map<String, FunctionStatement> functionMap = new HashMap<>();

    public FunctionStatement(Parser parser) {
        this.parser = parser;
    }

    public void salvarFuncao(String nome, List<String> parametros, Consumer<List<Object>> corpo) {
        this.nome = nome;
        this.parametros = parametros;
        this.corpo = corpo;
        functionMap.put(nome, this);

    }
    //direto no java
    public void executar(List<Object> argumentos) {
        if (corpo != null) {
            corpo.accept(argumentos);
        }
    }
    //criar um para a lang

    public void definirFuncao() {
        parser.eat(Token.TokenType.KEYWORD);
        String nomeFunction = parser.getCurrentToken().getValue();
        parser.advance();

        List<String> parametros = functionParametros();
        System.out.println("Parâmetros: " + parametros);
        parser.eat(Token.TokenType.DELIMITER);
        System.out.println("loog: " + parser.getCurrentToken());

        List<Object> corpo = functionBody();
        salvarFuncao(nomeFunction, parametros, (List<Object> c) -> {
            c.addAll(corpo);
            System.out.println("loog: " + c);
        }); System.out.println("Função salva:");
        System.out.println("Nome: " + nomeFunction);
        System.out.println("Parâmetros: " + parametros);
        System.out.println("Corpo: " + corpo);
    }

    private void functionConversor(String nome, List<String> parametros, Consumer<List<Object>> corpoConsumer) {
        List<Object> corpo = new ArrayList<>();
        corpoConsumer.accept(corpo);
        System.out.println("Conteúdo processado do corpo da função: " + corpo);

    }

    private List<Object> functionBody() {
        List<Object> corpo = new ArrayList<>();

        while (!(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                parser.getCurrentToken().getValue().equals("}"))) {
            Token currentToken = parser.getCurrentToken();

            switch (currentToken.getType()) {
                case KEYWORD:
                    switch (currentToken.getValue()) {
                        case "print":
                            corpo.add("print");
                            break;
                        case "int":
                        case "double":
                        case "string":
                        case "boolean":
                            corpo.add("variable");
                            break;
                        case "if":
                        case "else":
                        case "else if":
                            corpo.add("if");
                            break;
                        case "while":
                            corpo.add("while");

                            break;
                        case "input":
                            corpo.add("input");
                            break;
                        case "function":
                            corpo.add("function");
                            break;
                        default:
                            throw new RuntimeException("Erro de sintaxe: declaração inesperada " + currentToken);
                    }
                    break;

                case IDENTIFIER:
                    parser.advance();
                    if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                            parser.getCurrentToken().getValue().equals("=")) {
                        parser.advance();
                        corpo.add("atribuição: " + currentToken.getValue() + " = " + parser.getCurrentToken().getValue());
                        parser.advance();
                    } else {
                        corpo.add("identifier");
                    }
                    break;
                case STRING:
                    corpo.add("string: " + currentToken.getValue());
                    break;
                case NUMBER:
                    corpo.add("Number: " + currentToken.getValue());
                    break;
                case DELIMITER:
                    String delimiterValue = currentToken.getValue();
                    if (delimiterValue.equals(";")) {
                        break;
                    }
                    else if (delimiterValue.equals("(")) {
                        corpo.add("delimiter: (");
                    } else if (delimiterValue.equals(")")) {
                        corpo.add("delimiter: )");
                    } else if (delimiterValue.equals("{")) {

                        corpo.add("delimiter: {");
                    } else if (delimiterValue.equals("}")) {
                        corpo.add("delimiter: }");
                    } else {
                        throw new RuntimeException("Erro de sintaxe: delimitador inesperado " + currentToken);
                    }
                    break;

                default:
                    throw new RuntimeException("Erro de sintaxe: token inesperado " + currentToken);
            }

            parser.advance();
        }
        System.out.println(corpo);
        return corpo;
    }


    private List<String> functionParametros() {
        parser.eat(Token.TokenType.DELIMITER);
        List<String> parametros = new ArrayList<>();
        while (!(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER && parser.getCurrentToken().getValue().equals(")"))) {
            if (parser.getCurrentToken().getType() == Token.TokenType.IDENTIFIER) {
                parametros.add(parser.getCurrentToken().getValue());
            }
            parser.advance();
        }
        parser.eat(Token.TokenType.DELIMITER);

        return parametros;
    }

    private void skipFunction() {
        int braceLevel = 0;
        while (parser.getCurrentToken().getType() != Token.TokenType.EOF) {
            if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER) {
                if ("{".equals(parser.getCurrentToken().getValue())) {
                    braceLevel++;
                } else if ("}".equals(parser.getCurrentToken().getValue())) {
                    if (braceLevel == 0) {
                        return;
                    } else {
                        braceLevel--;
                    }
                }
            }
            parser.advance();
        }
    }

    public String getNome() {
        return nome;
    }

    public List<String> getParametros() {
        return parametros;
    }

    public static FunctionStatement getFunction(String nome) {
        return functionMap.get(nome);
    }

    public static void callFunction(String nome, List<Object> argumentos) {
        FunctionStatement function = functionMap.get(nome);
        if (function == null) {
            throw new RuntimeException("Função não encontrada: " + nome);
        }

        if (argumentos.size() != function.getParametros().size()) {
            throw new RuntimeException("Número incorreto de argumentos para a função: " + nome);
        }
        function.executar(argumentos);
}
}