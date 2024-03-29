package proyectojuego;

import java.awt.Font;

/**
 * Contenedor de la mayoría de las constantes de todo el programa.
 * @author Javier
 */
public final class Configuracion {
    /**
     * Dimensiones del campo de juego.
     */
    public static final short ESCENARIO_ANCHO = 320, ESCENARIO_ALTO = 240;
    
    /**
     * Altura de la barrita de puntuaciones.
     */
    public static final short BARRA_PUNTUACIONES_ALTO = 15;
    
    /**
     * Posibles valores de "movimientoHor" y "movimientoVer".
     * @see Sprite.movimientoHor
     */
    public static final Boolean MOV_NULO = null, MOV_IZQUIERDA = false, MOV_DERECHA = true, MOV_ABAJO = false, MOV_ARRIBA = true;
    
    /**
     * Posibles valores de Pelota.propietario
     * @see Pelota.propietario
     */
    public static final boolean JUGADOR_1 = false, JUGADOR_2 = true;
    
    /**
     * Las imagenes de este juego se manejan de la siguiente manera: 
     * estas poseen píxeles con los colores RGB (1, 0, 0), (0, 1, 0) y (0, 0, 1), 
     * los cuales, a simple vista, son todos negros.
     * Cuando el programa las lee, cambia estos colores especiales por otros.
     * Usando este mecanismo, no es necesario guardar varias imagenes de un
     * mismo objeto con colores distintos.
     * Estas constantes son los valores enteros de los colores especiales.
     */
    public static final int PALETA_DE_COLORES_1 = -16711680, PALETA_DE_COLORES_2 = -16776960, PALETA_DE_COLORES_3 = -16777215;
    
    /**
     * Fotogramas (cuántas veces se va a pintar la pantalla) por segundo.
     */
    public static final short FPS = 30;
    
    /**
     * Milisegundos que tarda un fotogram eqn pantalla.
     */
    public static final short MS_POR_FPS = 1000 / FPS;
    
    /**
     * Las ubicaciones de todos los recursos.
     */
    public static final String
            IMG_PELOTA = "graficos/pelota.png",
            IMG_BLOQUE_GRANDE = "graficos/bloqueG.png",
            IMG_BLOQUE_MEDIANO = "graficos/bloqueM.png",
            IMG_BLOQUE_CHICO = "graficos/bloqueCh.png",
            IMG_PODER = "graficos/poder.png",
            SFX_OBTENCION_DE_BONUS = "audio/sfx_bonus.mp3",
            SFX_GOLPEAR_UN_BLOQUE = "audio/sfx_golpe.mp3",
            SFX_PERDER_UNA_VIDA = "audio/sfx_lose.mp3",
            SFX_REBOTE_DE_PELOTA = "audio/sfx_rebote.mp3",
            DISENO_NIVEL_1 = "otros/nivel1.txt",
            DISENO_NIVEL_2 = "otros/nivel2.txt",
            DISENO_NIVEL_3 = "otros/nivel3.txt",
            DISENO_NIVEL_4 = "otros/nivel4.txt",
            MUSICA_NIVEL_1 = "audio/16x.xm",
            MUSICA_NIVEL_2 = "audio/manic miner samba.xm",
            MUSICA_NIVEL_3 = "audio/space-patrol.xm",
            MUSICA_NIVEL_4 = "audio/serkku kiljuhanhi.xm",
            MUSICA_NIVEL_RANDOM = "audio/silver.xm",
            MUSICA_MENU = "audio/story.xm",
            FUENTE_DE_LETRA = "otros/wcp.ttf",
            FUENTE_DE_LETRA_NOMBRE = "Windows Command Prompt";
    public static final String[] IMG_RELLENOS = {"graficos/paleta_relleno1.png", "graficos/paleta_relleno2.png", "graficos/paleta_relleno3.png", "graficos/paleta_relleno4.png"};
    
    public static final Font fuente = new Font(FUENTE_DE_LETRA_NOMBRE, Font.PLAIN, (int)(BARRA_PUNTUACIONES_ALTO * 1.3));
}