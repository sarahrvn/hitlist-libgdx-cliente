package com.punchline.hitlist.red.paquetes;

import java.io.Serializable;

public class DatosPersonaje implements Serializable {
    private static final long serialVersionUID = 1L;

    public float x, y;
    public boolean mirandoDerecha;
    public String estadoAnimacion;
    public boolean tieneArma;
    public String nombreArma;
    public int vidas;

    public DatosPersonaje() {}

    public DatosPersonaje(float x, float y, boolean mirandoDerecha, String estadoAnimacion,
                          boolean tieneArma, String nombreArma, int vidas) {
        this.x = x;
        this.y = y;
        this.mirandoDerecha = mirandoDerecha;
        this.estadoAnimacion = estadoAnimacion;
        this.tieneArma = tieneArma;
        this.nombreArma = nombreArma;
        this.vidas = vidas;
    }
}
