package com.punchline.hitlist.personajes;

public class Estadistica {

    private String nombre;
    private int valor;
    private final int VALOR_MIN = 0;
    private final int VALOR_MAX = 10;

    public Estadistica(String nombre, int valor) {
        this.nombre = nombre;
        setEstadistica(valor);
    }

    public String getNombre() {
        return nombre;
    }

    public int getValor() {
        return valor;
    }

    public void setEstadistica(int valor){
        if(valor < VALOR_MIN) {
            this.valor = VALOR_MIN;
        } else if (valor > VALOR_MAX) {
            this.valor = VALOR_MAX;
        } else {
            this.valor = valor;
        }
    }

    public void ajustarValor(int cantidad){
        setEstadistica(this.valor + cantidad);
    }
}
