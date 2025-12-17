package com.punchline.hitlist.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.punchline.hitlist.interacciones.TeclaListener;
import com.punchline.hitlist.elementosJuego.GestorSonidos;
import com.punchline.hitlist.elementosJuego.SonidoDisponible;
import com.punchline.hitlist.elementosJuego.SonidoDisponible;

public class PantallaMenu {

    // ... (Variables existentes: FONDO, TITULO, font, etc.) ...
    private final Texture FONDO;
    private final Texture TITULO;
    private BitmapFont font;
    private int opcionSeleccionada = 0;
    private final String[] opciones = {"Local", "Online", "Salir"};
    private final TeclaListener teclaListener;
    private boolean quiereJugar = false, quiereJugarOnline = false, quiereSalir = false;
    private final GlyphLayout layout = new GlyphLayout();
    private float tiempoAnimacion = 0;

    public PantallaMenu() {
        FONDO = new Texture("fondos/Fondo_Menu.png");
        TITULO = new Texture("logos/Hitlist_Titulo.png");

        teclaListener = new TeclaListener();
        Gdx.input.setInputProcessor(teclaListener);

        // --- AUDIO ---
        // 1. Cargar y reproducir música (gracias a la mejora, si ya suena, sigue de largo)
        GestorSonidos.getInstancia().cargarMusica(SonidoDisponible.MUSICA_MENU);
        GestorSonidos.getInstancia().reproducirMusica(SonidoDisponible.MUSICA_MENU, true);

        // 2. Cargar el efecto de sonido del menú
        GestorSonidos.getInstancia().cargarSonido(SonidoDisponible.MENU);

        // --- FUENTE ---
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/ari-w9500.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 40;
            parameter.borderWidth = 2;
            parameter.borderColor = Color.BLACK;
            parameter.shadowOffsetX = 3;
            parameter.shadowOffsetY = 3;
            font = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            font = new BitmapFont();
            font.getData().setScale(3f);
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camara) {
        manejarInput();
        tiempoAnimacion += Gdx.graphics.getDeltaTime();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        batch.draw(FONDO, 0, 0, camara.viewportWidth, camara.viewportHeight);

        // Título
        float tituloAncho = camara.viewportWidth / 5f;
        float tituloAlto = tituloAncho * ((float) TITULO.getHeight() / TITULO.getWidth());
        float tituloX = (camara.viewportWidth - tituloAncho) / 2f;
        float tituloY = camara.viewportHeight - tituloAlto - 50f;
        batch.draw(TITULO, tituloX, tituloY, tituloAncho, tituloAlto);

        // Opciones
        float centroXPantalla = camara.viewportWidth / 2f;
        float yBase = tituloY - 100f;

        for (int i = 0; i < opciones.length; i++) {
            String textoADibujar = opciones[i];
            if (i == opcionSeleccionada) {
                font.setColor(Color.ORANGE);
                textoADibujar = "<< " + textoADibujar + " >>";
                float escalaBase = 1.0f;
                float variacion = (float)Math.sin(tiempoAnimacion * 6) * 0.1f;
                font.getData().setScale(escalaBase + variacion);
            } else {
                font.setColor(Color.WHITE);
                font.getData().setScale(1.0f);
            }
            layout.setText(font, textoADibujar);
            float textoX = centroXPantalla - (layout.width / 2f);
            float textoY = yBase - (i * 100f);
            font.draw(batch, layout, textoX, textoY);
        }
        batch.end();
    }

    private void manejarInput() {
        if (teclaListener.isArribaJustPressed()) {
            opcionSeleccionada--;
            if (opcionSeleccionada < 0) opcionSeleccionada = opciones.length - 1;
            // SONIDO
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);
        }

        if (teclaListener.isAbajoJustPressed()) {
            opcionSeleccionada++;
            if (opcionSeleccionada >= opciones.length) opcionSeleccionada = 0;
            // SONIDO
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);
        }

        if (teclaListener.isEnterJustPressed()) {
            // Opcional: Sonido de confirmar (por ahora uso el mismo)
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);

            if (opcionSeleccionada == 0) {
                quiereJugar = true;
            } else if (opcionSeleccionada == 1 ) {
                quiereJugarOnline = true;
            } else if (opcionSeleccionada == 2) {
                quiereSalir = true;
            }
        }
    }

    public boolean quiereJugar() { return quiereJugar; }
    public boolean quiereJugarOnline() { return quiereJugarOnline; }
    public boolean quiereSalir() { return quiereSalir; }

    public void dispose() {
        FONDO.dispose();
        TITULO.dispose();
        font.dispose();
        // NO detenemos la música aquí para que siga sonando en la selección de personaje
    }
}
