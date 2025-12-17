package com.punchline.hitlist.red.paquetes;

import com.punchline.hitlist.red.PaqueteRed;
import java.util.ArrayList;

public class PaqueteEstado extends PaqueteRed {
    private static final long serialVersionUID = 1L;

    private DatosPersonaje jugador1;
    private DatosPersonaje jugador2;
    private ArrayList<DatosEspada> espadas;
    private float tiempoRestante;
    private boolean juegoTerminado;
    private int idGanador;

    public PaqueteEstado(DatosPersonaje j1, DatosPersonaje j2, ArrayList<DatosEspada> espadas,
                         float tiempoRestante, boolean juegoTerminado, int idGanador) {
        this.jugador1 = j1;
        this.jugador2 = j2;
        this.espadas = espadas;
        this.tiempoRestante = tiempoRestante;
        this.juegoTerminado = juegoTerminado;
        this.idGanador = idGanador;
    }

    @Override
    public TipoPaquete getTipo() { return TipoPaquete.ESTADO_JUEGO; }

    public DatosPersonaje getJugador1() { return jugador1; }
    public DatosPersonaje getJugador2() { return jugador2; }
    public ArrayList<DatosEspada> getEspadas() { return espadas; }
    public float getTiempoRestante() { return tiempoRestante; }
    public boolean isJuegoTerminado() { return juegoTerminado; }
    public int getIdGanador() { return idGanador; }
}
