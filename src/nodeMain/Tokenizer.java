package nodeMain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    public static List<String> tokenize(String codigo) {
        List<String> tokens = new ArrayList<>();

        // Expressão regular para identificar tokens válidos
        Pattern pattern = Pattern.compile(
                "(\\d+\\.\\d+|\\d+|\"[^\"]*\"|[a-zA-Z_][a-zA-Z_0-9]*|[=;])"
        );
        Matcher matcher = pattern.matcher(codigo);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }
}