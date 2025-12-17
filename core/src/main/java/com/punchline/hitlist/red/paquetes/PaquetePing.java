package com.punchline.hitlist.red.paquetes;

import com.punchline.hitlist.red.PaqueteRed;

public class PaquetePing extends PaqueteRed {
    private static final long serialVersionUID = 1L;

    private int idJugador;
    private long timestamp;

    public PaquetePing(int idJugador) {
        this.idJugador = idJugador;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.CONEXION; // Reutilizamos tipo CONEXION
    }

    public int getIdJugador() { return idJugador; }
    public long getTimestamp() { return timestamp; }
}
