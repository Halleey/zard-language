package editor.translate;

import editor.process.PreProcess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Execute {

    public static void main(String[] args) {
        try {
            // Lê o arquivo com o código-fonte
            String input = new String(Files.readAllBytes(Paths.get("src/editor/language/test.zd")));
            String basePatch = new String (Files.readAllBytes(Paths.get("src/editor/libs/Math.zd")));
            // Pré-processa o código para substituir macros
            PreProcess preprocessor = new PreProcess(input, basePatch);
            String preprocessedCode = preprocessor.preprocess();

            // Exibe o código pré-processado
            System.out.println("Código pré-processado:\n" + preprocessedCode);

            // Continua com o lexer e parser
            Lexer lexer = new Lexer(preprocessedCode);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

