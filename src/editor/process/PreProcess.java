package editor.process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.*;
import java.util.regex.*;


public class PreProcess {
    private final String input;
    private final String filePatch;
    public PreProcess(String input, String filePatch) {
        this.input = input;

        this.filePatch = filePatch;
    }

    public String preprocess() {
        // Primeiro, processa os arquivos incluídos
        String processedIncludes = processIncludes(input);
        // Depois, processa macros no código já incluído
        return processMacros(processedIncludes);
    }


    private String processMacros(String input) {
        Map<String, String> macros = new HashMap<>();


        Pattern definePattern = Pattern.compile("define\\s+(\\w+)\\s+(.+)");


        Matcher matcher = definePattern.matcher(input);

        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {

            String macroName = matcher.group(1);

            String macroValue = matcher.group(2);
            macros.put(macroName, macroValue);
            matcher.appendReplacement(buffer, "");
        }

        matcher.appendTail(buffer);

        String processedInput = buffer.toString();


        for (Map.Entry<String, String> entry : macros.entrySet()) {

            String macroName = entry.getKey();
            String macroValue = entry.getValue();

            processedInput = processedInput.replaceAll(
                    "\\b" + Pattern.quote(macroName) + "\\b",
                    Matcher.quoteReplacement(macroValue)
            );
        }
        return processedInput;
    }

    private String processIncludes(String input) {
        // Pattern para detectar import "arquivo.zd"
        Pattern includePattern = Pattern.compile("import\\s+\"([^\"]+)\"");
        Matcher matcher = includePattern.matcher(input);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String includeFile = matcher.group(1);
            String includedCode = loadIncludeFile(includeFile); // Carrega o conteúdo do arquivo incluído
            if (includedCode.isEmpty()) {
                System.err.println("Erro: Arquivo não encontrado ou vazio: " + includeFile);
            }
            matcher.appendReplacement(buffer, includedCode);   // Substitui a diretiva pelo conteúdo do arquivo
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }


    private String loadIncludeFile(String fileName) {
        Path filePath = Paths.get(filePatch, fileName);
        try {
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            System.err.println("Erro ao incluir arquivo: " + fileName);
            return "";
        }
    }
}