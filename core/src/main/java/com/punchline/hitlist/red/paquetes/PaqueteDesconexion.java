package com.punchline.hitlist.red.paquetes;

import com.punchline.hitlist.red.PaqueteRed;

public class PaqueteDesconexion extends PaqueteRed {
    private static final long serialVersionUID = 1L;

    private int idJugador;
    private String razon; // "VOLUNTARIA", "TIMEOUT", "ERROR", "SERVIDOR_CERRADO"

    public PaqueteDesconexion(int idJugador, String razon) {
        this.idJugador = idJugador;
        this.razon = razon;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.DESCONEXION;
    }

    public int getIdJugador() { return idJugador; }
    public String getRazon() { return razon; }
}
