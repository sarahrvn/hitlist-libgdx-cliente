package com.punchline.hitlist.elementosJuego;

public enum SonidoDisponible {
    // Sonidos de personaje
    SALTO("sonidos/salto.wav"),
    CAIDA("sonidos/caida.ogg"),
    MENU("sonidos/menu_mover.mp3"),

    // Música de fondo
    MUSICA_MENU("musica/menu_musica.mp3"),
    MUSICA_COMBATE("musica/combate.mp3");

    private final String RUTA_ARCHIVO;

    SonidoDisponible(String RUTA_ARCHIVO) {
        this.RUTA_ARCHIVO = RUTA_ARCHIVO;
    }

    public String getRutaArchivo() {
        return RUTA_ARCHIVO;
    }
}
