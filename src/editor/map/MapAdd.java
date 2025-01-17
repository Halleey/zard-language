//package editor.map;
//
//import editor.translate.Parser;
//import editor.translate.Token;
//
//public class MapAdd {
//    private final Parser parser;
//    private String mapName;
//
//    public MapAdd(Parser parser ) {
//        this.parser = parser;
//    }
//
//    public void execute() {
//
//        if(!parser.getCurrentToken().getType().equals(Token.TokenType.METHODS)) {
//            throw new RuntimeException("Erro de sintaxe: esperado um método após o ponto.");
//        }
//
//        String methodName = parser.getCurrentToken().getValue();
//        parser.advance();
//
//        if (!parser.getCurrentToken().getValue().equals("(")) {
//            throw new RuntimeException("Erro de sintaxe: esperado '(' após o método '" + methodName + "'.");
//        }
//    }
//
//}

