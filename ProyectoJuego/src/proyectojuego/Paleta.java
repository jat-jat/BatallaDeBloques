package proyectojuego;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import static proyectojuego.Configuracion.*;

public class Paleta extends Sprite{
    /**
     * Objeto que representa el área de colisión (que es rectangular) de este sprite.
     * Es una subclase del atributo "area", y el valor de "area" debe ser este objeto casteado.
     */
    protected Rectangle2D.Float areaRectangular;
    
    /**
     * El relleno de la paleta al ser dibujada.
     * Consiste en patrón formado por una imagen.
     */
    private TexturePaint relleno;
    
    byte vidas;
    short puntos;
    
    /**
     * Constructor por defecto.
     * Debe haber dos instancias en una partida.
     * @param jugador Indica a qué jugador pertenece: si es al #1, la paleta se posiciona en la parte inferior izquierda de la pantalla, si no, en la superior derecha. 
     */
    public Paleta(boolean jugador){
        /*
          Creamos el área de colisión, el cual es constante.
          Se puede hacer variable en el futuro.
        */
        areaRectangular = new Rectangle2D.Float(0, 0, 70, 10);
        area = areaRectangular;        
        
        try {
            BufferedImage imgRelleno = ImageIO.read(getClass().getResource(IMG_RELLENOS[(int)(Math.random()*IMG_RELLENOS.length)]));
            
            //Creamos un color al azar.
            Color colorA = new Color((int)(Math.random()*256), (int)(Math.random()*256), (int)(Math.random()*256));
            //Obtenemos una versión más clara del mismo.
            Color colorB = colorA.brighter().brighter();
            //Pintamos la imagen de la pelota
            pintar(imgRelleno, colorA, colorB, null);
            
            relleno = new TexturePaint(imgRelleno, new Rectangle(0, 0, imgRelleno.getWidth(), imgRelleno.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("NO SE PUDO CARGAR UN RELLENO");
            System.exit(-1);
        }
        
        //Posicionamos la paleta según el jugador.
        if(jugador == JUGADOR_2)
            areaRectangular.x = (short) (ESCENARIO_ANCHO - areaRectangular.width);
        else
            areaRectangular.y = (short) (ESCENARIO_ALTO - areaRectangular.height);
        
        //PENDIENTE: Agregar métodos get y set para estos 2 atributos,
        velocidad = 3;
        vidas = 3;
    }
    
    @Override
    public void mover(){
        try {
            //Si no hay movimiento...
            if(movimientoHor.value == MOV_NULO){
                seEstaMoviendo = false;
                //El hilo de movimiento se queda suspendido
                //(hasta que el jugador pulse una tecla de movimiento).
                synchronized(movimientoHor){
                    movimientoHor.wait();
                }
            }            
            
            seEstaMoviendo = true;
            
            //Mientras haya movimiento...
            while(movimientoHor.value != MOV_NULO){
                if(movimientoHor.value == MOV_IZQUIERDA){
                    //Si la paleta no se ha salido por el borde izquierdo, la movemos hacia esa dirección.
                    if(areaRectangular.x > 0)
                        areaRectangular.x -= velocidad;
                    else
                        areaRectangular.x = 0;
                }
                else if(movimientoHor.value == MOV_DERECHA){
                    //Si la paleta no se ha salido por el borde derecho, la movemos hacia esa dirección.
                    if(areaRectangular.x + areaRectangular.width < ESCENARIO_ANCHO)
                        areaRectangular.x += velocidad;
                    else
                        areaRectangular.x = (short) (ESCENARIO_ANCHO - areaRectangular.width);
                }
                
                //Si la paleta se quedó fuera de la pantalla, la colocamos en la posición correcta.
                if(areaRectangular.x < 0)
                    areaRectangular.x = 0;
                else if(areaRectangular.x + areaRectangular.width > ESCENARIO_ANCHO)
                    areaRectangular.x = (short) (ESCENARIO_ANCHO - areaRectangular.width);
                
                Thread.sleep(MS_POR_FPS);
            }                
        } catch (Exception e) {
            movimientoHor.value = MOV_NULO;
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

    @Override
    public boolean seEstaMoviendo() {
        return seEstaMoviendo;
    }
    
    @Override
    public void renderizar(Graphics2D lienzo){
        lienzo.setPaint(relleno);
        lienzo.fillRoundRect((int)areaRectangular.x, (int)areaRectangular.y, (int)areaRectangular.width, (int)areaRectangular.height, 10, 10);
        lienzo.setPaint(Color.BLACK);
        lienzo.drawRoundRect((int)areaRectangular.x, (int)areaRectangular.y, (int)areaRectangular.width, (int)areaRectangular.height, 10, 10);
    }
}