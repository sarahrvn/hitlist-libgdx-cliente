package com.punchline.hitlist.jugadores;

import com.punchline.hitlist.personajes.Personaje;
import com.punchline.hitlist.personajes.TipoPersonaje;

import java.util.HashMap;

public class Jugador {
    private String nombre;

    private HashMap<String, Personaje> compras = new HashMap<>();

    public Jugador(String nombre){
        this.nombre = nombre;
    }

    public void comprar(TipoPersonaje tipoElegido) {
        compras.put(tipoElegido.getNombre(), new Personaje(tipoElegido));
    }

}
