package lr0_sw;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class LR0_SW extends JFrame {
    private JTextArea inputArea;
    private JButton analyzeButton;
    private JTable table;
    private DefaultTableModel model;
    private int stepCounter;
    
    // Estructuras para el análisis LR(0)
    private Deque<String> pila;
    private List<String> tokens;
    private int tokenIndex;
    private boolean analysisComplete;
    private Map<String, Map<String, String>> actionTable;
    private Map<String, Map<String, String>> gotoTable;
    private Map<Integer, String[]> productions;

    public LR0_SW() {
        setTitle("Analizador Sintáctico LR(0) - switch-case");
        setSize(1350, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuración del área de entrada
        inputArea = new JTextArea(5, 50);
        
        // Botón de análisis
        analyzeButton = new JButton("Analizar Código");

        // Configuración del modelo de tabla
        model = new DefaultTableModel(new Object[]{"No.", "PILA", "ENTRADA", "ACCIÓN"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        
        // Ajustar tamaño de columnas
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(40);  // Columna No. más pequeña
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(500); // Columna PILA más grande
        column = table.getColumnModel().getColumn(2);
        column.setPreferredWidth(300);
        column = table.getColumnModel().getColumn(3);
        column.setPreferredWidth(400);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1300, 400));

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(new JLabel("Ingrese su código switch-case:"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        inputPanel.add(analyzeButton, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        analyzeButton.addActionListener(e -> analyzeAll());

        initializeTables();
        setVisible(true);
    }

    private void initializeTables() {
        // Inicializar tabla ACTION (acciones)
        actionTable = new HashMap<>();
        
        // Estado 0
        actionTable.put("0", new HashMap<>());
        actionTable.get("0").put("switch", "d3");
        actionTable.get("0").put("S", "1");
        actionTable.get("0").put("SWITCH", "2");
        
        // Estado 1
        actionTable.put("1", new HashMap<>());
        actionTable.get("1").put("$", "ACEPTAR");
        
        // Estado 2
        actionTable.put("2", new HashMap<>());
        actionTable.get("2").put("switch", "r4");
        actionTable.get("2").put("break", "r4");
        actionTable.get("2").put("$", "r4");
        actionTable.get("2").put("MASSWITCH", "4");
        
        // Estado 3
        actionTable.put("3", new HashMap<>());
        actionTable.get("3").put("(", "d5");
        
        // Estado 4
        actionTable.put("4", new HashMap<>());
        actionTable.get("4").put("switch", "d3");
        actionTable.get("4").put("$", "r1");
        actionTable.get("4").put("SWITCH", "6");
        
        // Estado 5
        actionTable.put("5", new HashMap<>());
        actionTable.get("5").put("id", "d7");
        
        // Estado 6
        actionTable.put("6", new HashMap<>());
        actionTable.get("6").put("switch", "r3");
        actionTable.get("6").put("break", "r3");
        actionTable.get("6").put("$", "r3");
        
        // Estado 7
        actionTable.put("7", new HashMap<>());
        actionTable.get("7").put(")", "d8");
        
        // Estado 8
        actionTable.put("8", new HashMap<>());
        actionTable.get("8").put("{", "d9");
        
        // Estado 9
        actionTable.put("9", new HashMap<>());
        actionTable.get("9").put("}", "r6");
        actionTable.get("9").put("case", "r6");
        actionTable.get("9").put("MASCASES", "10");
        
        // Estado 10
        actionTable.put("10", new HashMap<>());
        actionTable.get("10").put("}", "d11");
        actionTable.get("10").put("case", "d13");
        actionTable.get("10").put("CASES", "12");
        
        // Estado 11
        actionTable.put("11", new HashMap<>());
        actionTable.get("11").put("switch", "r2");
        actionTable.get("11").put("$", "r2");
        
        // Estado 12
        actionTable.put("12", new HashMap<>());
        actionTable.get("12").put("}", "r5");
        actionTable.get("12").put("case", "r5");
        
        // Estado 13
        actionTable.put("13", new HashMap<>());
        actionTable.get("13").put("num", "d15");
        actionTable.get("13").put("OPC", "14");
        
        // Estado 14
        actionTable.put("14", new HashMap<>());
        actionTable.get("14").put(":", "d16");
        
        // Estado 15
        actionTable.put("15", new HashMap<>());
        actionTable.get("15").put("..", "d18");
        actionTable.get("15").put(":", "r10");
        actionTable.get("15").put("RANGE", "17");
        
        // Estado 16
        actionTable.put("16", new HashMap<>());
        actionTable.get("16").put("switch", "r4");
        actionTable.get("16").put("break", "r4");
        actionTable.get("16").put("$", "r4");
        actionTable.get("16").put("MASSWITCH", "19");
        
        // Estado 17
        actionTable.put("17", new HashMap<>());
        actionTable.get("17").put(":", "r8");
        
        // Estado 18
        actionTable.put("18", new HashMap<>());
        actionTable.get("18").put("num", "d20");
        
        // Estado 19
        actionTable.put("19", new HashMap<>());
        actionTable.get("19").put("switch", "d3");
        actionTable.get("19").put("break", "d21");
        actionTable.get("19").put("SWITCH", "6");
        
        // Estado 20
        actionTable.put("20", new HashMap<>());
        actionTable.get("20").put(":", "r9");
        
        // Estado 21
        actionTable.put("21", new HashMap<>());
        actionTable.get("21").put(";", "d22");
        
        // Estado 22
        actionTable.put("22", new HashMap<>());
        actionTable.get("22").put("}", "r7");
        actionTable.get("22").put("case", "r7");

        // Inicializar tabla GOTO (solo se usa en desplazamientos)
        gotoTable = new HashMap<>();
        
        // Inicializar producciones
        productions = new HashMap<>();
        productions.put(1, new String[]{"S", "SWITCH MASSWITCH"});
        productions.put(2, new String[]{"SWITCH", "switch ( id ) { MASCASES }"});
        productions.put(3, new String[]{"MASSWITCH", "MASSWITCH SWITCH"});
        productions.put(4, new String[]{"MASSWITCH", "λ"});
        productions.put(5, new String[]{"MASCASES", "MASCASES CASES"});
        productions.put(6, new String[]{"MASCASES", "λ"});
        productions.put(7, new String[]{"CASES", "case OPC : MASSWITCH break ;"});
        productions.put(8, new String[]{"OPC", "num RANGE"});
        productions.put(9, new String[]{"RANGE", ".. num"});
        productions.put(10, new String[]{"RANGE", "λ"});
    }


    private void analyzeAll() {
        model.setRowCount(0); // Limpiar tabla
        stepCounter = 1;      // Reiniciar contador
        initializeAnalysis();
        
        while (!analysisComplete) {
            String stackTop = pila.peek();
            String currentToken = tokens.get(tokenIndex);
            
            String action = getAction(stackTop, currentToken);
            
            if (action == null) {
                model.addRow(new Object[]{stepCounter++, stackToString(), remainingInput(), "ERROR: No hay acción definida"});
                analysisComplete = true;
                return;
            }
            
            if (action.equals("ACEPTAR")) {
                model.addRow(new Object[]{stepCounter++, stackToString(), remainingInput(), "ACEPTAR: Análisis completado con éxito"});
                analysisComplete = true;
                return;
            }
            
            if (action.startsWith("d")) {
                // Desplazamiento
                String newState = action.substring(1);
                model.addRow(new Object[]{
                    stepCounter++, 
                    stackToString(), 
                    remainingInput(), 
                    "DESPLAZAR: " + currentToken + " → estado " + newState
                });
                
                pila.push(currentToken);
                pila.push(newState);
                tokenIndex++;
            } 
            else if (action.startsWith("r")) {
                // Reducción
                int productionNum = Integer.parseInt(action.substring(1));
                String[] production = productions.get(productionNum);
                String lhs = production[0];
                String[] rhs = production[1].split(" ");
                
                model.addRow(new Object[]{
                    stepCounter++, 
                    stackToString(), 
                    remainingInput(), 
                    "REDUCIR: " + lhs + " → " + production[1]
                });
                
                // Sacar 2*|β| elementos de la pila
                int popCount = rhs.length;
                if (rhs[0].equals("λ")) {
                    popCount = 0;
                }
                
                for (int i = 0; i < popCount * 2; i++) {
                    pila.pop();
                }
                
                String newStackTop = pila.peek();
                String gotoState = getGoto(newStackTop, lhs);
                
                if (gotoState == null) {
                    model.addRow(new Object[]{
                        stepCounter++, 
                        stackToString(), 
                        remainingInput(), 
                        "ERROR: No hay estado GOTO para (" + newStackTop + ", " + lhs + ")"
                    });
                    analysisComplete = true;
                    return;
                }
                
                pila.push(lhs);
                pila.push(gotoState);
            }
            else {
                model.addRow(new Object[]{
                    stepCounter++, 
                    stackToString(), 
                    remainingInput(), 
                    "ERROR: Acción no reconocida - " + action
                });
                analysisComplete = true;
            }
        }
    }

    private void initializeAnalysis() {
        String entrada = inputArea.getText();
        tokens = tokenize(entrada);
        tokens.add("$");
        tokenIndex = 0;
        pila = new ArrayDeque<>();
        pila.push("0"); // Estado inicial
        analysisComplete = false;
    }

    private String getAction(String state, String symbol) {
        if (actionTable.containsKey(state) && actionTable.get(state).containsKey(symbol)) {
            return actionTable.get(state).get(symbol);
        }
        return null;
    }

    private String getGoto(String state, String nonTerminal) {
        if (actionTable.containsKey(state) && actionTable.get(state).containsKey(nonTerminal)) {
            return actionTable.get(state).get(nonTerminal);
        }
        return null;
    }

    private String stackToString() {
        List<String> temp = new ArrayList<>(pila);
        Collections.reverse(temp);
        return String.join(" ", temp);
    }

    private String remainingInput() {
        return String.join(" ", tokens.subList(tokenIndex, tokens.size()));
    }

    private List<String> tokenize(String entrada) {
        List<String> tokens = new ArrayList<>();
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
                tokens.add(palabra);
            } else if (palabra.matches("\\d+")) {
                tokens.add("num");
            } else if (palabra.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                tokens.add("id");
            } else if (!palabra.isEmpty()) {
                System.out.println("Token desconocido ignorado: " + palabra);
            }
        }

        return tokens;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LR0_SW analyzer = new LR0_SW();
            analyzer.setVisible(true);
        });
    }
}