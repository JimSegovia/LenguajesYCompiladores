package analizador_sintactico_zoolang;

import java.util.Scanner;
import vista.VistaAnalizadorSintáctico;
import analizador_léxico_zoolang.Analizador_Léxico;

public class Main {
    public static void main(String[] args) {
        
        java.awt.EventQueue.invokeLater(() -> {
            VistaAnalizadorSintáctico ventana = new VistaAnalizadorSintáctico();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });        
        
        // Prueba por consola
        
       /* Analizador_Léxico lexer = new Analizador_Léxico();
        Scanner sc = new Scanner(System.in);
        System.out.print("Leer cadena: ");
        String input = sc.nextLine();

        lexer.analizar(input); // Produce tokens
        Parser parser = new Parser();
        parser.setTokens(lexer.getTokens());
        parser.setEntradaOriginal(lexer.getEntradaOriginal());

        boolean resultado = parser.sintactico();
        System.out.println("Resultado: " + (resultado ? "Cadena valida" : "Cadena invalida"));*/
    }
}
