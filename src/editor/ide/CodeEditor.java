package editor.ide;

import editor.translate.Lexer;
import editor.translate.Parser;
import editor.Token;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class CodeEditor extends JFrame {

    private JTextArea codeArea;
    private JTextArea consoleArea;
    private JButton runButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton openButton;
    private JButton newFileButton;
    private JFileChooser fileChooser;

    public CodeEditor() {

        codeArea = new JTextArea(20, 60);
        codeArea.setBackground(new Color(30, 30, 30));
        codeArea.setForeground(Color.LIGHT_GRAY);
        codeArea.setCaretColor(Color.WHITE); // Cor do cursor
        JScrollPane codeScrollPane = new JScrollPane(codeArea);

        consoleArea = new JTextArea(10, 60);
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(20, 20, 20));
        consoleArea.setForeground(Color.LIGHT_GRAY); // Texto claro
        consoleArea.setCaretColor(Color.BLACK); // Cor do cursor
        JScrollPane consoleScrollPane = new JScrollPane(consoleArea);

        // Configurações do botão
        runButton = new JButton("Executar");
        runButton.setBackground(new Color(8, 13, 114)); // Fundo verde
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

        saveButton = new JButton("Salvar");
        saveButton.setBackground(new Color(0, 150, 0)); // Fundo verde escuro
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        openButton = new JButton("Abrir");
        openButton.setBackground(new Color(255, 165, 0)); // Fundo laranja
        openButton.setForeground(Color.WHITE);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        newFileButton = new JButton("Novo Arquivo .zd");
        newFileButton.setBackground(new Color(0, 100, 200)); // Fundo azul
        newFileButton.setForeground(Color.WHITE);
        newFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewFile();
            }
        });

        fileChooser = new JFileChooser();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(codeScrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(openButton);
        buttonPanel.add(newFileButton);
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

    private void executeCode() {
        try {
            String code = codeArea.getText();

            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens, consoleArea);

            parser.parse();

            appendToConsole("Código executado com sucesso");

        } catch (Exception e) {
            appendToConsole("Erro ao executar o código: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveFile() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(selectedFile)) {
                writer.write(codeArea.getText());
                appendToConsole("Arquivo salvo: " + selectedFile.getName());
            } catch (IOException e) {
                appendToConsole("Erro ao salvar o arquivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void openFile() {
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".zd");
            }

            @Override
            public String getDescription() {
                return "Arquivos .zd";
            }
        });

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(selectedFile.toPath()));
                codeArea.setText(content);
                appendToConsole("Arquivo carregado: " + selectedFile.getName());
            } catch (IOException e) {
                appendToConsole("Erro ao abrir o arquivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createNewFile() {
        codeArea.setText("");
        appendToConsole("Novo arquivo .zd criado.");
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
