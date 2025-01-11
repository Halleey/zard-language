package editor.list;

import editor.translate.Parser;
import editor.translate.Token;

import java.util.ArrayList;
import java.util.List;

public class ListHandler {
    private final Parser parser;

    public ListHandler(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        parser.eat(Token.TokenType.KEYWORD);
        String listName = parser.getCurrentToken().getValue();
        parser.advance();

        // Verifica se é uma inicialização
        if (parser.getCurrentToken().getValue().equals("=")) {
            parser.advance();

            // Inicializa a lista
            if (parser.getCurrentToken().getValue().equals("[")) {
                parser.advance();

                List<Object> elements = new ArrayList<>();

                while (!parser.getCurrentToken().getValue().equals("]")) {
                    // Adiciona o elemento à lista
                    elements.add(parser.expression());

                    // Após cada elemento, verifica o próximo token
                    if (!parser.getCurrentToken().getValue().equals("]")) {
                        if (parser.getCurrentToken().getValue().equals(",")) {
                            parser.advance(); // Avança após a vírgula
                        } else {
                            throw new RuntimeException("Erro de sintaxe: Esperado ',' entre os elementos da lista.");
                        }
                    }
                }
                parser.advance(); // Avança após ']'

                parser.getVariableValues().put(listName, new ListStatement(elements));

                System.out.println("[DEBUG] Lista salva: " + listName + " = " + elements);
            } else {
                throw new RuntimeException("Erro de sintaxe: esperado '[' para inicializar a lista.");
            }
        } else {
            parser.getVariableValues().put(listName, new ListStatement());
            System.out.println("[DEBUG] Lista salva: " + listName + " = []");
        }
    }
}
