package editor.list;

import editor.translate.Parser;
import editor.translate.Token;

public class ListAdd {

    private final Parser parser;

    public ListAdd(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        parser.eat(Token.TokenType.IDENTIFIER);
        String listName = parser.getCurrentToken().getValue();
        parser.advance();

        if (!parser.getCurrentToken().getValue().equals(".")) {
            throw new RuntimeException("Erro de sintaxe: esperado '.' após a invocação da lista.");
        }
        parser.advance();

        parser.eat(Token.TokenType.METHODS);
        String methodName = parser.getCurrentToken().getValue();
        parser.advance();

        if (!parser.getCurrentToken().getValue().equals("(")) {
            throw new RuntimeException("Erro de sintaxe: esperado '(' após o nome do método.");
        }
        parser.advance();

        ListStatement listStatement = (ListStatement) parser.getVariableValues().get(listName);
        if (listStatement == null) {
            throw new RuntimeException("Erro: a variável '" + listName + "' não existe ou não é uma lista.");
        }

        switch (methodName) {
            case "add" -> handleAdd(listStatement);
            default -> throw new RuntimeException("Erro: método desconhecido '" + methodName + "'.");
        }

        if (!parser.getCurrentToken().getValue().equals(")")) {
            throw new RuntimeException("Erro de sintaxe: esperado ')' para fechar o método.");
        }
        parser.advance();
    }

    private void handleAdd(ListStatement listStatement) {
        // Obtém o elemento a ser adicionado
        Object element = parser.expression();

        // Adiciona o elemento à lista
        listStatement.getObjectList().add(element);

        System.out.println("[DEBUG] Elemento adicionado à lista: " + element);
    }
}

