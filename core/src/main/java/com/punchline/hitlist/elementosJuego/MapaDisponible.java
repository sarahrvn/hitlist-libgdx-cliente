package com.punchline.hitlist.elementosJuego;

public enum MapaDisponible {
    MAPA_YATE("mapas/mapa_yate.tmx", "mapas/mapa_yate.png"),
    MAPA_ALFOMBRA_ROJA("mapas/mapa_alfombra_roja.tmx", "mapas/mapa_alfombra_roja.png"),
    MAPA_CONCIERTO("mapas/mapa_concierto.tmx", "mapas/mapa_concierto.png"),
    MAPA_CARTEL("mapas/mapa_cartel.tmx", "mapas/mapa_cartel.png");

    private final String MAPA_TMX;
    private final String FONDO_PNG;

    MapaDisponible(String MAPA_TMX, String FONDO_PNG) {
        this.MAPA_TMX = MAPA_TMX;
        this.FONDO_PNG = FONDO_PNG;
    }

    public String getmapaTmx() {
        return MAPA_TMX;
    }

    public String getfondoPng() {
        return FONDO_PNG;
    }
}
