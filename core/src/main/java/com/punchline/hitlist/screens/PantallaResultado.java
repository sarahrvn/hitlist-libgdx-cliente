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
import com.punchline.hitlist.personajes.TipoPersonaje;
import com.punchline.hitlist.elementosJuego.GestorSonidos;
import com.punchline.hitlist.elementosJuego.SonidoDisponible;

public class PantallaResultado {

    private final Texture FONDO_BASE;
    private Texture imagenGanador;

    private BitmapFont fontTitulo;
    private BitmapFont fontOpciones;

    private String textoTitulo = "";
    private String nombreJugador = "";

    private final String[] opciones = {"Volver al Menu", "Salir"};
    private int opcionSeleccionada = 0;

    private final TeclaListener teclaListener;

    private boolean volverMenu = false;
    private boolean salir = false;

    private final GlyphLayout layout = new GlyphLayout();
    private float tiempoAnimacion = 0;

    public PantallaResultado(TipoPersonaje personajeGanador, int numeroJugador) {
        numeroJugador = numeroJugador;
        FONDO_BASE = new Texture("fondos/Fondo_Seleccion_Personaje.png");
System.out.println("Indice: "+ numeroJugador);
        if(numeroJugador == -1){
            String rutaImagen = obtenerRutaImagenMenu(TipoPersonaje.BILLIE_EILISH);
            imagenGanador = new Texture(rutaImagen);
            this.textoTitulo = "EMPATE";

        } else {
            String rutaImagen = obtenerRutaImagenMenu(personajeGanador);
            imagenGanador = new Texture(rutaImagen);
            this.textoTitulo = "GANADOR!!";
            this.nombreJugador = "JUGADOR " + (numeroJugador);
        }


        teclaListener = new TeclaListener();
        Gdx.input.setInputProcessor(teclaListener);

        GestorSonidos.getInstancia().reproducirMusica(SonidoDisponible.MUSICA_MENU, true);

        generarFuentes();
    }

    private void generarFuentes() {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/ari-w9500.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

            // Fuente Grande (Título y Nombre Jugador)
            params.size = 60;
            params.borderWidth = 3;
            params.borderColor = Color.BLACK;
            params.shadowOffsetX = 4;
            params.shadowOffsetY = 4;
            fontTitulo = generator.generateFont(params);

            // Fuente Mediana (Opciones)
            params.size = 30;
            params.borderWidth = 2;
            params.shadowOffsetX = 2;
            params.shadowOffsetY = 2;
            fontOpciones = generator.generateFont(params);

            generator.dispose();
        } catch (Exception e) {
            fontTitulo = new BitmapFont();
            fontTitulo.getData().setScale(3f);
            fontOpciones = new BitmapFont();
            fontOpciones.getData().setScale(2f);
        }
    }

    private String obtenerRutaImagenMenu(TipoPersonaje p) {
        String ruta;
        System.out.println("Tipo: " + p);
        switch(p) {
            case BILLIE_EILISH:
                ruta = "sprites/Billie_Victoria.png";
                System.out.println("Rutaaa: " + ruta);
                break;
            case MICHAEL_JACKSON:
                ruta = "sprites/Michael_Victoria.png";
                System.out.println("Rutaaa: " + ruta);
                break;
            case FRIDA_KAHLO:
                ruta = "sprites/Frida_Victoria.png";
                System.out.println("Rutaaa: " + ruta);
                break;
            case LEBRON_JAMES:
                ruta = "sprites/Lebron_Victoria.png";
                System.out.println("Rutaaa: " + ruta);
                break;
            default:
                ruta = "sprites/Billie_Victoria.png";
                System.out.println("Ruta: " + ruta);
                break;
        }

        return ruta;
    }

    public void render(SpriteBatch batch, OrthographicCamera camara) {
        manejarInput();
        tiempoAnimacion += Gdx.graphics.getDeltaTime();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        batch.draw(FONDO_BASE, 0, 0, camara.viewportWidth, camara.viewportHeight);
        batch.draw(imagenGanador, 0, 0, camara.viewportWidth, camara.viewportHeight);

        // UI alineada a la derecha (75% del ancho)
        float ejeX = camara.viewportWidth * 0.75f;

        fontTitulo.setColor(Color.GOLD);
        layout.setText(fontTitulo, textoTitulo);
        fontTitulo.draw(batch, layout, ejeX - layout.width / 2f, camara.viewportHeight - 50f);

        fontTitulo.setColor(Color.WHITE);
        layout.setText(fontTitulo, nombreJugador);
        fontTitulo.draw(batch, layout, ejeX - layout.width / 2f, camara.viewportHeight - 130f);

        float yOpciones = 150f;

        for (int i = 0; i < opciones.length; i++) {
            if (i == opcionSeleccionada) {
                fontOpciones.setColor(Color.ORANGE);
                float escala = 1.0f + (float)Math.sin(tiempoAnimacion * 6) * 0.1f;
                fontOpciones.getData().setScale(escala);
            } else {
                fontOpciones.setColor(Color.WHITE);
                fontOpciones.getData().setScale(1.0f);
            }

            layout.setText(fontOpciones, opciones[i]);
            fontOpciones.draw(batch, layout, ejeX - layout.width / 2f, yOpciones - (i * 60f));
        }

        batch.end();
    }

    private void manejarInput() {
        if (teclaListener.isArribaJustPressed() || teclaListener.isAbajoJustPressed()) {
            opcionSeleccionada = (opcionSeleccionada == 0) ? 1 : 0;
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);
        }

        if (teclaListener.isEnterJustPressed()) {
            GestorSonidos.getInstancia().reproducirSonido(SonidoDisponible.MENU);

            if (opcionSeleccionada == 0) {
                volverMenu = true;
            } else {
                salir = true;
            }
        }
    }

    public boolean debeVolverMenu() { return volverMenu; }
    public boolean debeSalir() { return salir; }

    public void dispose() {
        FONDO_BASE.dispose();
        imagenGanador.dispose();
        fontTitulo.dispose();
        fontOpciones.dispose();
    }
}
