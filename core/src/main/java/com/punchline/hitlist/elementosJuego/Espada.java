package com.punchline.hitlist.elementosJuego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Espada {
    private Sprite sprite;
    private Rectangle boundingBox;
    private float velocidadY = 0;
    private final float GRAVEDAD = -1000;
    private boolean activa = true;
    private Arma tipo;
    private static final Texture TEXTURA = new Texture("elementos/espada.png");

    public Espada(float x, float y, Arma tipo) {
        this.tipo = tipo;

        this.sprite = new Sprite(TEXTURA);
        this.sprite.setPosition(x, y);
        this.boundingBox = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
    }

    public void update(float delta, Array<Rectangle> colisiones) {
        if (!activa) return;
        velocidadY += GRAVEDAD * delta;
        boundingBox.y += velocidadY * delta;
        for (Rectangle colision : colisiones) {
            if (boundingBox.overlaps(colision)) {
                boundingBox.y = colision.y + colision.height;
                velocidadY = 0;
                break;
            }
        }
        sprite.setPosition(boundingBox.x, boundingBox.y);
    }

    public void render(SpriteBatch batch) {
        if (activa) sprite.draw(batch);
    }

    public Rectangle getArea() { return boundingBox; }
    public boolean isActiva() { return activa; }
    public Arma getTipo() { return tipo; }
    public void destruir() { this.activa = false; }

    public void dispose() {
    }
}
