package nodeMain;

import editor.nodes.ASTNode;
import nodeVariables.TypedValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
public class MainNode {
    public static void main(String[] args) {
        try {
            // Ler o arquivo como uma string
            String codigo = new String(Files.readAllBytes(Paths.get("src/nodeMain/teste.zd")));

            // Dividir o código em tokens (simples: por espaços e quebras de linha)
            List<String> tokens = Tokenizer.tokenize(codigo);

            // Criar parser e rodar AST
            ParserNode parser = new ParserNode(tokens);
            List<ASTNode> astNodes = new ArrayList<>();
            Map<String, TypedValue> variables = new HashMap<>();

            while (parser.getCurrentToken() != null) {
                ASTNode node = parser.parseStatement();
                if (node != null) {
                    astNodes.add(node);
                }
            }

            // Executar AST
            for (ASTNode node : astNodes) {
                node.evaluate(variables);
            }

            // Exibir valores finais das variáveis
            System.out.println("Valores finais das variáveis:");
            variables.forEach((key, value) -> System.out.println(key + " = " + value.getValue()));

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

}