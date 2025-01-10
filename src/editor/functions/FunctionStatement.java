package editor.functions;

import editor.globals.GlobalClass;
import editor.translate.Token;
import editor.translate.Parser;
import editor.whiles.WhileStatement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;


public class FunctionStatement extends GlobalClass {
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
    private final FunctionBody functionBody;


    public FunctionStatement(Parser parser) {
        this.parser = parser;
        this.variablesFunctions = new ArithmeticVariable(parser);
        this.substituteVariable = new SubstituteVariable(parser);
        this.functionBody = new FunctionBody(parser);
    }

    public void salvarFuncao(String nome, List<String> parametros, List<Object> corpo) {
        this.nome = nome;
        this.parametros = parametros;
        this.corpo = corpo;
        functionMap.put(nome, this);
        System.out.println("[DEBUG] Função salva: " + nome);
        System.out.println("[DEBUG] Parâmetros da função: " + parametros);
        System.out.println("[DEBUG] Corpo da função: " + corpo);
    }

    public static FunctionStatement getFunction(String nome) {
        System.out.println("[DEBUG] Buscando função: " + nome);
        return functionMap.get(nome);
    }

    public void consumir(List<Object> argumentos) {
        System.out.println("[DEBUG] Consumindo argumentos: " + argumentos);
        if (parametros != null && parametros.size() == argumentos.size()) {
            for (int i = 0; i < parametros.size(); i++) {
                parser.getVariableValues().put(parametros.get(i), argumentos.get(i));
                System.out.println("[DEBUG] Parâmetro " + parametros.get(i) + " atribuído valor " + argumentos.get(i));
            }
        } else {
            throw new RuntimeException("Número de argumentos incorreto para a função: " + nome);
        }

        if (corpo != null) {
            System.out.println("[DEBUG] Executando corpo da função: " + corpo);
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
        System.out.println("[DEBUG] Nome da função sendo definida: " + nomeFunction);
        parser.advance();
        List<String> parametros = functionParametros();
        List<Object> corpo = functionBody.saveFunctionBody();
        salvarFuncao(nomeFunction, parametros, corpo);
        System.out.println("[DEBUG] Função definida com sucesso.");
    }


    public void executeStatement(Object instrucao) {
        System.out.println("[DEBUG] Executando instrução: " + instrucao);
        if (instrucao instanceof String instrucaoStr) {

            if (instrucaoStr.endsWith("++") || instrucaoStr.endsWith("--")) {
                String nomeVariavel = instrucaoStr.substring(0, instrucaoStr.length() - 2).trim();
                int incremento = instrucaoStr.endsWith("++") ? 1 : -1;
                Object novoValor = variablesFunctions.calcularIncremento(nomeVariavel, incremento);
                parser.getVariableValues().put(nomeVariavel, novoValor);
                System.out.println("[DEBUG] Incremento/Decremento: " + nomeVariavel + " novo valor: " + novoValor);
                return;
            }

            // Trata instruções de impressão
            if (instrucaoStr.startsWith("print")) {
                String valorImprimir = instrucaoStr.substring(instrucaoStr.indexOf('(') + 1,
                        instrucaoStr.lastIndexOf(')')).trim();
                valorImprimir = substituteVariable.substituirVariaveis(valorImprimir);
                System.out.println("[DEBUG] Executando 'print': " + valorImprimir);
                System.out.println(valorImprimir);
                return;
            }

          if(((String) instrucao).startsWith("while")) {
              setFunctionWhile(true);
              WhileStatement whileStatement = new WhileStatement(parser);

              parser.backToWhile();
              if(isFunctionWhile()) {
                  whileStatement.execute();
              }
              return;
          }

            if (instrucaoStr.startsWith("return")) {
                String valorRetorno = instrucaoStr.substring(6).trim();
                Object valor = parser.getVariableValues().getOrDefault(valorRetorno, processarValor(valorRetorno));
                System.out.println("[DEBUG] Executando 'return': " + valor);
                if (valor != null) {
                    System.out.println(valor);
                } else {
                    throw new RuntimeException("Valor ou variável não encontrado para o return: " + valorRetorno);
                }
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
                System.out.println("[DEBUG] Atribuindo valor à variável " + nomeVariavel + ": " + valor);
                return;
            }
            // Caso não seja um tipo reconhecido, lança um erro
            throw new RuntimeException("TOKEN ATUAL INCORRETO PARA PROCESSAMENTO: " + parser.getCurrentToken().getValue());
        }
    }

    private Object processarValor(String valorStr) {
        System.out.println("[DEBUG] Processando valor: " + valorStr);
        // Se for uma expressão matemática, trata a expressão
        if (valorStr.contains("+") || valorStr.contains("-") || valorStr.contains("*") || valorStr.contains("/")) {
            return variablesFunctions.calcularExpressao(valorStr, "int");
        }
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
        System.out.println("[DEBUG] Iniciando análise dos parâmetros da função.");
        while (!(parser.getCurrentToken().getType() == Token.TokenType.DELIMITER && parser.getCurrentToken().getValue().equals(")"))) {

            if (parser.getCurrentToken().getType() == Token.TokenType.KEYWORD) {
                currentType = parser.getCurrentToken().getValue();
                parser.advance();
                System.out.println("[DEBUG] Tipo de variável: " + currentType);
            }

            if (parser.getCurrentToken().getType() == Token.TokenType.IDENTIFIER) {
                currentName = parser.getCurrentToken().getValue();  // Captura o nome da variável
                parametros.add(currentName);  // Adiciona o nome à lista de parâmetros
                variablesFunction.put(currentName, currentType);  // Salva a variável e seu tipo no Map
                System.out.println("[DEBUG] Nome da variável: " + currentName + ", Tipo: " + currentType);
            }
            parser.advance();
        }
        parser.eat(Token.TokenType.DELIMITER);
        System.out.println("[DEBUG] Parâmetros analisados: " + parametros);
        return parametros;
    }
}
