package editor.process;

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


        Pattern definePattern = Pattern.compile("define\\s+(\\w+)\\s+(.+)");

        // Associa o padrão ao texto de entrada usando um Matcher
        Matcher matcher = definePattern.matcher(input);
        // StringBuffer que armazenará o texto de entrada sem as diretivas #define
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {

            String macroName = matcher.group(1);

            // Captura o valor associado à macro (segundo grupo de captura)
            String macroValue = matcher.group(2);
            macros.put(macroName, macroValue);

            // Remove a linha #define do texto e adiciona o restante ao buffer
            matcher.appendReplacement(buffer, "");
        }

        // Adiciona ao buffer qualquer texto remanescente após o último #define encontrado
        matcher.appendTail(buffer);

        // Converte o buffer (sem diretivas #define) em uma string
        String processedInput = buffer.toString();

        // Itera sobre todas as macros armazenadas no mapa
        for (Map.Entry<String, String> entry : macros.entrySet()) {

            String macroName = entry.getKey();


            String macroValue = entry.getValue();

            processedInput = processedInput.replaceAll(
                    "\\b" + Pattern.quote(macroName) + "\\b",
                    Matcher.quoteReplacement(macroValue)
            );
        }

        // Retorna o texto processado com todas as macros substituídas
        return processedInput;
    }




}