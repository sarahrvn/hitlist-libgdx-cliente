package com.punchline.hitlist.elementosJuego;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.punchline.hitlist.personajes.Personaje;

public class Hud {

    private OrthographicCamera camaraHud;
    private Viewport viewport;
    private BitmapFont font;
    private Texture iconoVida;

    private int vidasP1 = 3;
    private int vidasP2 = 3;
    private boolean pausado = false;
    private float tiempoRestante = 0;

    public Hud() {
        camaraHud = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camaraHud);
        viewport.apply();

        camaraHud.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camaraHud.update();

        iconoVida = new Texture("hud/corazon_amarilloo.png");
        font = new BitmapFont();
    }

    public void render(SpriteBatch batch, Array<Personaje> personajes) {
        batch.setProjectionMatrix(camaraHud.combined);
        batch.begin();

        float anchoPantalla = viewport.getWorldWidth();
        float altoPantalla = viewport.getWorldHeight();

        // Dibujar vidas
        for (int i = 0; i < personajes.size; i++) {
            Personaje personaje = personajes.get(i);

            // Dibuja según el índice (0 es izq, 1 es der)
            float startX = (i == 0) ? 10 : anchoPantalla - 40;
            int direccion = (i == 0) ? 1 : -1; // P1 dibuja hacia derecha, P2 hacia izquierda

            font.draw(batch, "P" + (i + 1), startX, altoPantalla - 15);

            for (int v = 0; v < personaje.getVidas(); v++) {
                // Cálculo matemático para poner los corazones en fila
                float x = startX + (v * 35 * direccion);
                // Ajuste para P2 para que no se superponga con el texto
                if(i == 1) x -= 30;

                batch.draw(iconoVida, x, altoPantalla - 50, 30, 30);
            }
        }

        // Dibujar tiempo
        font.draw(batch, "Tiempo: " + (int) tiempoRestante, anchoPantalla - 130, altoPantalla - 20);

        // Mostrar pausa
        if (pausado) {
            font.draw(batch, "PAUSADO", anchoPantalla / 2 - 40, altoPantalla / 2);
        }

        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        font.dispose();
        iconoVida.dispose();
    }

    public void setTiempoRestante(float tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }
    public void mostrarPausa(boolean pausado) {
        this.pausado = pausado;
    }

}
