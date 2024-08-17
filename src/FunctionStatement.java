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
            System.out.println("Executando corpo da função" + corpo);
            for (Object statement : corpo) {
                executeStatement(statement);
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

    public void executeStatement(Object instrucao) {
        if (instrucao instanceof String instrucaoStr) {
            if (instrucaoStr.startsWith("print")) {
                String valorImprimir = instrucaoStr.substring(instrucaoStr.indexOf('(') + 1, instrucaoStr.lastIndexOf(')')).trim();
                valorImprimir = substituirVariaveis(valorImprimir);
                System.out.println(valorImprimir);
            } else if (instrucaoStr.startsWith("int")) {
                processarVariavel(instrucaoStr, "int");

            } else if (instrucaoStr.startsWith("double")) {
                processarVariavel(instrucaoStr, "double");

            } else if (instrucaoStr.startsWith("string")) {
                processarVariavel(instrucaoStr, "string");

            } else {
                throw new RuntimeException("Instrução de string desconhecida: " + instrucaoStr);
            }
        } else if (instrucao instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> functionCallData = (Map<String, Object>) instrucao;
            String functionName = (String) functionCallData.get("functionName");
            List<Object> argumentos = (List<Object>) functionCallData.get("argumentos");

            System.out.println("Executando chamada de função: " + functionName);
            System.out.println("Argumentos: " + argumentos);

            FunctionStatement func = FunctionStatement.getFunction(functionName);
            if (func != null) {
                System.out.println("Corpo da função: " + func.getCorpo());
                func.consumir(argumentos);
            } else {
                throw new RuntimeException("Função não encontrada: " + functionName);
            }
        } else {
            throw new RuntimeException("Tipo de instrução desconhecido: " + instrucao.getClass().getName());
        }
    }

    private String substituirVariaveis(String instrucoes) {
        // percorrer o mapa de chaves e valores  (variaveis e seus valores)
        for (Map.Entry<String, Object> entry : parser.getVariableValues().entrySet()) {
            String nomeVariavel = entry.getKey();
            Object valor = entry.getValue();
            // Substitui todas as referencias do nome da variavel pelo seu valor atribuido
            instrucoes = instrucoes.replace(nomeVariavel, valor.toString());
        }
        // Retorna a string 'instrucoes' com as variáveis substituídas pelos seus valores correspondentes.
        return instrucoes;
    }

    private void processarVariavel(String instrucaoStr, String tipo) {
        String resto = instrucaoStr.substring(tipo.length()).trim();
        String[] partes = resto.split("=");
        String nomeVariavel = partes[0].trim();

        Object valor = null;
        

        switch (tipo) {
            case "int":
                valor = Integer.parseInt(partes[1].trim());
                break;
            case "double":
                valor = Double.parseDouble(partes[1].trim());
                break;
        }
        parser.getVariableValues().put(nomeVariavel, valor);
        System.out.println("Variável " + nomeVariavel + " armazenada com valor " + valor);
    }
}
