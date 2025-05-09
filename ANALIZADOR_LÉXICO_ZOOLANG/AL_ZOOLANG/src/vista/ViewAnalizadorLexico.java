/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import al_zoolang.lexer.Lexer;
import al_zoolang.lexer.sym;
import java_cup.runtime.Symbol;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.StringReader;
import javax.swing.SwingConstants;  
/**
 *
 * @author ARIAN BEJAR
 */
public class ViewAnalizadorLexico extends javax.swing.JFrame {

    /**
     * Creates new form ViewAnalizadorSintatico
     */
    public ViewAnalizadorLexico() {
        initComponents();
        configurarTablas();
        personalizarTablas();
    }

    private void configurarTablas() {
    // Configurar modelo para la tabla de análisis léxico
    String[] columnasLexico = {"Código Token", "Descripción", "Lexema", "Línea", "Columna"};
    DefaultTableModel modelLexico = new DefaultTableModel(columnasLexico, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    tbAnalizadorLexico.setModel(modelLexico);
    
    // Configurar modelo para la tabla de símbolos
    String[] columnasSimbolos = {"Código Token", "Tipo", "Lexema", "Valor"};
    DefaultTableModel modelSimbolos = new DefaultTableModel(columnasSimbolos, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    tbSimbolos.setModel(modelSimbolos);
}

    
    private void personalizarTablas() {
        // Personalizar apariencia de las tablas
        tbAnalizadorLexico.getTableHeader().setReorderingAllowed(false);
        tbSimbolos.getTableHeader().setReorderingAllowed(false);
        
        // Ajustar anchos de columnas
        tbAnalizadorLexico.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbAnalizadorLexico.getColumnModel().getColumn(1).setPreferredWidth(150);
        tbAnalizadorLexico.getColumnModel().getColumn(2).setPreferredWidth(60);
        tbAnalizadorLexico.getColumnModel().getColumn(3).setPreferredWidth(60);
        tbAnalizadorLexico.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        tbSimbolos.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbSimbolos.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbSimbolos.getColumnModel().getColumn(2).setPreferredWidth(150);
    }
    
    public void configurarAnalizador() {
        btnAnalizar.addActionListener(e -> analizarCodigo());
    }
    
    private void analizarCodigo() {
        DefaultTableModel modelLexico = (DefaultTableModel) tbAnalizadorLexico.getModel();
        DefaultTableModel modelSimbolos = (DefaultTableModel) tbSimbolos.getModel();
        
        // Limpiar tablas
        modelLexico.setRowCount(0);
        modelSimbolos.setRowCount(0);
        
        String codigo = txAreaEntradaDatos.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese código para analizar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Lexer lexer = new Lexer(new StringReader(codigo));
            Symbol token;
            boolean hayErrores = false;
            
            while ((token = lexer.next_token()).sym != sym.EOF) {
                procesarToken(token, modelLexico, modelSimbolos);
                if (token.sym == sym.ERROR) {
                    hayErrores = true;
                }
            }
            
            if (hayErrores) {
                JOptionPane.showMessageDialog(this,
                    "Análisis completado con errores léxicos",
                    "Resultado", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Análisis léxico completado exitosamente",
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error durante el análisis: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void procesarToken(Symbol token, DefaultTableModel modelLexico, DefaultTableModel modelSimbolos) {
    // Obtener información del token
    int codigoToken = token.sym;
    String lexema = token.value != null ? token.value.toString() : "";
    String descripcion = obtenerDescripcionToken(codigoToken);
    
    // Agregar a tabla de análisis léxico
    modelLexico.addRow(new Object[]{
        codigoToken,  // Mostramos directamente el código numérico
        descripcion,
        lexema,
        token.left + 1,
        token.right + 1
    });
    
    // Agregar a tabla de símbolos si es relevante
    if (esSimboloRelevante(codigoToken)) {
        String tipo = obtenerTipoSimbolo(codigoToken);
        modelSimbolos.addRow(new Object[]{
            codigoToken,  // Código del token
            tipo,
            lexema,
            lexema  // Puedes ajustar esto según lo que quieras mostrar en valor
        });
    }
}
    
    // Métodos auxiliares (sin cambios)
private boolean esSimboloRelevante(int tokenSym) {
    return tokenSym == sym.ID_GLOBAL || tokenSym == sym.ID_CLASE ||
           tokenSym == sym.LIT_ENT || tokenSym == sym.LIT_REAL ||
           tokenSym == sym.LIT_STRING || tokenSym == sym.LIT_CHAR;
}

private String obtenerTipoSimbolo(int tokenSym) {
    switch(tokenSym) {
        case sym.ID_GLOBAL: return "ID Global";
        case sym.ID_CLASE: return "ID Clase";
        case sym.LIT_ENT: return "Literal Entero";
        case sym.LIT_REAL: return "Literal Real";
        case sym.LIT_STRING: return "Literal Cadena";
        case sym.LIT_CHAR: return "Literal Carácter";
        default: return "Desconocido";
    }
}
    
    private String obtenerNombreToken(int tokenSym) {
        // Mapeo de tokens a nombres legibles
        switch(tokenSym) {
            case sym.INIT_HABIT: return "INIT_HABIT";
            case sym.MAIN_ZOO: return "MAIN_ZOO";
            case sym.FIN_HABIT: return "FIN_HABIT";
            // ... agregar todos los tokens necesarios
            case sym.ID_GLOBAL: return "ID_GLOBAL";
            case sym.LIT_ENT: return "LIT_ENT";
            case sym.ERROR: return "ERROR";
            default: return "TOKEN_" + tokenSym;
        }
    }
    
    private String obtenerDescripcionToken(int tokenSym) {
    switch(tokenSym) {
        // Palabras reservadas (400-431)
        case sym.INIT_HABIT: return "Palabra reservada: Inicio de hábitat";
        case sym.MAIN_ZOO: return "Palabra reservada: Función principal";
        case sym.FIN_HABIT: return "Palabra reservada: Fin de hábitat";
        case sym.CLASS_HABIT: return "Palabra reservada: Definición de clase";
        case sym.ACCED: return "Palabra reservada: Modificador de acceso";
        case sym.MODIF: return "Palabra reservada: Modificador";
        case sym.MET: return "Palabra reservada: Definición de método";
        case sym.LIBRE: return "Palabra reservada: Modificador libre";
        case sym.ENCERRADO: return "Palabra reservada: Modificador encerrado";
        case sym.PROTECT: return "Palabra reservada: Modificador protegido";
        case sym.COMPOR: return "Palabra reservada: Comportamiento";
        case sym.ENT: return "Palabra reservada: Tipo entero";
        case sym.ANT: return "Palabra reservada: Tipo ant";
        case sym.BOUL: return "Palabra reservada: Tipo boul";
        case sym.CORPSE: return "Palabra reservada: Tipo corpse";
        case sym.STLORO: return "Palabra reservada: Tipo stloro";
        case sym.TORT: return "Palabra reservada: Tipo TORT";
        case sym.DEVOLVER: return "Palabra reservada: Devolver valor";
        case sym.RUGG: return "Palabra reservada: Rugido";
        case sym.RECI: return "Palabra reservada: Reci";
        case sym.CAMA: return "Palabra reservada: Cama";
        case sym.LEON: return "Palabra reservada: León";
        case sym.MERODEAR: return "Palabra reservada: Merodear";
        case sym.RONDAR: return "Palabra reservada: Rondar";
        case sym.ME: return "Palabra reservada: Me";
        case sym.INSTINTO: return "Palabra reservada: Instinto";
        case sym.INSTINTO_FINAL: return "Palabra reservada: Instinto final";
        case sym.REACCION: return "Palabra reservada: Reacción";
        case sym.HUIR: return "Palabra reservada: Huir";
        case sym.VERDAD: return "Palabra reservada: Valor verdadero";
        case sym.FALSO: return "Palabra reservada: Valor falso";
        case sym.NULO: return "Palabra reservada: Valor nulo";

        // Operadores de comparación (431-432)
        case sym.OP_IGUAL: return "Operador de comparación: Igualdad (==)";
        case sym.OP_DIF: return "Operador de comparación: Diferente (!=)";

        // Operadores relacionales (433-436)
        case sym.OP_MENOR: return "Operador relacional: Menor que (<)";
        case sym.OP_MENOR_IGUAL: return "Operador relacional: Menor o igual que (<=)";
        case sym.OP_MAYOR: return "Operador relacional: Mayor que (>)";
        case sym.OP_MAYOR_IGUAL: return "Operador relacional: Mayor o igual que (>=)";

        // Operadores lógicos (437-439)
        case sym.OP_Y: return "Operador lógico: Y (y¡)";
        case sym.OP_O: return "Operador lógico: O (o¡)";
        case sym.OP_NO: return "Operador lógico: Negación (!)";

        // Operadores aritméticos (440-445)
        case sym.OP_SUMA: return "Operador aritmético: Suma (+)";
        case sym.OP_INCREMENTO: return "Operador aritmético: Incremento (++)";
        case sym.OP_RESTA: return "Operador aritmético: Resta (-)";
        case sym.OP_DECREMENTO: return "Operador aritmético: Decremento (--)";
        case sym.OP_MULT: return "Operador aritmético: Multiplicación (*)";
        case sym.OP_DIV: return "Operador aritmético: División (/)";

        // Asignación (509-510)
        case sym.OP_ASIGN: return "Operador de asignación: Igual (=)";
        case sym.OP_ASIGN_ARR: return "Operador de asignación: Flecha (=>)";

        // Símbolos (446-456)
        case sym.PAR_ABIERTO: return "Símbolo: Paréntesis abierto (()";
        case sym.PAR_CERRADO: return "Símbolo: Paréntesis cerrado ())";
        case sym.LLAVE_ABIERTA: return "Símbolo: Llave abierta ({)";
        case sym.LLAVE_CERRADA: return "Símbolo: Llave cerrada (})";
        case sym.CORCH_ABIERTO: return "Símbolo: Corchete abierto ([)";
        case sym.CORCH_CERRADO: return "Símbolo: Corchete cerrado (])";
        case sym.PUNTO_COMA: return "Símbolo: Punto y coma (;)";
        case sym.DOS_PUNTOS: return "Símbolo: Dos puntos (:)";
        case sym.COMA: return "Símbolo: Coma (,)";
        case sym.PUNTO: return "Símbolo: Punto (.)";
        case sym.DOBLE_PUNTO: return "Símbolo: Doble punto (..)";

        // Identificadores y literales (500-507)
        case sym.ID_GLOBAL: return "Identificador global de variable";
        case sym.ID: return "Identificador general";
        case sym.ID_CLASE: return "Identificador de clase";
        case sym.LIT_STRING: return "Literal: Cadena de texto";
        case sym.LIT_CHAR: return "Literal: Carácter";
        case sym.LIT_ENT: return "Literal: Número entero";
        case sym.LIT_REAL: return "Literal: Número real";

        // Errores y fin de archivo
        case sym.ERROR: return "ERROR LÉXICO: Símbolo no reconocido";
        case sym.EOF: return "Fin de archivo";
            
        default: return "Token no clasificado (Código: " + tokenSym + ")";
    }
}
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        unmsm = new javax.swing.JLabel();
        btnAnalizar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txAreaEntradaDatos = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbSimbolos = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbAnalizadorLexico = new javax.swing.JTable();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 51));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("ANALIZADOR LÉXICO ZOOLANG");

        jSeparator1.setBackground(new java.awt.Color(0, 153, 153));

        unmsm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/UNMSMnew.png"))); // NOI18N

        btnAnalizar.setBackground(new java.awt.Color(0, 153, 153));
        btnAnalizar.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 18)); // NOI18N
        btnAnalizar.setText("ANALIZAR");

        jLabel3.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("INGRESAR DATOS :");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("ANALIZADOR LÉXICO");

        txAreaEntradaDatos.setColumns(20);
        txAreaEntradaDatos.setRows(5);
        jScrollPane1.setViewportView(txAreaEntradaDatos);

        tbSimbolos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Descripcion", "Lexema", "Token"
            }
        ));
        jScrollPane2.setViewportView(tbSimbolos);

        jLabel5.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("TABLA DE SÍMBOLOS");

        tbAnalizadorLexico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Descripcion", "Lexema", "Token"
            }
        ));
        jScrollPane3.setViewportView(tbAnalizadorLexico);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(121, 121, 121)
                .addComponent(unmsm, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 817, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                                .addComponent(btnAnalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(48, 48, 48))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(205, 205, 205))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(unmsm, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addGap(40, 40, 40)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAnalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(120, 120, 120)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ViewAnalizadorLexico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewAnalizadorLexico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewAnalizadorLexico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewAnalizadorLexico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewAnalizadorLexico().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnAnalizar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JTable tbAnalizadorLexico;
    public javax.swing.JTable tbSimbolos;
    public javax.swing.JTextArea txAreaEntradaDatos;
    public javax.swing.JLabel unmsm;
    // End of variables declaration//GEN-END:variables
}
