package proyectojuego;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import javax.xml.ws.Holder;
import static proyectojuego.Configuracion.*;

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
     * Velocidad default.
     */
    protected byte velocidadInicial;
    
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
                    e.printStackTrace();
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
    public boolean seEstaMoviendo(){
        return seEstaMoviendo;
    };
    
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
    
    /**
     * Aumenta la velocidad, si es posible.
     */
    public void acelerar(){
        if(velocidad < VELOCIDAD_MAX)
            velocidad++;
    }
    
    /**
     * Disminuye la velocidad, si es posible.
     */
    public void alentar(){
        if(velocidad > VELOCIDAD_MIN)
            velocidad--;
    }
    
    /**
     * Disminuye la velocidad, si es posible.
     */
    public void reiniciarVelocidad(){
        velocidad = velocidadInicial;
    }
    
    /**
     * Colorea una imagen cargada.
     * Este método no tendrá efecto si la imagen ya fue pintada una vez.
     * Algunos parámtetros de color pueden ser nulos.
     * @param img Una imagen con píxles RGB(1,0,0), RGB(0,1,0) y/o RGB(0,0,1).
     * @param cA Con qué color se van a pintar los píxeles RGB(1,0,0).
     * @param cB Con qué color se van a pintar los píxeles RGB(0,1,0).
     * @param cC Con qué color se van a pintar los píxeles RGB(0,0,1).
     * @see Configuracion.PALETA_DE_COLORES_1
     */
    public void pintar(BufferedImage img, Color cA, Color cB, Color cC){
        //Valores numéricos de los colores
        Integer c1, c2, c3;
        
        c1 = (cA != null ? cA.getRGB() : null);
        c2 = (cB != null ? cB.getRGB() : null);
        c3 = (cC != null ? cC.getRGB() : null);
        
        //Visitamos todos los pixeles de la imagen y cambiamos el color donde sea necesario.
        int pixelActual;
        for(short i = 0; i < img.getWidth(); i++)
            for(short j = 0; j < img.getHeight(); j++){
                pixelActual = img.getRGB(i, j);
                
                if(c1 != null && pixelActual == PALETA_DE_COLORES_1)
                    img.setRGB(i, j, c1);
                else if(c2 != null && pixelActual == PALETA_DE_COLORES_2)
                    img.setRGB(i, j, c2);
                else if(c3 != null && pixelActual == PALETA_DE_COLORES_3)
                    img.setRGB(i, j, c3);
            }
    }
}