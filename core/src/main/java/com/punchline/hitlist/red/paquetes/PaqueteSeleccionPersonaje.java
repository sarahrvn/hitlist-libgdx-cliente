package com.punchline.hitlist.red.paquetes;

import com.punchline.hitlist.red.PaqueteRed;

public class PaqueteSeleccionPersonaje extends PaqueteRed {
    private static final long serialVersionUID = 1L;

    private int idJugador;
    private String personajeElegido; // Nombre del TipoPersonaje

    public PaqueteSeleccionPersonaje(int idJugador, String personajeElegido) {
        this.idJugador = idJugador;
        this.personajeElegido = personajeElegido;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.INICIO_PARTIDA; // Reutilizamos este tipo
    }

    public int getIdJugador() { return idJugador; }
    public String getPersonajeElegido() { return personajeElegido; }
}
