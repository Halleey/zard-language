package editor.list;

import editor.translate.Parser;
import editor.translate.Token;
public class ListRemove {

    private final Parser parser;
    private final String listName;

    public ListRemove(Parser parser, String listName) {
        this.parser = parser;
        this.listName = listName;
    }

    public void execute() {
        if (!parser.getCurrentToken().getType().equals(Token.TokenType.METHODS)) {
            throw new RuntimeException("Erro de sintaxe: esperado um método após o ponto.");
        }

        String methodName = parser.getCurrentToken().getValue();
        parser.advance();

        if (!parser.getCurrentToken().getValue().equals("(")) {
            throw new RuntimeException("Erro de sintaxe: esperado '(' após o método.");
        }
        parser.advance();


        ListStatement listStatement = (ListStatement) parser.getVariableValues().get(listName);
        if (listStatement == null) {
            throw new RuntimeException("Erro: a variável '" + listName + "' não existe ou não é uma lista.");
        }

        switch (methodName) {
            case "remove" -> removeItem(listStatement);
            case "clear" -> cleanList(listStatement);
            default -> throw new RuntimeException("Erro: método desconhecido '" + methodName + "'.");
        }

        if (!parser.getCurrentToken().getValue().equals(")")) {
            throw new RuntimeException("Erro de sintaxe: esperado ')' para fechar o método.");
        }
        parser.advance();
    }

    public void removeItem(ListStatement listStatement) {
        Object listItem = parser.expression(); // Obtém o item a ser removido
        if (listStatement.remove(listItem)) {
            System.out.println("[DEBUG] Item removido da lista: " + listItem);
        } else {
            System.out.println("[DEBUG] Item não encontrado na lista: " + listItem);
        }
    }

    public void cleanList(ListStatement listStatement) {
        listStatement.clear(); // Limpa todos os itens da lista
        System.out.println("[DEBUG] Todos os itens foram removidos da lista: " + listName);
    }
}