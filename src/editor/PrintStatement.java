package editor;

import editor.translate.Parser;

public class PrintStatement {

    private final Parser parser;

    public PrintStatement(Parser parser) {
        this.parser = parser;
    }

    public void execute() {
        parser.eat(Token.TokenType.KEYWORD);
        parser.eat(Token.TokenType.DELIMITER);
       Object result = printExpression();
       System.out.println(result);
       parser.eat(Token.TokenType.DELIMITER);
        parser.eat(Token.TokenType.DELIMITER);
    }

    public Object printExpression() {
        StringBuilder sb = new StringBuilder();

        while (!parser.getCurrentToken().getValue().equals(")")) {
            if (parser.getCurrentToken().getType() == Token.TokenType.STRING) {
                sb.append(parser.getCurrentToken().getValue());
                parser.eat(Token.TokenType.STRING);
            }

            else if (parser.getCurrentToken().getType() == Token.TokenType.IDENTIFIER) {
                String identifier = parser.getCurrentToken().getValue();
                sb.append(parser.getVariableValues().getOrDefault(identifier, identifier));
                parser.eat(Token.TokenType.IDENTIFIER);
            } else if (parser.getCurrentToken().getType() == Token.TokenType.NUMBER) {
                String number = parser.getCurrentToken().getValue();
                sb.append(number);
                parser.eat(Token.TokenType.NUMBER);
            } else if (parser.getCurrentToken().getType() == Token.TokenType.OPERATOR && parser.getCurrentToken().getValue().equals("+")) {
                sb.append(" ");
                parser.eat(Token.TokenType.OPERATOR);

            } else {
                throw new RuntimeException("Erro de sintaxe: esperado STRING, IDENTIFIER, NUMBER ou OPERATOR mas encontrado " + parser.getCurrentToken().getType());
            }

            if (parser.getCurrentToken().getType() == Token.TokenType.DELIMITER && parser.getCurrentToken().getValue().equals(")")) {
                break;
            }
        }
        return sb.toString();
    }


}
