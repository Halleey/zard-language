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
        // Verifica se o método após o ponto é válido
        if (!parser.getCurrentToken().getType().equals(Token.TokenType.METHODS)) {
            throw new RuntimeException("Erro de sintaxe: esperado um método após o ponto.");
        }

        String methodName = parser.getCurrentToken().getValue();
        parser.advance(); // Avança após o método

        // Redireciona para o método apropriado
        switch (methodName) {
            case "set":
                handleSet();
                break;
//            case "setOrder":
//                handleSetOrder();
//                break;
            default:
                throw new RuntimeException("Erro de sintaxe: método desconhecido '" + methodName + "' para o mapa.");
        }
    }

    private void handleSet() {
        validateAndConsume("{"); // Valida '{'

        Object key = parser.expression(); // Extrai a chave
        validateAndConsume(":");
        Object value = parser.expression();
        validateAndConsume("}");

        Map<Object, Object> map = getMap(); // Recupera o mapa
        map.put(key, value); // Adiciona a chave e o valor ao mapa
        System.out.println("[DEBUG] Adicionado ao mapa: " + key + " = " + value);
    }

    private Map<Object, Object> getMap() {
        // Recupera o mapa armazenado no parser
        Map<Object, Object> map = (Map<Object, Object>) parser.getVariableValues().get(mapName);
        if (map == null) {
            throw new RuntimeException("Erro de execução: o mapa '" + mapName + "' não foi encontrado.");
        }
        return map;
    }

    private void validateAndConsume(String expected) {
        // Verifica e consome o token esperado
        if (!parser.getCurrentToken().getValue().equals(expected)) {
            throw new RuntimeException(
                    "Erro de sintaxe: esperado '" + expected + "' mas encontrado '" + parser.getCurrentToken().getValue() + "'."
            );
        }
        parser.advance();
    }
}

