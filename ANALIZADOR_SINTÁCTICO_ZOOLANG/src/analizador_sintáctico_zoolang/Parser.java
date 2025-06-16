package analizador_sintáctico_zoolang;

import java.util.List;
import java.util.Stack;

public class Parser {
    private TablaCLR tabla;
    private Stack<String> pila;
    private String[][] producciones;
    private List<Integer> tokens;
    private List<String> entradaOriginal;
    private int pos;

    public Parser() {
        tabla = new TablaCLR();
        pila = new Stack<>();

        producciones = new String[][]{
            {"E", "E", "506", "T"},    // E -> E + T
            {"E", "T"},                // E -> T
            {"T", "T", "507", "F"},    // T -> T * F
            {"T", "F"},                // T -> F
            {"F", "508", "E", "509"},  // F -> ( E )
            {"F", "504"}               // F -> id
        };
    }
    
    private vista.VistaAnalizadorSintáctico vista;

    public void setTokens(List<Integer> tokens) {
        this.tokens = tokens;
    }

    public void setEntradaOriginal(List<String> entradaOriginal) {
        this.entradaOriginal = entradaOriginal;
    }

    private String lexico() {
        if (pos >= tokens.size()) return "36"; // $
        return String.valueOf(tokens.get(pos));
    }

    private String entradaRestante() {
        StringBuilder sb = new StringBuilder();
        for (int i = pos; i < entradaOriginal.size(); i++) {
            sb.append(entradaOriginal.get(i)).append(" ");
        }
        return sb.toString().trim();
    }
    
    public void setVista(vista.VistaAnalizadorSintáctico vista) {
        this.vista = vista;
    }

    public boolean sintactico() {
        pila.clear();
        pos = 0;
        pila.push("0");

        //System.out.println("Pila\t\tEntrada\t\tAccion");
        //System.out.println("----\t\t-------\t\t------");

        while (true) {
            String estado = pila.peek();
            String simbolo = lexico();
            String accion = tabla.accion(estado, simbolo);

            /* System.out.print(pilaToString() + "\t\t" + entradaRestante() + "\t\t");

            if (accion == null || accion.equals(" ")) {
                System.out.println("Error: accion invalida [" + simbolo + "]");
                return false;
            }
            */
            
            String pilaActual = pilaToString();
            String entradaActual = entradaRestante();
            String accionTexto;
            
            
            if (accion.equals("Aceptar")) {
            vista.agregarFila(pilaActual, entradaActual, "Aceptar");
            return true;    
               /* System.out.println("Aceptar");
                return true;*/
            }
            
            
            if (accion.startsWith("d")) {
                /*System.out.println("Desplazar: " + simbolo + " -> estado " + accion.substring(1));
                pila.push(simbolo);
                pila.push(accion.substring(1));
                pos++;*/
                
                accionTexto = "Desplazar: " + simbolo + " -> estado " + accion.substring(1);
                vista.agregarFila(pilaActual, entradaActual, accionTexto);

                pila.push(simbolo);
                pila.push(accion.substring(1));
                pos++;

            } else if (accion.startsWith("r")) {
                /*int numProd = Integer.parseInt(accion.substring(1));
                String[] produccion = producciones[numProd - 1];
                String lhs = produccion[0]; // Lado izquierdo
                int rhsLen = produccion.length - 1; // Cantidad de símbolos en RHS


                System.out.print("Reducir: " + lhs + " -> ");
                for (int i = 1; i < produccion.length; i++) {
                    System.out.print(produccion[i] + " ");
                }
                System.out.println("por lookahead: " + simbolo);

                if (rhsLen > 0) {

                    for (int i = 0; i < rhsLen; i++) {
                        pila.pop(); // Símbolo
                        pila.pop(); // Estado
                    }
                }

                String estadoAnterior = pila.peek();
                pila.push(lhs);
                String gotoEstado = tabla.accion(estadoAnterior, lhs);
                pila.push(gotoEstado);*/
                
                int numProd = Integer.parseInt(accion.substring(1));
                String[] produccion = producciones[numProd - 1];
                String lhs = produccion[0];
                int rhsLen = produccion.length - 1;

                StringBuilder reduccion = new StringBuilder("Reducir: " + lhs + " -> ");
                for (int i = 1; i < produccion.length; i++) {
                    reduccion.append(produccion[i]).append(" ");
                }
                reduccion.append("por lookahead: ").append(simbolo);

                vista.agregarFila(pilaActual, entradaActual, reduccion.toString());

                // Pop de la pila
                for (int i = 0; i < rhsLen; i++) {
                    pila.pop(); // símbolo
                    pila.pop(); // estado
                }
                String estadoAnterior = pila.peek();
                pila.push(lhs);
                String gotoEstado = tabla.accion(estadoAnterior, lhs);
                pila.push(gotoEstado);

            } else {
               /* System.out.println("Error: accion desconocida");
                return false;*/         
            vista.agregarFila(pilaActual, entradaActual, "Error: acción inválida [" + simbolo + "]");
            return false;
            } 
        }  
    }

    private String pilaToString() {
        StringBuilder sb = new StringBuilder();
        for (String s : pila) {
            sb.append(s);
        }
        return sb.toString();
    }
}
