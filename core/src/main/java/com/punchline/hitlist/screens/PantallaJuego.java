package com.punchline.hitlist.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.punchline.hitlist.elementosJuego.*;
import com.punchline.hitlist.interacciones.TeclaListener;
import com.punchline.hitlist.interacciones.HiloTiempo;
import com.punchline.hitlist.personajes.Personaje;
import com.punchline.hitlist.personajes.TipoPersonaje;

import java.util.Random;

public class PantallaJuego {
    private Mapa mapa;
    private Array<Personaje> personajes;
    private final Hud HUD;
    private final OrthographicCamera camaraJuego;
    private final Viewport viewportJuego;

    // Estados
    private boolean enPausa = false;
    private boolean volverAlMenu = false;
    int indiceGanador = -1;

    // --- Variables para el Menú de Pausa ---
    private BitmapFont fontPausa;
    private Texture texturaOverlay;
    private GlyphLayout layoutPausa;
    private String[] opcionesPausa = {"Reanudar Juego", "Volver al Menu"};
    private int opcionPausaSeleccionada = 0;
    private float tiempoAnimacionPausa = 0;
    // ---------------------------------------

    // Variables de tiempo con hilo
    private int segundosRestantes = 60;
    private boolean tiempoCumplido = false;
    private HiloTiempo hiloTiempo;

    private TeclaListener teclaListener;
    private SpriteBatch batch;

    private final Vector2 POSICION_SPAWN;
    private Array<Espada> espadasEnJuego;
    private Random random;

    private int contadorSegundosArma = 0;
    private boolean debeSpawnearEspada = false;

    public PantallaJuego(MapaDisponible mapaSeleccionado, TipoPersonaje p1Seleccionado, TipoPersonaje p2Seleccionado) {
        mapa = new Mapa(mapaSeleccionado);
        HUD = new Hud();

        camaraJuego = new OrthographicCamera();
        viewportJuego = new StretchViewport(1024, 576, camaraJuego);
        viewportJuego.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // Centrar cámara en el mapa
        camaraJuego.position.set(mapa.getAncho() / 2f, mapa.getAlto() / 2f, 0);
        camaraJuego.update();

        float spawnX = mapa.getAncho() / 2f;
        float spawnY = mapa.getAlto() * 0.8f;
        POSICION_SPAWN = new Vector2(spawnX, spawnY);

        personajes = new Array<>();

        // Jugador 1
        Personaje p1 = new Personaje(p1Seleccionado);
        p1.setPosition(POSICION_SPAWN.x - 30f, POSICION_SPAWN.y);
        personajes.add(p1);

        // Jugador 2
        Personaje p2 = new Personaje(p2Seleccionado);
        p2.setPosition(POSICION_SPAWN.x + 30f, POSICION_SPAWN.y);
        p2.caminarIzquierda();
        personajes.add(p2);

        batch = new SpriteBatch();
        teclaListener = new TeclaListener();
        espadasEnJuego = new Array<>();
        random = new Random();

        cargarSonidos();
        reproducirMusicaMapa();

        hiloTiempo = new HiloTiempo(this);
        hiloTiempo.start();

        inicializarMenuPausa();
    }

    private void inicializarMenuPausa() {
        layoutPausa = new GlyphLayout();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        texturaOverlay = new Texture(pixmap);
        pixmap.dispose();

        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/ari-w9500.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 40;
            parameter.borderWidth = 2;
            parameter.borderColor = Color.BLACK;
            parameter.shadowOffsetX = 3;
            parameter.shadowOffsetY = 3;
            fontPausa = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            fontPausa = new BitmapFont();
            fontPausa.getData().setScale(3f);
        }
    }

    private void cargarSonidos() {
        GestorSonidos.getInstancia().cargarSonido(SonidoDisponible.SALTO);
        GestorSonidos.getInstancia().cargarSonido(SonidoDisponible.CAIDA);
        GestorSonidos.getInstancia().cargarSonido(SonidoDisponible.MENU);
    }

    private void reproducirMusicaMapa() {
        GestorSonidos.getInstancia().cargarMusica(SonidoDisponible.MUSICA_COMBATE);
        GestorSonidos.getInstancia().reproducirMusica(SonidoDisponible.MUSICA_COMBATE, true);
    }

    public void procesarSegundo() {
        if (!enPausa && !tiempoCumplido) {
            segundosRestantes--;
            contadorSegundosArma++;
            if (contadorSegundosArma >= 8) {
                debeSpawnearEspada = true;
                contadorSegundosArma = 0;
            }
            if (segundosRestantes <= 0) {
                segundosRestantes = 0;
                tiempoCumplido = true;
                Personaje p1 = personajes.get(0);
                Personaje p2 = personajes.get(1);

                if(p1.getVidas() > p2.getVidas()) {
                    indiceGanador = 0;
                } else if (p1.getVidas() < p2.getVidas()) {
                    indiceGanador = 1;
                } else {
                    indiceGanador = -1;
                }
                volverAlMenu = true;
                hiloTiempo.terminar();
            }
        }
    }

    private void spawnearEspadaReal() {
        float w = mapa.getAncho();
        float[] posX = { w * 0.2f, w * 0.5f, w * 0.8f };
        float x = posX[random.nextInt(3)];
        float y = POSICION_SPAWN.y;
        Espada nueva = new Espada(x, y, null);
        espadasEnJuego.add(nueva);
    }

    public void update(float delta) {
        Gdx.input.setInputProcessor(teclaListener);

        if (teclaListener.isEscapeJustPressed()) {
            enPausa = !enPausa;
            if (enPausa) {
                GestorSonidos.getInstancia().pausarMusica();
                opcionPausaSeleccionada = 0;
            } else {
                GestorSonidos.getInstancia().reanudarMusica();
            }
        }

        HUD.mostrarPausa(enPausa);

        if (enPausa) {
            tiempoAnimacionPausa += delta;
            manejarInputPausa();
        } else {
            manejarInputJuego();
            for (int i = 0; i < personajes.size; i++) {
                personajes.get(i).update(delta, mapa.getColisiones());
            }
            verificarColisionVacio();
            verificarCombate();

            if (debeSpawnearEspada) {
                spawnearEspadaReal();
                debeSpawnearEspada = false;
            }

            for (int i = espadasEnJuego.size - 1; i >= 0; i--) {
                Espada espada = espadasEnJuego.get(i);
                espada.update(delta, mapa.getColisiones());
                if (!espada.isActiva()) {
                    espada.dispose();
                    espadasEnJuego.removeIndex(i);
                }
            }
            HUD.setTiempoRestante(segundosRestantes);
        }
    }

    private void manejarInputPausa() {
        if (teclaListener.isArribaJustPressed()) {
            opcionPausaSeleccionada--;
            if (opcionPausaSeleccionada < 0) opcionPausaSeleccionada = opcionesPausa.length - 1;
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);
        }
        if (teclaListener.isAbajoJustPressed()) {
            opcionPausaSeleccionada++;
            if (opcionPausaSeleccionada >= opcionesPausa.length) opcionPausaSeleccionada = 0;
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);
        }
        if (teclaListener.isEnterJustPressed()) {
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);
            ejecutarAccionPausa();
        }
    }

    private void ejecutarAccionPausa() {
        switch (opcionPausaSeleccionada) {
            case 0: // Reanudar
                enPausa = false;
                GestorSonidos.getInstancia().reanudarMusica();
                break;
            case 1: // Volver al Menu
                volverAlMenu = true;
                break;
        }
    }

    private void manejarInputJuego() {
        Personaje p1 = personajes.get(0);
        Personaje p2 = personajes.get(1);

        if (teclaListener.isP1ArribaJustPressed()) { p1.saltar(); }
        if (teclaListener.isP1Izquierda()) { p1.caminarIzquierda(); }
        if (teclaListener.isP1Derecha()) { p1.caminarDerecha(); }
        if (teclaListener.isP1AgarrarJustPressed()) { verificarAgarre(p1); }
        if (teclaListener.isP1AtacarJustPressed()) { p1.atacar(); }

        if (teclaListener.isP2ArribaJustPressed()) { p2.saltar(); }
        if (teclaListener.isP2Izquierda()) { p2.caminarIzquierda(); }
        if (teclaListener.isP2Derecha()) { p2.caminarDerecha(); }
        if (teclaListener.isP2AgarrarJustPressed()) { verificarAgarre(p2); }
        if (teclaListener.isP2AtacarJustPressed()) { p2.atacar(); }
    }

    private void verificarAgarre(Personaje personaje) {
        Rectangle hitboxPJ = personaje.getHitbox();
        for (int i = 0; i < espadasEnJuego.size; i++) {
            Espada espada = espadasEnJuego.get(i);
            if(hitboxPJ.overlaps(espada.getArea()) && !personaje.isArmaEquipada()) {
                personaje.equiparArma();
                espada.destruir();
            }
        }
    }

    private void verificarCombate() {
        for (int i = 0; i < personajes.size; i++) {
            Personaje atacante = personajes.get(i);
            if (atacante.isAtacando()) {
                for (int j = 0; j < personajes.size; j++) {
                    Personaje victima = personajes.get(j);
                    if (atacante == victima) continue;
                    if (atacante.getHitboxAtaque().overlaps(victima.getHitbox())) {
                        int direccion = (atacante.getHitbox().x < victima.getHitbox().x) ? 1 : -1;
                        victima.recibirGolpe(atacante.getFuerza().getValor(), direccion);
                    }
                }
            }
        }
    }

    private void verificarColisionVacio() {
        for (int i = 0; i < personajes.size; i++) {
            Personaje personaje = personajes.get(i);
            for (Rectangle vacio : mapa.getColisionesVacio()) {
                if (personaje.getHitbox().overlaps(vacio)) {
                    personaje.sacarVida();
                    GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.CAIDA);
                    if (personaje.estaMuerto()) {
                        terminarPartida(personaje);
                    } else {
                        respawnearPersonaje(personaje);
                    }
                }
            }
        }
    }

    private void respawnearPersonaje(Personaje personaje) {
        if (personaje == personajes.get(0)) {
            personaje.setPosition(POSICION_SPAWN.x - 10f, POSICION_SPAWN.y);
        } else if (personaje == personajes.get(1)) {
            personaje.setPosition(POSICION_SPAWN.x + 10f, POSICION_SPAWN.y);
        }
        personaje.resetear();
    }

    private void terminarPartida(Personaje personajePerdedor) {
        if (hiloTiempo != null) { hiloTiempo.terminar(); }
        if (personajePerdedor == this.personajes.get(0)) {
            indiceGanador = 1;
        } else if (personajePerdedor == this.personajes.get(1)) {
            indiceGanador = 0;
        }
        volverAlMenu = true;
    }

    public int getIndiceGanador() { return this.indiceGanador; }

    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.begin();
        batch.setProjectionMatrix(camaraJuego.combined);
        mapa.renderFondo(batch, camaraJuego, viewportJuego);
        batch.end();

        mapa.renderMapa(camaraJuego);

        batch.setProjectionMatrix(camaraJuego.combined);
        batch.begin();
        for (int i = 0; i < espadasEnJuego.size; i++) {
            espadasEnJuego.get(i).render(batch);
        }
        for (int i = 0; i < personajes.size; i++) {
            personajes.get(i).dibujar(batch);
        }
        batch.end();

        HUD.render(batch, personajes);

        if (enPausa) {
            batch.setProjectionMatrix(camaraJuego.combined);
            batch.begin();

            // Calcular dimensiones de la vista de la cámara
            float camX = camaraJuego.position.x;
            float camY = camaraJuego.position.y;
            float viewW = camaraJuego.viewportWidth;
            float viewH = camaraJuego.viewportHeight;

            // Dibujar fondo oscuro centrado en la cámara
            batch.draw(texturaOverlay, camX - viewW/2, camY - viewH/2, viewW, viewH);

            // Título "PAUSA"
            String tituloPausa = "PAUSA";
            fontPausa.getData().setScale(1.5f);
            layoutPausa.setText(fontPausa, tituloPausa);
            fontPausa.setColor(Color.WHITE);
            float tituloX = camX - (layoutPausa.width / 2);
            float tituloY = camY + (viewH / 3);
            fontPausa.draw(batch, layoutPausa, tituloX, tituloY);

            // Dibujar opciones del menú
            float separacion = 100f;
            float yBase = camY;

            for (int i = 0; i < opcionesPausa.length; i++) {
                String textoADibujar = opcionesPausa[i];

                if (i == opcionPausaSeleccionada) {
                    fontPausa.setColor(Color.ORANGE);
                    textoADibujar = "<< " + textoADibujar + " >>";

                    // Animación de escala pulsante
                    float escalaBase = 1.0f;
                    float variacion = (float)Math.sin(tiempoAnimacionPausa * 6) * 0.1f;
                    fontPausa.getData().setScale(escalaBase + variacion);
                } else {
                    fontPausa.setColor(Color.WHITE);
                    fontPausa.getData().setScale(1.0f);
                }

                layoutPausa.setText(fontPausa, textoADibujar);
                float textoX = camX - (layoutPausa.width / 2);
                float textoY = yBase - (i * separacion);

                fontPausa.draw(batch, layoutPausa, textoX, textoY);
            }

            batch.end();
        }
    }

    public boolean debeVolverAlMenu() { return volverAlMenu; }

    public void ajustarCamara(int width, int height) {
        viewportJuego.update(width, height, true);
        camaraJuego.position.set(mapa.getAncho() / 2f, mapa.getAlto() / 2f, 0);
        camaraJuego.update();
    }

    public void dispose() {
        mapa.dispose();
        HUD.dispose();
        batch.dispose();
        for (int i = 0; i < personajes.size; i++) {
            personajes.get(i).dispose();
        }
        if (hiloTiempo != null) hiloTiempo.terminar();
        GestorSonidos.getInstancia().detenerMusica();

        if (texturaOverlay != null) texturaOverlay.dispose();
        if (fontPausa != null) fontPausa.dispose();
    }
}
