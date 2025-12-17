package com.punchline.hitlist;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.punchline.hitlist.elementosJuego.MapaDisponible;
import com.punchline.hitlist.screens.*;
import com.punchline.hitlist.personajes.TipoPersonaje;
import com.punchline.hitlist.cliente.ClienteHitlist;

public class Principal extends ApplicationAdapter {
    private OrthographicCamera camara;
    private SpriteBatch batch;

    private PantallaLogo pantallaLogo;
    private PantallaJuego pantallaJuego;
    private PantallaTitulo pantallaTitulo;
    private PantallaMenu pantallaMenu;
    private PantallaSeleccionPersonaje pantallaSeleccionPersonaje;
    private PantallaSeleccionMapa pantallaSeleccionMapa;
    private PantallaResultado pantallaResultado;

    // NUEVAS PANTALLAS ONLINE
    private PantallaSeleccionPersonajeOnline pantallaSeleccionPersonajeOnline;
    private PantallaSeleccionMapaOnline pantallaSeleccionMapaOnline;

    private EstadoScreen estadoActual;

    private TipoPersonaje seleccionP1, seleccionP2;
    private MapaDisponible mapaElegido;

    private float tiempo = 0;

    private ClienteHitlist clienteOnline;
    private PantallaJuegoCliente pantallaJuegoCliente;
    private BitmapFont fontCarga;

    // Variables para búsqueda de servidor
    private boolean buscandoServidor = false;
    private Thread hiloBusqueda;
    private volatile boolean conexionExitosa = false;
    private volatile boolean conexionFallida = false;
    private float tiempoBuscando = 0;

    @Override
    public void create() {
        camara = new OrthographicCamera();
        batch = new SpriteBatch();

        pantallaLogo = new PantallaLogo();
        estadoActual = EstadoScreen.LOGO;

        fontCarga = new BitmapFont();
        fontCarga.getData().setScale(2);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camara.update();
        batch.setProjectionMatrix(camara.combined);

        switch (estadoActual) {
            case LOGO:
                camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                camara.update();
                pantallaLogo.render(batch, camara);
                tiempo += delta;
                if (tiempo >= 7f) {
                    Gdx.gl.glClearColor(1, 1, 1, 1);
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                    pantallaTitulo = new PantallaTitulo();
                    estadoActual = EstadoScreen.TITULO;
                }
                break;

            case TITULO:
                Gdx.gl.glClearColor(0.98f, 0.49f, 0.043f, 1f);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                pantallaTitulo.render(batch, camara);

                if (pantallaTitulo.SaltarAMenu()) {
                    pantallaMenu = new PantallaMenu();
                    estadoActual = EstadoScreen.MENU;
                }
                break;

            case MENU:
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                camara.update();

                pantallaMenu.render(batch, camara);

                if (pantallaMenu.quiereJugar()) {
                    pantallaSeleccionPersonaje = new PantallaSeleccionPersonaje();
                    estadoActual = EstadoScreen.SELECCION_DE_PERSONAJE;
                } else if (pantallaMenu.quiereJugarOnline()) {
                    iniciarBusquedaServidor();
                } else if (pantallaMenu.quiereSalir()) {
                    Gdx.app.exit();
                }
                break;

            case SELECCION_DE_PERSONAJE:
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                camara.update();

                // Determinar si es selección local u online
                if (pantallaSeleccionPersonaje != null) {
                    // MODO LOCAL
                    pantallaSeleccionPersonaje.render(batch, camara);

                    if (pantallaSeleccionPersonaje.estanTodosSeleccionados()) {
                        seleccionP1 = pantallaSeleccionPersonaje.getSeleccionP1();
                        seleccionP2 = pantallaSeleccionPersonaje.getSeleccionP2();
                        pantallaSeleccionMapa = new PantallaSeleccionMapa();
                        estadoActual = EstadoScreen.SELECCION_DE_MAPA;
                    }
                } else if (pantallaSeleccionPersonajeOnline != null) {
                    // MODO ONLINE
                    pantallaSeleccionPersonajeOnline.render(batch, camara);

                    if (pantallaSeleccionPersonajeOnline.estaTerminado()) {
                        System.out.println("Selección de personajes completada, pasando a mapas...");
                        pantallaSeleccionPersonajeOnline.dispose();
                        pantallaSeleccionPersonajeOnline = null;
                        pantallaSeleccionMapaOnline = new PantallaSeleccionMapaOnline(clienteOnline);
                        estadoActual = EstadoScreen.SELECCION_DE_MAPA;
                    }
                }
                break;

            case SELECCION_DE_MAPA:
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                camara.update();

                // Determinar si es selección local u online
                if (pantallaSeleccionMapa != null) {
                    // MODO LOCAL
                    pantallaSeleccionMapa.render(batch, camara);

                    if (pantallaSeleccionMapa.hayMapaSeleccionado()) {
                        mapaElegido = pantallaSeleccionMapa.getMapaSeleccionado();
                        pantallaJuego = new PantallaJuego(mapaElegido, seleccionP1, seleccionP2);
                        pantallaJuego.ajustarCamara(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                        estadoActual = EstadoScreen.JUEGO;
                    }
                } else if (pantallaSeleccionMapaOnline != null) {
                    // MODO ONLINE
                    pantallaSeleccionMapaOnline.render(batch, camara);

                    if (pantallaSeleccionMapaOnline.estaTerminado()) {
                        System.out.println("Selección de mapa completada, esperando inicio...");
                        pantallaSeleccionMapaOnline.dispose();
                        pantallaSeleccionMapaOnline = null;

                        // Esperar configuración de partida del servidor
                        estadoActual = EstadoScreen.ESPERA_JUGADORES;
                    }

                    // Verificar si ya llegó la configuración de partida
                    if (clienteOnline.getConfiguracionPartida() != null && pantallaSeleccionMapaOnline.estaTerminado()) {
                        pantallaJuegoCliente = new PantallaJuegoCliente(clienteOnline, clienteOnline.getConfiguracionPartida());
                        pantallaJuegoCliente.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                        estadoActual = EstadoScreen.JUEGO_ONLINE;
                    }
                }
                break;

            case JUEGO:
                pantallaJuego.render(delta);
                if (pantallaJuego.debeVolverAlMenu()) {
                    int indiceGanador = pantallaJuego.getIndiceGanador();

                    TipoPersonaje personajeGanador;
                    int numeroJugadorGanador;

                    if (indiceGanador == 0) {
                        personajeGanador = seleccionP1;
                        numeroJugadorGanador = 1;
                    } else {
                        personajeGanador = seleccionP2;
                        numeroJugadorGanador = 2;
                    }

                    pantallaResultado = new PantallaResultado(personajeGanador, numeroJugadorGanador);
                    estadoActual = EstadoScreen.RESULTADO;

                    pantallaJuego.dispose();
                    pantallaJuego = null;
                }
                break;

            case CONECTANDO:
                renderPantallaBuscandoServidor(delta);
                break;

            case ESPERA_JUGADORES:
                renderPantallaEsperaJugadores(delta);
                break;

            case JUEGO_ONLINE:
                renderJuegoOnline(delta);
                break;

            case RESULTADO:
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                camara.update();

                pantallaResultado.render(batch, camara);

                if (pantallaResultado.debeVolverMenu()) {
                    pantallaMenu = new PantallaMenu();
                    estadoActual = EstadoScreen.MENU;

                    pantallaResultado.dispose();
                    pantallaResultado = null;
                } else if (pantallaResultado.debeSalir()) {
                    pantallaResultado.dispose();
                    Gdx.app.exit();
                }
                break;
        }
    }

    private void iniciarBusquedaServidor() {
        clienteOnline = new ClienteHitlist();
        buscandoServidor = true;
        conexionExitosa = false;
        conexionFallida = false;
        tiempoBuscando = 0;
        estadoActual = EstadoScreen.CONECTANDO;

        hiloBusqueda = new Thread(() -> {
            boolean exito = clienteOnline.conectarAutomatico(10);

            if (exito) {
                conexionExitosa = true;
            } else {
                conexionFallida = true;
            }
            buscandoServidor = false;
        }, "Busqueda-Servidor");

        hiloBusqueda.start();
    }

    private void renderPantallaBuscandoServidor(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camara.update();
        batch.setProjectionMatrix(camara.combined);

        tiempoBuscando += delta;

        int puntos = ((int)(tiempoBuscando * 2)) % 4;
        String animacion = "";
        for (int i = 0; i < puntos; i++) animacion += ".";

        batch.begin();
        fontCarga.draw(batch, "BUSCANDO SERVIDOR EN LA RED" + animacion, 250, 320);
        fontCarga.draw(batch, "Tiempo: " + (int)tiempoBuscando + "s / 10s", 350, 260);
        batch.end();

        if (conexionExitosa) {
            System.out.println("Conexión exitosa, esperando rival...");
            estadoActual = EstadoScreen.ESPERA_JUGADORES;
        } else if (conexionFallida) {
            mostrarErrorYVolver("NO SE ENCONTRÓ SERVIDOR EN LA RED");
        }
    }

    private void renderPantallaEsperaJugadores(float delta) {
        if (clienteOnline.isServidorDesconectado()) {
            mostrarErrorYVolver("SERVIDOR DESCONECTADO: " + clienteOnline.getRazonDesconexion());
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        fontCarga.draw(batch, "CONECTADO: ESPERANDO RIVAL...", 300, 300);
        fontCarga.draw(batch, "Tu ID: " + clienteOnline.getMiId(), 300, 250);
        fontCarga.draw(batch, "Presiona ESC para cancelar", 300, 200);
        batch.end();

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            clienteOnline.desconectar();
            pantallaMenu = new PantallaMenu();
            estadoActual = EstadoScreen.MENU;
            return;
        }

        // Verificar si el servidor inició selección de personajes
        if (clienteOnline.getUltimoTiempoSeleccion() != null) {
            String etapa = clienteOnline.getUltimoTiempoSeleccion().getEtapa();
            if ("PERSONAJE".equals(etapa)) {
                System.out.println("Iniciando selección de personajes...");
                pantallaSeleccionPersonajeOnline = new PantallaSeleccionPersonajeOnline(clienteOnline);
                estadoActual = EstadoScreen.SELECCION_DE_PERSONAJE;
            }
        }

        // Verificar si ya llegó configuración de partida (por si salteó las selecciones)
        if (clienteOnline.getConfiguracionPartida() != null) {
            System.out.println("Iniciando partida...");
            pantallaJuegoCliente = new PantallaJuegoCliente(clienteOnline, clienteOnline.getConfiguracionPartida());
            pantallaJuegoCliente.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            estadoActual = EstadoScreen.JUEGO_ONLINE;
        }
    }

    private void renderJuegoOnline(float delta) {
        if (clienteOnline.isServidorDesconectado()) {
            if (pantallaJuegoCliente != null) {
                pantallaJuegoCliente.dispose();
                pantallaJuegoCliente = null;
            }
            mostrarErrorYVolver("SERVIDOR DESCONECTADO: " + clienteOnline.getRazonDesconexion());
            return;
        }

        if (pantallaJuegoCliente != null) {
            pantallaJuegoCliente.render(delta);

            if (pantallaJuegoCliente.debeVolverAlMenu()) {
                int indiceGanador = pantallaJuegoCliente.getIndiceGanador();
                TipoPersonaje pGanador = pantallaJuegoCliente.getTipoPersonajeGanador();

                pantallaResultado = new PantallaResultado(pGanador, indiceGanador);
                estadoActual = EstadoScreen.RESULTADO;

                pantallaJuegoCliente.dispose();
                pantallaJuegoCliente = null;
                clienteOnline.desconectar();
            }
        }
    }

    private void mostrarErrorYVolver(String mensaje) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        fontCarga.setColor(Color.RED);
        fontCarga.draw(batch, "" + mensaje, 200, 300);
        fontCarga.setColor(Color.WHITE);
        fontCarga.draw(batch, "Presiona ESC para volver al menú", 250, 200);
        batch.end();

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            if (clienteOnline != null) {
                clienteOnline.desconectar();
            }
            pantallaMenu = new PantallaMenu();
            estadoActual = EstadoScreen.MENU;
        }
    }

    public void resize(int width, int height) {
        if (pantallaJuego != null) {
            pantallaJuego.ajustarCamara(width, height);
        }
    }

    @Override
    public void dispose() {
        if (clienteOnline != null) {
            clienteOnline.desconectar();
        }

        if (pantallaLogo != null) pantallaLogo.dispose();
        if (pantallaTitulo != null) pantallaTitulo.dispose();
        if (pantallaMenu != null) pantallaMenu.dispose();
        if (pantallaSeleccionPersonaje != null) pantallaSeleccionPersonaje.dispose();
        if (pantallaSeleccionMapa != null) pantallaSeleccionMapa.dispose();
        if (pantallaJuego != null) pantallaJuego.dispose();
        if (pantallaJuegoCliente != null) pantallaJuegoCliente.dispose();
        if (pantallaResultado != null) pantallaResultado.dispose();
        if (pantallaSeleccionPersonajeOnline != null) pantallaSeleccionPersonajeOnline.dispose();
        if (pantallaSeleccionMapaOnline != null) pantallaSeleccionMapaOnline.dispose();
        batch.dispose();
    }
}
