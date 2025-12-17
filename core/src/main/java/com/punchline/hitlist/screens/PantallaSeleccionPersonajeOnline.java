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
import com.punchline.hitlist.interacciones.TeclaListener;
import com.punchline.hitlist.personajes.TipoPersonaje;
import com.punchline.hitlist.red.paquetes.PaqueteSeleccionPersonaje;
import com.punchline.hitlist.red.paquetes.PaqueteTiempoSeleccion;

public class PantallaSeleccionPersonajeOnline {
    private final Texture FONDO;
    private BitmapFont fontNombre, fontStats, fontTiempo;
    private final Texture texturaCuadrado;
    private final Texture[] previews = new Texture[4];

    private int indiceSeleccionado = 0;
    private final TeclaListener teclaListener;
    private final GlyphLayout layout = new GlyphLayout();
    private float tiempoAnimacion = 0;

    private ClienteHitlist cliente;
    private int miId;
    private TipoPersonaje miSeleccion = null;
    private boolean terminado = false;

    // Info del servidor
    private float tiempoRestante = 40f;
    private String personajeOponente = null;

    public PantallaSeleccionPersonajeOnline(ClienteHitlist cliente) {
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

        previews[0] = new Texture("fondos/Michael_Menu.png");
        previews[1] = new Texture("fondos/Billie_Menu.png");
        previews[2] = new Texture("fondos/Lebron_Menu.png");
        previews[3] = new Texture("fondos/Frida_Menu.png");
    }

    public void render(SpriteBatch batch, OrthographicCamera camara) {
        // Recibir actualización del servidor
        PaqueteTiempoSeleccion paquete = cliente.getUltimoTiempoSeleccion();
        if (paquete != null && "PERSONAJE".equals(paquete.getEtapa())) {
            tiempoRestante = paquete.getTiempoRestante();

            // Ver si el oponente ya eligió
            if (miId == 1 && paquete.getPersonajeJ2() != null) {
                personajeOponente = paquete.getPersonajeJ2();
            } else if (miId == 2 && paquete.getPersonajeJ1() != null) {
                personajeOponente = paquete.getPersonajeJ1();
            }

            // Si ambos eligieron o se acabó el tiempo, terminar
            if (tiempoRestante <= 0 || (paquete.getPersonajeJ1() != null && paquete.getPersonajeJ2() != null)) {
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
            "ELIGE TU LUCHADOR" : "ESPERANDO AL OPONENTE...";
        Color colorTitulo = miId == 1 ? Color.CYAN : Color.RED;

        fontNombre.setColor(colorTitulo);
        fontNombre.getData().setScale(1.2f);
        layout.setText(fontNombre, textoTitulo);
        fontNombre.draw(batch, layout, (camara.viewportWidth - layout.width)/2, camara.viewportHeight - 20);

        // Tiempo restante
        fontTiempo.setColor(tiempoRestante < 10 ? Color.RED : Color.YELLOW);
        fontTiempo.getData().setScale(1.5f);
        String textoTiempo = String.format("%.0f", tiempoRestante);
        layout.setText(fontTiempo, textoTiempo);
        fontTiempo.draw(batch, layout, (camara.viewportWidth - layout.width)/2, camara.viewportHeight - 70);

        // Tarjetas
        float margen = 15f;
        float anchoCard = (camara.viewportWidth / 2f) - (margen * 2);
        float altoCard = (camara.viewportHeight / 2f) - (margen * 2);
        float xIzq = margen;
        float xDer = (camara.viewportWidth / 2f) + margen;
        float yArriba = (camara.viewportHeight / 2f) + margen;
        float yAbajo = margen;

        TipoPersonaje[] tipos = {
            TipoPersonaje.MICHAEL_JACKSON, TipoPersonaje.BILLIE_EILISH,
            TipoPersonaje.LEBRON_JAMES, TipoPersonaje.FRIDA_KAHLO
        };

        dibujarTarjeta(batch, previews[0], tipos[0], 0, xIzq, yArriba, anchoCard, altoCard, colorTitulo, true);
        dibujarTarjeta(batch, previews[1], tipos[1], 1, xDer, yArriba, anchoCard, altoCard, colorTitulo, false);
        dibujarTarjeta(batch, previews[2], tipos[2], 2, xIzq, yAbajo, anchoCard, altoCard, colorTitulo, true);
        dibujarTarjeta(batch, previews[3], tipos[3], 3, xDer, yAbajo, anchoCard, altoCard, colorTitulo, false);

        batch.end();
    }

    private void dibujarTarjeta(SpriteBatch batch, Texture imagen, TipoPersonaje tipo, int indiceTarjeta,
                                float x, float y, float w, float h, Color colorCursor, boolean alinearDerecha) {
        boolean esElCursor = (indiceSeleccionado == indiceTarjeta);
        boolean esMiSeleccion = (miSeleccion == tipo);
        boolean esSeleccionOponente = (personajeOponente != null && personajeOponente.equals(tipo.name()));

        float grosor = 6f;
        Color colorBorde = Color.GRAY;
        float escalaTexto = 1.0f;

        if (esMiSeleccion) {
            colorBorde = miId == 1 ? Color.CYAN : Color.RED;
        } else if (esSeleccionOponente) {
            colorBorde = miId == 1 ? Color.RED : Color.CYAN;
        } else if (esElCursor && miSeleccion == null) {
            colorBorde = colorCursor;
            escalaTexto = 1.0f + (float)Math.sin(tiempoAnimacion * 6) * 0.1f;
        }

        // Marco
        batch.setColor(colorBorde);
        batch.draw(texturaCuadrado, x, y + h - grosor, w, grosor);
        batch.draw(texturaCuadrado, x, y, w, grosor);
        batch.draw(texturaCuadrado, x, y, grosor, h);
        batch.draw(texturaCuadrado, x + w - grosor, y, grosor, h);
        batch.setColor(Color.WHITE);

        // Etiquetas
        if (esMiSeleccion) {
            fontStats.setColor(miId == 1 ? Color.CYAN : Color.RED);
            fontStats.draw(batch, "TÚ", x + 10, y + h - 10);
        }
        if (esSeleccionOponente) {
            fontStats.setColor(miId == 1 ? Color.RED : Color.CYAN);
            fontStats.draw(batch, "RIVAL", x + w - 70, y + h - 10);
        }

        // Imagen
        batch.draw(imagen, x + grosor, y + grosor, w - (grosor * 2), h - (grosor * 2));

        // Nombre
        fontNombre.setColor(esElCursor ? colorCursor : Color.WHITE);
        fontNombre.getData().setScale(escalaTexto);
        layout.setText(fontNombre, tipo.getNombre());
        float textoX = alinearDerecha ? (x + w) - layout.width - 20 : x + 20;
        fontNombre.draw(batch, layout, textoX, y + h - 20);

        // Stats
        fontStats.setColor(Color.GOLD);
        float statsY = y + 100;
        float statsX = alinearDerecha ? (x + w) - 120f : x + 20f;
        fontStats.draw(batch, "FUE: " + tipo.getFuerza(), statsX, statsY);
        fontStats.draw(batch, "VEL: " + tipo.getVelocidad(), statsX, statsY - 22);
    }

    private void manejarInput() {
        if (miSeleccion != null) return; // Ya elegí

        // Movimiento
        if (teclaListener.isDerechaJustPressed() && indiceSeleccionado % 2 == 0) indiceSeleccionado++;
        if (teclaListener.isIzquierdaJustPressed() && indiceSeleccionado % 2 != 0) indiceSeleccionado--;
        if (teclaListener.isAbajoJustPressed() && indiceSeleccionado < 2) indiceSeleccionado += 2;
        if (teclaListener.isArribaJustPressed() && indiceSeleccionado >= 2) indiceSeleccionado -= 2;

        // Selección
        if (teclaListener.isEnterJustPressed()) {
            TipoPersonaje[] tipos = {
                TipoPersonaje.MICHAEL_JACKSON, TipoPersonaje.BILLIE_EILISH,
                TipoPersonaje.LEBRON_JAMES, TipoPersonaje.FRIDA_KAHLO
            };

            miSeleccion = tipos[indiceSeleccionado];

            // Enviar al servidor
            PaqueteSeleccionPersonaje paquete = new PaqueteSeleccionPersonaje(miId, miSeleccion.name());
            cliente.enviarPaqueteSeleccion(paquete);

            System.out.println("Seleccionaste: " + miSeleccion.name());
        }
    }

    public boolean estaTerminado() { return terminado; }

    private void inicializarFuentes() {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/ari-w9500.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
            param.size = 36; param.borderWidth = 2; param.borderColor = Color.BLACK;
            fontNombre = generator.generateFont(param);
            param.size = 25; param.color = Color.GOLD;
            fontStats = generator.generateFont(param);
            param.size = 40; param.color = Color.YELLOW;
            fontTiempo = generator.generateFont(param);
            generator.dispose();
        } catch (Exception e) {
            fontNombre = new BitmapFont();
            fontStats = new BitmapFont();
            fontTiempo = new BitmapFont();
        }
    }

    public void dispose() {
        FONDO.dispose();
        fontNombre.dispose();
        fontStats.dispose();
        fontTiempo.dispose();
        texturaCuadrado.dispose();
        for (Texture t : previews) t.dispose();
    }
}
