package sample;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.table.DefaultTableModel;


public class ADPGUI extends JFrame {
    private JTextArea inputArea;
    private JButton analyzeButton;
    private JTable table;
    private DefaultTableModel model;
    private int stepCounter;

    public ADPGUI() {
        setTitle("Analizador Sintáctico Predictivo - switch-case");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inputArea = new JTextArea(5, 50);
        analyzeButton = new JButton("Analizar");

        model = new DefaultTableModel(new Object[]{"No.", "PILA", "ENTRADA", "SALIDA"}, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Escribe tu código:"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        inputPanel.add(analyzeButton, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        analyzeButton.addActionListener(e -> analizar());

        setVisible(true);
    }

    private void analizar() {
    model.setRowCount(0); // Limpiar tabla
    stepCounter = 1; // Reiniciar contador de pasos
    String entrada = inputArea.getText();
    List<String> tokens = tokenizar(entrada);
    tokens.add("$");

    Deque<String> pila = new ArrayDeque<>();
    pila.push("$");
    pila.push("S");

    int i = 0;

    try {
        while (!pila.isEmpty()) {
            String top = pila.pop();
            String current = tokens.get(i);

            if (top.equals("$") && current.equals("$")) {
                model.addRow(new Object[]{stepCounter++, "$", "$", "SE ACEPTA"});
                break;
            }

            if (isTerminal(top)) {
                if (top.equals(current)) {
                    // Mostrar coincidencia antes de consumir
                    model.addRow(new Object[]{
                        stepCounter++,
                        pilaToString(pila),  // Pila antes de consumir
                        entradaDesde(tokens, i),
                        "Coincidencia: " + top
                    });

                    i++;  // Avanzar en la entrada
                } else {
                    throw new Exception("Se esperaba '" + top + "', se encontró '" + current + "'");
                }
            } else {
                String prod = getProduction(top, current);
                if (prod == null) {
                    throw new Exception("No hay producción para (" + top + ", " + current + ")");
                } else {
                    // Mostrar la producción aplicada
                    model.addRow(new Object[]{
                        stepCounter++,
                        pilaToString(pila),
                        entradaDesde(tokens, i),
                        top + " → " + prod
                    });

                    // Descomponer la producción y empujarla a la pila
                    String[] parts = prod.split(" ");
                    for (int j = parts.length - 1; j >= 0; j--) {
                        if (!parts[j].equals("λ")) {
                            pila.push(parts[j]);
                        }
                    }
                }
            }
        }
    } catch (Exception ex) {
        model.addRow(new Object[]{"", "", "", "X " + ex.getMessage()});
    }
}

private void empilar(String prod, Deque<String> pila) {
    String[] parts = prod.split(" ");
    for (int j = parts.length - 1; j >= 0; j--) {
        if (!parts[j].equals("λ")) {
            pila.push(parts[j]);
        }
    }
}

private String pilaToString(Deque<String> pila) {
    List<String> temp = new ArrayList<>(pila);
    Collections.reverse(temp); // Mostrar la pila en el orden original
    return String.join(" ", temp);
}

    private String entradaDesde(List<String> tokens, int i) {
        return String.join(" ", tokens.subList(i, tokens.size()));
    }

    private boolean isTerminal(String symbol) {
        return Arrays.asList("switch", "(", ")", "{", "}", "case", ":", "break", ";", "num", "..", "id", "$").contains(symbol);
    }

    private String getProduction(String vn, String vt) {
    if (vn.equals("S") && vt.equals("switch")) return "SWITCH MASSWITCH";
    if (vn.equals("SWITCH") && vt.equals("switch")) return "switch ( id ) { MASCASES }"; // Esta producción debe retornarse cuando sea necesario
    if (vn.equals("MASSWITCH") && vt.equals("switch")) return "SWITCH MASSWITCH";
    if (vn.equals("MASSWITCH") && (vt.equals("}") || vt.equals("$"))) return "λ";
    if (vn.equals("MASCASES") && vt.equals("case")) return "CASES MASCASES";
    if (vn.equals("MASCASES") && vt.equals("}")) return "λ";
    if (vn.equals("CASES") && vt.equals("case")) return "case OPC : MASSWITCH break ;";
    if (vn.equals("OPC") && vt.equals("num")) return "num RANGE";
    if (vn.equals("RANGE") && vt.equals("..")) return ".. num";
    if (vn.equals("RANGE") && vt.equals(":")) return "λ";
    if (vn.equals("MASSWITCH") && vt.equals("break")) return "λ";
    return null;
}
    private List<String> tokenizar(String entrada) {
        List<String> toks = new ArrayList<>();
        String[] palabras = entrada.replace("(", " ( ")
                .replace(")", " ) ")
                .replace("{", " { ")
                .replace("}", " } ")
                .replace(":", " : ")
                .replace(";", " ; ")
                .replace("..", " .. ")
                .trim().split("\\s+");

        for (String palabra : palabras) {
            if (palabra.equals("switch") || palabra.equals("case") || palabra.equals("break") ||
                palabra.equals("(") || palabra.equals(")") || palabra.equals("{") || palabra.equals("}") ||
                palabra.equals(":") || palabra.equals(";") || palabra.equals("..")) {
                toks.add(palabra);
            } else if (palabra.matches("\\d+")) {
                toks.add("num");
            } else if (palabra.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                toks.add("id");
            } else {
                System.out.println(" Token desconocido ignorado: " + palabra);
            }
        }

        return toks;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ADPGUI::new);
    }
}