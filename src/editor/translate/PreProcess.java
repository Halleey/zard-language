package editor.translate;

import java.util.Map;

import java.util.*;
import java.util.regex.*;

public class PreProcess {
    private final String input;

    public PreProcess(String input) {
        this.input = input;
    }

    // Método principal para processar o código
    public String preprocess() {
        return processMacros(input);
    }

    // Processa diretivas #define e substitui as macros
    private String processMacros(String input) {
        Map<String, String> macros = new HashMap<>();

        // Captura todas as diretivas #define
        Pattern definePattern = Pattern.compile("#define\\s+(\\w+)\\s+(.+)");
        Matcher matcher = definePattern.matcher(input);
        StringBuffer buffer = new StringBuffer();

        // Coleta as macros definidas
        while (matcher.find()) {
            String macroName = matcher.group(1);
            String macroValue = matcher.group(2);
            macros.put(macroName, macroValue);
            matcher.appendReplacement(buffer, ""); // Remove as diretivas do código
        }
        matcher.appendTail(buffer);

        String processedInput = buffer.toString();

        // Substitui todas as ocorrências das macros no código
        for (Map.Entry<String, String> entry : macros.entrySet()) {
            String macroName = entry.getKey();
            String macroValue = entry.getValue();
            processedInput = processedInput.replaceAll("\\b" + Pattern.quote(macroName) + "\\b", Matcher.quoteReplacement(macroValue));
        }

        return processedInput;
    }
}
