package proyectojuego;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class VentanaJuego extends javax.swing.JFrame {
    Juego juego;
    Menu ventanaMenu;
    
    /**
     * Constructor por defecto
     * @param menu El JFrame de la pantalla principal (el menú).
     * @param nivel Ruta de un archivo con la definición de un nivel, o nulo.
     */
    public VentanaJuego(JFrame menu, String nivel) {
        initComponents();
        ventanaMenu = (Menu) menu;
        
        juego = new Juego(nivel);
        this.setLayout(new BorderLayout());
        this.add(juego);
        setTitle("Batalla de bloques - Versión Alpha");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        juego.liberarRecursos();
        ventanaMenu.reiniciarMusica();
        ventanaMenu.setVisible(true);
        ventanaMenu = null;
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}