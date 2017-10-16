package proyectojuego;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import static proyectojuego.Configuracion.*;

/**
 * Clase que administra todos los poderes presentes en el campo de juego.
 * No hace uso de "movimientoHor", ni de "movimientoVer", la modificación de sus valores no tiene efecto.
 * @author Javier Alberto Argüello Tello
 */
public class AdminPoderes extends Sprite{
    /**
     * Imagen del contenedor de un poder.
     */
    private BufferedImage grafico;
    
    /**
     * Lista con todos los bloques.
     */
    private final ArrayList<Poder> poderes;
    
    /**
     * Números que se suman a las coordenadas de impresion de texto.
     */
    private final short addX, addY;
    
    /**
     * El poder que incluye cada bloque.
     */
    public enum TipoPoder { VIDA, PUNTOS, VELOCIDAD_PELOTA_AUMENTAR, VELOCIDAD_PELOTA_DISMINUIR, VELOCIDAD_PALETA_DISMINUIR, VELOCIDAD_PALETA_AUMENTAR, NINGUNO};
    
    public AdminPoderes(){
        poderes = new ArrayList<>();
        
        try {
            grafico = ImageIO.read(getClass().getResource(IMG_PODER));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("NO SE PUDO CARGAR LA IMAGEN DEL PODER");
            System.exit(-1);
        }
        
        velocidadInicial = 2; //La velocidad con la que se mueven los poderes.
        area = null;
        reiniciarVelocidad();
        
        addX = (short)(grafico.getWidth() / 2 - 4);
        addY = (short)(grafico.getHeight() / 2 + 5);
    }
    
    @Override
    protected void mover() {
        try {
            //Si no ningún poder en pantalla...
            if(poderes.isEmpty()){
                seEstaMoviendo = false;
                //El hilo de movimiento se queda suspendido hasta que se cree un poder.
                synchronized(movimientoVer){
                    movimientoVer.wait();
                }
            }
            
            seEstaMoviendo = true;
            
            while(!poderes.isEmpty()){
                for(byte i = 0; i < poderes.size(); i++){
                    //Movemos cada poder y lo eliminamos si se sale de la pantalla.
                    poderes.get(i).mover();
                    if(poderes.get(i).seSalioDePantalla())
                        poderes.remove(poderes.get(i));
                }
                Thread.sleep(MS_POR_FPS);
            }
        } catch (Exception e) {
            poderes.clear();
        }
    }
    
    /**
     * Añade un nuevo poder al campo de juego, después de haber eliminado un bloque con dicho poder.
     * @param tipo Tipo de poder, contenido en el bloque recién destruido.
     * @param x Posición en x donde va a aparecer.
     * @param y Posición en Y.
     * @param jugador Cuál jugador destruyó el bloque.
     */
    public void crearPoder(TipoPoder tipo, short x, short y, boolean jugador){
        if(tipo == TipoPoder.NINGUNO)
            return;
        
        poderes.add(new Poder(tipo, x, y, jugador));
        if(!seEstaMoviendo)
            iniciarMovimiento();
    }
    
    /**
     * Checa si el sprite (un jugador) chocó con un poder.
     * De ser así, elimina el poder y se devuelve el tipo de poder tocado.
     * @param s El sprite de un jugador/paleta.
     * @return El poder que ganó el jugador, o nulo, si no hubo colisión.
     */
    public InfoPoder checarColisionYObtenerPoder(Sprite s){
        try {
            for(byte i = 0; i < poderes.size(); i++){
                if(poderes.get(i).hayColision(s)){
                    Poder aux = poderes.remove(i);
                    return new InfoPoder(aux.tipo, aux.jugador);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    
    @Override
    public boolean checarColision(Sprite s){
        for(Poder aux : poderes){
            if(aux.hayColision(s))
                return true;
        }
        return false;
    }
    
    @Override
    public void iniciarMovimiento() {
        if(!seEstaMoviendo){
            synchronized(movimientoVer){
                movimientoVer.notify();
            }
        }
    }

    @Override
    public void renderizar(Graphics2D lienzo) {
        for(Poder aux : poderes)
            aux.renderizar(lienzo);
    }
    
    class Poder{
        TipoPoder tipo;
        //Posición
        short posX, posY;
        final boolean jugador;
        
        Poder(TipoPoder tipo, short x, short y, boolean jugador){
            this.tipo = tipo;
            posX = x;
            posY = y;
            this.jugador = jugador;
        }
        
        void mover(){
            if(jugador == JUGADOR_1)
                posY += velocidad;
            else
                posY -= velocidad;
        }
        
        boolean seSalioDePantalla(){
            if(jugador == JUGADOR_1)
                return (posY > ESCENARIO_ALTO);
            else
                return (posY < -(grafico.getHeight()));
        }
        
        boolean hayColision(Sprite x){
            return x.checarColision(posX, posY, (short)grafico.getWidth(), (short)grafico.getHeight());
        }
        
        void renderizar(Graphics2D lienzo){
            char caracter = ' ';
            
            switch(tipo){
                case PUNTOS:
                    lienzo.setPaint(Color.WHITE);
                    caracter = 'P';
                    break;
                case VIDA:
                    lienzo.setPaint(Color.GREEN);
                    caracter = 'V';
                    break;
                case VELOCIDAD_PELOTA_DISMINUIR:
                    lienzo.setPaint(Color.YELLOW);
                    caracter = '-';
                    break;
                case VELOCIDAD_PELOTA_AUMENTAR:
                    lienzo.setPaint(Color.YELLOW);
                    caracter = '+';
                    break;
                case VELOCIDAD_PALETA_DISMINUIR:
                    lienzo.setPaint(Color.CYAN);
                    caracter = '-';
                    break;
                case VELOCIDAD_PALETA_AUMENTAR:
                    lienzo.setPaint(Color.CYAN);
                    caracter = '+';
                    break;
            }
            
            lienzo.fillRect(posX, posY, grafico.getWidth(), grafico.getHeight());
            lienzo.drawImage(grafico, posX, posY, null);
            lienzo.setPaint(Color.BLACK);
            lienzo.setFont(fuente);
            lienzo.drawString(Character.toString(caracter), posX + addX, posY + addY);
        }
    }
    
    @Override
    public void destruir(){
        poderes.clear();
        super.destruir();
    }
}