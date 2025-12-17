package com.punchline.hitlist.personajes;

import com.punchline.hitlist.elementosJuego.Arma;

public enum TipoPersonaje {
    BILLIE_EILISH("Billie Eilish", Arma.MICROFONO, 3, 4, 2, 7, "sprites/Billie_Eilish.txt"),
    MICHAEL_JACKSON("Michael Jackson", Arma.GUANTE, 5, 7, 3, 4, "sprites/Michael_Jackson.txt"),
    FRIDA_KAHLO("Frida Kahlo", Arma.PINCEL, 3, 5, 7, 6, "sprites/Frida_Kahlo.txt"),
    LEBRON_JAMES("Lebron James", Arma.PELOTA, 8, 4, 2, 2, "sprites/Lebron_James.txt");

    private final String NOMBRE;
    private final Arma ARMA_ASIGNADA; // El arma exclusiva del personaje
    private final int FUERZA, DESTREZA, DEFENSA, VELOCIDAD;
    private final String RUTASPRITE;

    TipoPersonaje(String nombre, Arma arma, int fuerza, int destreza, int defensa, int velocidad, String rutaSprite) {
        this.NOMBRE = nombre;
        this.ARMA_ASIGNADA = arma;
        this.FUERZA = fuerza;
        this.DESTREZA = destreza;
        this.DEFENSA = defensa;
        this.VELOCIDAD = velocidad;
        this.RUTASPRITE = rutaSprite;
    }

    public Arma getArmaAsignada() { return ARMA_ASIGNADA; }
    public String getNombre() { return NOMBRE; }
    public int getFuerza() { return FUERZA; }
    public int getDestreza() { return DESTREZA; }
    public int getDefensa() { return DEFENSA; }
    public int getVelocidad() { return VELOCIDAD; }
    public String getRutaSprite() { return RUTASPRITE; }
}
