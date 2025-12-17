package com.punchline.hitlist.red.paquetes;

import java.io.Serializable;

public class DatosEspada implements Serializable {
    private static final long serialVersionUID = 1L;

    public float x, y;
    public String tipoArma;
    public boolean activa;

    public DatosEspada(float x, float y, String tipoArma, boolean activa) {
        this.x = x;
        this.y = y;
        this.tipoArma = tipoArma;
        this.activa = activa;
    }
}
