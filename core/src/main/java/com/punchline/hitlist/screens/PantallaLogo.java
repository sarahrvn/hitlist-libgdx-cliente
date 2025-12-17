package com.punchline.hitlist.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PantallaLogo {

    private final Texture LOGO;

    public PantallaLogo() {
        LOGO = new Texture("logos/Punchline_Logo.png");
    }

    public void render(SpriteBatch batch, OrthographicCamera camara) {
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        float x = (camara.viewportWidth - LOGO.getWidth()) / 2f;
        float y = (camara.viewportHeight - LOGO.getHeight()) / 2f;
        batch.draw(LOGO, x, y);
        batch.end();
    }

    public void dispose() {
        LOGO.dispose();
    }
}
