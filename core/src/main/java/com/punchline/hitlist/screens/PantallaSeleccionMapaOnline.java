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
import com.punchline.hitlist.cliente.ClienteHitlist;
import com.punchline.hitlist.elementosJuego.MapaDisponible;
import com.punchline.hitlist.interacciones.TeclaListener;
import com.punchline.hitlist.red.paquetes.PaqueteSeleccionMapa;
import com.punchline.hitlist.red.paquetes.PaqueteTiempoSeleccion;

public class PantallaSeleccionMapaOnline {
    private final Texture FONDO;
    private BitmapFont fontNombre, fontTiempo;
    private final Texture texturaCuadrado;
    private final Texture[] previews = new Texture[4];

    private int indiceSeleccionado = 0;
    private final TeclaListener teclaListener;
    private final GlyphLayout layout = new GlyphLayout();
    private float tiempoAnimacion = 0;

    private ClienteHitlist cliente;
    private int miId;
    private MapaDisponible miSeleccion = null;
    private boolean terminado = false;

    private float tiempoRestante = 40f;
    private String mapaOponente = null;

    public PantallaSeleccionMapaOnline(ClienteHitlist cliente) {
        this.cliente = cliente;
        this.miId = cliente.getMiId();

        FONDO = new Texture("fondos/Fondo_Seleccion_Personaje.png");
        teclaListener = new TeclaListener();
        Gdx.input.setInputProcessor(teclaListener);

        inicializarFuentes();

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        texturaCuadrado = new Texture(pix);
        pix.dispose();

        previews[0] = new Texture("mapas/mapa_yate.png");
        previews[1] = new Texture("mapas/mapa_alfombra_roja.png");
        previews[2] = new Texture("mapas/mapa_concierto.png");
        previews[3] = new Texture("mapas/mapa_cartel.png");
    }

    public void render(SpriteBatch batch, OrthographicCamera camara) {
        // Recibir actualización del servidor
        PaqueteTiempoSeleccion paquete = cliente.getUltimoTiempoSeleccion();
        if (paquete != null && "MAPA".equals(paquete.getEtapa())) {
            tiempoRestante = paquete.getTiempoRestante();

            if (miId == 1 && paquete.getMapaJ2() != null) {
                mapaOponente = paquete.getMapaJ2();
            } else if (miId == 2 && paquete.getMapaJ1() != null) {
                mapaOponente = paquete.getMapaJ1();
            }

            if (tiempoRestante <= 0 || (paquete.getMapaJ1() != null && paquete.getMapaJ2() != null)) {
                terminado = true;
            }
        }

        manejarInput();
        tiempoAnimacion += Gdx.graphics.getDeltaTime();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        batch.draw(FONDO, 0, 0, camara.viewportWidth, camara.viewportHeight);

        // Título
        String textoTitulo = miSeleccion == null ?
            "ELIGE EL MAPA" : "ESPERANDO AL OPONENTE...";
        Color colorTitulo = miId == 1 ? Color.CYAN : Color.RED;

        fontNombre.setColor(colorTitulo);
        fontNombre.getData().setScale(1.2f);
        layout.setText(fontNombre, textoTitulo);
        fontNombre.draw(batch, layout, (camara.viewportWidth - layout.width)/2, camara.viewportHeight - 20);

        // Tiempo
        fontTiempo.setColor(tiempoRestante < 10 ? Color.RED : Color.YELLOW);
        fontTiempo.getData().setScale(1.5f);
        String textoTiempo = String.format("⏱%.0f", tiempoRestante);
        layout.setText(fontTiempo, textoTiempo);
        fontTiempo.draw(batch, layout, (camara.viewportWidth - layout.width)/2, camara.viewportHeight - 70);

        // Info adicional
        if (miSeleccion != null && mapaOponente == null) {
            fontNombre.setColor(Color.WHITE);
            fontNombre.getData().setScale(0.8f);
            String info = "Tu elección será sorteada con la del rival";
            layout.setText(fontNombre, info);
            fontNombre.draw(batch, layout, (camara.viewportWidth - layout.width)/2, camara.viewportHeight - 120);
        }

        // Tarjetas
        float mitadAncho = camara.viewportWidth / 2f;
        float mitadAlto = camara.viewportHeight / 2f;
        float margen = 15f;
        float anchoCard = mitadAncho - (margen * 2);
        float altoCard = mitadAlto - (margen * 2);
        float xIzq = margen;
        float xDer = mitadAncho + margen;
        float yAbajo = margen;
        float yArriba = mitadAlto + margen;

        MapaDisponible[] mapas = {
            MapaDisponible.MAPA_YATE, MapaDisponible.MAPA_ALFOMBRA_ROJA,
            MapaDisponible.MAPA_CONCIERTO, MapaDisponible.MAPA_CARTEL
        };
        String[] nombres = {"YATE", "ALFOMBRA ROJA", "CONCIERTO", "CARTEL HOLLYWOOD"};

        dibujarTarjeta(batch, previews[0], mapas[0], nombres[0], 0, xIzq, yArriba, anchoCard, altoCard, colorTitulo, true);
        dibujarTarjeta(batch, previews[1], mapas[1], nombres[1], 1, xDer, yArriba, anchoCard, altoCard, colorTitulo, false);
        dibujarTarjeta(batch, previews[2], mapas[2], nombres[2], 2, xIzq, yAbajo, anchoCard, altoCard, colorTitulo, true);
        dibujarTarjeta(batch, previews[3], mapas[3], nombres[3], 3, xDer, yAbajo, anchoCard, altoCard, colorTitulo, false);

        batch.end();
    }

    private void dibujarTarjeta(SpriteBatch batch, Texture imagen, MapaDisponible mapa, String nombre,
                                int indiceTarjeta, float x, float y, float w, float h,
                                Color colorCursor, boolean alinearTextoDerecha) {
        boolean esElCursor = (indiceSeleccionado == indiceTarjeta);
        boolean esMiSeleccion = (miSeleccion == mapa);
        boolean esSeleccionOponente = (mapaOponente != null && mapaOponente.equals(mapa.name()));

        float grosor = 6f;
        Color colorBorde = Color.GRAY;

        if (esMiSeleccion) {
            colorBorde = miId == 1 ? Color.CYAN : Color.RED;
        } else if (esSeleccionOponente) {
            colorBorde = miId == 1 ? Color.RED : Color.CYAN;
        } else if (esElCursor && miSeleccion == null) {
            colorBorde = Color.WHITE;
        }

        // Marco
        batch.setColor(colorBorde);
        batch.draw(texturaCuadrado, x, y + h - grosor, w, grosor);
        batch.draw(texturaCuadrado, x, y, w, grosor);
        batch.draw(texturaCuadrado, x, y, grosor, h);
        batch.draw(texturaCuadrado, x + w - grosor, y, grosor, h);
        batch.setColor(Color.WHITE);

        // Imagen
        batch.draw(imagen, x + grosor, y + grosor, w - (grosor * 2), h - (grosor * 2));

        // Nombre
        if (esElCursor && miSeleccion == null) {
            fontNombre.setColor(Color.ORANGE);
            float pulsacion = (float)Math.sin(tiempoAnimacion * 6) * 0.1f;
            fontNombre.getData().setScale(1.0f + pulsacion);
        } else {
            fontNombre.setColor(Color.WHITE);
            fontNombre.getData().setScale(1.0f);
        }

        layout.setText(fontNombre, nombre);
        float padding = 20f;
        float textoX = alinearTextoDerecha ? (x + w) - layout.width - padding : x + padding;
        float textoY = y + h - padding;
        fontNombre.draw(batch, layout, textoX, textoY);
    }

    private void manejarInput() {
        if (miSeleccion != null) return;

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

        if (teclaListener.isEnterJustPressed()) {
            MapaDisponible[] mapas = {
                MapaDisponible.MAPA_YATE, MapaDisponible.MAPA_ALFOMBRA_ROJA,
                MapaDisponible.MAPA_CONCIERTO, MapaDisponible.MAPA_CARTEL
            };

            miSeleccion = mapas[indiceSeleccionado];

            PaqueteSeleccionMapa paquete = new PaqueteSeleccionMapa(miId, miSeleccion.name());
            cliente.enviarPaqueteSeleccion(paquete);

            System.out.println("✅ Seleccionaste: " + miSeleccion.name());
        }
    }

    public boolean estaTerminado() { return terminado; }

    private void inicializarFuentes() {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/ari-w9500.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
            params.size = 36; params.borderWidth = 2; params.borderColor = Color.BLACK;
            fontNombre = generator.generateFont(params);
            params.size = 40; params.color = Color.YELLOW;
            fontTiempo = generator.generateFont(params);
            generator.dispose();
        } catch (Exception e) {
            fontNombre = new BitmapFont();
            fontTiempo = new BitmapFont();
        }
    }

    public void dispose() {
        FONDO.dispose();
        fontNombre.dispose();
        fontTiempo.dispose();
        texturaCuadrado.dispose();
        for (Texture t : previews) t.dispose();
    }
}
