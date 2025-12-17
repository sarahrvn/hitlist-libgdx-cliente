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
import com.punchline.hitlist.personajes.TipoPersonaje;

public class PantallaSeleccionPersonaje {

    private final Texture FONDO;
    private BitmapFont fontNombre;
    private BitmapFont fontStats;
    private final Texture texturaCuadrado;

    private final Texture previewBillie;
    private final Texture previewMichael;
    private final Texture previewFrida;
    private final Texture previewLebron;

    private int indiceSeleccionado = 0;

    private final TeclaListener teclaListener;

    // --- NUEVAS VARIABLES PARA SELECCIÓN DOBLE ---
    private TipoPersonaje seleccionP1 = null;
    private TipoPersonaje seleccionP2 = null;
    private boolean turnoP1 = true; // true = Le toca al P1, false = Le toca al P2

    private final GlyphLayout layout = new GlyphLayout();
    private float tiempoAnimacion = 0;

    public PantallaSeleccionPersonaje() {
        FONDO = new Texture("fondos/Fondo_Seleccion_Personaje.png");
        teclaListener = new TeclaListener();
        Gdx.input.setInputProcessor(teclaListener);

        inicializarFuentes(); // Moví esto a un método privado para limpiar el constructor

        // Textura blanca para bordes
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        texturaCuadrado = new Texture(pix);
        pix.dispose();

        previewBillie = new Texture("fondos/Billie_Menu.png");
        previewMichael = new Texture("fondos/Michael_Menu.png");
        previewFrida = new Texture("fondos/Frida_Menu.png");
        previewLebron = new Texture("fondos/Lebron_Menu.png");
    }

    public void render(SpriteBatch batch, OrthographicCamera camara) {
        manejarInput();
        tiempoAnimacion += Gdx.graphics.getDeltaTime();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        batch.draw(FONDO, 0, 0, camara.viewportWidth, camara.viewportHeight);

        // --- TÍTULO INDICADOR DE TURNO ---
        String textoTitulo;
        Color colorTitulo;

        if (turnoP1) {
            textoTitulo = "JUGADOR 1: ELIGE TU LUCHADOR";
            colorTitulo = Color.CYAN;
        } else if (seleccionP2 == null) {
            textoTitulo = "JUGADOR 2: ELIGE TU LUCHADOR";
            colorTitulo = Color.RED;
        } else {
            textoTitulo = "¡LISTOS PARA LA PELEA!";
            colorTitulo = Color.GREEN;
        }

        fontNombre.setColor(colorTitulo);
        fontNombre.getData().setScale(1.2f);
        layout.setText(fontNombre, textoTitulo);
        fontNombre.draw(batch, layout, (camara.viewportWidth - layout.width)/2, camara.viewportHeight - 20);


        // --- DIBUJAR TARJETAS ---
        float margen = 15f;
        float anchoCard = (camara.viewportWidth / 2f) - (margen * 2);
        float altoCard = (camara.viewportHeight / 2f) - (margen * 2);
        float xIzq = margen;
        float xDer = (camara.viewportWidth / 2f) + margen;
        float yArriba = (camara.viewportHeight / 2f) + margen;
        float yAbajo = margen;

        // Determinar el color del cursor actual (Cyan si es P1, Rojo si es P2)
        Color colorCursor = turnoP1 ? Color.CYAN : Color.RED;

        // Dibujamos las 4 tarjetas
        dibujarTarjeta(batch, previewMichael, TipoPersonaje.MICHAEL_JACKSON, 0, xIzq, yArriba, anchoCard, altoCard, colorCursor, true);
        dibujarTarjeta(batch, previewBillie, TipoPersonaje.BILLIE_EILISH, 1, xDer, yArriba, anchoCard, altoCard, colorCursor, false);
        dibujarTarjeta(batch, previewLebron, TipoPersonaje.LEBRON_JAMES, 2, xIzq, yAbajo, anchoCard, altoCard, colorCursor, true);
        dibujarTarjeta(batch, previewFrida, TipoPersonaje.FRIDA_KAHLO, 3, xDer, yAbajo, anchoCard, altoCard, colorCursor, false);

        batch.end();
    }

    private void dibujarTarjeta(SpriteBatch batch, Texture imagen, TipoPersonaje tipo, int indiceTarjeta,
                                float x, float y, float w, float h, Color colorCursor, boolean alinearDerecha) {

        boolean esElCursor = (indiceSeleccionado == indiceTarjeta);

        // Verificamos si este personaje YA fue elegido por alguien
        boolean elegidoPorP1 = (seleccionP1 == tipo);
        boolean elegidoPorP2 = (seleccionP2 == tipo);

        float grosor = 6f;
        Color colorBorde = Color.GRAY; // Color por defecto (inactivo)
        float escalaTexto = 1.0f;

        // Lógica de colores de borde
        if (esElCursor && seleccionP2 == null) {
            // Si el cursor está aquí y no hemos terminado, mostramos el color del jugador actual
            colorBorde = colorCursor;
            // Animación de latido
            escalaTexto = 1.0f + (float)Math.sin(tiempoAnimacion * 6) * 0.1f;
        } else if (elegidoPorP1) {
            colorBorde = Color.CYAN; // Ya elegido por P1
        } else if (elegidoPorP2) {
            colorBorde = Color.RED; // Ya elegido por P2
        }

        // --- DIBUJAR MARCO ---
        batch.setColor(colorBorde);
        batch.draw(texturaCuadrado, x, y + h - grosor, w, grosor);
        batch.draw(texturaCuadrado, x, y, w, grosor);
        batch.draw(texturaCuadrado, x, y, grosor, h);
        batch.draw(texturaCuadrado, x + w - grosor, y, grosor, h);
        batch.setColor(Color.WHITE); // Reset color

        // --- ETIQUETA P1/P2 (Opcional, para saber quién eligió qué) ---
        if (elegidoPorP1) fontStats.draw(batch, "P1", x + 10, y + h - 10);
        if (elegidoPorP2) fontStats.draw(batch, "P2", x + w - 40, y + h - 10);

        // --- IMAGEN ---
        batch.draw(imagen, x + grosor, y + grosor, w - (grosor * 2), h - (grosor * 2));

        // --- NOMBRE ---
        fontNombre.setColor(esElCursor ? colorCursor : Color.WHITE);
        fontNombre.getData().setScale(escalaTexto);
        layout.setText(fontNombre, tipo.getNombre());

        float textoX = alinearDerecha ? (x + w) - layout.width - 20 : x + 20;
        fontNombre.draw(batch, layout, textoX, y + h - 20);

        // --- STATS ---
        fontStats.setColor(Color.GOLD); // Reset color stats
        float statsY = y + 100;
        float statsX = alinearDerecha ? (x + w) - 120f : x + 20f;

        fontStats.draw(batch, "FUE: " + tipo.getFuerza(), statsX, statsY);
        fontStats.draw(batch, "VEL: " + tipo.getVelocidad(), statsX, statsY - 22);
        // ... otros stats
    }

    private void manejarInput() {
        // Si ya eligieron los dos, no hacemos nada (la clase Principal debería cambiar de pantalla)
        if (seleccionP2 != null) return;

        // Movimiento del cursor
        if(turnoP1) {
            if (teclaListener.isP1DerechaJustPressed() && indiceSeleccionado % 2 == 0) indiceSeleccionado++;
            if (teclaListener.isP1IzquierdaJustPressed() && indiceSeleccionado % 2 != 0) indiceSeleccionado--;
            if (teclaListener.isP1AbajoJustPressed() && indiceSeleccionado < 2) indiceSeleccionado += 2;
            if (teclaListener.isP1ArribaJustPressed() && indiceSeleccionado >= 2) indiceSeleccionado -= 2;
        } else {
            if (teclaListener.isP2DerechaJustPressed() && indiceSeleccionado % 2 == 0) indiceSeleccionado++;
            if (teclaListener.isP2IzquierdaJustPressed() && indiceSeleccionado % 2 != 0) indiceSeleccionado--;
            if (teclaListener.isP2AbajoJustPressed() && indiceSeleccionado < 2) indiceSeleccionado += 2;
            if (teclaListener.isP2ArribaJustPressed() && indiceSeleccionado >= 2) indiceSeleccionado -= 2;
        }


        // Selección con Enter
        if (teclaListener.isEnterJustPressed()) {

            // Obtenemos qué personaje está bajo el cursor
            TipoPersonaje personajeBajoCursor = null;
            switch (indiceSeleccionado) {
                case 0: personajeBajoCursor = TipoPersonaje.MICHAEL_JACKSON; break;
                case 1: personajeBajoCursor = TipoPersonaje.BILLIE_EILISH; break;
                case 2: personajeBajoCursor = TipoPersonaje.LEBRON_JAMES; break;
                case 3: personajeBajoCursor = TipoPersonaje.FRIDA_KAHLO; break;
            }

            if (turnoP1) {
                // Turno P1: Guardamos y pasamos el turno
                seleccionP1 = personajeBajoCursor;
                turnoP1 = false; // Ahora le toca a P2

                // Opcional: Sonido "Player 1 Ready"
                indiceSeleccionado = 0; // Resetear cursor arriba para P2
            } else {
                // Turno P2
                seleccionP2 = personajeBajoCursor;
                // Listo! seleccionP2 ya no es null, el juego puede comenzar
            }
        }
    }

    // --- GETTERS PARA LA CLASE PRINCIPAL ---

    public boolean estanTodosSeleccionados() {
        return seleccionP1 != null && seleccionP2 != null;
    }

    public TipoPersonaje getSeleccionP1() { return seleccionP1; }
    public TipoPersonaje getSeleccionP2() { return seleccionP2; }

    // (El dispose y generador de fuentes sigue igual...)
    private void inicializarFuentes() {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/ari-w9500.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
            param.size = 36; param.borderWidth = 2; param.borderColor = Color.BLACK;
            fontNombre = generator.generateFont(param);

            param.size = 25; param.color = Color.GOLD;
            fontStats = generator.generateFont(param);
            generator.dispose();
        } catch (Exception e) {
            fontNombre = new BitmapFont(); fontStats = new BitmapFont();
        }
    }

    public void dispose() {
        FONDO.dispose(); fontNombre.dispose(); fontStats.dispose(); texturaCuadrado.dispose();
        previewBillie.dispose(); previewMichael.dispose(); previewFrida.dispose(); previewLebron.dispose();
    }
}
