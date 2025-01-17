package editor.map;

import editor.translate.Parser;
import editor.translate.Token;
import java.util.HashMap;
import java.util.Map;

public class MapHandler {
    private final Parser parser;

    public MapHandler(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        parser.eat(Token.TokenType.KEYWORD); // Consome a palavra-chave 'map'
        String mapName = parser.getCurrentToken().getValue();
        parser.advance();

        // Verifica se é uma inicialização
        if (parser.getCurrentToken().getValue().equals("=")) {
            parser.advance();

            // Inicializa o mapa
            if (parser.getCurrentToken().getValue().equals("{")) {
                parser.advance();

                Map<Object, Object> entries = new HashMap<>();

                while (!parser.getCurrentToken().getValue().equals("}")) {
                    // Obtém a chave como expressão
                    Object key = parser.expression();

                    if (!parser.getCurrentToken().getValue().equals(":")) {
                        throw new RuntimeException("Erro de sintaxe: esperado ':' após a chave.");
                    }
                    parser.advance(); // Avança após ':'

                    // Obtém o valor como expressão
                    Object value = parser.expression();

                    // Trata o caso de valores literais ou variáveis inexistentes
                    if (value == null) {
                        throw new RuntimeException("Erro de execução: valor não resolvido para a chave '" + key + "'.");
                    }

                    // Adiciona a entrada ao mapa
                    entries.put(key, value);

                    // Após cada par, verifica o próximo token
                    if (!parser.getCurrentToken().getValue().equals("}")) {
                        if (parser.getCurrentToken().getValue().equals(",")) {
                            parser.advance(); // Avança após a vírgula
                        } else {
                            throw new RuntimeException("Erro de sintaxe: esperado ',' entre as entradas do mapa ou '}' para fechar.");
                        }
                    }
                }
                parser.advance(); // Avança após '}'

                // Salva o mapa como variável
                parser.getVariableValues().put(mapName, entries);

                System.out.println("[DEBUG] Mapa salvo: " + mapName + " = " + entries);
            } else {
                throw new RuntimeException("Erro de sintaxe: esperado '{' para inicializar o mapa.");
            }
        } else {
            // Cria um mapa vazio se não houver inicialização
            parser.getVariableValues().put(mapName, new HashMap<>());
            System.out.println("[DEBUG] Mapa salvo: " + mapName + " = {}");
        }
    }
}
