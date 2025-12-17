package com.punchline.hitlist.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.punchline.hitlist.interacciones.TeclaListener;
import com.punchline.hitlist.elementosJuego.MapaDisponible;
import com.punchline.hitlist.elementosJuego.GestorSonidos;
import com.punchline.hitlist.elementosJuego.SonidoDisponible;

public class PantallaSeleccionMapa {

    // ... (Variables existentes) ...
    private final Texture FONDO;
    private BitmapFont fontNombre;
    private final Texture texturaCuadrado;
    private final Texture previewYate;
    private final Texture previewAlfombraRoja;
    private final Texture previewConcierto;
    private final Texture previewCartel;
    private int indiceSeleccionado = 0;
    private final TeclaListener teclaListener;
    private MapaDisponible mapaFinal = null;
    private final GlyphLayout layout = new GlyphLayout();
    private float tiempoAnimacion = 0;

    public PantallaSeleccionMapa() {
        FONDO = new Texture("fondos/Fondo_Seleccion_Personaje.png");
        teclaListener = new TeclaListener();
        Gdx.input.setInputProcessor(teclaListener);

        // --- AUDIO ---
        GestorSonidos.getInstancia().cargarMusica(SonidoDisponible.MUSICA_MENU);
        GestorSonidos.getInstancia().reproducirMusica(SonidoDisponible.MUSICA_MENU, true);
        GestorSonidos.getInstancia().cargarSonido(SonidoDisponible.MENU);

        // --- FUENTES ---
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/ari-w9500.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
            params.size = 36;
            params.borderWidth = 2;
            params.borderColor = Color.BLACK;
            params.shadowOffsetX = 3;
            params.shadowOffsetY = 3;
            fontNombre = generator.generateFont(params);
            generator.dispose();
        } catch (Exception e) {
            fontNombre = new BitmapFont();
            fontNombre.getData().setScale(2f);
        }

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        texturaCuadrado = new Texture(pix);
        pix.dispose();

        previewYate = new Texture("mapas/mapa_yate.png");
        previewAlfombraRoja = new Texture("mapas/mapa_alfombra_roja.png");
        previewConcierto = new Texture("mapas/mapa_concierto.png");
        previewCartel = new Texture("mapas/mapa_cartel.png");
    }

    public void render(SpriteBatch batch, OrthographicCamera camara) {
        manejarInput();
        tiempoAnimacion += Gdx.graphics.getDeltaTime();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        batch.draw(FONDO, 0, 0, camara.viewportWidth, camara.viewportHeight);

        float mitadAncho = camara.viewportWidth / 2f;
        float mitadAlto = camara.viewportHeight / 2f;
        float margen = 15f;
        float anchoCard = mitadAncho - (margen * 2);
        float altoCard = mitadAlto - (margen * 2);
        float xIzq = margen;
        float xDer = mitadAncho + margen;
        float yAbajo = margen;
        float yArriba = mitadAlto + margen;

        dibujarTarjeta(batch, previewYate, "YATE", xIzq, yArriba, anchoCard, altoCard, Color.GOLD, (indiceSeleccionado == 0), true);
        dibujarTarjeta(batch, previewAlfombraRoja, "ALFOMBRA ROJA", xDer, yArriba, anchoCard, altoCard, Color.GOLD, (indiceSeleccionado == 1), false);
        dibujarTarjeta(batch, previewConcierto, "CONCIERTO", xIzq, yAbajo, anchoCard, altoCard, Color.GOLD, (indiceSeleccionado == 2), true);
        dibujarTarjeta(batch, previewCartel, "CARTEL HOLLYWOOD", xDer, yAbajo, anchoCard, altoCard, Color.GOLD, (indiceSeleccionado == 3), false);

        batch.end();
    }

    private void manejarInput() {
        if (mapaFinal != null) return;

        boolean seMovio = false;

        if (teclaListener.isDerechaJustPressed()) {
            if (indiceSeleccionado % 2 == 0) { indiceSeleccionado++; seMovio = true; }
        }
        if (teclaListener.isIzquierdaJustPressed()) {
            if (indiceSeleccionado % 2 != 0) { indiceSeleccionado--; seMovio = true; }
        }
        if (teclaListener.isAbajoJustPressed()) {
            if (indiceSeleccionado < 2) { indiceSeleccionado += 2; seMovio = true; }
        }
        if (teclaListener.isArribaJustPressed()) {
            if (indiceSeleccionado >= 2) { indiceSeleccionado -= 2; seMovio = true; }
        }

        if (seMovio) {
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);
        }

        if (teclaListener.isEnterJustPressed()) {
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);
            switch (indiceSeleccionado) {
                case 0: mapaFinal = MapaDisponible.MAPA_YATE; break;
                case 1: mapaFinal = MapaDisponible.MAPA_ALFOMBRA_ROJA; break;
                case 2: mapaFinal = MapaDisponible.MAPA_CONCIERTO; break;
                case 3: mapaFinal = MapaDisponible.MAPA_CARTEL; break;
            }
        }
    }

    // ... (El resto del código dibujarTarjeta y dispose se mantiene igual) ...
    private void dibujarTarjeta(SpriteBatch batch, Texture imagen, String nombreMapa, float x, float y, float w, float h, Color colorBorde, boolean seleccionado, boolean alinearTextoDerecha) {
        float grosor = 6f;
        batch.setColor(seleccionado ? Color.WHITE : colorBorde);
        batch.draw(texturaCuadrado, x, y + h - grosor, w, grosor);
        batch.draw(texturaCuadrado, x, y, w, grosor);
        batch.draw(texturaCuadrado, x, y, grosor, h);
        batch.draw(texturaCuadrado, x + w - grosor, y, grosor, h);
        batch.setColor(Color.WHITE);
        batch.draw(imagen, x + grosor, y + grosor, w - (grosor * 2), h - (grosor * 2));

        if (seleccionado) {
            fontNombre.setColor(Color.ORANGE);
            float pulsacion = (float)Math.sin(tiempoAnimacion * 6) * 0.1f;
            fontNombre.getData().setScale(1.0f + pulsacion);
        } else {
            fontNombre.setColor(Color.WHITE);
            fontNombre.getData().setScale(1.0f);
        }

        layout.setText(fontNombre, nombreMapa);
        float padding = 20f;
        float textoX = alinearTextoDerecha ? (x + w) - layout.width - padding : x + padding;
        float textoY = y + h - padding;
        fontNombre.draw(batch, layout, textoX, textoY);
    }

    public boolean hayMapaSeleccionado() { return mapaFinal != null; }
    public MapaDisponible getMapaSeleccionado() { return mapaFinal; }
    public void dispose() {
        FONDO.dispose();
        fontNombre.dispose();
        previewYate.dispose();
        previewAlfombraRoja.dispose();
        previewConcierto.dispose();
        previewCartel.dispose();
        texturaCuadrado.dispose();
    }
}
