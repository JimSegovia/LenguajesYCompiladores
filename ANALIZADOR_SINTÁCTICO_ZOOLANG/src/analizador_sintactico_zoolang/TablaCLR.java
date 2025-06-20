package analizador_sintactico_zoolang;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TablaCLR {
    private Map<Integer, Map<String, String>> tabla;

    public TablaCLR() {
        tabla = new HashMap<>();
        inicializarTabla();
    }

    private void inicializarTabla() {
    try (BufferedReader br = new BufferedReader(new FileReader("src/analizador_sintactico_zoolang/tablaCLR.txt"))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            
            // Saltar líneas vacías o comentarios (opcional)
            if (linea.trim().isEmpty() || linea.startsWith("//")) continue;

            String[] partes = linea.split(",", 3);
            if (partes.length == 3) {
                int estado = Integer.parseInt(partes[0].trim());
                String simbolo = partes[1].trim();
                String accion = partes[2].trim();

                // Si no existe la fila para ese estado, créala
                if (!tabla.containsKey(estado)) {
                    tabla.put(estado, new HashMap<>());
                }

                tabla.get(estado).put(simbolo, accion);
            }
        }
    } catch (IOException e) {
        System.err.println("Error al leer la tabla CLR: " + e.getMessage());
        e.printStackTrace();
}
    System.out.println("Se cargó la tabla, filas cargadas: " + tabla.size());
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
        return null;
    }

}