package editor;

import editor.translate.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static FunctionStatement getFunction(String nome) {
        System.out.println("Buscando função: " + nome);
        return functionMap.get(nome);
    }


    public void consumir(List<Object> argumentos) {
        if (parametros != null && parametros.size() == argumentos.size()) {
            for (int i = 0; i < parametros.size(); i++) {
                parser.getVariableValues().put(parametros.get(i), argumentos.get(i));
            }
        } else {
            throw new RuntimeException("Número de argumentos incorreto para a função: " + nome);
        }

        if (corpo != null) {
            System.out.println("Executando corpo da função: " + corpo);
            for (Object statement : corpo) {
                executeStatement(statement);
            }
        } else {
            throw new RuntimeException("Corpo da função não definido para: " + nome);
        }
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
    public void definirFuncao() {
        parser.eat(Token.TokenType.KEYWORD);
        String nomeFunction = parser.getCurrentToken().getValue();
        parser.advance();
        List<String> parametros = functionParametros();
        List<Object> corpo = functionBody();
        salvarFuncao(nomeFunction, parametros, corpo);
        System.out.println("function name  " + getNome());
        System.out.println("parametros " + getParametros());
        System.out.println("corpo " + getCorpo());
    }

    private List<Object> functionBody() {
        List<Object> corpo = new ArrayList<>();
        int chave = 0;

        if (parser.getCurrentToken().getValue().equals("{")) {
            chave++;
            parser.advance();

            StringBuilder instrucaoCompleta = new StringBuilder();
            while (chave > 0) {
                String tokenValue = parser.getCurrentToken().getValue();

                if (tokenValue.equals("{")) {
                    chave++;
                }

                if (tokenValue.equals("}")) {
                    chave--;
                }

                // Verifica se é uma string e mantém as aspas
                if (parser.getCurrentToken().getType() == Token.TokenType.STRING) {
                    instrucaoCompleta.append("\"").append(tokenValue).append("\""); // Adiciona as aspas manualmente
                } else {
                    instrucaoCompleta.append(tokenValue).append(" ");
                }

                parser.advance();

                if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                        parser.getCurrentToken().getValue().equals(";")) {
                    corpo.add(instrucaoCompleta.toString().trim());
                    instrucaoCompleta.setLength(0);
                    parser.advance();
                }
            }
        }
        System.out.println("Corpo da função salvo: " + corpo);
        return corpo;
    }


    public void executeStatement(Object instrucao) {

        if (instrucao instanceof String instrucaoStr) {

            // Trata operações de incremento/decremento
            if (instrucaoStr.endsWith("++") || instrucaoStr.endsWith("--")) {
                String nomeVariavel = instrucaoStr.substring(0, instrucaoStr.length() - 2).trim();
                int incremento = instrucaoStr.endsWith("++") ? 1 : -1;
                Object novoValor = calcularIncremento(nomeVariavel, incremento);
                parser.getVariableValues().put(nomeVariavel, novoValor);
                parser.log("Variável " + nomeVariavel + " atualizada para " + novoValor);
                return;
            }
            // Trata instruções de impressão
            if (instrucaoStr.startsWith("print")) {
                String valorImprimir = instrucaoStr.substring(instrucaoStr.indexOf('(') + 1,
                        instrucaoStr.lastIndexOf(')')).trim();
                valorImprimir = substituirVariaveis(valorImprimir);
                parser.log(valorImprimir);
                System.out.println(valorImprimir);
                return;
            }
            // Trata declarações de variáveis
            if (instrucaoStr.startsWith("int") || instrucaoStr.startsWith("double") || instrucaoStr.startsWith("string")) {
                processarVariavel(instrucaoStr, instrucaoStr.split("")[0]);
            }
            else
            {
                throw new RuntimeException("TOKEN ATUAL INCORRETO PARA PROCESSAMENTO  " + parser.getCurrentToken().getValue());
            }
        }
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



    private String substituirVariaveis(String instrucoes) {
        StringBuilder resultado = new StringBuilder();
        boolean dentroAspas = false; // Variável para controlar quando estamos dentro de aspas

        // Dividir a instrução em caracteres individuais para percorrer com controle de aspas
        StringBuilder palavra = new StringBuilder();
        for (int i = 0; i < instrucoes.length(); i++) {
            char c = instrucoes.charAt(i);

            if (c == '\"') { // Verifica se encontramos aspas
                if (dentroAspas) {
                    // Se já estamos dentro de aspas, terminamos a parte literal
                    resultado.append(palavra);
                    palavra.setLength(0); // Limpar palavra para próximo uso
                } else {
                    // Se não estamos dentro de aspas, começamos a parte literal
                    // Não adiciona as aspas ao resultado
                }
                dentroAspas = !dentroAspas; // Alterna entre dentro e fora de aspas
            } else if (c == ' ' && !dentroAspas) { // Se estivermos fora de aspas e encontrar espaço
                if (palavra.length() > 0) {
                    // Substitui as variáveis apenas fora de aspas
                    String variavel = palavra.toString().trim();
                    if (parser.getVariableValues().containsKey(variavel)) {
                        resultado.append(parser.getVariableValues().get(variavel));
                    } else {
                        resultado.append(variavel); // Caso não seja uma variável, adiciona a palavra
                    }
                    palavra.setLength(0); // Limpar palavra após processar
                }
                resultado.append(" "); // Adiciona o espaço entre palavras
            } else {
                palavra.append(c); // Adiciona o caractere à palavra atual
            }
        }

        // Processar a última palavra fora de aspas, caso exista
        if (palavra.length() > 0) {
            String variavel = palavra.toString().trim();
            if (parser.getVariableValues().containsKey(variavel)) {
                resultado.append(parser.getVariableValues().get(variavel));
            } else {
                resultado.append(variavel);
            }
        }

        return resultado.toString();
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
                yield  calcularResultado(op1, op2, operador);
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