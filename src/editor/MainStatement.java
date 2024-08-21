package editor;

import editor.translate.Parser;

public class MainStatement {
    Parser parser;
    public MainStatement(Parser parser) {
        this.parser = parser;
    }


    public void execute() {
        parser.eat(Token.TokenType.KEYWORD);
        parser.eat(Token.TokenType.DELIMITER);//(
        parser.eat(Token.TokenType.DELIMITER);//)
        parser.eat(Token.TokenType.DELIMITER);//{
        parser.parseBlock();

    }
}
