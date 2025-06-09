package lr1_sw;

import java.util.*;

public class ParserLR1 {
    private List<Produccion> producciones = new ArrayList<>();
    private Map<Integer, Map<String, Accion>> actionTable = new HashMap<>();
    private Map<Integer, Map<String, Integer>> gotoTable = new HashMap<>();

    public ParserLR1() {
        cargarProducciones();
        cargarTablaAcciones();
        cargarTablaGoto();
    }

    public List<String[]> analizar(List<String> tokens) {
        Stack<Integer> pila = new Stack<>();
        pila.push(0);

        List<String[]> pasos = new ArrayList<>();
        int index = 0;

        while (true) {
            int estado = pila.peek();
            String simbolo = index < tokens.size() ? tokens.get(index) : "$";

            Accion accion = actionTable.getOrDefault(estado, new HashMap<>()).get(simbolo);
            if (accion == null) {
                pasos.add(new String[]{pila.toString(), tokens.subList(index, tokens.size()).toString(), "Error: No hay acción definida para '" + simbolo + "' en estado " + estado});
                break;
            }

            if (accion.tipo.equals("shift")) {
                pila.push(accion.valor);
                pasos.add(new String[]{pila.toString(), tokens.subList(index, tokens.size()).toString(), "Shift " + simbolo + " -> Estado " + accion.valor});
                index++;
            } else if (accion.tipo.equals("reduce")) {
                Produccion p = producciones.get(accion.valor-1);
                for (int i = 0; i < p.der.size(); i++) {
                    pila.pop();
                }
                int nuevoEstado = pila.peek();
                String noTerminal = p.izq;
                Integer siguienteEstado = gotoTable.getOrDefault(nuevoEstado, new HashMap<>()).get(noTerminal);
                
                if (siguienteEstado == null) {
                    pasos.add(new String[]{pila.toString(), tokens.subList(index, tokens.size()).toString(), "Error: No hay transición GOTO para '" + noTerminal + "' en estado " + nuevoEstado});
                    break;
                }
                
                pila.push(siguienteEstado);
                pasos.add(new String[]{pila.toString(), tokens.subList(index, tokens.size()).toString(), "Reduce " + p + " -> GOTO(" + nuevoEstado + ", " + noTerminal + ") = " + siguienteEstado});
            } else if (accion.tipo.equals("accept")) {
                pasos.add(new String[]{pila.toString(), tokens.subList(index, tokens.size()).toString(), "Aceptar"});
                break;
            }
        }

        return pasos;
    }

    private void cargarProducciones() {
        producciones.add(new Produccion("S'", List.of("S")));
        producciones.add(new Produccion("S", List.of("SWITCH", "MASSWITCH")));
        producciones.add(new Produccion("SWITCH", List.of("switch", "(", "id", ")", "{", "MASCASES", "DEFAULT", "}")));
        producciones.add(new Produccion("MASSWITCH", List.of("SWITCH", "MASSWITCH")));
        producciones.add(new Produccion("MASSWITCH", List.of())); // λ
        producciones.add(new Produccion("MASCASES", List.of("CASES", "MASCASES")));
        producciones.add(new Produccion("MASCASES", List.of())); // λ
        producciones.add(new Produccion("CASES", List.of("case", "OPC", ":", "MASSWITCH", "BREAK")));
        producciones.add(new Produccion("DEFAULT", List.of("default", ":", "MASSWITCH")));
        producciones.add(new Produccion("DEFAULT", List.of())); // λ
        producciones.add(new Produccion("BREAK", List.of("break", ";")));
        producciones.add(new Produccion("BREAK", List.of())); // λ
        producciones.add(new Produccion("OPC", List.of("num", "RANGE")));
        producciones.add(new Produccion("RANGE", List.of("..", "num")));
        producciones.add(new Produccion("RANGE", List.of())); // λ
    }

    private void cargarTablaAcciones() {
        // Estado 0
        actionTable.computeIfAbsent(0, k -> new HashMap<>()).put("switch", new Accion("shift", 3));

        // Estado 1
        actionTable.computeIfAbsent(1, k -> new HashMap<>()).put("$", new Accion("accept", 0));

        // Estado 2
        actionTable.computeIfAbsent(2, k -> new HashMap<>()).put("switch", new Accion("shift", 3));
        actionTable.computeIfAbsent(2, k -> new HashMap<>()).put("$", new Accion("reduce", 5));

        // Estado 3
        actionTable.computeIfAbsent(3, k -> new HashMap<>()).put("(", new Accion("shift", 6));

        // Estado 4
        actionTable.computeIfAbsent(4, k -> new HashMap<>()).put("$", new Accion("reduce", 2));

        // Estado 5
        actionTable.computeIfAbsent(5, k -> new HashMap<>()).put("switch", new Accion("shift", 3));
        actionTable.computeIfAbsent(5, k -> new HashMap<>()).put("$", new Accion("reduce", 5));

        // Estado 6
        actionTable.computeIfAbsent(6, k -> new HashMap<>()).put("id", new Accion("shift", 8));

        // Estado 7
        actionTable.computeIfAbsent(7, k -> new HashMap<>()).put("$", new Accion("reduce", 4));

        // Estado 8
        actionTable.computeIfAbsent(8, k -> new HashMap<>()).put(")", new Accion("shift", 9));

        // Estado 9
        actionTable.computeIfAbsent(9, k -> new HashMap<>()).put("{", new Accion("shift", 10));

        // Estado 10
        actionTable.computeIfAbsent(10, k -> new HashMap<>()).put("}", new Accion("reduce", 7));
        actionTable.computeIfAbsent(10, k -> new HashMap<>()).put("case", new Accion("shift", 13));
        actionTable.computeIfAbsent(10, k -> new HashMap<>()).put("default", new Accion("reduce", 7));

        // Estado 11
        actionTable.computeIfAbsent(11, k -> new HashMap<>()).put("}", new Accion("reduce", 10));
        actionTable.computeIfAbsent(11, k -> new HashMap<>()).put("default", new Accion("shift", 15));

        // Estado 12
        actionTable.computeIfAbsent(12, k -> new HashMap<>()).put("}", new Accion("reduce", 7));
        actionTable.computeIfAbsent(12, k -> new HashMap<>()).put("case", new Accion("shift", 13));
        actionTable.computeIfAbsent(12, k -> new HashMap<>()).put("default", new Accion("reduce", 7));

        // Estado 13
        actionTable.computeIfAbsent(13, k -> new HashMap<>()).put("num", new Accion("shift", 18));

        // Estado 14
        actionTable.computeIfAbsent(14, k -> new HashMap<>()).put("}", new Accion("shift", 19));

        // Estado 15
        actionTable.computeIfAbsent(15, k -> new HashMap<>()).put(":", new Accion("shift", 20));

        // Estado 16
        actionTable.computeIfAbsent(16, k -> new HashMap<>()).put("}", new Accion("reduce", 6));
        actionTable.computeIfAbsent(16, k -> new HashMap<>()).put("default", new Accion("reduce", 6));

        // Estado 17
        actionTable.computeIfAbsent(17, k -> new HashMap<>()).put(":", new Accion("shift", 21));

        // Estado 18
        actionTable.computeIfAbsent(18, k -> new HashMap<>()).put("..", new Accion("shift", 23));
        actionTable.computeIfAbsent(18, k -> new HashMap<>()).put(":", new Accion("reduce", 15));

        // Estado 19
        actionTable.computeIfAbsent(19, k -> new HashMap<>()).put("switch", new Accion("reduce", 3));
        actionTable.computeIfAbsent(19, k -> new HashMap<>()).put("$", new Accion("reduce", 3));

        // Estado 20
        actionTable.computeIfAbsent(20, k -> new HashMap<>()).put("switch", new Accion("shift", 26));
        actionTable.computeIfAbsent(20, k -> new HashMap<>()).put("}", new Accion("reduce", 5));

        // Estado 21
        actionTable.computeIfAbsent(21, k -> new HashMap<>()).put("switch", new Accion("shift", 29));
        actionTable.computeIfAbsent(21, k -> new HashMap<>()).put("}", new Accion("reduce", 5));
        actionTable.computeIfAbsent(21, k -> new HashMap<>()).put("break", new Accion("reduce", 5));
        actionTable.computeIfAbsent(21, k -> new HashMap<>()).put("default", new Accion("reduce", 5));
        actionTable.computeIfAbsent(21, k -> new HashMap<>()).put("case", new Accion("reduce", 5));

        // Estado 22
        actionTable.computeIfAbsent(22, k -> new HashMap<>()).put(":", new Accion("reduce", 13));

        // Estado 23
        actionTable.computeIfAbsent(23, k -> new HashMap<>()).put("num", new Accion("shift", 30));

        // Estado 24
        actionTable.computeIfAbsent(24, k -> new HashMap<>()).put("}", new Accion("reduce", 9));

        // Estado 25
        actionTable.computeIfAbsent(25, k -> new HashMap<>()).put("}", new Accion("reduce", 5));
        actionTable.computeIfAbsent(25, k -> new HashMap<>()).put("switch", new Accion("shift", 26));

        // Estado 26
        actionTable.computeIfAbsent(26, k -> new HashMap<>()).put("(", new Accion("shift", 32));

        // Estado 27
        actionTable.computeIfAbsent(27, k -> new HashMap<>()).put("}", new Accion("reduce", 12));
        actionTable.computeIfAbsent(27, k -> new HashMap<>()).put("case", new Accion("reduce", 12));
        actionTable.computeIfAbsent(27, k -> new HashMap<>()).put("break", new Accion("shift", 34));
        actionTable.computeIfAbsent(27, k -> new HashMap<>()).put("default", new Accion("reduce", 12));

        // Estado 28
        actionTable.computeIfAbsent(28, k -> new HashMap<>()).put("switch", new Accion("shift", 29));
        actionTable.computeIfAbsent(28, k -> new HashMap<>()).put("}", new Accion("reduce", 5));
        actionTable.computeIfAbsent(28, k -> new HashMap<>()).put("case", new Accion("reduce", 5));
        actionTable.computeIfAbsent(28, k -> new HashMap<>()).put("break", new Accion("reduce", 5));
        actionTable.computeIfAbsent(28, k -> new HashMap<>()).put("default", new Accion("reduce", 5));

        // Estado 29
        actionTable.computeIfAbsent(29, k -> new HashMap<>()).put("(", new Accion("shift", 36));

        // Estado 30
        actionTable.computeIfAbsent(30, k -> new HashMap<>()).put(":", new Accion("reduce", 14));

        // Estado 31
        actionTable.computeIfAbsent(31, k -> new HashMap<>()).put("}", new Accion("reduce", 4));

        // Estado 32
        actionTable.computeIfAbsent(32, k -> new HashMap<>()).put("id", new Accion("shift", 37));

        // Estado 33
        actionTable.computeIfAbsent(33, k -> new HashMap<>()).put("}", new Accion("reduce", 8));
        actionTable.computeIfAbsent(33, k -> new HashMap<>()).put("case", new Accion("reduce", 8));
        actionTable.computeIfAbsent(33, k -> new HashMap<>()).put("default", new Accion("reduce", 8));

        // Estado 34
        actionTable.computeIfAbsent(34, k -> new HashMap<>()).put(";", new Accion("shift", 38));

        // Estado 35
        actionTable.computeIfAbsent(35, k -> new HashMap<>()).put("}", new Accion("reduce", 4));
        actionTable.computeIfAbsent(35, k -> new HashMap<>()).put("case", new Accion("reduce", 4));
        actionTable.computeIfAbsent(35, k -> new HashMap<>()).put("break", new Accion("reduce", 4));
        actionTable.computeIfAbsent(35, k -> new HashMap<>()).put("default", new Accion("reduce", 4));

        // Estado 36
        actionTable.computeIfAbsent(36, k -> new HashMap<>()).put("id", new Accion("shift", 39));

        // Estado 37
        actionTable.computeIfAbsent(37, k -> new HashMap<>()).put(")", new Accion("shift", 40));

        // Estado 38
        actionTable.computeIfAbsent(38, k -> new HashMap<>()).put("}", new Accion("reduce", 11));
        actionTable.computeIfAbsent(38, k -> new HashMap<>()).put("case", new Accion("reduce", 11));
        actionTable.computeIfAbsent(38, k -> new HashMap<>()).put("default", new Accion("reduce", 11));

        // Estado 39
        actionTable.computeIfAbsent(39, k -> new HashMap<>()).put(")", new Accion("shift", 41));

        // Estado 40
        actionTable.computeIfAbsent(40, k -> new HashMap<>()).put("{", new Accion("shift", 42));

        // Estado 41
        actionTable.computeIfAbsent(41, k -> new HashMap<>()).put("{", new Accion("shift", 43));

        // Estado 42
        actionTable.computeIfAbsent(42, k -> new HashMap<>()).put("}", new Accion("reduce", 7));
        actionTable.computeIfAbsent(42, k -> new HashMap<>()).put("case", new Accion("shift", 13));
        actionTable.computeIfAbsent(42, k -> new HashMap<>()).put("default", new Accion("reduce", 7));

        // Estado 43
        actionTable.computeIfAbsent(43, k -> new HashMap<>()).put("}", new Accion("reduce", 7));
        actionTable.computeIfAbsent(43, k -> new HashMap<>()).put("case", new Accion("shift", 13));
        actionTable.computeIfAbsent(43, k -> new HashMap<>()).put("default", new Accion("reduce", 7));

        // Estado 44
        actionTable.computeIfAbsent(44, k -> new HashMap<>()).put("}", new Accion("reduce", 10));
        actionTable.computeIfAbsent(44, k -> new HashMap<>()).put("default", new Accion("shift", 15));

        // Estado 45
        actionTable.computeIfAbsent(45, k -> new HashMap<>()).put("}", new Accion("reduce", 10));
        actionTable.computeIfAbsent(45, k -> new HashMap<>()).put("default", new Accion("shift", 15));

        // Estado 46
        actionTable.computeIfAbsent(46, k -> new HashMap<>()).put("}", new Accion("shift", 48));

        // Estado 47
        actionTable.computeIfAbsent(47, k -> new HashMap<>()).put("}", new Accion("shift", 49));

        // Estado 48
        actionTable.computeIfAbsent(48, k -> new HashMap<>()).put("switch", new Accion("reduce", 3));
        actionTable.computeIfAbsent(48, k -> new HashMap<>()).put("}", new Accion("reduce", 3));

        // Estado 49
        actionTable.computeIfAbsent(49, k -> new HashMap<>()).put("switch", new Accion("reduce", 3));
        actionTable.computeIfAbsent(49, k -> new HashMap<>()).put("}", new Accion("reduce", 3));
        actionTable.computeIfAbsent(49, k -> new HashMap<>()).put("case", new Accion("reduce", 3));
        actionTable.computeIfAbsent(49, k -> new HashMap<>()).put("break", new Accion("reduce", 3));
        actionTable.computeIfAbsent(49, k -> new HashMap<>()).put("default", new Accion("reduce", 3));
    }

    private void cargarTablaGoto() {
        // Estado 0
        gotoTable.computeIfAbsent(0, k -> new HashMap<>()).put("S", 1);
        gotoTable.computeIfAbsent(0, k -> new HashMap<>()).put("SWITCH", 2);

        // Estado 2
        gotoTable.computeIfAbsent(2, k -> new HashMap<>()).put("SWITCH", 5);
        gotoTable.computeIfAbsent(2, k -> new HashMap<>()).put("MASSWITCH", 4);

        // Estado 5
        gotoTable.computeIfAbsent(5, k -> new HashMap<>()).put("SWITCH", 5);
        gotoTable.computeIfAbsent(5, k -> new HashMap<>()).put("MASSWITCH", 7);

        // Estado 10
        gotoTable.computeIfAbsent(10, k -> new HashMap<>()).put("CASES", 12);
        gotoTable.computeIfAbsent(10, k -> new HashMap<>()).put("MASCASES", 11);

        // Estado 11
        gotoTable.computeIfAbsent(11, k -> new HashMap<>()).put("DEFAULT", 14);

        // Estado 12
        gotoTable.computeIfAbsent(12, k -> new HashMap<>()).put("CASES", 12);
        gotoTable.computeIfAbsent(12, k -> new HashMap<>()).put("MASCASES", 16);

        // Estado 13
        gotoTable.computeIfAbsent(13, k -> new HashMap<>()).put("OPC", 17);

        // Estado 18
        gotoTable.computeIfAbsent(18, k -> new HashMap<>()).put("RANGE", 22);

        // Estado 20
        gotoTable.computeIfAbsent(20, k -> new HashMap<>()).put("SWITCH", 25);
        gotoTable.computeIfAbsent(20, k -> new HashMap<>()).put("MASSWITCH", 24);

        // Estado 21
        gotoTable.computeIfAbsent(21, k -> new HashMap<>()).put("SWITCH", 28);
        gotoTable.computeIfAbsent(21, k -> new HashMap<>()).put("MASSWITCH", 27);

        // Estado 25
        gotoTable.computeIfAbsent(25, k -> new HashMap<>()).put("SWITCH", 25);
        gotoTable.computeIfAbsent(25, k -> new HashMap<>()).put("MASSWITCH", 31);

        // Estado 27
        gotoTable.computeIfAbsent(27, k -> new HashMap<>()).put("BREAK", 33);

        // Estado 28
        gotoTable.computeIfAbsent(28, k -> new HashMap<>()).put("SWITCH", 28);
        gotoTable.computeIfAbsent(28, k -> new HashMap<>()).put("MASSWITCH", 35);

        // Estado 42
        gotoTable.computeIfAbsent(42, k -> new HashMap<>()).put("CASES", 12);
        gotoTable.computeIfAbsent(42, k -> new HashMap<>()).put("MASCASES", 44);

        // Estado 43
        gotoTable.computeIfAbsent(43, k -> new HashMap<>()).put("CASES", 12);
        gotoTable.computeIfAbsent(43, k -> new HashMap<>()).put("MASCASES", 45);

        // Estado 44
        gotoTable.computeIfAbsent(44, k -> new HashMap<>()).put("DEFAULT", 46);

        // Estado 45
        gotoTable.computeIfAbsent(45, k -> new HashMap<>()).put("DEFAULT", 47);
        
        
        
    }
}

class Produccion {
    String izq;
    List<String> der;

    public Produccion(String izq, List<String> der) {
        this.izq = izq;
        this.der = der;
    }

    @Override
    public String toString() {
        return izq + " -> " + (der.isEmpty() ? "λ" : String.join(" ", der));
    }
}

class Accion {
    String tipo;
    int valor;

    public Accion(String tipo, int valor) {
        this.tipo = tipo;
        this.valor = valor;
    }
}