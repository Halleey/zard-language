package editor.ide;

import editor.translate.Lexer;
import editor.translate.Parser;
import editor.Token;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CodeEditor extends JFrame {

    private JTextArea codeArea;
    private JTextArea consoleArea;
    private JButton runButton;

    public CodeEditor() {
        // Configurações do editor de código
        codeArea = new JTextArea(20, 60);
        codeArea.setBackground(new Color(30, 30, 30)); // Fundo escuro
        codeArea.setForeground(Color.LIGHT_GRAY); // Texto claro
        codeArea.setCaretColor(Color.WHITE); // Cor do cursor
        JScrollPane codeScrollPane = new JScrollPane(codeArea);

        // Configurações do console
        consoleArea = new JTextArea(10, 60);
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(20, 20, 20)); // Fundo escuro
        consoleArea.setForeground(Color.LIGHT_GRAY); // Texto claro
        consoleArea.setCaretColor(Color.WHITE); // Cor do cursor
        JScrollPane consoleScrollPane = new JScrollPane(consoleArea);

        // Configurações do botão
        runButton = new JButton("Executar");
        runButton.setBackground(new Color(50, 150, 50)); // Fundo verde
        runButton.setForeground(Color.WHITE); // Texto branco
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCode();
            }
        });

        // Configurações do painel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(codeScrollPane, BorderLayout.CENTER);
        panel.add(runButton, BorderLayout.NORTH); // Botão no topo
        panel.add(consoleScrollPane, BorderLayout.SOUTH); // Console na parte inferior

        add(panel);
        setTitle("Zard Editor");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void executeCode() {
        try {
            // Caminho do arquivo
            String filePath = "src/editor/test.zd";

            // Lê o código do arquivo
            String code = new String(Files.readAllBytes(Paths.get(filePath)));

            // Cria o lexer e o parser
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens, consoleArea); // Passa a referência do editor

            // Executa o código
            parser.parse();

            appendToConsole("Código executado com sucesso!");

        } catch (IOException e) {
            appendToConsole("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void appendToConsole(String message) {
        consoleArea.append(message + "\n");
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CodeEditor().setVisible(true);
            }
        });
    }
}
