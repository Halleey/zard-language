package editor.map;

import editor.translate.Parser;
import editor.translate.Token;

import java.util.Map;

public class MapAdd {
    private final Parser parser;
    private String mapName;

    public MapAdd(Parser parser, String mapName) {
        this.parser = parser;
        this.mapName = mapName;
    }

    public void execute() {

        // Verifica se o método após o ponto é "put"
        if (!parser.getCurrentToken().getType().equals(Token.TokenType.METHODS)) {
            throw new RuntimeException("Erro de sintaxe: esperado um método após o ponto.");
        }

        String methodName = parser.getCurrentToken().getValue();
        parser.advance();  // Avança para o próximo token (deve ser '(')

        if (!methodName.equals("set")) {
            throw new RuntimeException("Erro de sintaxe: método desconhecido '" + methodName + "' para o mapa.");
        }

        // Verifica se o próximo token é um parêntese de abertura '{'
        if (!parser.getCurrentToken().getValue().equals("{")) {
            throw new RuntimeException("Erro de sintaxe: esperado '(' após o método 'put'.");
        }

        parser.advance();  // Avança após o '{'

        // A primeira expressão é a chave
        Object key = parser.expression();

        // Verifica se o próximo token é a vírgula ':'
        if (!parser.getCurrentToken().getValue().equals(":")) {
            throw new RuntimeException("Erro de sintaxe: esperado ',' entre a chave e o valor.");
        }
        parser.advance();  // Avança após a vírgula

        // A segunda expressão é o valor
        Object value = parser.expression();

        // Verifica se o próximo token é o parêntese de fechamento '}'
        if (!parser.getCurrentToken().getValue().equals("}")) {
            throw new RuntimeException("Erro de sintaxe: esperado ')' após o valor.");
        }

        parser.advance();  // Avança após o '}'

        // Recupera o mapa armazenado no parser
        Map<Object, Object> map = (Map<Object, Object>) parser.getVariableValues().get(mapName);

        if (map == null) {
            throw new RuntimeException("Erro de execução: o mapa '" + mapName + "' não foi encontrado.");
        }

        // Adiciona a chave e o valor ao mapa
        map.put(key, value);
        System.out.println("[DEBUG] Adicionado ao mapa: " + key + " = " + value);
    }
}

