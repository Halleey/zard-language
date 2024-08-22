package editor;
import editor.translate.Parser;
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
        parser.log("Função '" + nome + "' salva. Funções disponíveis: " + functionMap.keySet());
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
            parser.log("Executando corpo da função" + corpo);
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
        parser.log("function name  " + getNome());
        parser.log("parametros " + getParametros());
        parser.log("corpo " + getCorpo());
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
            if (instrucaoStr.endsWith("++") || instrucaoStr.endsWith("--")) {
                String nomeVariavel = instrucaoStr.substring(0, instrucaoStr.length() - 2).trim();
                int incremento = instrucaoStr.endsWith("++") ? 1 : -1;
                Object novoValor = calcularIncremento(nomeVariavel, incremento);
                parser.getVariableValues().put(nomeVariavel, novoValor);
                parser.log("Variável " + nomeVariavel + " atualizada para " + novoValor);
                return;
            }

            if (instrucaoStr.startsWith("print")) {
                // Extrair e limpar a string dentro dos parênteses
                String valorImprimir = instrucaoStr.substring(instrucaoStr.indexOf('(') + 1, instrucaoStr.lastIndexOf(')')).trim();

                // Substituir variáveis no texto a ser impresso
                valorImprimir = substituirVariaveis(valorImprimir);

                // Imprimir resultado
                parser.log(valorImprimir);
            } else if (instrucaoStr.startsWith("int") || instrucaoStr.startsWith("double") || instrucaoStr.startsWith("string")) {
                processarVariavel(instrucaoStr, instrucaoStr.split(" ")[0]);
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
        StringBuilder resultado = new StringBuilder();
        String[] partes = instrucoes.split("\\s+");

        for (String parte : partes) {
            if (parser.getVariableValues().containsKey(parte)) {
                resultado.append(parser.getVariableValues().get(parte));
            } else {
                resultado.append(parte);
            }
            resultado.append(" ");
        }

        return resultado.toString().trim();
    }


    private void processarVariavel(String instrucaoStr, String tipo) {
        String resto = instrucaoStr.substring(tipo.length()).trim();
        String[] partes = resto.split("=");
        String nomeVariavel = partes[0].trim();

        Object valor = null;

        if (partes.length == 2) {
            String expressao = partes[1].trim();

            if (expressao.contains("+") || expressao.contains("-") || expressao.contains("*") || expressao.contains("/")) {
                valor = calcularExpressao(expressao, tipo);
            } else if (expressao.equals(nomeVariavel + "++") || expressao.equals("++" + nomeVariavel)) {
                valor = calcularIncremento(nomeVariavel, 1);
            } else if (expressao.equals(nomeVariavel + "--") || expressao.equals("--" + nomeVariavel)) {
                valor = calcularIncremento(nomeVariavel, -1);
            } else {
                valor = switch (tipo) {
                    case "int" -> Integer.parseInt(expressao);
                    case "double" -> Double.parseDouble(expressao);
                    case "boolean" -> Boolean.parseBoolean(expressao);
                    case "string" -> {
                        // Remover aspas caso estejam presentes
                        String valorStr = expressao;
                        if (expressao.startsWith("\"") && expressao.endsWith("\"")) {
                            valorStr = expressao.substring(1, expressao.length() - 1);
                        }
                        yield valorStr;
                    }
                    default -> valor;
                };
            }
        }
        // operação `a++;` ou `a--;`
        else if (partes.length == 1) {
            if (nomeVariavel.endsWith("++")) {
                nomeVariavel = nomeVariavel.substring(0, nomeVariavel.length() - 2).trim();
                valor = calcularIncremento(nomeVariavel, 1);
            } else if (nomeVariavel.endsWith("--")) {
                nomeVariavel = nomeVariavel.substring(0, nomeVariavel.length() - 2).trim();
                valor = calcularIncremento(nomeVariavel, -1);
            }
        }

        if (valor != null) {
            parser.getVariableValues().put(nomeVariavel, valor);
            System.out.println("Variável " + nomeVariavel + " armazenada com valor " + valor);
        }
    }


    private Object calcularExpressao(String expressao, String tipo) {
        String[] operandos = expressao.split("\\s*([+\\-*/])\\s*");
        String operador = expressao.replaceAll("[^+\\-*/]", "").trim();

        double resultado = switch (tipo) {
            case "int", "double" -> {
                double op1 = obterValorComoDouble(operandos[0].trim());

                double op2 = obterValorComoDouble(operandos[1].trim());

                yield calcularResultado(op1, op2, operador);
            }
            default -> 0;
        };

        return tipo.equals("int") ? (int) resultado : resultado;
    }

    private double obterValorComoDouble(String operando) {
        // Se o operando é uma variável, pegue seu valor; caso contrário, converta diretamente
        if (parser.getVariableValues().containsKey(operando)) {
            return ((Number) parser.getVariableValues().get(operando)).doubleValue();
        } else {
            return Double.parseDouble(operando);
        }
    }

    private double calcularResultado(double op1, double op2, String operador) {

        return switch (operador) {
            case "+" -> op1 + op2;
            case "-" -> op1 - op2;
            case "*" -> op1 * op2;
            case "/" -> op1 / op2;
            default -> 0;
        };
    }

    private Object calcularIncremento(String variavel, int plus) {
        Object valorAtual = parser.getVariableValues().get(variavel);
        if (valorAtual instanceof Integer) {
            return (Integer) valorAtual + plus;
        } else if (valorAtual instanceof Double) {
            return (Double) valorAtual + plus;
        }
        return null;
    }
}
