package editor;

import editor.translate.Parser;

import java.util.Scanner;

public class InputStatement {
    private Parser parser;
    public InputStatement(Parser parser) {
        this.parser = parser;
    }
    public void execute() {
        parser.eat(Token.TokenType.KEYWORD);
        if(parser.getCurrentToken().getType() != Token.TokenType.IDENTIFIER){
            throw new RuntimeException("Erro de sintaxe: É esperado um identificador");
        }
        String name = parser.getCurrentToken().getValue();
        parser.advance();
        Scanner entrada = new Scanner(System.in);
        String input = entrada.nextLine();
        Object value;
        try {
            if (parser.getVariableValues().containsKey(name)) {
                Object existingValue = parser.getVariableValues().get(name);
                if (existingValue instanceof Integer) {
                    value = Integer.parseInt(input);
                } else if (existingValue instanceof Double) {
                    value = Double.parseDouble(input);
                } else {
                    value = input;
                }
            } else {
                value = input;
            }
        } catch (NumberFormatException e) {
            value = input;
        }
        parser.getVariableValues().put(name, value);
        parser.eat(Token.TokenType.DELIMITER);
    }

    }
