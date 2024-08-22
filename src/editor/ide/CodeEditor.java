package editor.ide;

import editor.translate.Lexer;
import editor.translate.Parser;
import editor.Token;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class CodeEditor extends JFrame {

    private JTextArea codeArea;
    private JTextArea consoleArea;
    private JButton runButton;
    private JButton clearButton;
    private JButton toggleConsoleButton;
    private boolean isConsoleVisible = true;

    public CodeEditor() {
        // Configurações do editor de código
        codeArea = new JTextArea(20, 60);
        codeArea.setBackground(new Color(30, 30, 30));
        codeArea.setForeground(Color.LIGHT_GRAY);
        codeArea.setCaretColor(Color.WHITE);
        JScrollPane codeScrollPane = new JScrollPane(codeArea);

        consoleArea = new JTextArea(10, 60);
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(20, 20, 20));
        consoleArea.setForeground(Color.LIGHT_GRAY); // Texto claro
        consoleArea.setCaretColor(Color.BLACK); // Cor do cursor
        JScrollPane consoleScrollPane = new JScrollPane(consoleArea);

        // Configurações dos botões
        runButton = new JButton("Executar");
        runButton.setBackground(new Color(8, 13, 114));
        runButton.setForeground(Color.WHITE);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCode();
            }
        });

        clearButton = new JButton("Limpar");
        clearButton.setBackground(new Color(221, 1, 1)); // Fundo vermelho
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearConsole();
            }
        });

        toggleConsoleButton = new JButton("Exibir/Esconder Console");
        toggleConsoleButton.setBackground(new Color(60, 60, 60)); // Fundo cinza
        toggleConsoleButton.setForeground(Color.WHITE); // Texto branco
        toggleConsoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleConsoleVisibility(consoleScrollPane);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(toggleConsoleButton);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(codeScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(consoleScrollPane, BorderLayout.SOUTH);

        add(panel);
        setTitle("Zard Editor");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void clearConsole() {
        consoleArea.setText("");
    }

    private void toggleConsoleVisibility(JScrollPane consoleScrollPane) {
        isConsoleVisible = !isConsoleVisible;
        consoleScrollPane.setVisible(isConsoleVisible);
        pack();
    }

    private void executeCode() {
        try {
            String code = codeArea.getText();

            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens, consoleArea);

            parser.parse();

            appendToConsole("Código executado com sucesso!");

        } catch (Exception e) {
            appendToConsole("Erro ao executar o código: " + e.getMessage());
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
