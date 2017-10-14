package proyectojuego;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;
import static proyectojuego.Configuracion.*;

public class Juego extends javax.swing.JPanel {
    private Paleta jugador1, jugador2;
    private Pelota pelota;
    private ColeccionBloques bloques;
    
    //Variables relacionadas con la barra de progreso.
    private final float alturaTotal;
    private final Font fuente;
    private final short posInfoJ2; //Posición en X donde se imprime la información del jugador #2.
    
    public Juego() {
        initComponents();
        
        alturaTotal = ESCENARIO_ALTO + BARRA_PUNTUACIONES_ALTO;
        fuente = new Font(FUENTE_DE_LETRA_NOMBRE, Font.PLAIN, (int)(BARRA_PUNTUACIONES_ALTO * 1.3));
        posInfoJ2 = (short)((ESCENARIO_ANCHO / 8.0) * 3);
        
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
        bloques = new ColeccionBloques();
        
        Thread renderizacion = new Thread(() -> {
            while(true){
                try {
                    if(pelota.movimientoHor.value != MOV_NULO && pelota.movimientoVer.value != MOV_NULO){                        
                        if (bloques.checarColision(pelota)){
                            //Cuando la pelota choca con un bloque
                            pelota.movimientoHor.value = !pelota.movimientoHor.value;
                            pelota.movimientoVer.value = !pelota.movimientoVer.value;
                            
                            //El propietario de la pelota gana puntos.
                            if(pelota.propietario == JUGADOR_1)
                                jugador1.puntos++;
                            else
                                jugador2.puntos++;
                        }
                        
                        //El juego acaba porque: se acabaron los bloques.
                        if(bloques.yaNoHayBloques()){
                            pelota.movimientoHor.value = pelota.movimientoVer.value = MOV_NULO;
                            JOptionPane.showMessageDialog(null, "El jugador " + (jugador2.puntos > jugador1.puntos ? "2" : "1") + " ganó.");
                            reiniciarPartida();
                        }
                    } else if(pelota.movimientoHor.value == MOV_NULO && pelota.movimientoVer.value == MOV_NULO){
                        //Cuando el juego se acaba.
                        
                        //Quien se haya quedado con la propiedad de la pelota, pierde una vida.
                        if(pelota.propietario == JUGADOR_1)
                            jugador1.vidas--;
                        else
                            jugador2.vidas--;
                        
                        //El juego acaba porque: un jugador perdió sus vidas.
                        if(jugador1.vidas == 0 || jugador2.vidas == 0){
                            JOptionPane.showMessageDialog(null, "El jugador " + (jugador1.vidas == 0 ? "2" : "1") + " ganó.");
                            reiniciarPartida();
                        }
                        
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
    
    //PENDIENTE: Mejorar este método.
    public void reiniciarPartida(){
        jugador1.vidas = 3;
        jugador2.vidas = 3;
        jugador1.puntos = 0;
        jugador2.puntos = 0;
        bloques.crearBloques();
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
        
        //Pintamos la barra de estado.
        lienzo.setPaint(Color.BLACK);
        lienzo.fillRect(0, ESCENARIO_ALTO, ESCENARIO_ANCHO, BARRA_PUNTUACIONES_ALTO);
        lienzo.setFont(fuente);
        lienzo.setPaint(Color.GREEN);
        lienzo.drawString("J1: " + jugador1.vidas + "up - " + jugador1.puntos + " p.", 0, ESCENARIO_ALTO + BARRA_PUNTUACIONES_ALTO - 2);
        lienzo.setPaint(Color.YELLOW);
        lienzo.drawString("J2: " + jugador2.vidas + "up - " + jugador2.puntos + " p.", posInfoJ2, ESCENARIO_ALTO + BARRA_PUNTUACIONES_ALTO - 2);
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