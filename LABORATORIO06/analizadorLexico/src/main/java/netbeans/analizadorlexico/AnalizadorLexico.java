package netbeans.analizadorlexico;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AnalizadorLexico {

    // Método que toma la entrada del usuario y realiza el análisis léxico
    public void analizar(String entrada, JTable tbAnalizadorLexico, JTable tbSimbolos) {
        DefaultTableModel modeloAnalizador = (DefaultTableModel) tbAnalizadorLexico.getModel();
        DefaultTableModel modeloSimbolos = (DefaultTableModel) tbSimbolos.getModel();

        modeloAnalizador.setRowCount(0);
        modeloSimbolos.setRowCount(0);

        // Variable para manejar los créditos (CRE)
        String tokenBuilder = "";
        boolean esCredito = false;
        String numeroCredito = "";

        // Recorrer la entrada para obtener tokens válidos (sin dividir por espacios)
        for (int i = 0; i < entrada.length(); i++) {
            char c = entrada.charAt(i);

            // Si es un carácter alfanumérico, agregar al token
            if (Character.isLetterOrDigit(c)) {
                tokenBuilder += c;

                // Si el token empieza con "CRE", entonces estamos buscando los créditos
                if (tokenBuilder.equals("CRE") && !esCredito) {
                    esCredito = true;  // Iniciar búsqueda del número
                    numeroCredito = ""; // Limpiar el número anterior
                }

                // Si ya es un "CRE" y estamos leyendo un número, lo agregamos al número
                if (esCredito && Character.isDigit(c)) {
                    numeroCredito += c;

                    // Si ya hemos leído 2 dígitos, procesamos y reiniciamos el "CRE"
                    if (numeroCredito.length() == 2) {
                        int creditos = Integer.parseInt(numeroCredito);
                        if (creditos >= 1 && creditos <= 59) {
                            modeloAnalizador.addRow(new Object[] {"Créditos de Alumno", "CRE" + numeroCredito, 100});
                            modeloSimbolos.addRow(new Object[] {"Créditos de Alumno", "CRE" + numeroCredito, 100});
                        } else {
                            modeloAnalizador.addRow(new Object[] {"Error", "CRE" + numeroCredito, 911});
                            modeloSimbolos.addRow(new Object[] {"Error", "CRE" + numeroCredito, 911});
                        }
                        tokenBuilder = ""; // Limpiar el token y esperar el siguiente
                        numeroCredito = ""; // Limpiar el número de créditos
                        esCredito = false; // Fin de la detección de créditos
                    }
                }
            } else {
                // Si encontramos un carácter no alfanumérico, procesamos el token
                if (tokenBuilder.length() > 0) {
                    procesarToken(tokenBuilder, modeloAnalizador, modeloSimbolos);
                    tokenBuilder = "";  // Limpiar el token
                }

                // Si es un error (como "o0" o cualquier carácter no válido), procesar
                if (!Character.isWhitespace(c)) {
                    modeloAnalizador.addRow(new Object[] {"Error", String.valueOf(c), 911});
                    modeloSimbolos.addRow(new Object[] {"Error", String.valueOf(c), 911});
                }
            }
        }

        // Procesar el último token si existe
        if (tokenBuilder.length() > 0) {
            procesarToken(tokenBuilder, modeloAnalizador, modeloSimbolos);
        }
    }

    // Método que procesa un token y lo clasifica
    private void procesarToken(String token, DefaultTableModel modeloAnalizador, DefaultTableModel modeloSimbolos) {
        // Procesar CRExx (ahora puede ser con uno o dos dígitos después de CRE)
        if (token.startsWith("CRE")) {
            String numeroParte = token.substring(3); // Parte del número después de "CRE"
            try {
                int creditos = Integer.parseInt(numeroParte);
                if (creditos >= 1 && creditos <= 59) {
                    modeloAnalizador.addRow(new Object[] {"Créditos de Alumno", token, 100});
                    modeloSimbolos.addRow(new Object[] {"Créditos de Alumno", token, 100});
                } else {
                    modeloAnalizador.addRow(new Object[] {"Error", token, 911});
                    modeloSimbolos.addRow(new Object[] {"Error", token, 911});
                }
            } catch (NumberFormatException e) {
                modeloAnalizador.addRow(new Object[] {"Error", token, 911});
                modeloSimbolos.addRow(new Object[] {"Error", token, 911});
            }
        }
        // Detectar números reales (ej. 7.8)
        else if (esNumeroReal(token)) {
            modeloAnalizador.addRow(new Object[] {"Real", token, 201});
            modeloSimbolos.addRow(new Object[] {"Real", token, 201});
        }
        // Detectar números octales (ej. 173o0) y procesar sólo si es válido
        else if (esNumeroOctal(token)) {
            String[] parts = token.split("o0");
            String beforeO = parts[0];  // Número antes del "o0"
            if (beforeO.matches("[0-7]+")) {
                modeloAnalizador.addRow(new Object[] {"Octal", token, 202});
                modeloSimbolos.addRow(new Object[] {"Octal", token, 202});
            } else {
                // Si no es octal válido, lo marcamos como error
                modeloAnalizador.addRow(new Object[] {"Error", token, 911});
                modeloSimbolos.addRow(new Object[] {"Error", token, 911});
            }
        }
        // Detectar números enteros (ej. 123)
        else if (esNumeroEntero(token)) {
            modeloAnalizador.addRow(new Object[] {"Entero", token, 200});
            modeloSimbolos.addRow(new Object[] {"Entero", token, 200});
        }
        // Si no coincide con ninguno de los patrones, marcarlo como error
        else {
            modeloAnalizador.addRow(new Object[] {"Error", token, 911});
            modeloSimbolos.addRow(new Object[] {"Error", token, 911});
        }
    }

    // Método para verificar si es un número real
    private boolean esNumeroReal(String token) {
        int punto = token.indexOf(".");
        if (punto != -1) {
            String entero = token.substring(0, punto);
            String decimal = token.substring(punto + 1);
            return !entero.isEmpty() && !decimal.isEmpty() && esNumeroEntero(entero) && esNumeroEntero(decimal);
        }
        return false;
    }

    // Método para verificar si es un número octal (como 173o0)
    private boolean esNumeroOctal(String token) {
        return token.endsWith("o0") && esNumeroEntero(token.substring(0, token.length() - 2)) && token.substring(0, token.length() - 2).matches("[0-7]+");
    }

    // Método para verificar si es un número entero
    private boolean esNumeroEntero(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
