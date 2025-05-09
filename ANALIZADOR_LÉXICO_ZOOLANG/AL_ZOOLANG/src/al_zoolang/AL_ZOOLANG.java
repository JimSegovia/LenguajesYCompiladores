package al_zoolang;

import vista.ViewAnalizadorLexico;

public class AL_ZOOLANG {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ViewAnalizadorLexico ventana = new ViewAnalizadorLexico();
                ventana.setLocationRelativeTo(null); // Centrar ventana
                ventana.setVisible(true);
                
                // Configurar el lexer y acciones
                ventana.configurarAnalizador();
            }
        });
    }
}