package editor.functions;
import editor.translate.Parser;

public class ArithmeticVariable {
    private final Parser parser;

    public ArithmeticVariable(Parser parser) {
        this.parser = parser;
    }


    public void processarVariavel(String instrucaoStr, String tipo) {
        String resto = instrucaoStr.substring(tipo.length()).trim();
        String[] partes = resto.split("=");
        String nomeVariavel = partes[0].trim();
        
        Object valor = null;

        if (partes.length == 2) {
            String expressao = partes[1].trim();

            if (expressao.contains("+") || expressao.contains("-") || expressao.contains("*") || expressao.contains("/")) {
                valor = calcularExpressao(expressao, tipo);
            } else if (expressao.equals(nomeVariavel + "++") || expressao.equals("++" + nomeVariavel)) {
                valor = calcularIncremento(nomeVariavel, 1);
            } else if (expressao.equals(nomeVariavel + "--") || expressao.equals("--" + nomeVariavel)) {
                valor = calcularIncremento(nomeVariavel, -1);
            } else {
                valor = switch (tipo) {
                    case "int" -> Integer.parseInt(expressao);
                    case "double" -> Double.parseDouble(expressao);
                    case "boolean" -> Boolean.parseBoolean(expressao);
                    case "string" -> {
                        // Remover aspas caso estejam presentes
                        String valorStr = expressao;
                        if (expressao.startsWith("\"") && expressao.endsWith("\"")) {
                            valorStr = expressao.substring(1, expressao.length() - 1);
                        }
                        yield valorStr;
                    }
                    default -> valor;
                };
            }
        }

        // operação `a++;` ou `a--;`
        else if (partes.length == 1) {
            if (nomeVariavel.endsWith("++")) {
                nomeVariavel = nomeVariavel.substring(0, nomeVariavel.length() - 2).trim();
                valor = calcularIncremento(nomeVariavel, 1);
            } else if (nomeVariavel.endsWith("--")) {
                nomeVariavel = nomeVariavel.substring(0, nomeVariavel.length() - 2).trim();
                valor = calcularIncremento(nomeVariavel, -1);
            }
        }
        if (valor != null) {
            parser.getVariableValues().put(nomeVariavel, valor);
            System.out.println("Variável " + nomeVariavel + " armazenada com valor " + valor);
        }
    }

    public Object calcularExpressao(String expressao, String tipo) {
        String[] operandos = expressao.split("\\s*([+\\-*/])\\s*");
        String operador = expressao.replaceAll("[^+\\-*/]", "").trim();

        double resultado = switch (tipo) {
            case "int", "double" -> {
                double op1 = obterValorComoDouble(operandos[0].trim());
                double op2 = obterValorComoDouble(operandos[1].trim());
                yield  calcularResultado(op1, op2, operador);
            }
            default -> 0;
        };
        return tipo.equals("int") ? (int) resultado : resultado;
    }

    public double obterValorComoDouble(String operando) {
        // Se o operando é uma variável, pegue seu valor; caso contrário, converta diretamente
        if (parser.getVariableValues().containsKey(operando)) {
            return ((Number) parser.getVariableValues().get(operando)).doubleValue();
        } else {
            return Double.parseDouble(operando);
        }
    }

    public double calcularResultado(double op1, double op2, String operador) {
        return switch (operador) {
            case "+" -> op1 + op2;
            case "-" -> op1 - op2;
            case "*" -> op1 * op2;
            case "/" -> op1 / op2;
            default -> 0;
        };
    }

    public Object calcularIncremento(String variavel, int plus) {
        Object valorAtual = parser.getVariableValues().get(variavel);
        if (valorAtual instanceof Integer) {
            return (Integer) valorAtual + plus;
        } else if (valorAtual instanceof Double) {
            return (Double) valorAtual + plus;
        }
        return null;
    }
}
