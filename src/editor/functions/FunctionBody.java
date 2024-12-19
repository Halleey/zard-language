package editor.functions;

import editor.translate.Parser;
import editor.translate.Token;

import java.util.ArrayList;
import java.util.List;

public class FunctionBody {

    private final Parser parser;

    public FunctionBody(Parser parser) {
        this.parser = parser;
    }

    public  List<Object> saveFunctionBody() {
        List<Object> corpo = new ArrayList<>();
        int chave = 0;

        if (parser.getCurrentToken().getValue().equals("{")) {
            chave++;
            parser.advance();

            StringBuilder instrucaoCompleta = new StringBuilder();
            while (chave > 0) {
                String tokenValue = parser.getCurrentToken().getValue();

                if (tokenValue.equals("{")) {
                    chave++;
                }

                if (tokenValue.equals("}")) {
                    chave--;
                }

                // Verifica se é uma string e mantém as aspas
                if (parser.getCurrentToken().getType() == Token.TokenType.STRING) {
                    instrucaoCompleta.append("\"").append(tokenValue).append("\""); // Adiciona as aspas manualmente
                } else {
                    instrucaoCompleta.append(tokenValue).append(" ");
                }

                parser.advance();

                if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER &&
                        parser.getCurrentToken().getValue().equals(";")) {
                    corpo.add(instrucaoCompleta.toString().trim());
                    instrucaoCompleta.setLength(0);
                    parser.advance();
                }
            }
        }
        System.out.println("Corpo da função salvo: " + corpo);
        return corpo;
    }
}
