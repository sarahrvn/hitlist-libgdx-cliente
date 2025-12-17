package com.punchline.hitlist.red.paquetes;

import com.punchline.hitlist.red.PaqueteRed;

public class PaqueteConexion extends PaqueteRed {
    private static final long serialVersionUID = 1L;
    private int idJugadorAsignado;
    private boolean aceptado;

    public PaqueteConexion(int id, boolean aceptado) {
        this.idJugadorAsignado = id;
        this.aceptado = aceptado;
    }

    @Override
    public TipoPaquete getTipo() { return TipoPaquete.CONEXION; }
    public int getIdJugadorAsignado() { return idJugadorAsignado; }
    public boolean isAceptado() { return aceptado; }
}
