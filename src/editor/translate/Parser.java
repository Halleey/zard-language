package editor.translate;
import editor.expressions.ExpressionStatement;
import editor.functions.FunctionStatement;
import editor.functions.ValidateArgs;
import editor.globals.GlobalClass;
import editor.globals.MainStatement;
import editor.globals.PrintStatement;
import editor.ifStatement.IfStatement;
import editor.inputs.InputStatement;
import editor.variables.VariableStatement;
import editor.whiles.WhileStatement;
import java.util.*;

public class Parser extends GlobalClass {
    private final List<Token> tokens;
    private int pos;
    private Token currentToken;
    private final Map<String, Object> variableValues;
    private boolean mainFound;
    private int whilePosition;
    private ExpressionStatement expressionEvaluator;


    public void backToWhile() {
        while (pos >= 0) {
            if (currentToken.getType() == Token.TokenType.KEYWORD &&
                    "while".equals(currentToken.getValue())) {
                whilePosition = pos;
                System.out.println("Encontrado o token 'while': " + currentToken);
                return;
            }
            back();
        }

        System.out.println("Token 'while' não encontrado. Chegou ao início.");
    }
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.currentToken = tokens.get(pos);
        this.variableValues = new HashMap<>();
        this.mainFound = false;
        this.expressionEvaluator = new ExpressionStatement(this); // Inicializa expressionEvaluator
    }


    public Token getCurrentToken() {
        return currentToken;
    }

    public Map<String, Object> getVariableValues() {
        return variableValues;
    }

    public Token peekNextToken() {
        int nextPos = pos + 1;
        if (nextPos < tokens.size()) {
            return tokens.get(nextPos);
        }
        return null; // Retorna null se não houver próximo token
    }

    public void advance() {
        pos++;
        if (pos < tokens.size()) {
            // System.out.println("Token current for debug "+ getCurrentToken().getValue());
            currentToken = tokens.get(pos);
        } else {
            currentToken = new Token(Token.TokenType.EOF, "");
        }
    }

    public void back() {
        pos--;
        if (pos >= 0) {
            currentToken = tokens.get(pos);
        } else {
            currentToken = new Token(Token.TokenType.EOF, "");
        }
    }

    public void eat(Token.TokenType type) {
        if (currentToken.getType() == type) {
            advance();
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado " + type + " mas encontrado " + currentToken.getType());
        }
    }
    public void parseBlock() {
        System.out.println("Parsing block start");
        while (currentToken.getType() != Token.TokenType.DELIMITER || !currentToken.getValue().equals("}")) {
            if(currentToken.getValue().equals("return")) {
                setFoundReturn(true);
                System.out.println("condition is " + isFoundReturn());
                return;
            }
            System.out.println("Inside block, current token: " + currentToken);
            statement();
            if (currentToken.getType() == Token.TokenType.EOF) {
                System.out.println("LAST CHAR" + getCurrentToken());
                throw new RuntimeException("Erro de sintaxe: bloco não terminado");
            }
        }
        System.out.println("End of block");
        eat(Token.TokenType.DELIMITER); // Consome '}'
        System.out.println("Post-block token: " + currentToken);
    }

    public Object expression() {
        return expressionEvaluator.expression();
    }

    public Object term() {
        return expressionEvaluator.term();
    }

    public Object factor() {
        return expressionEvaluator.factor();
    }

    public Object calc() {
        return expressionEvaluator.calc();
    }


    public void statement() {
        // System.out.println("Current token in statement: " + currentToken);

        if (currentToken.getType() == Token.TokenType.KEYWORD) {
            switch (currentToken.getValue()) {
                case "print":
                    System.out.println("Executing print statement.");
                    new PrintStatement(this).execute();
                    break;
                case "int":
                case "double":
                case "string":
                case "boolean":

                    new VariableStatement(this).execute();
                    break;
                case "main":

                    new MainStatement(this).execute();
                    mainFound = true;
                    break;
                case "if":
                case "else if":
                case "else":

                    new IfStatement(this).execute();
                    break;
                case "input":
                    System.out.println("Executing input statement.");
                    new InputStatement(this).execute();
                    break;
                case "while":

                    new WhileStatement(this).execute();
                    break;
                case "function":

                    new FunctionStatement(this).definirFuncao();
                    break;
                case "call":
                    ValidateArgs validateArgs = new ValidateArgs();
                    advance();
                    String functionName = getCurrentToken().getValue(); // Captura o nome da função
                    advance();
                    if (!getCurrentToken().getValue().equals("(")) {
                        throw new RuntimeException("Erro de sintaxe: esperado '(' mas encontrado " + getCurrentToken().getValue());
                    }
                    eat(Token.TokenType.DELIMITER);

                    // Captura os argumentos fornecidos
                    List<Object> argumentos = new ArrayList<>();
                    while (!(getCurrentToken().getType() == Token.TokenType.DELIMITER && getCurrentToken().getValue().equals(")"))) {
                        if (getCurrentToken().getType() == Token.TokenType.DELIMITER && getCurrentToken().getValue().equals(",")) {
                            advance(); // Ignora a vírgula e passa para o próximo token
                            continue;
                        }
                        if (getCurrentToken().getType() == Token.TokenType.NUMBER) {
                            String valorToken = getCurrentToken().getValue(); // Obtém o valor do token como string.

                            // Verifica se o número contém um ponto decimal.
                            if (valorToken.contains(".")) {
                                // Se sim, interpreta como um número de ponto flutuante (double).
                                argumentos.add(Double.parseDouble(valorToken));
                            } else {
                                // Caso contrário, interpreta como um número inteiro.
                                argumentos.add(Integer.parseInt(valorToken));
                            }
                        } else if (getCurrentToken().getType() == Token.TokenType.STRING || getCurrentToken().getType() == Token.TokenType.IDENTIFIER) {
                            argumentos.add(getCurrentToken().getValue()); // Strings e identificadores são adicionados diretamente
                        } else if (getCurrentToken().getType() == Token.TokenType.BOOLEAN) {
                            argumentos.add(Boolean.parseBoolean(getCurrentToken().getValue()));
                        } else {
                            throw new RuntimeException("Token inesperado ao processar argumentos: " + getCurrentToken());
                        }
                        advance();
                    }

                    eat(Token.TokenType.DELIMITER); // Consome ')'

                    // Busca a função e valida os tipos
                    FunctionStatement func = FunctionStatement.getFunction(functionName);
                    if (func == null) {
                        throw new RuntimeException("Função não encontrada: " + functionName);
                    }

                    // Validação de tipos
                    Map<String, String> parametrosTipos = func.variablesFunction;
                    List<String> parametros = func.getParametros();
                    if (parametros.size() != argumentos.size()) {
                        throw new RuntimeException("Número de argumentos incorreto para a função: " + functionName);
                    }

                    for (int i = 0; i < parametros.size(); i++) {
                        String nomeParametro = parametros.get(i);
                        String tipoEsperado = parametrosTipos.get(nomeParametro); // Busca o tipo esperado do parâmetro com base no nome.
                        Object argumento = argumentos.get(i); // Obtém o argumento fornecido para o parâmetro atual.

                        if (!validateArgs.validarTipo(tipoEsperado, argumento)) {
                            throw new RuntimeException("Tipo incompatível para o parâmetro '" + nomeParametro +
                                    "': esperado " + tipoEsperado + ", recebido " + argumento.getClass().getSimpleName());
                        }
                    }

                    // Executa a função
                    func.consumir(argumentos);
                    break;
                case "return":
                    break;
                case "}":
                case ";":
                    advance();
                    break;
                default:
                    throw new RuntimeException("Erro de sintaxe: declaração inesperada " + currentToken);
            }
        } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            // Processa identificadores e atribuições
            String variableName = currentToken.getValue();
            advance();
            System.out.println("Processing : " + getCurrentToken());

            if (currentToken.getType() == Token.TokenType.OPERATOR &&
                    currentToken.getValue().equals("=")) {
                new VariableStatement(this).atribuir(variableName);
            } else if (currentToken.getType() == Token.TokenType.OPERATOR &&
                    (currentToken.getValue().equals("++") || currentToken.getValue().equals("--"))) {
                // Processa operadores de incremento/decremento
                String operator = currentToken.getValue();

                advance(); // Avança para o próximo token (onde está o valor)

                if (variableValues.containsKey(variableName)) {
                    Object value = variableValues.get(variableName);

                    if (value instanceof Integer) {
                        int intValue = (int) value;
                        if (operator.equals("++")) {
                            intValue++;
                        } else if (operator.equals("--")) {
                            intValue--;
                        }
                        variableValues.put(variableName, intValue);  // Atualiza o valor da variável
                    } else if (value instanceof Double) {
                        double doubleValue = (double) value;
                        if (operator.equals("++")) {
                            doubleValue += 1.0;
                        } else if (operator.equals("--")) {
                            doubleValue -= 1.0;
                        }
                        variableValues.put(variableName, doubleValue);  // Atualiza o valor da variável
                    } else {
                        throw new RuntimeException("Tipo de variável incompatível para incremento/decremento: " + variableName);
                    }
                } else {
                    throw new RuntimeException("Variável não declarada: " + variableName);
                }
            } else {
                // Atribui valor padrão se não for um operador
                System.out.println("Assigning value to variable.");
                new VariableStatement(this).assignValue();
            }
        } else if (currentToken.getType() == Token.TokenType.DELIMITER &&
                (currentToken.getValue().equals("}") || currentToken.getValue().equals(";"))) {

            advance();
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado KEYWORD ou IDENTIFIER mas encontrado " + currentToken.getType() + " " + currentToken.getValue());
        }
    }
    public void parse() {
        while (currentToken.getType() != Token.TokenType.EOF) {
            if (currentToken.getValue().equals("main")) {
                statement();
            } else {
                advance();
            }
        }
        if (!mainFound) {

            throw new RuntimeException("Main method not defined");
        }
    }
}
