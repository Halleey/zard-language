package editor.functions;

import editor.translate.Parser;

public class SubstituteVariable {
    private final Parser parser;

    public SubstituteVariable(Parser parser) {
        this.parser = parser;
    }


    public String substituirVariaveis(String instrucoes) {
        StringBuilder resultado = new StringBuilder();
        boolean dentroAspas = false; // Variável para controlar quando estamos dentro de aspas

        // Dividir a instrução em caracteres individuais para percorrer com controle de aspas
        StringBuilder palavra = new StringBuilder();
        for (int i = 0; i < instrucoes.length(); i++) {
            char c = instrucoes.charAt(i);

            if (c == '\"') { // Verifica se encontramos aspas
                if (dentroAspas) {
                    // Se já estamos dentro de aspas, terminamos a parte literal
                    resultado.append(palavra);
                    palavra.setLength(0); // Limpar palavra para próximo uso
                } else {
                    // Se não estamos dentro de aspas, começamos a parte literal
                    // Não adiciona as aspas ao resultado
                }
                dentroAspas = !dentroAspas; // Alterna entre dentro e fora de aspas
            } else if (c == ' ' && !dentroAspas) { // Se estivermos fora de aspas e encontrar espaço
                if (palavra.length() > 0) {
                    // Substitui as variáveis apenas fora de aspas
                    String variavel = palavra.toString().trim();
                    if (parser.getVariableValues().containsKey(variavel)) {
                        resultado.append(parser.getVariableValues().get(variavel));
                    } else {
                        resultado.append(variavel); // Caso não seja uma variável, adiciona a palavra
                    }
                    palavra.setLength(0); // Limpar palavra após processar
                }
                resultado.append(" "); // Adiciona o espaço entre palavras
            } else {
                palavra.append(c); // Adiciona o caractere à palavra atual
            }
        }

        // Processar a última palavra fora de aspas, caso exista
        if (palavra.length() > 0) {
            String variavel = palavra.toString().trim();
            if (parser.getVariableValues().containsKey(variavel)) {
                resultado.append(parser.getVariableValues().get(variavel));
            } else {
                resultado.append(variavel);
            }
        }

        return resultado.toString();
    }
}
