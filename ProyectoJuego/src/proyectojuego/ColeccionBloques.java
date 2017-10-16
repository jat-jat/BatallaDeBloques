package proyectojuego;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import proyectojuego.AdminPoderes.TipoPoder;
import static proyectojuego.Configuracion.*;

/**
 * Clase que administra todos los bloques presentes en el campo de juego.
 * @author Javier Alberto Argüello Tello
 */
public class ColeccionBloques {    
    /**
     * Cuántas veces puede ser golpeado un bloque, como máximo,
     * antes de ser destruido.
     */
    public static final byte VIDA_MAXIMA = 5;
    
    /**
     * Lista con todos los bloques.
     */
    private final ArrayList<Bloque> bloques;
    
    /**
     * Colección de imagenes según el tamaño del bloque.
     * Contiene la misma imagen repetida con distintos colores
     * (cada color corresponde a cierta cantidad de vida).
     */
    private BufferedImage[] graficos_BloquesG, graficos_BloquesM, graficos_BloquesCh;
    
    enum Tamano { GRANDE, MEDIANO, CHICO }
    
    /**
     * Constructor.
     * @param nivel Ruta del archivo con la definición de un nivel, o nulo.
     */
    public ColeccionBloques(String nivel){
        bloques = new ArrayList<>();
        Color[] colores = new Color[VIDA_MAXIMA];
        colores[0] = new Color(220, 0, 0);
        colores[1] = new Color(255, 204, 0);
        colores[2] = new Color(51, 204, 51);
        colores[3] = new Color(105, 94, 255);
        colores[4] = new Color(128, 128, 128);
        
        
        for(byte i = 0; i < 3; i++){
            BufferedImage img = null;
            short ancho, alto;
            BufferedImage[] grupoImg = null;
            byte l;
            
            try {
                if(i == 0){
                    grupoImg = graficos_BloquesG = new BufferedImage[VIDA_MAXIMA];
                    img = ImageIO.read(getClass().getResource(IMG_BLOQUE_GRANDE));
                }
                else if(i == 1){
                    grupoImg = graficos_BloquesM = new BufferedImage[VIDA_MAXIMA];
                    img = ImageIO.read(getClass().getResource(IMG_BLOQUE_MEDIANO));
                }
                else{
                    grupoImg = graficos_BloquesCh = new BufferedImage[VIDA_MAXIMA];
                    img = ImageIO.read(getClass().getResource(IMG_BLOQUE_CHICO));
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("NO SE PUDO CARGAR LA IMAGEN DE UN BLOQUE");
                System.exit(-1);
            }
            
            ancho = (short)img.getWidth();
            alto = (short)img.getHeight();
            
            for(byte j = 0; j < VIDA_MAXIMA; j++){
                grupoImg[j] = new BufferedImage(ancho, alto, img.getType());
            }
            
            for(short j = 0; j < ancho; j++)
                for(short k = 0; k < alto; k++){
                    if(img.getRGB(j, k) == PALETA_DE_COLORES_1)
                        for(l = 0; l < VIDA_MAXIMA; l++)
                            grupoImg[l].setRGB(j, k, colores[l].brighter().brighter().getRGB());
                    else if(img.getRGB(j, k) == PALETA_DE_COLORES_2)
                        for(l = 0; l < VIDA_MAXIMA; l++)
                            grupoImg[l].setRGB(j, k, colores[l].getRGB());
                    else if(img.getRGB(j, k) == PALETA_DE_COLORES_3)
                        for(l = 0; l < VIDA_MAXIMA; l++)
                            grupoImg[l].setRGB(j, k, colores[l].darker().darker().getRGB());
                }
        }
        
        crearBloques(nivel);
    }

    /**
     * Crea los bloques de la partida.
     * @param archivo La ruta a un archivo con lo datos de los bloques, o nulo, para generar un nivel random.
     */
    public void crearBloques(String archivo){
        BufferedReader br = null;
        InputStreamReader fr = null;

        try {
            fr = new InputStreamReader(getClass().getResourceAsStream(archivo));
            br = new BufferedReader(fr);
            
            String linea;
            String[] parametros;
            
            while ((linea = br.readLine()) != null) {
                parametros = linea.split(",");
                if(parametros.length == 4)
                    bloques.add(new Bloque((parametros[0].equalsIgnoreCase("G") ? Tamano.GRANDE : parametros[0].equalsIgnoreCase("M") ? Tamano.MEDIANO : Tamano.CHICO), Byte.parseByte(parametros[3]), Short.parseShort(parametros[1]), Short.parseShort(parametros[2])));
            }
        } catch (Exception e) {
            if(archivo != null)
                e.printStackTrace();
            
            //Creamos bloques al azar
            byte tam;
            bloques.clear();
            for(byte i = 0; i < 20; i++){
                tam = (byte)(Math.random() * 3);
                
                bloques.add(new Bloque((tam == 0 ? Tamano.GRANDE : tam == 1 ? Tamano.MEDIANO : Tamano.CHICO), (byte)(Math.random() * VIDA_MAXIMA + 1), (short)(Math.random() * (ESCENARIO_ANCHO - 50)), (short)((Math.random() * (ESCENARIO_ALTO - 80)) + 30)));
            }
        } finally{
            try {
                if(br != null)
                    br.close();
                if(fr != null)
                    fr.close();
            } catch (Exception e) {}
        }
    }
    
    /**
     * Indica si todos los bloques han sido eliminados.
     * @return Un booleano.
     */
    public boolean yaNoHayBloques(){
        return bloques.isEmpty();
    }
    
    /**
     * Revisa si un sprite ha chocado con uno de los bloques y de
     * ser así, disimuye la vida (y elimina, de ser necesario)
     * el bloque que fue colisionado.
     * @param s Un sprite, por defecto, la pelota.
     * @return Si hay colisión y el bloque fue destruido, el poder que almacenaba el bloque.
     * Si hay colisión y el bloque no fue destruido, el tipo de poder "NINGUNO".
     * Si no hubo colisión, null.
     */
    public TipoPoder checarColision(Sprite s){
        for(Bloque aux : bloques){
            if(aux.hayColision(s.area)){
                if(--aux.vida == 0){
                    bloques.remove(aux);
                    return aux.poder;
                }
                return TipoPoder.NINGUNO;
            }
        }
        
        return null;
    }
    
    //PENDIENTE: Decidir si crear una interfaz "Renderizable", implementada por esta clase y por Sprite.
    public void renderizar(Graphics2D lienzo){
        for(Bloque aux : bloques)
            aux.renderizar(lienzo);
    }
    
    private class Bloque{
        Tamano tam;
        TipoPoder poder;
        //Posición
        short posX, posY;
        byte vida;
        
        Bloque(Tamano tam, byte vida, short x, short y){
            this.tam = tam;
            posX = x;
            posY = y;
            
            if(vida < 1)
                vida = 1;
            else if (vida > VIDA_MAXIMA)
                vida = VIDA_MAXIMA;
            this.vida = vida;
            
            //Elegimos un poder para el bloque al azar.
            byte auxPoder = (byte)(Math.random() * 31);
            if(auxPoder == 4 || auxPoder == 5)
                poder = TipoPoder.VELOCIDAD_PALETA_DISMINUIR;
            else if(auxPoder == 6 || auxPoder == 7)
                poder = TipoPoder.VELOCIDAD_PALETA_AUMENTAR;
            else if(auxPoder >= 11 && auxPoder <= 20)
                poder = TipoPoder.PUNTOS;
            else if(auxPoder == 23 || auxPoder == 24)
                poder = TipoPoder.VELOCIDAD_PELOTA_DISMINUIR;
            else if(auxPoder == 25 || auxPoder == 26)
                poder = TipoPoder.VELOCIDAD_PELOTA_AUMENTAR;
            else if(auxPoder == 0 || auxPoder == 30)
                poder = TipoPoder.VIDA;
            else
                poder = TipoPoder.NINGUNO;
        }
        
        boolean hayColision(Shape x){
            switch(tam){
                case GRANDE:
                    return x.intersects(posX, posY, 50, 20);
                case MEDIANO:
                    return x.intersects(posX, posY, 25, 20);
                case CHICO:
                    return x.intersects(posX, posY, 25, 10);
                default:
                    return false;
            }
        }
        
        void renderizar(Graphics2D lienzo){
            //lienzo.drawImage(grafico, (int)areaCircular.x, (int)areaCircular.y, null);
            switch(tam){
                case GRANDE:
                    lienzo.drawImage(graficos_BloquesG[vida - 1], posX, posY, null);
                    break;
                case MEDIANO:
                    lienzo.drawImage(graficos_BloquesM[vida - 1], posX, posY, null);
                    break;
                case CHICO:
                   lienzo.drawImage(graficos_BloquesCh[vida - 1], posX, posY, null);
                   break;
            }
        }
    }
}