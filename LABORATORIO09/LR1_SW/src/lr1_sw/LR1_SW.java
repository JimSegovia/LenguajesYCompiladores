package lr1_sw;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;


public class LR1_SW extends JFrame {
    private JTextArea inputArea;
    private JButton analyzeButton;
    private JTable table;
    private DefaultTableModel model;

    public LR1_SW() {
        setTitle("Analizador Sintáctico LR(1) - switch-case-break");
        setSize(1350, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inputArea = new JTextArea(5, 50);
        analyzeButton = new JButton("Analizar Código");
        model = new DefaultTableModel(new Object[]{"No.", "PILA", "ENTRADA", "ACCIÓN"}, 0);
        table = new JTable(model);
        
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(40);  
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(500); 
        column = table.getColumnModel().getColumn(2);
        column.setPreferredWidth(300);
        column = table.getColumnModel().getColumn(3);
        column.setPreferredWidth(400);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1300, 400));

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(new JLabel("Ingrese su código switch-case-break:"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        inputPanel.add(analyzeButton, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        analyzeButton.addActionListener(e -> analyzeAll());

        setVisible(true);
    }

    private void analyzeAll() {
        String entrada = inputArea.getText();
        List<String> tokens = tokenize(entrada);
        ParserLR1 parser = new ParserLR1();
        List<String[]> pasos = parser.analizar(tokens);

        model.setRowCount(0);
        int paso = 1;
        for (String[] fila : pasos) {
            model.addRow(new Object[]{paso++, fila[0], fila[1], fila[2]});
        }
    }

    private List<String> tokenize(String entrada) {
        List<String> tokens = new ArrayList<>();
        String[] palabras = entrada.replace("(", " ( ").replace(")", " ) ")
            .replace("{", " { ").replace("}", " } ")
            .replace(":", " : ").replace(";", " ; ")
            .replace("..", " .. ")
            .trim().split("\\s+");

        for (String palabra : palabras) {
            if (palabra.matches("switch|case|break|default|\\(|\\)|\\{|\\}|:|;|\\.\\.")) {
                tokens.add(palabra);
            } else if (palabra.matches("\\d+")) {
                tokens.add("num");
            } else if (palabra.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                tokens.add("id");
            } else if (!palabra.isEmpty()) {
                System.out.println("Token desconocido ignorado: " + palabra);
            }
        }

        tokens.add("$");  // fin de entrada
        return tokens;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LR1_SW::new);
    }
}
