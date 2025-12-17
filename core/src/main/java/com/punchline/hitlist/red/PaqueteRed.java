package com.punchline.hitlist.red;

import java.io.*;

public abstract class PaqueteRed implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoPaquete {
        CONEXION,
        DESCONEXION,
        INICIO_PARTIDA,
        INPUT_JUGADOR,
        ESTADO_JUEGO
    }

    public abstract TipoPaquete getTipo();

    public byte[] serializar() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        return bos.toByteArray();
    }

    public static PaqueteRed deserializar(byte[] datos) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(datos);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (PaqueteRed) ois.readObject();
    }
}
