import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List; // Importando a classe correta

public class CodeEditor extends JFrame {

    private JTextArea codeArea;
    private JButton runButton;

    public CodeEditor() {

        codeArea = new JTextArea(20, 60);
        JScrollPane scrollPane = new JScrollPane(codeArea);


        runButton = new JButton("Executar");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCode();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(runButton, BorderLayout.SOUTH);

        add(panel);
        setTitle("Zard editor");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void executeCode() {

        String code = codeArea.getText();

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);

        parser.parse();

        JOptionPane.showMessageDialog(this, "Código executado com sucesso!");
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
