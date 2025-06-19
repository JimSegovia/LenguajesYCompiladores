package analizador_léxico_zoolang;

import java.io.File;

public class GenerarLexer {
    public static void main(String[] args) {
        // Generar el analizador léxico
        generarLexer("src/analizador_léxico_zoolang/Lexer.flex");     
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
            
            File lexerJava = new File("src/analizador_léxico_zoolang/Lexer.java");
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