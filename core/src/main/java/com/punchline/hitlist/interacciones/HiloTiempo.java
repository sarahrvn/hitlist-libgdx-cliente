package com.punchline.hitlist.interacciones;

import com.punchline.hitlist.screens.PantallaJuego;

public class HiloTiempo extends Thread {

    private PantallaJuego pantallaJuego;
    private boolean corriendo = true;

    public HiloTiempo(PantallaJuego pantallaJuego) {
        this.pantallaJuego = pantallaJuego;
    }

    @Override
    public void run() {
        while (corriendo) {
            try {
                Thread.sleep(1000); // Espera 1 segundo

                if (corriendo) {
                    pantallaJuego.procesarSegundo();
                }

            } catch (InterruptedException e) {
                corriendo = false;
            }
        }
    }

    public void terminar() {
        corriendo = false;
        this.interrupt();
    }
}
