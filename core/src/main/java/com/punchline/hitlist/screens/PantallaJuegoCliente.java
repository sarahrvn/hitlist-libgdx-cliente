package com.punchline.hitlist.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen; // Importante implementar Screen
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.punchline.hitlist.cliente.ClienteHitlist;
import com.punchline.hitlist.elementosJuego.*;
import com.punchline.hitlist.interacciones.TeclaListener;
import com.punchline.hitlist.personajes.Personaje;
import com.punchline.hitlist.personajes.TipoPersonaje;
import com.punchline.hitlist.red.paquetes.*;

public class PantallaJuegoCliente implements Screen {

    // --- ELEMENTOS VISUALES ---
    private Mapa mapa;
    private Array<Personaje> personajes;
    private Personaje p1, p2;
    private Hud HUD;
    private Array<Espada> espadasVisuales; // Lista visual para las espadas del suelo

    // --- CÁMARA Y RENDER ---
    private OrthographicCamera camaraJuego;
    private Viewport viewportJuego;
    private SpriteBatch batch;

    // --- RED Y LÓGICA ---
    private ClienteHitlist cliente;
    private TeclaListener teclaListener;
    private boolean volverAlMenu = false;
    private int idGanador = 0;

    // Constructor
    public PantallaJuegoCliente(ClienteHitlist cliente, PaqueteInicioPartida config) {
        this.cliente = cliente;

        // Cargar mapa según configuración del servidor
        MapaDisponible mapaEnum = MapaDisponible.valueOf(config.getNombreMapa());
        this.mapa = new Mapa(mapaEnum);

        // Configurar Cámara (1024x576 estándar 16:9)
        camaraJuego = new OrthographicCamera();
        viewportJuego = new StretchViewport(1024, 576, camaraJuego);
        viewportJuego.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // Centrar cámara
        camaraJuego.position.set(mapa.getAncho() / 2f, mapa.getAlto() / 2f, 0);
        camaraJuego.update();

        // Inicializar Personajes
        personajes = new Array<>();

        TipoPersonaje tp1 = TipoPersonaje.valueOf(config.getTipoP1());
        p1 = new Personaje(tp1);
        personajes.add(p1);

        TipoPersonaje tp2 = TipoPersonaje.valueOf(config.getTipoP2());
        p2 = new Personaje(tp2);
        personajes.add(p2);

        // Inicializar HUD y Batch
        HUD = new Hud();
        batch = new SpriteBatch();
        espadasVisuales = new Array<>();

        // Input
        teclaListener = new TeclaListener();
        Gdx.input.setInputProcessor(teclaListener);

        // Música
        GestorSonidos.getInstancia().cargarMusica(SonidoDisponible.MUSICA_COMBATE);
        GestorSonidos.getInstancia().reproducirMusica(SonidoDisponible.MUSICA_COMBATE, true);
    }

    @Override
    public void show() {
        // Se llama cuando esta pantalla se convierte en la actual
    }

    @Override
    public void render(float delta) {
        // Procesa input local y la manda al servidor
        procesarInputLocal();

        // Recibe estado del servidor
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado != null) {
            actualizarMundo(estado);
        }

        // Actualiza animaciones locales
        // Esto permite que el ciclo de caminar avance visualmente aunque la posición la dicte el server
        for (Personaje p : personajes) {
            p.updateAnimacion(delta);
        }

        // Dibuuja
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camaraJuego.combined);

        // Fondo del mapa
        batch.begin();
        mapa.renderFondo(batch, camaraJuego, viewportJuego);
        batch.end();

        // Plataformas del mapa
        mapa.renderMapa(camaraJuego);

        batch.begin();

        // Dibujar espadas
        for (Espada e : espadasVisuales) {
            e.render(batch);
        }

        // Dibujar personajes
        for (Personaje p : personajes) {
            p.dibujar(batch);
        }

        batch.end();

        // D. HUD (Interfaz de usuario)
        HUD.render(batch, personajes);
    }

    private void procesarInputLocal() {
        boolean salto = teclaListener.isArribaJustPressed() ||
            teclaListener.isP1ArribaJustPressed() ||
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

        boolean izq = teclaListener.isP1Izquierda() || teclaListener.isP2Izquierda();
        boolean der = teclaListener.isP1Derecha() || teclaListener.isP2Derecha();
        boolean abajo = teclaListener.isP1Abajo() || teclaListener.isP2Abajo();

        boolean atacar = teclaListener.isP1AtacarJustPressed() || teclaListener.isP2AtacarJustPressed();
        boolean agarrar = teclaListener.isP1AgarrarJustPressed() || teclaListener.isP2AgarrarJustPressed();

        // Enviar paquete al servidor
        cliente.enviarInput(salto, abajo, izq, der, atacar, agarrar);
    }

    private void actualizarMundo(PaqueteEstado estado) {
        // Sincronizar Jugador 1
        DatosPersonaje d1 = estado.getJugador1();

        p1.sincronizarDesdeRed(d1.x, d1.y, d1.mirandoDerecha, d1.estadoAnimacion, d1.tieneArma, d1.vidas);

        // Sincronizar Jugador 2
        DatosPersonaje d2 = estado.getJugador2();
        p2.sincronizarDesdeRed(d2.x, d2.y, d2.mirandoDerecha, d2.estadoAnimacion, d2.tieneArma, d2.vidas);

        // Sincronizar espadas
        // Reconstruye la lista para asegurar que solo se ven las que existen en el server
        espadasVisuales.clear();
        if (estado.getEspadas() != null) {
            for (DatosEspada de : estado.getEspadas()) {
                if (de.activa) {
                    Arma tipo = Arma.valueOf(de.tipoArma);
                    espadasVisuales.add(new Espada(de.x, de.y, tipo));
                }
            }
        }

        // Actualizar HUD
        HUD.setTiempoRestante(estado.getTiempoRestante());

        // Verificar fin de juego
        if (estado.isJuegoTerminado()) {
            this.volverAlMenu = true;
            this.idGanador = estado.getIdGanador();
        }
    }

    public boolean debeVolverAlMenu() { return volverAlMenu; }

    public int getIndiceGanador() {
        if (idGanador == 1){
            return 1;
        } else if (idGanador == 2) {
            return 2;
        } else {
            return -1;
        }
    }

    public TipoPersonaje getTipoPersonajeGanador() {
        // idGanador viene del servidor
        if (idGanador == 1) {
            return p1.getTipoPersonaje();
        } else if (idGanador == 2) {
            return p2.getTipoPersonaje();
        }
        return TipoPersonaje.BILLIE_EILISH;
    }

    @Override
    public void resize(int width, int height) {
        viewportJuego.update(width, height, true);
        camaraJuego.position.set(mapa.getAncho() / 2f, mapa.getAlto() / 2f, 0);
        camaraJuego.update();
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { dispose(); }

    @Override
    public void dispose() {
        if(mapa != null) mapa.dispose();
        if(HUD != null) HUD.dispose();
        if(batch != null) batch.dispose();

        for(Personaje p : personajes) {
            if(p != null) p.dispose();
        }

        GestorSonidos.getInstancia().detenerMusica();
    }
}
