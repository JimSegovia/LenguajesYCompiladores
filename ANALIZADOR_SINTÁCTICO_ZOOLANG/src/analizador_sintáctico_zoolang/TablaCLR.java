package analizador_sintáctico_zoolang;

import java.util.HashMap;
import java.util.Map;

public class TablaCLR {
    private Map<Integer, Map<String, String>> tabla;

    public TablaCLR() {
        tabla = new HashMap<>();
        inicializarTabla();
    }

    
    private void inicializarTabla() { //REMPALZAR ESTA TABLA USANDO EL GENERADOR, FALTA CORREGIR EL GENERADOR PARA QUE CAMBIE LOS NO TERMINALES POR TOKENS AUTOAMTICAMENTE
    Map<String, String> fila0 = new HashMap<>();
    fila0.put("E", "1");
    fila0.put("T", "2");
    fila0.put("F", "3");
    fila0.put("508", "d4");
    fila0.put("504", "d5");
    tabla.put(0, fila0);

    Map<String, String> fila1 = new HashMap<>();
    fila1.put("36", "Aceptar");
    fila1.put("506", "d6");
    tabla.put(1, fila1);

    Map<String, String> fila2 = new HashMap<>();
    fila2.put("36", "r2");
    fila2.put("506", "r2");
    fila2.put("507", "d7");
    tabla.put(2, fila2);

    Map<String, String> fila3 = new HashMap<>();
    fila3.put("36", "r4");
    fila3.put("506", "r4");
    fila3.put("507", "r4");
    tabla.put(3, fila3);

    Map<String, String> fila4 = new HashMap<>();
    fila4.put("E", "8");
    fila4.put("T", "9");
    fila4.put("F", "10");
    fila4.put("508", "d11");
    fila4.put("504", "d12");
    tabla.put(4, fila4);

    Map<String, String> fila5 = new HashMap<>();
    fila5.put("36", "r6");
    fila5.put("506", "r6");
    fila5.put("507", "r6");
    tabla.put(5, fila5);

    Map<String, String> fila6 = new HashMap<>();
    fila6.put("T", "13");
    fila6.put("F", "3");
    fila6.put("508", "d4");
    fila6.put("504", "d5");
    tabla.put(6, fila6);

    Map<String, String> fila7 = new HashMap<>();
    fila7.put("F", "14");
    fila7.put("508", "d4");
    fila7.put("504", "d5");
    tabla.put(7, fila7);

    Map<String, String> fila8 = new HashMap<>();
    fila8.put("509", "d16");
    fila8.put("506", "d15");
    tabla.put(8, fila8);

    Map<String, String> fila9 = new HashMap<>();
    fila9.put("509", "r2");
    fila9.put("506", "r2");
    fila9.put("507", "d17");
    tabla.put(9, fila9);

    Map<String, String> fila10 = new HashMap<>();
    fila10.put("509", "r4");
    fila10.put("506", "r4");
    fila10.put("507", "r4");
    tabla.put(10, fila10);

    Map<String, String> fila11 = new HashMap<>();
    fila11.put("E", "18");
    fila11.put("T", "9");
    fila11.put("F", "10");
    fila11.put("508", "d11");
    fila11.put("504", "d12");
    tabla.put(11, fila11);

    Map<String, String> fila12 = new HashMap<>();
    fila12.put("509", "r6");
    fila12.put("506", "r6");
    fila12.put("507", "r6");
    tabla.put(12, fila12);

    Map<String, String> fila13 = new HashMap<>();
    fila13.put("36", "r1");
    fila13.put("506", "r1");
    fila13.put("507", "d7");
    tabla.put(13, fila13);

    Map<String, String> fila14 = new HashMap<>();
    fila14.put("36", "r3");
    fila14.put("506", "r3");
    fila14.put("507", "r3");
    tabla.put(14, fila14);

    Map<String, String> fila15 = new HashMap<>();
    fila15.put("T", "19");
    fila15.put("F", "10");
    fila15.put("508", "d11");
    fila15.put("504", "d12");
    tabla.put(15, fila15);

    Map<String, String> fila16 = new HashMap<>();
    fila16.put("36", "r5");
    fila16.put("506", "r5");
    fila16.put("507", "r5");
    tabla.put(16, fila16);

    Map<String, String> fila17 = new HashMap<>();
    fila17.put("F", "20");
    fila17.put("508", "d11");
    fila17.put("504", "d12");
    tabla.put(17, fila17);

    Map<String, String> fila18 = new HashMap<>();
    fila18.put("509", "d21");
    fila18.put("506", "d15");
    tabla.put(18, fila18);

    Map<String, String> fila19 = new HashMap<>();
    fila19.put("509", "r1");
    fila19.put("506", "r1");
    fila19.put("507", "d17");
    tabla.put(19, fila19);

    Map<String, String> fila20 = new HashMap<>();
    fila20.put("509", "r3");
    fila20.put("506", "r3");
    fila20.put("507", "r3");
    tabla.put(20, fila20);

    Map<String, String> fila21 = new HashMap<>();
    fila21.put("509", "r5");
    fila21.put("506", "r5");
    fila21.put("507", "r5");
    tabla.put(21, fila21);
    }
    
    public String accion(String estado, String simbolo) {
        try {
            int est = Integer.parseInt(estado);
            Map<String, String> fila = tabla.get(est);
            if (fila != null && fila.containsKey(simbolo)) {
                return fila.get(simbolo);
            }
        } catch (NumberFormatException e) {
            // Ignorar, por si acaso
        }
        return " ";
    }

}