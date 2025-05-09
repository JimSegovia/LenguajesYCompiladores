/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package al_zoolang;

import al_zoolang.lexer.Lexer;
import al_zoolang.lexer.Tokens;
import java.io.FileReader;
import java_cup.runtime.Symbol;


import vista.ViewAnalizadorLexico;
/**
 *
 * @author JimXL
 */
public class AL_ZOOLANG {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java AL_ZOOLANG <archivo.azoo>");
            return;
        }
        
        try {
            Lexer lexer = new Lexer(new FileReader(args[0]));
            
            System.out.println("Análisis léxico iniciado:");
            System.out.println("------------------------");
            
            Symbol token;
            do {
                token = lexer.next_token();
                
                if (token.sym != Tokens.EOF) {
                    System.out.printf("Línea %d, Columna %d: ", token.left, token.right);
                    
                    switch (token.sym) {
                        case Tokens.ID_GLOBAL:
                            System.out.printf("Identificador global '%s'%n", token.value);
                            break;
                        case Tokens.LIT_ENT:
                            System.out.printf("Entero %d%n", token.value);
                            break;
                        // ... agregar más casos según necesites
                        default:
                            System.out.printf("Token %d%n", token.sym);
                    }
                }
            } while (token.sym != Tokens.EOF);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     
     * 
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ViewAnalizadorLexico ventana = new ViewAnalizadorLexico();
    
                // Centramos la ventana en la pantalla
        ventana.setLocationRelativeTo(null);
    
                // Hacemos visible la ventana
                ventana.setVisible(true);
            }
        });
    }
    */
}
