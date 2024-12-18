package editor.functions;

public class ValidateArgs {
    public boolean validarTipo(String tipoEsperado, Object argumento) {
        switch (tipoEsperado) {
            case "int":
                return argumento instanceof Integer;
            case "double":
                return argumento instanceof Double;
            case "string":
                return argumento instanceof String;
            case "boolean":
                return  argumento instanceof  Boolean;
            default:
                throw new RuntimeException("Tipo desconhecido: " + tipoEsperado);
        }
    }
}
