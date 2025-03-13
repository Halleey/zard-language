package nodeMain;

import editor.nodes.ASTNode;
import nodeVariables.LiteralNode;
import nodeVariables.VariableAssignmentNode;
import nodeVariables.VariableDeclarationNode;

import java.util.*;

public class ParserNode {
    private List<String> tokens;
    private int position;

    public ParserNode(List<String> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    public String getCurrentToken() {
        return position < tokens.size() ? tokens.get(position) : null;
    }

    private void eat(String expected) {
        if (expected.equals(getCurrentToken())) {
            position++;
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado '" + expected + "' mas encontrado '" + getCurrentToken() + "'");
        }
    }

    public ASTNode parseStatement() {
        String token = getCurrentToken();
        if (token == null) return null;

        if (token.matches("int|double|string|boolean")) {
            return parseVariableDeclaration();
        } else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return parseVariableAssignment();
        }
        throw new RuntimeException("Erro de sintaxe: expressão inesperada '" + token + "'");
    }

    private ASTNode parseVariableDeclaration() {
        String type = getCurrentToken();
        eat(type);
        String name = getCurrentToken();
        eat(name);

        ASTNode value = null;
        if (getCurrentToken() != null && getCurrentToken().equals("=")) {
            eat("=");
            value = parseExpression();
        }

        eat(";"); // Esperamos um delimitador ";"
        return new VariableDeclarationNode(type, name, value);
    }

    private ASTNode parseVariableAssignment() {
        String name = getCurrentToken();
        eat(name);
        eat("="); // Esperamos um '='
        ASTNode value = parseExpression();
        eat(";");

        return new VariableAssignmentNode(name, value);
    }

    private ASTNode parseExpression() {
        String token = getCurrentToken();
        if (token.matches("-?\\d+")) {
            eat(token);
            return new LiteralNode(Integer.parseInt(token));
        } else if (token.matches("-?\\d+\\.\\d+")) {
            eat(token);
            return new LiteralNode(Double.parseDouble(token));
        } else if (token.startsWith("\"") && token.endsWith("\"")) {
            eat(token);
            return new LiteralNode(token.substring(1, token.length() - 1));
        } else if (token.equals("true") || token.equals("false")) {
            eat(token);
            return new LiteralNode(Boolean.parseBoolean(token));
        }

        throw new RuntimeException("Erro na expressão: token inesperado '" + token + "'");
    }
}

