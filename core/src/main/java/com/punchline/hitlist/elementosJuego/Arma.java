package com.punchline.hitlist.elementosJuego;

public enum Arma {
    // Definimos (Fuerza, Velocidad, Defensa)
    PINCEL(3, 2, 0),
    MICROFONO(1, 0, 3),
    GUANTE(4, -3, 0),
    PELOTA(0, 3, 1);

    private int modFuerza, modVelocidad, modDefensa;

    Arma(int f, int v, int d) {
        this.modFuerza = f;
        this.modVelocidad = v;
        this.modDefensa = d;
    }

    public int getModFuerza() { return modFuerza; }
    public int getModVelocidad() { return modVelocidad; }
    public int getModDefensa() { return modDefensa; }
}
