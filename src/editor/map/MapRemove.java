package editor.map;

import editor.translate.Parser;
import editor.translate.Token;

import java.util.Map;

public class MapRemove {

    private final Parser parser;
    private String mapName;

    public MapRemove(Parser parser, String mapName) {
        this.parser = parser;
        this.mapName = mapName;
    }

    public void execute() {
        System.out.println("TOKEN ATUALMENTE ENFRENTADO"+ parser.getCurrentToken());

        if (!parser.getCurrentToken().getType().equals(Token.TokenType.METHODS)) {
            throw new RuntimeException("Erro de sintaxe: esperado um método após o ponto.");
        }

        String methodName = parser.getCurrentToken().getValue();
        parser.advance();
        System.out.println("TOKEN ATUALMENTE ENFRENTADO"+ parser.getCurrentToken());
        switch (methodName) {
            case "remove"->
                removeItem();
            case "clear"->
                removeAll();
        }
    }

    private void removeAll() {
       //recupera e limpa lista, invoque o eat em meio ao processo para que ele remova os '()' e prossiga com o codigo
        parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER);

        Map<Object, Object> map = (Map<Object, Object>) parser.getVariableValues().get(mapName);
        if(map == null){
            throw new RuntimeException("Mapa não existe");
        }
        System.out.println("Limpando mapa ---" + map);
        map.clear();
    }


    private void removeItem() {
        parser.eat(Token.TokenType.DELIMITER);
        Object indice = parser.expression();
        parser.eat(Token.TokenType.DELIMITER);

        Map<Object, Object> map = (Map<Object, Object>) parser.getVariableValues().get(mapName);
        if (map == null) {
            throw new RuntimeException("Erro: o mapa '" + mapName + "' não existe.");
        }

        if (map.containsKey(indice)) {
            map.remove(indice);
            System.out.println("Chave '" + indice + "' removida do mapa.");
        } else {
            throw new RuntimeException("Erro: chave '" + indice + "' não encontrada no mapa.");
        }
    }
}
