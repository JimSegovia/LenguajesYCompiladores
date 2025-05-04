package netbeans.analizadorlexico;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AnalizadorLexico {

    // Estados del autómata
    private static final int S = 0;
    private static final int Q1 = 1;
    private static final int Q2 = 2;
    private static final int Q3 = 3;
    private static final int Q4 = 4;
    private static final int Q5 = 5;
    private static final int Q7 = 7;
    private static final int Q8 = 8;
    private static final int Q9 = 9;
    private static final int Q11 = 11;
    private static final int Q12 = 12;
    private static final int ERROR = -1;

    // Tokens
    private static final int TOKEN_CREDITO = 100;
    private static final int TOKEN_ENTERO = 200;
    private static final int TOKEN_REAL = 201;
    private static final int TOKEN_OCTAL = 202;
    private static final int TOKEN_ERROR = 911;

    public void analizar(String entrada, JTable tbAnalizadorLexico, JTable tbSimbolos) {
        DefaultTableModel modeloAnalizador = (DefaultTableModel) tbAnalizadorLexico.getModel();
        DefaultTableModel modeloSimbolos = (DefaultTableModel) tbSimbolos.getModel();

        modeloAnalizador.setRowCount(0);
        modeloSimbolos.setRowCount(0);

        int estadoActual = S;
        String lexema = "";
        int i = 0;
        int n = entrada.length();
        boolean octalValido = true;

        while (i < n) {
            char c = entrada.charAt(i);

            // Ignorar espacios en blanco
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Proceso de análisis según el autómata
            switch (estadoActual) {
                case S:
                    if (c == 'C') {
                        estadoActual = Q1;
                        lexema += c;
                        i++;
                    } else if (Character.isDigit(c)) {
                        estadoActual = Q7;
                        lexema += c;
                        if (c > '7') {
                        octalValido = false;
        }
                        i++;
                    } else {
                        // Carácter no válido en estado inicial
                        modeloAnalizador.addRow(new Object[]{"Error", String.valueOf(c), TOKEN_ERROR});
                        modeloSimbolos.addRow(new Object[]{"Error", String.valueOf(c), TOKEN_ERROR});
                        i++;
                    }
                    break;

                case Q1:
                    if (c == 'R') {
                        estadoActual = Q2;
                        lexema += c;
                        i++;
                    } else {
                        estadoActual = ERROR;
                    }
                    break;

                case Q2:
                    if (c == 'E') {
                        estadoActual = Q3;
                        lexema += c;
                        i++;
                    } else {
                        estadoActual = ERROR;
                    }
                    break;

                case Q3:
                    if (Character.isDigit(c)) {
                        estadoActual = Q4;
                        lexema += c;
                        i++;
                    } else {
                        estadoActual = ERROR;
                    }
                    break;

                case Q4:
                    if (Character.isDigit(c)) {
                        estadoActual = Q5;
                        lexema += c;
                        i++;
                    } else {
                        // Transición lambda (aceptar con un solo dígito)
                        String numStr = lexema.substring(3); // Extraer la parte numérica
                        int creditos = Integer.parseInt(numStr);
                        if (creditos >= 0 && creditos <= 59) {
                            modeloAnalizador.addRow(new Object[]{"Créditos de Alumno", lexema, TOKEN_CREDITO});
                            modeloSimbolos.addRow(new Object[]{"Créditos de Alumno", lexema, TOKEN_CREDITO});
                        } else {
                            modeloAnalizador.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                            modeloSimbolos.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                        }
                        lexema = "";
                        estadoActual = S;
                    }
                    break;

                case Q5:
                    // Estado final para créditos (CREdd)
                    String numStr = lexema.substring(3); // Extraer la parte numérica
                    int creditos = Integer.parseInt(numStr);
                    if (creditos >= 0 && creditos <= 59) {
                        modeloAnalizador.addRow(new Object[]{"Créditos de Alumno", lexema, TOKEN_CREDITO});
                        modeloSimbolos.addRow(new Object[]{"Créditos de Alumno", lexema, TOKEN_CREDITO});
                    } else {
                        modeloAnalizador.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                        modeloSimbolos.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                    }
                    lexema = "";
                    estadoActual = S;
                    break;

                case Q7:
                    if (Character.isDigit(c)) {
                        lexema += c;
                        if (octalValido && c > '7') {
                            octalValido = false;
                        }
                        i++;
                        // Permanecemos en Q7 (autociclo)
                    } else if (c == '.') {
                        estadoActual = Q8;
                        lexema += c;
                        i++;
                        octalValido = false; // Ya no es octal si tiene punto
                    } else if (c == 'o') {
                        estadoActual = Q11;
                        lexema += c;
                        i++;
                    } else {
                        // Estado final para enteros (d+)
                        modeloAnalizador.addRow(new Object[]{"Entero", lexema, TOKEN_ENTERO});
                        modeloSimbolos.addRow(new Object[]{"Entero", lexema, TOKEN_ENTERO});
                        lexema = "";
                        estadoActual = S;
                        octalValido = true;
                    }
                    break;

                case Q8:
                    if (Character.isDigit(c)) {
                        estadoActual = Q9;
                        lexema += c;
                        i++;
                    } else {
                        estadoActual = ERROR;
                    }
                    break;

                case Q9:
                    if (Character.isDigit(c)) {
                        lexema += c;
                        i++;
                    } else {
                        // Estado final para reales (d+.d+)
                        modeloAnalizador.addRow(new Object[]{"Real", lexema, TOKEN_REAL});
                        modeloSimbolos.addRow(new Object[]{"Real", lexema, TOKEN_REAL});
                        lexema = "";
                        estadoActual = S;
                    }
                    break;

                case Q11:
                    if (c == '0') {
                        estadoActual = Q12;
                        lexema += c;
                        i++;
                    } else {
                        estadoActual = ERROR;
                    }
                    break;

                case Q12:
                    // Estado final para octales (d+o0)
                    if (octalValido) {
                        modeloAnalizador.addRow(new Object[]{"Octal", lexema, TOKEN_OCTAL});
                        modeloSimbolos.addRow(new Object[]{"Octal", lexema, TOKEN_OCTAL});
                    } else {
                        modeloAnalizador.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                        modeloSimbolos.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                    }
                    lexema = "";
                    estadoActual = S;
                    octalValido = true;
                    break;

                case ERROR:
                    // Procesar el error
                    String errorLexema = lexema + c;
                    modeloAnalizador.addRow(new Object[]{"Error", errorLexema, TOKEN_ERROR});
                    modeloSimbolos.addRow(new Object[]{"Error", errorLexema, TOKEN_ERROR});
                    lexema = "";
                    estadoActual = S;
                    octalValido = true;
                    i++;
                    break;
            }

            // Manejar errores de transición
            if (estadoActual == ERROR) {
                modeloAnalizador.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                modeloSimbolos.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                lexema = "";
                estadoActual = S;
                octalValido = true;
            }
        }

        // Procesar cualquier lexema pendiente al final de la entrada
        if (!lexema.isEmpty()) {
            switch (estadoActual) {
                case Q4:
                    // Aceptar crédito con un solo dígito (CREd)
                    String numStr = lexema.substring(3);
                    int creditos = Integer.parseInt(numStr);
                    if (creditos >= 0 && creditos <= 59) {
                        modeloAnalizador.addRow(new Object[]{"Créditos de Alumno", lexema, TOKEN_CREDITO});
                        modeloSimbolos.addRow(new Object[]{"Créditos de Alumno", lexema, TOKEN_CREDITO});
                    } else {
                        modeloAnalizador.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                        modeloSimbolos.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                    }
                    break;
                case Q5:
                    // Aceptar crédito con dos dígitos (CREdd)
                    numStr = lexema.substring(3);
                    creditos = Integer.parseInt(numStr);
                    if (creditos >= 0 && creditos <= 59) {
                        modeloAnalizador.addRow(new Object[]{"Créditos de Alumno", lexema, TOKEN_CREDITO});
                        modeloSimbolos.addRow(new Object[]{"Créditos de Alumno", lexema, TOKEN_CREDITO});
                    } else {
                        modeloAnalizador.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                        modeloSimbolos.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                    }
                    break;
                case Q7:
                    // Aceptar número entero (d+)
                    modeloAnalizador.addRow(new Object[]{"Entero", lexema, TOKEN_ENTERO});
                    modeloSimbolos.addRow(new Object[]{"Entero", lexema, TOKEN_ENTERO});
                    break;
                case Q9:
                    // Aceptar número real (d+.d+)
                    modeloAnalizador.addRow(new Object[]{"Real", lexema, TOKEN_REAL});
                    modeloSimbolos.addRow(new Object[]{"Real", lexema, TOKEN_REAL});
                    break;
                case Q12:
                    if (octalValido) {
                        modeloAnalizador.addRow(new Object[]{"Octal", lexema, TOKEN_OCTAL});
                        modeloSimbolos.addRow(new Object[]{"Octal", lexema, TOKEN_OCTAL});
                    } else {
                        modeloAnalizador.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                        modeloSimbolos.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                    }
                    break;
                default:
                    // Cualquier otro caso es error
                    modeloAnalizador.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                    modeloSimbolos.addRow(new Object[]{"Error", lexema, TOKEN_ERROR});
                    octalValido = true;
                    break;
            }
        }
    }
}