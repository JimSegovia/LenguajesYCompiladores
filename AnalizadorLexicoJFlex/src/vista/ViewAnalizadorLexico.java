/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import codigo.Lexer;  // Cambiar de al_zoolang.lexer.Lexer a codigo.Lexer
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.StringReader;
import codigo.Tokens;
import java.util.HashMap;
import java.util.Map;
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
    String[] columnasLexico = {"Descripción", "Lexema",  "Lexico", "Token"};
    DefaultTableModel modelLexico = new DefaultTableModel(columnasLexico, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    tbAnalizadorLexico.setModel(modelLexico);
    
    // Configurar modelo para la tabla de símbolos
    String[] columnasSimbolos = {"Descripción", "Lexema", "Cantidad", "Token"};
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
        tbAnalizadorLexico.getColumnModel().getColumn(2).setPreferredWidth(60);
        tbAnalizadorLexico.getColumnModel().getColumn(3).setPreferredWidth(5);
        tbSimbolos.getColumnModel().getColumn(0).setPreferredWidth(160);
        tbSimbolos.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbSimbolos.getColumnModel().getColumn(2).setPreferredWidth(60);
        tbSimbolos.getColumnModel().getColumn(3).setPreferredWidth(5);
        
        
    }
    
    public void configurarAnalizador() {
        btnAnalizar.addActionListener(e -> analizarCodigo());
        btnLimpiar.addActionListener(e -> analizarCodigo());
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

        Map<String, Integer> conteoSimbolos = new HashMap<>();
        Map<String, Integer> tokenPorLexema = new HashMap<>();
        Map<String, String> descripcionPorLexema = new HashMap<>();

        while ((token = lexer.yylex()) != Tokens.EOF) {
            procesarToken(lexer, token, modelLexico, modelSimbolos, conteoSimbolos, tokenPorLexema, descripcionPorLexema);
            if (token == Tokens.ERROR) {
                hayErrores = true;
            }
        }
        
        for (String lexema : conteoSimbolos.keySet()) {
        modelSimbolos.addRow(new Object[]{
            descripcionPorLexema.get(lexema),
            lexema,
            conteoSimbolos.get(lexema),
            tokenPorLexema.get(lexema)
        });
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
                           DefaultTableModel modelSimbolos,
                           Map<String, Integer> conteoSimbolos,
                           Map<String, Integer> tokenPorLexema,
                           Map<String, String> descripcionPorLexema) {
    int codigoToken = token.getTokenNumber(); // Usamos el método de tu enum
    String lexema = lexer.getLexema(); // Usamos el getter en lugar del campo directo
    String descripcion = obtenerDescripcionToken(codigoToken);
    
    modelLexico.addRow(new Object[]{
        descripcion,
        lexema,
        obtenerNombreToken(codigoToken),
        codigoToken
    });
    
        conteoSimbolos.put(lexema, conteoSimbolos.getOrDefault(lexema, 0) + 1);
        tokenPorLexema.putIfAbsent(lexema, codigoToken);
        descripcionPorLexema.putIfAbsent(lexema, descripcion);
    
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
        case 418: return "RUGIR";
        case 419: return "RECI";
        case 420: return "CAMA";
        case 421: return "LEON";
        case 422: return "MERODEAR";
        case 423: return "RONDAR";
        case 424: return "ME";
        case 425: return "INSTINTO";
        case 426: return "INSTINTO_FINAL";
        case 427: return "REACCION";
        case 428: return "HUIR";
        case 429: return "VERDAD";
        case 430: return "FALSO";
        case 504: return "LIT_NULO";
        case 480: return "CHAR";
        case 481: return "SELF";
        case 482: return "NUEVO";
        case 483: return "INICIAR";

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

        // Comentario
        case 508: return "COMENTARIO";
        
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
        case 418: return "Palabra reservada: rugg ";
        case 419: return "Palabra reservada: reci";
        case 420: return "Palabra reservada: cama";
        case 421: return "Palabra reservada: leon";
        case 422: return "Palabra reservada: merodear";
        case 423: return "Palabra reservada: rondar";
        case 424: return "Palabra reservada: me";
        case 425: return "Palabra reservada: instinto";
        case 426: return "Palabra reservada: instintoFinal";
        case 427: return "Palabra reservada: reaccion";
        case 428: return "Palabra reservada: huir";
        case 429: return "Palabra reservada: verdad (Valor verdadero)";
        case 430: return "Palabra reservada: falso (Valor falso)";
        case 504: return "Palabra reservada: nulo (Valor nulo)";
        case 480: return "Palabra reservada: char (Tipo carácter)";
        case 481: return "Palabra reservada: self ";
        case 482: return "Palabra reservada: NUEVO ";
        case 483: return "Palabra reservada: INICIAR ";

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
        case 508: return "Comentario en Bloque";

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
        jSeparator1 = new javax.swing.JSeparator();
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
        btnLimpiar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        unmsm = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 51));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jSeparator1.setBackground(new java.awt.Color(0, 153, 153));

        btnAnalizar.setBackground(new java.awt.Color(0, 153, 153));
        btnAnalizar.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 18)); // NOI18N
        btnAnalizar.setText("ANALIZAR");
        btnAnalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarActionPerformed(evt);
            }
        });

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

        btnLimpiar.setBackground(new java.awt.Color(255, 0, 0));
        btnLimpiar.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 18)); // NOI18N
        btnLimpiar.setText("LIMPIAR");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 51, 51));
        jPanel2.setAutoscrolls(true);

        unmsm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/UNMSMnew.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("ANALIZADOR LÉXICO ZOOLANG");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(unmsm, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87)
                .addComponent(jLabel2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(unmsm, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(34, 34, 34)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addGap(299, 299, 299))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane3)
                                .addGap(67, 67, 67)
                                .addComponent(jScrollPane2)))
                        .addGap(48, 48, 48))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(121, 121, 121)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addGap(438, 438, 438)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAnalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                        .addGap(205, 205, 205))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(btnAnalizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAnalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarActionPerformed
    
    }//GEN-LAST:event_btnAnalizarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
     // Limpiar las tablas
    DefaultTableModel modelLexico = (DefaultTableModel) tbAnalizadorLexico.getModel();
    DefaultTableModel modelSimbolos = (DefaultTableModel) tbSimbolos.getModel();
    modelLexico.setRowCount(0);
    modelSimbolos.setRowCount(0);
    
    // Limpiar el área de texto
    txAreaEntradaDatos.setText("");
   
    }//GEN-LAST:event_btnLimpiarActionPerformed

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
    public javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
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
