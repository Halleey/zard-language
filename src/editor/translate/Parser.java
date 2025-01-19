package editor.translate;
import editor.expressions.ExpressionStatement;
import editor.functions.FunctionStatement;
import editor.functions.ValidateArgs;
import editor.globals.GlobalClass;
import editor.globals.MainStatement;
import editor.globals.PrintStatement;
import editor.ifStatement.IfStatement;
import editor.inputs.InputStatement;
import editor.list.ListAdd;
import editor.list.ListHandler;
import editor.list.ListRemove;
import editor.map.MapAdd;
import editor.map.MapHandler;
import editor.process.IdentifierProcessor;
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
    private final ValidateFunction validateFunction;
    private final IdentifierProcessor identifierProcessor;


    public void backToWhile() {
        System.out.println("[DEBUG] Backtracking to 'while'...");
        while (pos >= 0) {
            if (currentToken.getType() == Token.TokenType.KEYWORD &&
                    "while".equals(currentToken.getValue())) {
                whilePosition = pos;
                System.out.println("[DEBUG] Encontrado o token 'while': " + currentToken);
                return;
            }
            back();
        }

        System.out.println("[DEBUG] Token 'while' não encontrado. Chegou ao início.");
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.currentToken = tokens.get(pos);
        this.variableValues = new HashMap<>();
        this.mainFound = false;
        this.expressionEvaluator = new ExpressionStatement(this); // Inicializa expressionEvaluator
        this.validateFunction = new ValidateFunction(this);
        this.identifierProcessor = new IdentifierProcessor(this, variableValues);
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
            throw new RuntimeException("Erro de sintaxe: esperado " + type + " mas encontrado " + currentToken.getType() + "  valor  " + getCurrentToken().getValue());
        }
    }

    public void parseBlock() {

        while (currentToken.getType() != Token.TokenType.DELIMITER || !currentToken.getValue().equals("}")) {
            if (currentToken.getValue().equals("return")) {
                setFoundReturn(true);

                return;
            }

            statement();
            if (currentToken.getType() == Token.TokenType.EOF) {

                throw new RuntimeException("Erro de sintaxe: bloco não terminado");
            }
        }
        System.out.println("[DEBUG] Fim do bloco.");
        eat(Token.TokenType.DELIMITER); // Consome '}'
        System.out.println("[DEBUG] Token pós-bloco: " + currentToken);
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
        System.out.println("[DEBUG] Processando declaração: " + currentToken);

        if (currentToken.getType() == Token.TokenType.KEYWORD) {
            switch (currentToken.getValue()) {
                case "print":
                    new PrintStatement(this).execute();
                    break;
                case "map":
                    new MapHandler(this).execute();  // Agora trata o map
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
                    new InputStatement(this).execute();
                    break;
                case "while":
                    new WhileStatement(this).execute();
                    break;
                case "function":
                    new FunctionStatement(this).definirFuncao();
                    break;
                case "call":
                    callFunction();
                    break;
                case "list":
                    new ListHandler(this).execute();
                    break;
                case "return":
                    System.out.println("[DEBUG] Encontrado 'return'. Finalizando execução.");
                    break;
                case "}":
                case ";":
                    advance(); // Avança corretamente após ';'
                    break;
                default:
                    throw new RuntimeException("Erro de sintaxe: declaração inesperada " + currentToken);
            }
        } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            // Verifica se o identificador é seguido por um "."
            String identifier = currentToken.getValue();
            advance(); // Avança para verificar o próximo token

            if (currentToken.getValue().equals(".")) {
                // Consome o ponto e delega o processamento para ListAdd
                advance(); // Avança para o método após o "."
                System.out.println("[DEBUG] TOKEN APÓS INVOCAR LISTA: " + currentToken);

                // Passa o nome da lista como parâmetro
                if (currentToken.getType() == Token.TokenType.METHODS) {
                    String methodName = currentToken.getValue();
                    switch (methodName) {

                        case "set":
                            new MapAdd(this, identifier).execute();
                            break;
                        case "add":
                        case "size":
                            new ListAdd(this, identifier).execute();
                            break;
                        case "remove":
                        case "clear":
                            new ListRemove(this, identifier).execute();
                            break;
                        default:
                            throw new RuntimeException("Erro: método desconhecido '" + methodName + "' para lista '" + identifier + "'.");
                    }
                } else {
                    throw new RuntimeException("Erro: método esperado após '.' para a lista '" + identifier + "'.");
                }
            } else if (currentToken.getType() == Token.TokenType.OPERATOR &&
                    (currentToken.getValue().equals("++") || currentToken.getValue().equals("--"))) {
                // Identificamos que a variável está sendo incrementada ou decrementada
                String operator = currentToken.getValue();
                advance();  // Avançamos para o próximo token

                // Verificamos o tipo da variável e aplicamos a operação de incremento ou decremento
                if (variableValues.containsKey(identifier)) {
                    Object value = variableValues.get(identifier);

                    if (value instanceof Integer) {
                        int intValue = (int) value;
                        if (operator.equals("++")) {
                            intValue++;  // Incrementa
                        } else if (operator.equals("--")) {
                            intValue--;  // Decrementa
                        }
                        variableValues.put(identifier, intValue);
                        System.out.println("[DEBUG] Variável " + identifier + " atualizada para: " + intValue);
                    } else if (value instanceof Double) {
                        double doubleValue = (double) value;
                        if (operator.equals("++")) {
                            doubleValue += 1.0;  // Incrementa
                        } else if (operator.equals("--")) {
                            doubleValue -= 1.0;  // Decrementa
                        }
                        variableValues.put(identifier, doubleValue);
                        System.out.println("[DEBUG] Variável " + identifier + " atualizada para: " + doubleValue);
                    } else {
                        throw new RuntimeException("Tipo de variável incompatível para incremento/decremento: " + identifier);
                    }
                } else {
                    throw new RuntimeException("Variável não declarada: " + identifier);
                }
            } else {
                // Trata como uma declaração ou uso de variável normal
                identifierProcessor.processIdentifier();
            }

            System.out.println("TOKEN APOS INVOCAR LISTA  " + getCurrentToken().getValue());
        } else if (currentToken.getType() == Token.TokenType.DELIMITER &&
                (currentToken.getValue().equals("}") || currentToken.getValue().equals(";"))) {
            advance();
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado KEYWORD ou IDENTIFIER mas encontrado " +
                    currentToken.getType() + " " + currentToken.getValue());
        }
    }


    public void callFunction() {
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
            if (getCurrentToken().getType() == Token.TokenType.NUMBER) {
                String valorToken = getCurrentToken().getValue();
                if (valorToken.contains(".")) {
                    // Se sim, interpreta como um número de ponto flutuante
                    argumentos.add(Double.parseDouble(valorToken));
                } else {
                    // Caso contrário, interpreta como um número inteiro
                    argumentos.add(Integer.parseInt(valorToken));
                }
            } else if (getCurrentToken().getType() == Token.TokenType.STRING || getCurrentToken().getType() == Token.TokenType.IDENTIFIER) {
                argumentos.add(getCurrentToken().getValue());
            } else if (getCurrentToken().getType() == Token.TokenType.BOOLEAN) {
                argumentos.add(Boolean.parseBoolean(getCurrentToken().getValue()));
            } else {
                throw new RuntimeException("Token inesperado ao processar argumentos: " + getCurrentToken());
            }
            advance();
        }
        eat(Token.TokenType.DELIMITER); // Consome ')'

        // Busca a função
        FunctionStatement func = FunctionStatement.getFunction(functionName);
        if (func == null) {
            throw new RuntimeException("Função não encontrada: " + functionName);
        }

        // Valida os tipos dos argumentos
        validateFunction.validarTiposDeArgumentos(func, argumentos, validateArgs);

        // Executa a função
        func.consumir(argumentos);
    }

    public void parse() {
        while (currentToken.getType() != Token.TokenType.EOF) {
            if (currentToken.getValue().equals("main")) {
                System.out.println("[DEBUG] Iniciando análise do método main...");
                statement();
            } else {
                advance();
            }
        }
        if (!mainFound) {
            throw new RuntimeException("Método main não definido");
        }
    }
}
