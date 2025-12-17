package com.punchline.hitlist.elementosJuego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class GestorSonidos {
    private static GestorSonidos instancia;

    private HashMap<SonidoDisponible, Sound> sonidos;
    private HashMap<SonidoDisponible, Music> musicas;

    private float volumenSonidos = 1f;
    private float volumenMusica = 0.5f;

    private Music musicaActual = null;
    // Variable para saber qué enum se está reproduciendo actualmente
    private SonidoDisponible idMusicaActual = null;

    private GestorSonidos() {
        sonidos = new HashMap<>();
        musicas = new HashMap<>();
    }

    public static GestorSonidos getInstancia() {
        if (instancia == null) {
            instancia = new GestorSonidos();
        }
        return instancia;
    }

    // --- CARGA ---
    public void cargarSonido(SonidoDisponible sonido) {
        if (!sonidos.containsKey(sonido)) {
            try {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(sonido.getRutaArchivo()));
                sonidos.put(sonido, sound);
            } catch (Exception e) {
                Gdx.app.error("Audio", "Error cargando sonido: " + sonido, e);
            }
        }
    }

    public void cargarMusica(SonidoDisponible musica) {
        if (!musicas.containsKey(musica)) {
            try {
                Music music = Gdx.audio.newMusic(Gdx.files.internal(musica.getRutaArchivo()));
                musicas.put(musica, music);
            } catch (Exception e) {
                Gdx.app.error("Audio", "Error cargando musica: " + musica, e);
            }
        }
    }

    // --- REPRODUCCION SONIDOS ---
    public void reproducirSonido(SonidoDisponible sonido) {
        Sound sound = sonidos.get(sonido);
        if (sound != null) {
            sound.play(volumenSonidos);
        }
    }

    public void reproducirSonido(SonidoDisponible sonido, float volumen) {
        Sound sound = sonidos.get(sonido);
        if (sound != null) {
            sound.play(volumen);
        }
    }

    // --- REPRODUCCION MUSICA (MEJORADO) ---
    public void reproducirMusica(SonidoDisponible musica, boolean loop) {
        Music music = musicas.get(musica);

        if (music != null) {
            if (musicaActual == music && musicaActual.isPlaying()) {
                return;
            }

            // Si es una canción diferente, detenemos la anterior
            detenerMusica();

            musicaActual = music;
            idMusicaActual = musica; // Guardamos cuál es
            music.setLooping(loop);
            music.setVolume(volumenMusica);
            music.play();
        }
    }

    public void pausarMusica() {
        if (musicaActual != null && musicaActual.isPlaying()) {
            musicaActual.pause();
        }
    }

    public void reanudarMusica() {
        if (musicaActual != null && !musicaActual.isPlaying()) {
            musicaActual.play();
        }
    }

    public void detenerMusica() {
        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual = null;
            idMusicaActual = null;
        }
    }

    // --- VOLUMEN ---
    public void setVolumenSonidos(float volumen) {
        this.volumenSonidos = Math.max(0f, Math.min(1f, volumen));
    }

    public void setVolumenMusica(float volumen) {
        this.volumenMusica = Math.max(0f, Math.min(1f, volumen));
        if (musicaActual != null) {
            musicaActual.setVolume(this.volumenMusica);
        }
    }

    public void dispose() {
        for (Sound sound : sonidos.values()) {
            if (sound != null) sound.dispose();
        }
        for (Music music : musicas.values()) {
            if (music != null) music.dispose();
        }
        sonidos.clear();
        musicas.clear();
        musicaActual = null;
    }
}
