package editor.map;

import editor.translate.Parser;
import editor.translate.Token;

import java.util.Map;
public class MapGet {
    private final Parser parser;
    private String mapName;

    public MapGet(Parser parser, String mapName) {
        this.parser = parser;
        this.mapName = mapName;
    }

    public void execute() {
        // Verifica se há um método válido após o identificador do mapa
        if (!parser.getCurrentToken().getType().equals(Token.TokenType.METHODS)) {
            throw new RuntimeException("Erro de sintaxe: esperado um método após o ponto.");
        }

        String methodName = parser.getCurrentToken().getValue();
        parser.advance(); // Avança após o nome do método

        switch (methodName) {
            case "get":
                handleGet();
                break;
            case "getValues":
                handleValues();
                break;
            case "getKeys":
                handleKeys();
                break;
            default:
                throw new RuntimeException("Erro de sintaxe: método desconhecido '" + methodName + "' para o mapa.");
        }
    }

    private void handleGet() {
        validateAndConsume("{"); // Valida '{'

        Object key = parser.expression(); // Obtém a chave corretamente
        validateAndConsume("}");

        Map<Object, Object> map = getMap(); // Recupera o mapa

        // Verifica se a chave existe
        if (!map.containsKey(key)) {
            throw new RuntimeException("Erro de execução: chave '" + key + "' não encontrada no mapa '" + mapName + "'");
        }

        // Retorna o valor associado à chave
        System.out.println("[DEBUG] Valor retornado: " + map.get(key));
    }

    private Map<Object, Object> getMap() {
        // Recupera o mapa armazenado no parser
        Map<Object, Object> map = (Map<Object, Object>) parser.getVariableValues().get(mapName);
        if (map == null) {
            throw new RuntimeException("Erro de execução: o mapa '" + mapName + "' não foi encontrado.");
        }
        return map;
    }

    private void handleKeys() {
        System.out.println("TOKEN----" + parser.getCurrentToken());
        parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER);
        System.out.println("TOKEN----" + parser.getCurrentToken());

        Map<Object, Object> map = getMap();

        System.out.println(map.keySet());
    }

    private void handleValues() {
        parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER);
        Map<Object, Object> map = getMap();
        System.out.println(map.values());
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
