package editor.list;

import editor.translate.Parser;
import editor.translate.Token;

public class ListRemove {

    private final Parser parser;
    private String listName;
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

        if(!parser.getCurrentToken().getValue().equals("(")) {
            throw new RuntimeException("Esperado (");
        }
        ListStatement listStatement = (ListStatement) parser.getVariableValues().get(listName);
        switch (methodName) {
            case "remove" -> removeItem(listStatement);
            default -> throw new RuntimeException("Erro: método desconhecido '" + methodName + "'.");
        }

        parser.advance();
    }

    public void removeItem(ListStatement listStatement) {

        Object listItem = parser.expression();
        listStatement.remove(listItem);
        System.out.println("ITEM REMOVIDO DA LISTA"+ listItem);
    }

}
