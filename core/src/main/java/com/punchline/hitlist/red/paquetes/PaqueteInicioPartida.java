package com.punchline.hitlist.red.paquetes;

import com.punchline.hitlist.red.PaqueteRed;

public class PaqueteInicioPartida extends PaqueteRed {
    private static final long serialVersionUID = 1L;
    private String tipoP1;
    private String tipoP2;
    private String nombreMapa;

    public PaqueteInicioPartida(String p1, String p2, String mapa) {
        this.tipoP1 = p1;
        this.tipoP2 = p2;
        this.nombreMapa = mapa;
    }

    @Override
    public TipoPaquete getTipo() { return TipoPaquete.INICIO_PARTIDA; }

    public String getTipoP1() { return tipoP1; }
    public String getTipoP2() { return tipoP2; }
    public String getNombreMapa() { return nombreMapa; }
}
