package codigo;

import java.io.File;
import vista.ViewAnalizadorLexico;

public class Principal {
    public static void main(String[] args) {
        // Generar el analizador léxico
        generarLexer("src/codigo/Lexer.flex");
        
        // Mostrar la interfaz gráfica
        java.awt.EventQueue.invokeLater(() -> {
            ViewAnalizadorLexico ventana = new ViewAnalizadorLexico();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
            ventana.configurarAnalizador();
        });
    }

    public static void generarLexer(String ruta) {
        try {
            File archivo = new File(ruta);
            if (!archivo.exists()) {
                System.err.println("El archivo no existe: " + archivo.getAbsolutePath());
                return;
            }
            
            System.out.println("Generando lexer desde: " + archivo.getAbsolutePath());
            JFlex.Main.generate(archivo);
            
            File lexerJava = new File("src/codigo/Lexer.java");
            if (lexerJava.exists()) {
                System.out.println("Lexer generado exitosamente");
            } else {
                System.err.println("Error: No se generó el archivo Lexer.java");
            }
        } catch (Exception e) {
            System.err.println("Error al generar el lexer:");
        }
    }
}