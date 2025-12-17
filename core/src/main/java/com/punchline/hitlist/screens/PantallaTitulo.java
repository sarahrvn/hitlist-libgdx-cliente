package com.punchline.hitlist.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class PantallaTitulo {

    private final Texture TITULO;
    private final BitmapFont font;
    private final GlyphLayout layout;

    private boolean PaseAMenu = false;

    public PantallaTitulo() {
        TITULO = new Texture("logos/Hitlist_Titulo.png");
        font = new BitmapFont();
        layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch, OrthographicCamera camara) {
        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        float x = (camara.viewportWidth - TITULO.getWidth()) / 2f;
        float y = (camara.viewportHeight - TITULO.getHeight()) / 2f;
        batch.draw(TITULO, x, y);

        String mensaje = "Presiona ENTER o ESPACIO para comenzar";
        layout.setText(font, mensaje);
        float textoX = (camara.viewportWidth - layout.width) / 2f;
        float textoY = y - 40;
        font.draw(batch, layout, textoX, textoY);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            PaseAMenu = true;
        }
    }

    public boolean SaltarAMenu() {
        return PaseAMenu;
    }

    public void dispose() {
        TITULO.dispose();
        font.dispose();
    }
}
