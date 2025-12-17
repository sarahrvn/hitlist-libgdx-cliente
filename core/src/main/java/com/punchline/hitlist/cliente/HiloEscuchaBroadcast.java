package com.punchline.hitlist.cliente;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HiloEscuchaBroadcast implements Runnable {
    private static final int PUERTO_BROADCAST = 25566;
    private static final String MENSAJE_ESPERADO = "HITLIST_SERVER";

    private volatile boolean ejecutando = true;
    private volatile String ipServidorEncontrado = null;
    private volatile int puertoServidorEncontrado = -1;

    @Override
    public void run() {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(PUERTO_BROADCAST);
            socket.setBroadcast(true);

            byte[] buffer = new byte[256];
            System.out.println("Escuchando broadcasts en puerto " + PUERTO_BROADCAST + "...");

            while (ejecutando) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String mensaje = new String(packet.getData(), 0, packet.getLength()).trim();

                    // Formato esperado: "HITLIST_SERVER:25565"
                    if (mensaje.startsWith(MENSAJE_ESPERADO)) {
                        String[] partes = mensaje.split(":");

                        if (partes.length == 2) {
                            ipServidorEncontrado = packet.getAddress().getHostAddress();
                            puertoServidorEncontrado = Integer.parseInt(partes[1]);

                            System.out.println("SERVIDOR ENCONTRADO!");
                            System.out.println("   IP: " + ipServidorEncontrado);
                            System.out.println("   Puerto: " + puertoServidorEncontrado);

                            // Una vez encontrado, dejamos de escuchar
                            break;
                        }
                    }

                } catch (Exception e) {
                    if (ejecutando) {
                        System.err.println("Error recibiendo broadcast: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error iniciando escucha de broadcast: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    public void detener() {
        ejecutando = false;
    }

    public boolean servidorEncontrado() {
        return ipServidorEncontrado != null && puertoServidorEncontrado != -1;
    }

    public String getIpServidor() {
        return ipServidorEncontrado;
    }

    public int getPuertoServidor() {
        return puertoServidorEncontrado;
    }
}
