package editor.list;

import editor.translate.Parser;
import editor.translate.Token;

import java.util.ArrayList;
import java.util.List;


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
        switch (methodName) {
            case "add" ->handleAdd(listStatement);
            case "addAll" -> handleAddAll(listStatement);
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

    private void handleAddAll(ListStatement listStatement) {
        List<Object> elements = new ArrayList<>();

        while (!parser.getCurrentToken().getValue().equals(")")) {
            elements.add(parser.expression());

            // separando itens por virgula
            if (parser.getCurrentToken().getValue().equals(",")) {
                parser.advance();
            } else {
                break;
            }
        }
        listStatement.addAll(elements);
        System.out.println("[DEBUG] Elementos adicionados à lista: " + elements);
    }


}
