package editor.list;

import editor.translate.Parser;
import editor.translate.Token;

public class ListGet {

    private final Parser parser;
    private final String nameList;

    public ListGet(String nameList, Parser parser) {
        this.nameList = nameList;
        this.parser = parser;
    }

    public void execute() {
        if (!parser.getCurrentToken().getType().equals(Token.TokenType.METHODS)) {
            throw new RuntimeException("Erro de sintaxe: esperado um método após o ponto.");
        }
        String methodName = parser.getCurrentToken().getValue();
        parser.advance();

        if (methodName.equals("get")) {
            handleGetItemList();
        } else {
            throw new RuntimeException("Erro: método desconhecido '" + methodName + "' para listas.");
        }
    }

    private void handleGetItemList() {
        validateAndConsume("(");
        Object indexObj = parser.expression();
        validateAndConsume(")");

        if (!(indexObj instanceof Integer)) {
            throw new RuntimeException("Erro: o índice deve ser um número inteiro.");
        }
        int index = (Integer) indexObj;

        ListStatement listStatement = (ListStatement) parser.getVariableValues().get(nameList);
        if (listStatement == null) {
            throw new RuntimeException("Erro: a variável '" + nameList + "' não existe ou não é uma lista.");
        }

        try {
            Object item = listStatement.get(index);
            System.out.println("[DEBUG] Elemento obtido da lista: " + item);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Erro: índice fora dos limites da lista.");
        }
    }

    private void validateAndConsume(String expected) {
        if (!parser.getCurrentToken().getValue().equals(expected)) {
            throw new RuntimeException(
                    "Erro de sintaxe: esperado '" + expected + "' mas encontrado '" + parser.getCurrentToken().getValue() + "'."
            );
        }
        parser.advance();
    }
}