package editor.ide;
import editor.translate.Lexer;
import editor.translate.Parser;
import editor.Token;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
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
        // Configurações do editor de código
        codeArea = new JTextArea(20, 60);
        codeArea.setBackground(new Color(30, 30, 30)); // Fundo escuro
        codeArea.setForeground(Color.LIGHT_GRAY); // Texto claro
        codeArea.setCaretColor(Color.WHITE); // Cor do cursor
        JScrollPane codeScrollPane = new JScrollPane(codeArea);

        consoleArea = new JTextArea(20, 60);
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(20, 20, 20)); // Fundo escuro
        consoleArea.setForeground(Color.LIGHT_GRAY); // Texto claro
        consoleArea.setCaretColor(Color.BLACK); // Cor do cursor
        JScrollPane consoleScrollPane = new JScrollPane(consoleArea);

        // Configurações dos botões
        runButton = createStyledButton("Executar", new Color(8, 13, 114), e -> executeCode());
        clearButton = createStyledButton("Limpar", new Color(221, 1, 1), e -> clearConsole());
        saveButton = createStyledButton("Salvar", new Color(0, 150, 0), e -> saveFile());
        openButton = createStyledButton("Abrir", new Color(255, 165, 0), e -> openFile());
        newFileButton = createStyledButton("Novo Arquivo .zd", new Color(0, 100, 200), e -> createNewFile());

        // Configuração do JFileChooser com filtro de extensão .zd
        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos .zd", "zd");
        fileChooser.setFileFilter(filter);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(openButton);
        buttonPanel.add(newFileButton);


        JPanel panel = new JPanel(new BorderLayout());
        panel.add(codeScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(consoleScrollPane, BorderLayout.SOUTH);

        add(panel);
        setTitle("Zard Editor");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text, Color bgColor, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.addActionListener(actionListener);
        button.setFocusPainted(false);
        return button;
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

            appendToConsole("Código executado com sucesso!");

        } catch (Exception e) {
            appendToConsole("Erro ao executar o código: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveFile() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().endsWith(".zd")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".zd");
            }
            try (FileWriter writer = new FileWriter(selectedFile)) {
                writer.write(codeArea.getText());
                appendToConsole("Arquivo salvo: " + selectedFile.getName());
                JOptionPane.showMessageDialog(this, "Arquivo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                appendToConsole("Erro ao salvar o arquivo: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo!", "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(selectedFile.toPath()));
                codeArea.setText(content);
                appendToConsole("Arquivo carregado: " + selectedFile.getName());
                JOptionPane.showMessageDialog(this, "Arquivo carregado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                appendToConsole("Erro ao abrir o arquivo: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Erro ao abrir o arquivo!", "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void createNewFile() {
        int response = JOptionPane.showConfirmDialog(this, "Deseja criar um novo arquivo? Todas as mudanças não salvas serão perdidas.", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            codeArea.setText("");
            appendToConsole("Novo arquivo .zd criado.");
        }
    }

    private void appendToConsole(String message) {
        consoleArea.append(message + "\n");
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CodeEditor().setVisible(true));
    }
}
