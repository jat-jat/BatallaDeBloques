package proyectojuego;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javafx.scene.media.AudioClip;
import javax.imageio.ImageIO;
import static proyectojuego.Configuracion.*;

/**
 * Clase que representa a la pelota del juego.
 * @author Javier Alberto Argüello Tello
 */
public class Pelota extends Sprite{
    /**
     * Objeto que representa el área de colisión (que es circular) de este sprite.
     * Es una subclase del atributo "area", y el valor de "area" debe ser este objeto casteado.
     */
    private final Ellipse2D.Float areaCircular;
    
    /**
     * Imagen de la pelota.
     */
    private BufferedImage grafico;
    
    /**
     * Indica cuál jugador ha tenido contacto con la pelota por última vez,
     * para saber a quién darle los puntos por destruir un bloque.
     * Sus posibles valores deben ser las constantes "JUGADOR_1" y "JUGADOR_2".
     * @see Configuracion
     */
    public boolean propietario;
    
    /**
     * Espejos de las áreas de colisión de las paletas de ambos jugadores.
     * Se usan para que la pelota pueda estar pegada a una paleta al inicio de una ronda.
     * @see Jugador.area
     */
    private final Rectangle2D.Float posJ1, posJ2;
    
    /**
     * Sonido cuando la pelota rebota.
     */
    private final AudioClip sfx_rebotar;
    
    /**
     * Constructor por defecto.
     * Recibe a los jugadores como parámetro para obtener espejos de sus áreas de colisión.
     * @param j1 El objeto que representa al jugador 1.
     * @param j2 El objeto que representa al jugador 2.
     */
    public Pelota(Paleta j1, Paleta j2){
        sfx_rebotar = new AudioClip(getClass().getResource(SFX_REBOTE_DE_PELOTA).toString());
        
        try {
            grafico = ImageIO.read(getClass().getResource(IMG_PELOTA));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("NO SE PUDO CARGAR LA IMAGEN DE LA PELOTA");
            System.exit(-1);
        }
        
        areaCircular = new Ellipse2D.Float(0, 0, grafico.getWidth(), grafico.getHeight());
        area = areaCircular;
        
        //Creamos un color al azar.
        Color colorA = new Color((int)(Math.random()*256), (int)(Math.random()*256), (int)(Math.random()*256));
        //Obtenemos una versión más clara del mismo.
        Color colorB = colorA.brighter().brighter();
        //Pintamos la imagen de la pelota
        pintar(grafico, colorA, colorB, null);
        
        posJ1 = j1.areaRectangular;
        posJ2 = j2.areaRectangular;
            
        velocidadInicial = 3;
        reiniciarVelocidad();
    }
    
    @Override
    public void mover() {
        try {
            if(movimientoHor.value == MOV_NULO && movimientoVer.value == MOV_NULO){
                seEstaMoviendo = false;
                synchronized(movimientoHor){
                        movimientoHor.wait();
                }
            }
            
            seEstaMoviendo = true;
            
            if(movimientoHor.value != MOV_NULO && movimientoVer.value == MOV_NULO){
                areaCircular.y = (propietario == JUGADOR_1 ? posJ1.y - areaCircular.height : posJ2.height);
                float tmp = (propietario == JUGADOR_1 ? (posJ1.width / 2) - (areaCircular.width / 2) : (posJ2.width / 2) - (areaCircular.width / 2));
                
                while(movimientoHor.value != MOV_NULO && movimientoVer.value == MOV_NULO){
                    if(propietario == JUGADOR_1)
                        areaCircular.x = posJ1.x + tmp;
                    else
                        areaCircular.x = posJ2.x + tmp;
                    
                    Thread.sleep(MS_POR_FPS);
                }
                
                sfx_rebotar.play();
            } else if(movimientoHor.value != MOV_NULO && movimientoVer.value != MOV_NULO){
                boolean noHaTocadoPaleta = true;
                movimientoVer.value = (propietario == JUGADOR_1 ? MOV_ARRIBA : MOV_ABAJO);
                movimientoHor.value = (propietario == JUGADOR_1 ? MOV_DERECHA : MOV_IZQUIERDA);
                
                while(movimientoHor.value != MOV_NULO && movimientoVer.value != MOV_NULO){
                    areaCircular.x += (velocidad * (movimientoHor.value == MOV_DERECHA ? 1 : -1));
                    areaCircular.y += (velocidad * (movimientoVer.value == MOV_ABAJO ? 1 : -1));
                    
                    //IMPORTANTE: areaCircular.intersects(posJ1) hace lo mismo que pelota.checarColision(jugador1).
                    if(noHaTocadoPaleta){
                        //Cuando la pelota choca con una paleta.
                        if(areaCircular.intersects(posJ1) || areaCircular.intersects(posJ2)){
                            sfx_rebotar.play();
                            
                            //Actualizamos al propietario.
                            propietario = (areaCircular.intersects(posJ1) ? JUGADOR_1 : JUGADOR_2);
                            //La pelota rebota verticalmente.
                            movimientoVer.value = !movimientoVer.value;

                            noHaTocadoPaleta = false;
                        }
                    } else{
                        if(!(areaCircular.intersects(posJ1) || areaCircular.intersects(posJ2)))
                            noHaTocadoPaleta = true;
                    }
                    
                    if(areaCircular.x <= 0){ //Si toca el borde izquierdo.
                        areaCircular.x = 0;
                        movimientoHor.value = MOV_DERECHA; //Rebote
                    }
                    else if(areaCircular.x + areaCircular.width >= ESCENARIO_ANCHO){ //Si toca el borde derecho.
                        areaCircular.x = ESCENARIO_ANCHO - areaCircular.width;
                        movimientoHor.value = MOV_IZQUIERDA; //Rebote
                    }
                    
                    //Si la pelota se sale de la pantalla verticalmente.
                    if(areaCircular.y < -areaCircular.height || areaCircular.y > ESCENARIO_ALTO + areaCircular.height){
                        //Detenemos la bola
                        movimientoHor.value = movimientoVer.value = MOV_NULO;
                        
                        //Actualizamos al propietario
                        if((propietario == JUGADOR_1 && areaCircular.y < -areaCircular.height) || ((propietario == JUGADOR_2 && areaCircular.y > ESCENARIO_ALTO + areaCircular.height))){
                            //Si el propietario es el jugador 1 y la pelota se sale por la parte de arriba
                            // o si el propietario es el jugador 2 y la pelota se sale por la parte de abajo cambiamos de dueño.
                            propietario = !propietario;
                        }
                        
                        //El que sea el dueño de la pelota es quien la ha dejado salir de pantalla.
                    }
                    Thread.sleep(MS_POR_FPS);
                }
            }
        } catch (Exception e) {
            movimientoHor.value = movimientoVer.value = MOV_NULO;
        }
    }
    
    @Override
    public void iniciarMovimiento() {
        if(!seEstaMoviendo){
            synchronized(movimientoHor){
                movimientoHor.notify();
            }
        }
    }
    
    /**
     * Hace que el sprite se empiece a mover.
     * No tendrá efecto si el sprite ya se está moviendo.
     * @param modo Si es falso, la pelota se mantendrá pegada encima de la paleta del jugador propietario.
     * Si es verdadero, la pelota se moverá por la pantalla rebotando.
     */
    public void iniciarMovimiento(boolean modo) {
        if(modo){
            movimientoVer.value = MOV_ARRIBA;
            iniciarMovimiento();
        } else{
            movimientoHor.value = MOV_DERECHA;
            iniciarMovimiento();
        }
    }
    
    @Override
    public void renderizar(Graphics2D lienzo) {
        //PENDIENTE: Cógido para mostrar la bola girando
        //AffineTransformOp op = new AffineTransformOp(AffineTransform.getRotateInstance(Math.toRadians(++rotacion), areaCircular.width / 2, areaCircular.height / 2), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        //lienzo.drawImage(op.filter(grafico, null), (int)areaCircular.x, (int)areaCircular.y, null);
        
        lienzo.drawImage(grafico, (int)areaCircular.x, (int)areaCircular.y, null);
    }
    
    public short getX(){
        return (short)areaCircular.x;
    }
    
    public short getY(){
        return (short)areaCircular.y;
    }
}