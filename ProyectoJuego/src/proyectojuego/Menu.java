package proyectojuego;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import javax.swing.JOptionPane;
import static proyectojuego.Configuracion.*;

public class Menu extends javax.swing.JFrame {
    private Reproductor player;
    
    public Menu() {
        initComponents();
        player = new Reproductor();
        //Cargamos nuestra fuente de letra.
        try {
            reiniciarMusica();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(FUENTE_DE_LETRA)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void reiniciarMusica(){
        try {
            player.detener();
            player.cargarArchivo(getClass().getResourceAsStream(MUSICA_MENU));
            
            if(opcionSonido.isSelected())
                player.reproducir();
        } catch (Exception e) {}
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titulo = new javax.swing.JLabel();
        acercaDe = new javax.swing.JButton();
        opcionSonido = new javax.swing.JToggleButton();
        selectorNivel = new javax.swing.JComboBox<>();
        btnJugar = new javax.swing.JButton();
        barrita = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menú");
        setResizable(false);

        titulo.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
        titulo.setText("Batalla de bloques");

        acercaDe.setText("Acerca de");
        acercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acercaDeActionPerformed(evt);
            }
        });

        opcionSonido.setSelected(true);
        opcionSonido.setText("Música");
        opcionSonido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionSonidoActionPerformed(evt);
            }
        });

        selectorNivel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nivel 1", "Nivel 2", "Nivel 3", "Nivel 4", "Random" }));

        btnJugar.setBackground(new java.awt.Color(204, 0, 0));
        btnJugar.setForeground(new java.awt.Color(255, 255, 255));
        btnJugar.setText("¡Jugar!");
        btnJugar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJugarActionPerformed(evt);
            }
        });

        barrita.setBackground(new java.awt.Color(204, 204, 204));
        barrita.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(selectorNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnJugar))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titulo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(opcionSonido)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(acercaDe))
                    .addComponent(barrita))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(titulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(selectorNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnJugar))
                .addGap(2, 2, 2)
                .addComponent(barrita, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(opcionSonido)
                    .addComponent(acercaDe))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void acercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acercaDeActionPerformed
        JOptionPane.showMessageDialog(null, "UP Chiapas\nProgramación concurrente - 7°\n\nPROYECTO DE LA UNIDAD 1\nJuego con hilos no sincronizados\n\nJavier Alberto Argüello Tello - 153217\nJosé Julián Molina Ocaña - 153169\nFrancisco Javier de la Cruz Jiménez - 153181\nMauricio Armando Pérez Hernández - 153188\nJaime Francisco Ruiz López - 153189\nMónica Alejandra Peña Robles - 153209");
    }//GEN-LAST:event_acercaDeActionPerformed

    private void opcionSonidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionSonidoActionPerformed
        if(opcionSonido.isSelected()){
            player.reproducir();
        }
        else{
            player.detener();
        }
    }//GEN-LAST:event_opcionSonidoActionPerformed

    private void btnJugarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJugarActionPerformed
        setVisible(false);
        String nivel, musica;
        
        switch(selectorNivel.getSelectedIndex()){
            case 0:
                nivel = DISENO_NIVEL_1;
                musica = MUSICA_NIVEL_1;
                break;
            case 1:
                nivel = DISENO_NIVEL_2;
                musica = MUSICA_NIVEL_2;
                break;
            case 2:
                nivel = DISENO_NIVEL_3;
                musica = MUSICA_NIVEL_3;
                break;
            case 3:
                nivel = DISENO_NIVEL_4;
                musica = MUSICA_NIVEL_4;
                break;
            default:
                nivel = null;
                musica = MUSICA_NIVEL_RANDOM;
        }
        
        if(player.estaReproduciendo()){
            try {
                player.detener();
                player.cargarArchivo(getClass().getResourceAsStream(musica));
                player.reproducir();
            } catch (Exception e) {}
        }
        
        new VentanaJuego(this, nivel).setVisible(true);
    }//GEN-LAST:event_btnJugarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton acercaDe;
    private javax.swing.JSeparator barrita;
    private javax.swing.JButton btnJugar;
    private javax.swing.JToggleButton opcionSonido;
    private javax.swing.JComboBox<String> selectorNivel;
    private javax.swing.JLabel titulo;
    // End of variables declaration//GEN-END:variables
}