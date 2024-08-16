import java.util.*;
public class FunctionStatement {
    private final Parser parser;
    private String nome;
    private List<String> parametros;
    private List<Object> corpo;
    private static final Map<String, FunctionStatement> functionMap = new HashMap<>();

    public FunctionStatement(Parser parser) {
        this.parser = parser;
    }

    public void salvarFuncao(String nome, List<String> parametros, List<Object> corpo) {
        this.nome = nome;
        this.parametros = parametros;
        this.corpo = corpo;
        functionMap.put(nome, this);
        System.out.println("Função '" + nome + "' salva. Funções disponíveis: " + functionMap.keySet());
    }

    // No método getFunction
    public static FunctionStatement getFunction(String nome) {
        System.out.println("Buscando função: " + nome);
        return functionMap.get(nome);
    }


    public void consumir(List<Object> argumentos) {

        if (parametros != null && parametros.size() == argumentos.size()) {
            for (int i = 0; i < parametros.size(); i++) {
                parser.getVariableValues().put(parametros.get(i), argumentos.get(i));
            }
        }
        if (corpo != null) {
            System.out.println("Executando corpo da função " + nome + ": " + corpo);
            for (Object statement : corpo) {
                parser.executeStatement(statement);
            }
        } else {
            throw new RuntimeException("Corpo da função não definido para: " + nome);
        }
    }

    public void definirFuncao() {
        parser.eat(Token.TokenType.KEYWORD);
        String nomeFunction = parser.getCurrentToken().getValue();
        parser.advance();

        List<String> parametros = functionParametros();
        parser.eat(Token.TokenType.DELIMITER);
        List<Object> corpo = functionBody();
        salvarFuncao(nomeFunction, parametros, corpo);
        System.out.println("function name  " + getNome());
        System.out.println("parametros " + getParametros());
        System.out.println("corpo " + getCorpo());
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

    public String getNome() {
        return nome;
    }

    public List<String> getParametros() {
        return parametros;
    }

    public List<Object> getCorpo() {
        return corpo;
    }

    private List<Object> functionBody() {
        List<Object> corpo = new ArrayList<>();
        while (!(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                parser.getCurrentToken().getValue().equals("}"))) {
            StringBuilder instrucaoCompleta = new StringBuilder();

            while (!(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                    parser.getCurrentToken().getValue().equals(";"))) {
                instrucaoCompleta.append(parser.getCurrentToken().getValue()).append(" ");
                parser.advance();
            }

            corpo.add(instrucaoCompleta.toString().trim());
            parser.advance();
        }
        parser.advance();
        return corpo;
    }
}
