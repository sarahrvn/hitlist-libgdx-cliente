package com.punchline.hitlist.red.paquetes;

import com.punchline.hitlist.red.PaqueteRed;

/**
 * Paquete para sincronizar el tiempo restante de selección entre servidor y clientes
 */
public class PaqueteTiempoSeleccion extends PaqueteRed {
    private static final long serialVersionUID = 1L;

    private float tiempoRestante;
    private String etapa; // "PERSONAJE" o "MAPA"
    private String personajeJ1; // null si aún no eligió
    private String personajeJ2;
    private String mapaJ1;
    private String mapaJ2;

    public PaqueteTiempoSeleccion(float tiempoRestante, String etapa,
                                  String pj1, String pj2, String m1, String m2) {
        this.tiempoRestante = tiempoRestante;
        this.etapa = etapa;
        this.personajeJ1 = pj1;
        this.personajeJ2 = pj2;
        this.mapaJ1 = m1;
        this.mapaJ2 = m2;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.ESTADO_JUEGO; // Reutilizamos
    }

    public float getTiempoRestante() { return tiempoRestante; }
    public String getEtapa() { return etapa; }
    public String getPersonajeJ1() { return personajeJ1; }
    public String getPersonajeJ2() { return personajeJ2; }
    public String getMapaJ1() { return mapaJ1; }
    public String getMapaJ2() { return mapaJ2; }
}
