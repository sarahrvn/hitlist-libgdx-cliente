package com.punchline.hitlist.red.paquetes;

import com.punchline.hitlist.red.PaqueteRed;

public class PaqueteInput extends PaqueteRed {
    private static final long serialVersionUID = 1L;

    private int idJugador;
    private boolean arriba, izquierda, derecha;
    private boolean atacar, agarrar;

    public PaqueteInput(int idJugador, boolean arriba, boolean izquierda, boolean derecha,
                        boolean atacar, boolean agarrar) {
        this.idJugador = idJugador;
        this.arriba = arriba;
        this.izquierda = izquierda;
        this.derecha = derecha;
        this.atacar = atacar;
        this.agarrar = agarrar;
    }

    @Override
    public TipoPaquete getTipo() { return TipoPaquete.INPUT_JUGADOR; }

    public int getIdJugador() { return idJugador; }
    public boolean isSaltar() { return arriba; }
    public boolean isIzquierda() { return izquierda; }
    public boolean isDerecha() { return derecha; }
    public boolean isAtacar() { return atacar; }
    public boolean isAgarrar() { return agarrar; }
}
