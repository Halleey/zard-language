package editor.translate;
import editor.expressions.ExpressionStatement;
import editor.functions.FunctionStatement;
import editor.functions.ValidateArgs;
import editor.globals.GlobalClass;
import editor.globals.MainStatement;
import editor.globals.PrintStatement;
import editor.ifStatement.IfStatement;
import editor.inputs.InputStatement;
import editor.list.ListHandler;
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
        System.out.println("[DEBUG] Iniciando parsing do bloco.");
        while (currentToken.getType() != Token.TokenType.DELIMITER || !currentToken.getValue().equals("}")) {
            if (currentToken.getValue().equals("return")) {
                setFoundReturn(true);
                System.out.println("[DEBUG] Encontrei 'return' no bloco. Retornando...");
                return;
            }
            System.out.println("[DEBUG] Dentro do bloco, token atual: " + currentToken);
            statement();
            if (currentToken.getType() == Token.TokenType.EOF) {
                System.out.println("[DEBUG] Último token: " + getCurrentToken());
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
                    System.out.println("[DEBUG] Executando comando print.");
                    new PrintStatement(this).execute();
                    break;
                case "int":
                case "double":
                case "string":
                case "boolean":
                    System.out.println("[DEBUG] Executando declaração de variável.");
                    new VariableStatement(this).execute();
                    break;
                case "main":
                    System.out.println("[DEBUG] Encontrado 'main'. Iniciando execução do método main.");
                    new MainStatement(this).execute();
                    mainFound = true;
                    break;
                case "if":
                case "else if":
                case "else":
                    System.out.println("[DEBUG] Executando estrutura de controle if/else.");
                    new IfStatement(this).execute();
                    break;
                case "input":
                    System.out.println("[DEBUG] Executando comando de entrada.");
                    new InputStatement(this).execute();
                    break;
                case "while":
                    System.out.println("[DEBUG] Executando loop while.");
                    new WhileStatement(this).execute();
                    break;
                case "function":
                    System.out.println("[DEBUG] Definindo função.");
                    new FunctionStatement(this).definirFuncao();
                    break;
                case "list":
                    System.out.println("[DEBUG] Executando declaração de lista.");
                    new ListHandler(this).execute();
                    break;
                case "call":
                    System.out.println("[DEBUG] Chamando função.");
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
                    break;
                case "return":
                    System.out.println("[DEBUG] Encontrado 'return'. Finalizando execução.");
                    break;
                case "}":
                case ";":
                    advance();
                    break;
                default:
                    throw new RuntimeException("Erro de sintaxe: declaração inesperada " + currentToken);
            }
        } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            // Lógica de identificador extraída
            identifierProcessor.processIdentifier();
        } else if (currentToken.getType() == Token.TokenType.DELIMITER &&
                (currentToken.getValue().equals("}") || currentToken.getValue().equals(";"))) {
            advance();
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado KEYWORD ou IDENTIFIER mas encontrado " +
                    currentToken.getType() + " " + currentToken.getValue());
        }
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
