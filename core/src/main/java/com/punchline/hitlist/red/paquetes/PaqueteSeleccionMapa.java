package com.punchline.hitlist.red.paquetes;

import com.punchline.hitlist.red.PaqueteRed;

public class PaqueteSeleccionMapa extends PaqueteRed {
    private static final long serialVersionUID = 1L;

    private int idJugador;
    private String mapaElegido; // Nombre del MapaDisponible

    public PaqueteSeleccionMapa(int idJugador, String mapaElegido) {
        this.idJugador = idJugador;
        this.mapaElegido = mapaElegido;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.INICIO_PARTIDA;
    }

    public int getIdJugador() { return idJugador; }
    public String getMapaElegido() { return mapaElegido; }
}
