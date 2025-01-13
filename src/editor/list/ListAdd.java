package editor.list;

import editor.translate.Parser;
import editor.translate.Token;

public class ListAdd {

    private final Parser parser;
    private final String listName;

    public ListAdd(Parser parser, String listName) {
        this.parser = parser;
        this.listName = listName;
    }

    public void execute() {
        // Recupera o nome do método
        if (!parser.getCurrentToken().getType().equals(Token.TokenType.METHODS)) {
            throw new RuntimeException("Erro de sintaxe: esperado um método após o ponto.");
        }
        String methodName = parser.getCurrentToken().getValue();
        parser.advance(); // Consome o nome do método

        // Verifica se o próximo token é "("
        if (!parser.getCurrentToken().getValue().equals("(")) {
            throw new RuntimeException("Erro de sintaxe: esperado '(' após o método '" + methodName + "'.");
        }
        parser.advance(); // Consome o token "("

        // Recupera a lista do escopo de variáveis usando o nome da lista
        ListStatement listStatement = (ListStatement) parser.getVariableValues().get(listName);
        if (listStatement == null) {
            throw new RuntimeException("Erro: a variável '" + listName + "' não existe ou não é uma lista.");
        }

        // Lida com métodos específicos
        switch (methodName) {
            case "add" -> handleAdd(listStatement);
            case "size" -> handleSize(listStatement);
            default -> throw new RuntimeException("Erro: método desconhecido '" + methodName + "'.");
        }

        // Verifica se o próximo token é ")"
        if (!parser.getCurrentToken().getValue().equals(")")) {
            throw new RuntimeException("Erro de sintaxe: esperado ')' para fechar o método.");
        }
        parser.advance(); // Consome o token ")"
    }

    private void handleAdd(ListStatement listStatement) {
        // Obtém o elemento a ser adicionado
        Object element = parser.expression();

        // Adiciona o elemento à lista
        listStatement.getObjectList().add(element);

        System.out.println("[DEBUG] Elemento adicionado à lista: " + element);
    }

    public void handleSize(ListStatement listStatement) {
        System.out.println("invocando tamanho da lista");
        System.out.println(listStatement.size());
    }
}
