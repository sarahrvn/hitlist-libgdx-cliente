package com.punchline.hitlist.personajes;

public enum Categoria {
    CANTANTE("Cantante"), ACTOR("Actor"), DEPORTISTA("Deportista"), YOUTUBER("Youtuber");

    private String nombre;

    Categoria(String nombre) {
        this.nombre = nombre;
    }
}
