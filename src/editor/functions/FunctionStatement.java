package editor.functions;

import editor.translate.Token;
import editor.translate.Parser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

public class FunctionStatement  {
    private final Parser parser;
    private String nome;
    private List<String> parametros;
    private List<Object> corpo;
    private static final Map<String, FunctionStatement> functionMap = new HashMap<>();
    public Map<String, String> variablesFunction = new HashMap<>();
    String currentName = null;
    String currentType = null;
    private final ArithmeticVariable variablesFunctions;
    private final SubstituteVariable substituteVariable;
    public FunctionStatement(Parser parser) {
        this.parser = parser;
        this.variablesFunctions = new ArithmeticVariable(parser);
        this.substituteVariable = new SubstituteVariable(parser);
    }

    public void salvarFuncao(String nome, List<String> parametros, List<Object> corpo) {
        this.nome = nome;
        this.parametros = parametros;
        this.corpo = corpo;
        functionMap.put(nome, this);
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
                Object novoValor = variablesFunctions.calcularIncremento(nomeVariavel, incremento);
                parser.getVariableValues().put(nomeVariavel, novoValor);

                return;
            }

            // Trata instruções de impressão
            if (instrucaoStr.startsWith("print")) {
                String valorImprimir = instrucaoStr.substring(instrucaoStr.indexOf('(') + 1,
                        instrucaoStr.lastIndexOf(')')).trim();
                valorImprimir = substituteVariable.substituirVariaveis(valorImprimir);

                System.out.println(valorImprimir);
                return;
            }

            // Trata declarações de variáveis ou atribuições
            if (instrucaoStr.startsWith("int") || instrucaoStr.startsWith("double") || instrucaoStr.startsWith("string")
            || ((String) instrucao).startsWith("boolean")) {
                variablesFunctions.processarVariavel(instrucaoStr, instrucaoStr.split(" ")[0]);
                return;
            }

            // Verifica se a instrução é uma atribuição de variável simples
            String[] partes = instrucaoStr.split("=");
            if (partes.length == 2) {
                String nomeVariavel = partes[0].trim();
                String valorStr = partes[1].trim();

                // Processa o valor e atualiza a variável
                Object valor = processarValor(valorStr);
                parser.getVariableValues().put(nomeVariavel, valor);
                return;
            }
            // Caso não seja um tipo reconhecido, lança um erro
            throw new RuntimeException("TOKEN ATUAL INCORRETO PARA PROCESSAMENTO: " + parser.getCurrentToken().getValue());
        }
    }


    private Object processarValor(String valorStr) {
        // Se for uma expressão matemática, trata a expressão
        if (valorStr.contains("+") || valorStr.contains("-") || valorStr.contains("*") || valorStr.contains("/")) {
            return variablesFunctions.calcularExpressao(valorStr, "int"); // Ou o tipo que você precisar
        }
        // Caso contrário, converte diretamente o valor para o tipo correspondente
        if (valorStr.matches("-?\\d+")) {
            return Integer.parseInt(valorStr); // Para inteiros
        } else if (valorStr.matches("-?\\d+\\.\\d+")) {
            return Double.parseDouble(valorStr); // Para doubles
        } else if (valorStr.equalsIgnoreCase("true") || valorStr.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(valorStr); // Para booleans
        } else {
            // Trata como string ou tenta pegar o valor da variável
            return substituteVariable.substituirVariaveis(valorStr);
        }
    }


    public List<String> functionParametros() {
        parser.eat(Token.TokenType.DELIMITER);
        List<String> parametros = new ArrayList<>();


        while (!(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER && parser.getCurrentToken().getValue().equals(")"))) {

            if (parser.getCurrentToken().getType() == Token.TokenType.KEYWORD) {
                currentType = parser.getCurrentToken().getValue();
                parser.advance();
            }


            if (parser.getCurrentToken().getType() == Token.TokenType.IDENTIFIER) {
                currentName = parser.getCurrentToken().getValue();  // Captura o nome da variável
                parametros.add(currentName);  // Adiciona o nome à lista de parâmetros
                variablesFunction.put(currentName, currentType);  // Salva a variável e seu tipo no Map
                System.out.println("Nome da variável: " + currentName + ", Tipo: " + currentType);
            }
            parser.advance();
        }
        parser.eat(Token.TokenType.DELIMITER);
        return parametros;
    }
}