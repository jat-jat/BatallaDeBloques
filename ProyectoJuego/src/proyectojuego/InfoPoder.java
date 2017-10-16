package proyectojuego;

import proyectojuego.AdminPoderes.TipoPoder;

/**
 * Almacén de los datos de un poder obtenido por un jugador:
 * qué tipo de poder es y quién lo obtuvo.
 * @author Javier Alberto Argüello Tello
 */
public class InfoPoder {
    private final TipoPoder tipo;
    private final boolean jugador;

    public InfoPoder(TipoPoder tipo, boolean jugador) {
        this.tipo = tipo;
        this.jugador = jugador;
    }

    public TipoPoder getTipo() {
        return tipo;
    }

    public boolean getJugador() {
        return jugador;
    }
}