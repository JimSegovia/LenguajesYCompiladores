/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import codigo.Lexer;  // Cambiar de al_zoolang.lexer.Lexer a codigo.Lexer
import codigo.Tokens;  // Cambiar de al_zoolang.lexer.sym a codigo.Tokens
import java_cup.runtime.Symbol;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.StringReader;
import javax.swing.SwingConstants; 
import codigo.LexerToken;
import codigo.Tokens;
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
    String[] columnasLexico = {"Descripción", "Lexema", "Token"};
    DefaultTableModel modelLexico = new DefaultTableModel(columnasLexico, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    tbAnalizadorLexico.setModel(modelLexico);
    
    // Configurar modelo para la tabla de símbolos
    String[] columnasSimbolos = {"Descripción", "Lexema", "Token"};
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
        tbAnalizadorLexico.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbAnalizadorLexico.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbAnalizadorLexico.getColumnModel().getColumn(2).setPreferredWidth(20);
        
        tbSimbolos.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbSimbolos.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbSimbolos.getColumnModel().getColumn(2).setPreferredWidth(20);
    }
    
    public void configurarAnalizador() {
        btnAnalizar.addActionListener(e -> analizarCodigo());
    }
    
    private void analizarCodigo() {
    DefaultTableModel modelLexico = (DefaultTableModel) tbAnalizadorLexico.getModel();
    DefaultTableModel modelSimbolos = (DefaultTableModel) tbSimbolos.getModel();
    
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
        Tokens token;
        boolean hayErrores = false;

        while ((token = lexer.yylex()) != Tokens.EOF) {
            procesarToken(lexer, token, modelLexico, modelSimbolos);
            if (token == Tokens.ERROR) {
                hayErrores = true;
            }
        }
        
        if (hayErrores) {
            JOptionPane.showMessageDialog(this,
                "Análisis completado con errores léxicos",
                "Resultado", JOptionPane.WARNING_MESSAGE);
        } 
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Error durante el análisis: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
    
  private void procesarToken(Lexer lexer, Tokens token, 
                         DefaultTableModel modelLexico, 
                         DefaultTableModel modelSimbolos) {
    int codigoToken = token.getTokenNumber(); // Usamos el método de tu enum
    String lexema = lexer.getLexema(); // Usamos el getter en lugar del campo directo
    String descripcion = obtenerDescripcionToken(codigoToken);
    
    modelLexico.addRow(new Object[]{
        descripcion,
        lexema,
        codigoToken,
    });
    
    if (esSimboloRelevante(codigoToken)) {
        String tipo = obtenerTipoSimbolo(codigoToken);
        modelSimbolos.addRow(new Object[]{
            descripcion,
            lexema,
            token,
        });
    }
}
    
    // Métodos auxiliares (sin cambios)
private boolean esSimboloRelevante(int tokenSym) {
    return tokenSym == Tokens.ID_VAR.getTokenNumber() || 
           tokenSym == Tokens.ID_CLASE.getTokenNumber() ||
           tokenSym == Tokens.LIT_ENT.getTokenNumber() || 
           tokenSym == Tokens.LIT_REAL.getTokenNumber() ||
           tokenSym == Tokens.LIT_STRING.getTokenNumber() || 
           tokenSym == Tokens.LIT_CHAR.getTokenNumber();
}

private String obtenerTipoSimbolo(int tokenSym) {
    if (tokenSym == Tokens.ID_VAR.getTokenNumber()) return "ID Variable";
    if (tokenSym == Tokens.ID_CLASE.getTokenNumber()) return "ID Clase";
    if (tokenSym == Tokens.LIT_ENT.getTokenNumber()) return "Literal Entero";
    if (tokenSym == Tokens.LIT_REAL.getTokenNumber()) return "Literal Real";
    if (tokenSym == Tokens.LIT_STRING.getTokenNumber()) return "Literal Cadena";
    if (tokenSym == Tokens.LIT_CHAR.getTokenNumber()) return "Literal Carácter";
    return "Desconocido";
}
    
    private String obtenerNombreToken(int tokenSym) {
    // Mapeo de tokens a nombres legibles
    switch(tokenSym) {
        // Palabras Reservadas (400-431)
        case 400: return "INIT_HABIT";
        case 401: return "MAIN_ZOOP";
        case 402: return "FIN_HABIT";
        case 403: return "CLASS_HABIT";
        case 404: return "ACCEDER";
        case 405: return "MODIFICAR";
        case 406: return "METODO";
        case 407: return "LIBRE";
        case 408: return "ENCERRADO";
        case 409: return "PROTECT";
        case 410: return "COMPOR";
        case 411: return "ENT";
        case 412: return "ANT";
        case 413: return "BOUL";
        case 414: return "CORPSE";
        case 415: return "STLORO";
        case 416: return "TORT";
        case 417: return "DEVOLVER";
        case 420: return "CAMA";
        case 421: return "LEON";
        case 422: return "MERODEAR";
        case 423: return "RONDAR";
        case 425: return "INSTINTO";
        case 426: return "INSTINTO_FINAL";
        case 427: return "REACCION";
        case 428: return "HUIR";
        case 429: return "VERDAD";
        case 430: return "FALSO";
        case 504: return "LIT_NULO";
        case 231: return "CHAR";

        // Operadores y Símbolos (431-456)
        case 431: return "IGUAL_IGUAL";
        case 432: return "DIFERENTE";
        case 433: return "MENOR";
        case 434: return "MENOR_IGUAL";
        case 435: return "MAYOR";
        case 436: return "MAYOR_IGUAL";
        case 437: return "AND";
        case 438: return "OR";
        case 439: return "NOT";
        case 440: return "SUMA";
        case 441: return "RESTA";
        case 442: return "MULTIPLICACION";
        case 443: return "DIVISION";
        case 444: return "INCREMENTO";
        case 445: return "DECREMENTO";
        case 446: return "PARENTESIS_IZQ";
        case 447: return "PARENTESIS_DER";
        case 448: return "LLAVE_IZQ";
        case 449: return "LLAVE_DER";
        case 450: return "CORCHETE_IZQ";
        case 451: return "CORCHETE_DER";
        case 452: return "PUNTO_COMA";
        case 453: return "DOS_PUNTOS";
        case 454: return "COMA";
        case 455: return "PUNTO";
        case 456: return "PUNTO_PUNTO";
        case 509: return "ASIGNACION";
        case 510: return "ASIGNACION_ESPECIAL";

        // Identificadores y Literales (500-510)
        case 500: return "ID_VAR";
        case 502: return "ID_CLASE";
        case 503: return "LIT_STRING";
        case 505: return "LIT_CHAR";
        case 506: return "LIT_ENT";
        case 507: return "LIT_REAL";

        // Error
        case 911: return "ERROR";
            
        default: return "TOKEN_" + tokenSym;
    }
}

private String obtenerDescripcionToken(int tokenSym) {
    switch(tokenSym) {
        // Palabras reservadas (400-431)
        case 400: return "Palabra reservada: initHabit (Inicio de hábitat)";
        case 401: return "Palabra reservada: mainZoo (Función principal)";
        case 402: return "Palabra reservada: finHabit (Fin de hábitat)";
        case 403: return "Palabra reservada: ClassHabit (Definición de clase)";
        case 404: return "Palabra reservada: acced-> (Modificador de acceso)";
        case 405: return "Palabra reservada: modif-> (Modificador)";
        case 406: return "Palabra reservada: met-> (Definición de método)";
        case 407: return "Palabra reservada: libre (Modificador libre)";
        case 408: return "Palabra reservada: encerrado (Modificador encerrado)";
        case 409: return "Palabra reservada: protect (Modificador protegido)";
        case 410: return "Palabra reservada: compor (Comportamiento)";
        case 411: return "Palabra reservada: ent (Tipo entero)";
        case 412: return "Palabra reservada: ant (Tipo ant)";
        case 413: return "Palabra reservada: boul (Tipo boul)";
        case 414: return "Palabra reservada: corpse (Tipo corpse)";
        case 415: return "Palabra reservada: stloro (Tipo stloro)";
        case 416: return "Palabra reservada: TORT (Tipo TORT)";
        case 417: return "Palabra reservada: devolver (Devolver valor)";
        case 420: return "Palabra reservada: cama";
        case 421: return "Palabra reservada: leon";
        case 422: return "Palabra reservada: merodear";
        case 423: return "Palabra reservada: rondar";
        case 425: return "Palabra reservada: instinto";
        case 426: return "Palabra reservada: instintoFinal";
        case 427: return "Palabra reservada: reaccion";
        case 428: return "Palabra reservada: huir";
        case 429: return "Palabra reservada: verdad (Valor verdadero)";
        case 430: return "Palabra reservada: falso (Valor falso)";
        case 504: return "Palabra reservada: nulo (Valor nulo)";
        case 231: return "Palabra reservada: char (Tipo carácter)";

        // Operadores de comparación (431-432)
        case 431: return "Operador de comparación: Igualdad (==)";
        case 432: return "Operador de comparación: Diferente (!=)";

        // Operadores relacionales (433-436)
        case 433: return "Operador relacional: Menor que (<)";
        case 434: return "Operador relacional: Menor o igual que (<=)";
        case 435: return "Operador relacional: Mayor que (>)";
        case 436: return "Operador relacional: Mayor o igual que (>=)";

        // Operadores lógicos (437-439)
        case 437: return "Operador lógico: Y (y¡)";
        case 438: return "Operador lógico: O (o¡)";
        case 439: return "Operador lógico: Negación (!)";

        // Operadores aritméticos (440-445)
        case 440: return "Operador aritmético: Suma (+)";
        case 441: return "Operador aritmético: Resta (-)";
        case 442: return "Operador aritmético: Multiplicación (*)";
        case 443: return "Operador aritmético: División (/)";
        case 444: return "Operador aritmético: Incremento (++)";
        case 445: return "Operador aritmético: Decremento (--)";

        // Símbolos (446-456)
        case 446: return "Símbolo: Paréntesis abierto (()";
        case 447: return "Símbolo: Paréntesis cerrado ())";
        case 448: return "Símbolo: Llave abierta ({)";
        case 449: return "Símbolo: Llave cerrada (})";
        case 450: return "Símbolo: Corchete abierto ([)";
        case 451: return "Símbolo: Corchete cerrado (])";
        case 452: return "Símbolo: Punto y coma (;)";
        case 453: return "Símbolo: Dos puntos (:)";
        case 454: return "Símbolo: Coma (,)";
        case 455: return "Símbolo: Punto (.)";
        case 456: return "Símbolo: Doble punto (..)";

        // Asignación (509-510)
        case 509: return "Operador de asignación: Igual (=)";
        case 510: return "Operador de asignación: Flecha (=>)";

        // Identificadores y literales (500-510)
        case 500: return "Identificador: Variable";
        case 502: return "Identificador: Clase";
        case 503: return "Literal: Cadena de texto";
        case 505: return "Literal: Carácter";
        case 506: return "Literal: Número entero";
        case 507: return "Literal: Número real";

        // Error
        case 911: return "ERROR LÉXICO: Símbolo no reconocido";
            
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
