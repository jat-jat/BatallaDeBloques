package proyectojuego;

import java.awt.Graphics2D;
import java.awt.Shape;
import javax.xml.ws.Holder;
import static proyectojuego.Configuracion.MOV_NULO;

/**
 * Clase abstracta que representa un objeto en pantalla móvil capaz de interactuar con otros elementos de su entorno.
 * @author Javier
 */
public abstract class Sprite {
    public static final byte VELOCIDAD_MIN = 1, VELOCIDAD_MAX = 10;
    
    /**
     * Indica si el sprite está en movimiento.
     */
    protected boolean seEstaMoviendo; 
    
    /**
     * Bolsita/contenedor de un booleano que indica si el sprite
     * se está moviendo en un eje. Los valores del booleano deben
     * ser indicados por las constantes: "MOV_NULO", "MOV_IZQUIERDA",
     * "MOV_DERECHA", "MOV_ABAJO" y "MOV_ARRIBA".
     * @see Configuracion
     */
    public final Holder<Boolean> movimientoHor, movimientoVer;

    /**
     * El área de colisión (una región en pantalla finita que
     * se usa para detectar colisiones) de este sprite.
     */
    protected Shape area;

    /**
     * Indica cuántos pixeles se moverá este sprite por fotograma,
     * en caso de haber movimiento.
     */
    protected byte velocidad;
    
    /**
     * Constructor por defecto.
     */
    public Sprite(){
        movimientoHor = new Holder<>(MOV_NULO);
        movimientoVer = new Holder<>(MOV_NULO);
        velocidad = VELOCIDAD_MIN;
        seEstaMoviendo = false;
        
        Thread gestorDeMovimiento = new Thread(() -> {
            while(true){
                try {
                    mover();
                } catch (Exception e) {
                }
            }
        });
        gestorDeMovimiento.setDaemon(true);
        gestorDeMovimiento.start();
    }

    /**
     * Controla el movimiento del sprite.
     * Este método debe ser ejecutado dentro de un ciclo,
     * en un Thread exclusido para este sprite.
     * Este Thread es creado e iniciado automáticamente
     * en la superclase Sprite.
     */
    protected abstract void mover();
    
    /**
     * Hace que el sprite se empiece a mover.
     * No tendrá efecto si el sprite ya se está moviendo.
     * Dependiendo de la subclase, deberá modificar la o las
     * direcciones (movimiento vertical y/u horizontal).
     */
    public abstract void iniciarMovimiento();
    
    /**
     * Permite saber si el sprite está en movimiento.
     * @return Un booleano.
     */
    public abstract boolean seEstaMoviendo();
    
    /**
     * Dibuja este sprite en un fotograma que será mostrado en pantalla.
     * @param lienzo Objeto que permite dibujar elementos en pantalla.
     */
    public abstract void renderizar(Graphics2D lienzo);

    /**
     * Aumenta la velocidad (cuántos pixeles se mueve este sprite por fotograma) del sprite.
     */
    public void aumentarVelocidad(){
        if(velocidad < VELOCIDAD_MAX)
            velocidad++;
    }

    /**
     * Disminuye la velocidad (cuántos pixeles se mueve este sprite por fotograma) del sprite.
     */
    public void disminuirVelocidad(){
        if(velocidad > VELOCIDAD_MIN)
            velocidad--;
    }

    /**
     * Comprueba si el área de colisión de este sprite tiene
     * contacto con el área de colisión de otro sprite.
     * @param s Sprite con el que se evaluará la colisión.
     * @return Si hay colisión.
     */
    public boolean checarColision(Sprite s){
        return area.intersects(s.area.getBounds2D());
    }

    /**
     * Comprueba si el área de colisión de este sprite tiene
     * contacto con un área de colisión rectangular especificada
     * por los parámetros.
     * @param x Posición en el eje X del área.
     * @param y Posición en el eje Y del área.
     * @param w Anchura del área.
     * @param h Altura del área.
     * @return Si hay colisión.
     */
    public boolean checarColision(short x, short y, short w, short h){
        return area.intersects(x, y, w, h);
    }
}