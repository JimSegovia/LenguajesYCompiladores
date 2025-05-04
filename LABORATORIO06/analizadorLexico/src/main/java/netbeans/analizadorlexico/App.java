/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package netbeans.analizadorlexico;

import vista.ViewLabo6;

/**
 *
 * @author ARIAN BEJAR
 */
public class App {
    

    public static void main(String[] args) {
            java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Aquí creamos e inicializamos la interfaz gráfica
                new ViewLabo6().setVisible(true);
            }
        });
    }
}
