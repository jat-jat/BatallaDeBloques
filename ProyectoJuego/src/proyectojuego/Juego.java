package proyectojuego;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javafx.scene.media.AudioClip;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import static proyectojuego.Configuracion.*;

public class Juego extends javax.swing.JPanel {
    private Paleta jugador1, jugador2;
    private Pelota pelota;
    private ColeccionBloques bloques;
    private AdminPoderes poderes;
    private AudioClip sfx_bonus, sfx_golpeBloque, sfx_perderVida, sfx_rebotar;
    
    //Variables relacionadas con la barra de progreso.
    private final float alturaTotal;
    private final short posInfoJ2; //Posición en X donde se imprime la información del jugador #2.
    
    private boolean renderizar;
    
    public Juego(String nivel) {
        initComponents();
        
        sfx_bonus = new AudioClip(getClass().getResource(SFX_OBTENCION_DE_BONUS).toString());
        sfx_golpeBloque = new AudioClip(getClass().getResource(SFX_GOLPEAR_UN_BLOQUE).toString());
        sfx_perderVida = new AudioClip(getClass().getResource(SFX_PERDER_UNA_VIDA).toString());
        
        alturaTotal = ESCENARIO_ALTO + BARRA_PUNTUACIONES_ALTO;
        posInfoJ2 = (short)((ESCENARIO_ANCHO / 8.0) * 3);
        
        renderizar = true;
        
        this.addKeyListener((new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                int tecla = e.getKeyCode();
        
                if(tecla == KeyEvent.VK_A || tecla == KeyEvent.VK_D){
                    if(tecla == KeyEvent.VK_A)
                        jugador1.movimientoHor.value = MOV_IZQUIERDA;
                    else
                        jugador1.movimientoHor.value = MOV_DERECHA;
                    
                    if(!jugador1.seEstaMoviendo)
                        jugador1.iniciarMovimiento();
                } else if(tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_RIGHT){
                    if(tecla == KeyEvent.VK_LEFT)
                        jugador2.movimientoHor.value = MOV_IZQUIERDA;
                    else
                        jugador2.movimientoHor.value = MOV_DERECHA;

                    if(!jugador2.seEstaMoviendo)
                        jugador2.iniciarMovimiento();
                } else if(pelota.movimientoHor.value != MOV_NULO && pelota.movimientoVer.value == MOV_NULO){
                    //Cuando es el inicio de la partida y la pelota está pegada a una paleta.
                    if((pelota.propietario == JUGADOR_1 && tecla == KeyEvent.VK_W) || (pelota.propietario == JUGADOR_2 && tecla == KeyEvent.VK_DOWN)){
                        pelota.iniciarMovimiento(true);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int tecla = e.getKeyCode();
        
                if(tecla == KeyEvent.VK_A || tecla == KeyEvent.VK_D){
                    if(jugador1.movimientoHor.value != MOV_NULO)
                        if((tecla == KeyEvent.VK_A && jugador1.movimientoHor.value == MOV_IZQUIERDA) || (tecla == KeyEvent.VK_D && jugador1.movimientoHor.value == MOV_DERECHA))
                            jugador1.movimientoHor.value = MOV_NULO;
                } else if(tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_RIGHT){
                    if(jugador2.movimientoHor.value != MOV_NULO)
                        if((tecla == KeyEvent.VK_LEFT && jugador2.movimientoHor.value == MOV_IZQUIERDA) || (tecla == KeyEvent.VK_RIGHT && jugador2.movimientoHor.value == MOV_DERECHA))
                            jugador2.movimientoHor.value = MOV_NULO;
                }
            }
        }));
        
        //Creamos los objetos y sprites.
        jugador1 = new Paleta(JUGADOR_1);
        jugador2 = new Paleta(JUGADOR_2);
        pelota = new Pelota(jugador1, jugador2);
        bloques = new ColeccionBloques(nivel);
        poderes = new AdminPoderes();
        
        Thread renderizacion = new Thread(() -> {
            AdminPoderes.TipoPoder x;
            while(renderizar){
                try {
                    if(pelota.movimientoHor.value != MOV_NULO && pelota.movimientoVer.value != MOV_NULO){                        
                        x = bloques.checarColision(pelota);
                        //Cuando la pelota choca con un bloque
                        if (x != null){
                            sfx_golpeBloque.play();
                            
                            pelota.movimientoHor.value = !pelota.movimientoHor.value;
                            pelota.movimientoVer.value = !pelota.movimientoVer.value;
                            
                            //El propietario de la pelota gana puntos.
                            if(pelota.propietario == JUGADOR_1)
                                jugador1.ganarPunto();
                            else
                                jugador2.ganarPunto();
                            
                            //Creamos un poder si se da el caso
                            if(x != AdminPoderes.TipoPoder.NINGUNO)
                                poderes.crearPoder(x, pelota.getX(), pelota.getY(), pelota.propietario);
                        }
                        
                        //Si hay poderes en pantalla
                        if(poderes.seEstaMoviendo()){
                            //Vemos si cada jugador tocó un poder. De ser así, el poder desaparece y surte efecto.
                            aplicarPoder(poderes.checarColisionYObtenerPoder(jugador1));
                            aplicarPoder(poderes.checarColisionYObtenerPoder(jugador2));
                        }
                        
                        //El juego acaba porque: se acabaron los bloques.
                        if(bloques.yaNoHayBloques()){
                            pelota.movimientoHor.value = pelota.movimientoVer.value = MOV_NULO;
                            JOptionPane.showMessageDialog(null, "El jugador " + (jugador2.getPuntos() > jugador1.getPuntos() ? "2" : "1") + " ganó.");
                            liberarRecursos();
                            ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose();
                        }
                    } else if(pelota.movimientoHor.value == MOV_NULO && pelota.movimientoVer.value == MOV_NULO){
                        //Cuando el juego termina.
                        boolean elJugador1Perdio = false, elJugador2Perdio = false;
                        
                        sfx_perderVida.play();
                        
                        //Quien se haya quedado con la propiedad de la pelota, pierde una vida.
                        if(pelota.propietario == JUGADOR_1){
                            elJugador1Perdio = jugador1.perderVida();
                            jugador1.reiniciarVelocidad();
                        }
                        else{
                            elJugador2Perdio = jugador2.perderVida();
                            jugador2.reiniciarVelocidad();
                        }
                        
                        //El juego acaba porque: un jugador perdió sus vidas.
                        if(elJugador1Perdio || elJugador2Perdio){
                            JOptionPane.showMessageDialog(null, "El jugador " + (elJugador1Perdio ? "2" : "1") + " ganó.");
                            liberarRecursos();
                            ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose();
                        }
                        
                        pelota.reiniciarVelocidad();
                        pelota.iniciarMovimiento(false);
                    }
                    
                    //Pintamos un fotograma
                    repaint();
                    Thread.sleep(MS_POR_FPS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        renderizacion.setDaemon(true);
        renderizacion.start();
        
        pelota.propietario = JUGADOR_1;
        pelota.iniciarMovimiento(false); //Si se elimina esta línea, se le quitará una vida a un jugador, nada más empezar.
        
        //Estas instrucciones son necesarias para que los eventos del teclado se puedan ejecutar.
        setFocusable(true);
        requestFocusInWindow();
    }
    
    private void aplicarPoder(InfoPoder poderObtenido){
        //Si el poder no es nulo, quiere decir que hubo colisión y el poder tocado desapareció
        if(poderObtenido != null){
            sfx_bonus.play();
            
            switch(poderObtenido.getTipo()){
                case PUNTOS:
                    if(poderObtenido.getJugador() == JUGADOR_1)
                        jugador1.ganarPunto();
                    else
                        jugador2.ganarPunto();
                    break;
                case VELOCIDAD_PALETA_AUMENTAR:
                    if(poderObtenido.getJugador() == JUGADOR_1)
                        jugador1.acelerar();
                    else
                        jugador2.acelerar();
                    break;
                case VELOCIDAD_PALETA_DISMINUIR:
                    if(poderObtenido.getJugador() == JUGADOR_1)
                        jugador1.alentar();
                    else
                        jugador2.alentar();
                    break;
                case VELOCIDAD_PELOTA_AUMENTAR:
                    pelota.acelerar();
                    break;
                case VELOCIDAD_PELOTA_DISMINUIR:
                    pelota.alentar();
                    break;
                case VIDA:
                    if(poderObtenido.getJugador() == JUGADOR_1)
                        jugador1.ganarVida();
                    else
                        jugador2.ganarVida();
            }
        }
    }
    
    /**
     * Dibuja un fotograma en pantalla.
     * @param g Objeto que controla los gráficos del JPanel.
     */
    private void renderizar(Graphics g) {
        //Obtenemos el objeto que nos permitirá pintar la pantalla.
        Graphics2D lienzo = (Graphics2D) g;
        //Duplicamos el tamaño del fotograma actual.
        lienzo.scale(getWidth() / (ESCENARIO_ANCHO * 1.0), getHeight() / alturaTotal);
        //Se borra todo lo que haya en pantalla.
        lienzo.clearRect(0, 0, ESCENARIO_ANCHO, ESCENARIO_ALTO);
        //Pintamos los elementos en la pantalla
        jugador1.renderizar(lienzo);
        jugador2.renderizar(lienzo);
        pelota.renderizar(lienzo);
        bloques.renderizar(lienzo);
        poderes.renderizar(lienzo);
        
        //Pintamos la barra de estado.
        lienzo.setPaint(Color.BLACK);
        lienzo.fillRect(0, ESCENARIO_ALTO, ESCENARIO_ANCHO, BARRA_PUNTUACIONES_ALTO);
        lienzo.setFont(fuente);
        lienzo.setPaint(Color.GREEN);
        lienzo.drawString("J1: " + jugador1.getVidas() + "up - " + jugador1.getPuntos() + " p.", 0, ESCENARIO_ALTO + BARRA_PUNTUACIONES_ALTO - 2);
        lienzo.setPaint(Color.YELLOW);
        lienzo.drawString("J2: " + jugador2.getVidas() + "up - " + jugador2.getPuntos() + " p.", posInfoJ2, ESCENARIO_ALTO + BARRA_PUNTUACIONES_ALTO - 2);
    }

    @Override
    /**
     * Método que se ejecuta luego de llamar a "repaint()" y
     * pinta un fotograma.
     * @param g Objeto que controla los gráficos del JPanel.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderizar(g);
    }
    
    public void liberarRecursos(){
        renderizar = false;
        jugador1.destruir();
        jugador2.destruir();
        pelota.destruir();
        poderes.destruir();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}