package editor.translate;

import editor.functions.FunctionStatement;
import editor.functions.ValidateArgs;

import java.util.List;
import java.util.Map;

public class ValidateFunction {


    private final Parser parser;

    public ValidateFunction (Parser parser) {
        this.parser = parser;
    }
    public void validarTiposDeArgumentos(FunctionStatement func, List<Object> argumentos, ValidateArgs validateArgs) {
        Map<String, String> parametrosTipos = func.variablesFunction;
        List<String> parametros = func.getParametros();

        if (parametros.size() != argumentos.size()) {
            throw new RuntimeException("Número de argumentos incorreto para a função: " + func.getNome());
        }

        for (int i = 0; i < parametros.size(); i++) {
            String nomeParametro = parametros.get(i);
            String tipoEsperado = parametrosTipos.get(nomeParametro); // Busca o tipo esperado do parâmetro com base no nome.
            Object argumento = argumentos.get(i); // Obtém o argumento fornecido para o parâmetro atual.

            if (!validateArgs.validarTipo(tipoEsperado, argumento)) {
                throw new RuntimeException("Tipo incompatível para o parâmetro '" + nomeParametro +
                        "': esperado " + tipoEsperado + ", recebido " + argumento.getClass().getSimpleName());
            }
        }
    }


}
