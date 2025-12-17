package com.punchline.hitlist.cliente;

import com.punchline.hitlist.red.PaqueteRed;
import com.punchline.hitlist.red.paquetes.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteHitlist {
    private DatagramSocket socket;
    private InetAddress ipServidor;
    private int puertoServidor = 25565;

    private int miId = -1;
    private volatile boolean conectado = false;

    private volatile PaqueteEstado ultimoEstado;
    private volatile PaqueteInicioPartida configuracionPartida;
    private volatile PaqueteTiempoSeleccion ultimoTiempoSeleccion;

    // Variables para detectar desconexión del servidor
    private volatile boolean servidorDesconectado = false;
    private volatile String razonDesconexion = "";
    private long ultimoEstadoRecibido = 0;
    private static final long TIMEOUT_SERVIDOR = 3000;

    // Hilo para enviar pings periódicos
    private Thread hiloPing;
    private volatile boolean enviandoPings = false;

    public boolean conectar(String ip) {
        try {
            socket = new DatagramSocket();
            ipServidor = InetAddress.getByName(ip);

            enviarPaquete(new PaqueteConexion(0, false));

            socket.setSoTimeout(2000);
            byte[] buffer = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);
            PaqueteRed respuesta = PaqueteRed.deserializar(packet.getData());

            if (respuesta instanceof PaqueteConexion) {
                PaqueteConexion pc = (PaqueteConexion) respuesta;
                if (pc.isAceptado()) {
                    this.miId = pc.getIdJugadorAsignado();
                    this.conectado = true;
                    this.ultimoEstadoRecibido = System.currentTimeMillis();

                    new Thread(this::escucharServidor, "Escucha-Servidor").start();
                    iniciarPing();

                    System.out.println("Conectado con ID: " + miId);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Fallo al conectar: " + e.getMessage());
        }
        return false;
    }

    public boolean conectarAutomatico(int timeoutSegundos) {
        System.out.println("Buscando servidor en la red local...");

        HiloEscuchaBroadcast escucha = new HiloEscuchaBroadcast();
        Thread hiloEscucha = new Thread(escucha, "Escucha-Broadcast");
        hiloEscucha.start();

        long tiempoInicio = System.currentTimeMillis();
        long tiempoMaximo = timeoutSegundos * 1000;

        while (!escucha.servidorEncontrado()) {
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

            if (tiempoTranscurrido > tiempoMaximo) {
                System.out.println("Tiempo de espera agotado");
                escucha.detener();
                return false;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                escucha.detener();
                return false;
            }
        }

        escucha.detener();
        String ipEncontrada = escucha.getIpServidor();

        System.out.println("Servidor encontrado en: " + ipEncontrada);
        System.out.println("Conectando...");

        return conectar(ipEncontrada);
    }

    private void escucharServidor() {
        byte[] buffer = new byte[4096];
        while (conectado && !servidorDesconectado) {
            try {
                if(socket.getSoTimeout() != 0) socket.setSoTimeout(0);

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                PaqueteRed p = PaqueteRed.deserializar(packet.getData());

                ultimoEstadoRecibido = System.currentTimeMillis();

                // Clasificar paquetes recibidos
                if (p instanceof PaqueteEstado) {
                    ultimoEstado = (PaqueteEstado) p;
                } else if (p instanceof PaqueteInicioPartida) {
                    configuracionPartida = (PaqueteInicioPartida) p;
                    System.out.println("Configuración de partida recibida!");
                } else if (p instanceof PaqueteTiempoSeleccion) {
                    ultimoTiempoSeleccion = (PaqueteTiempoSeleccion) p;
                } else if (p instanceof PaqueteDesconexion) {
                    PaqueteDesconexion pd = (PaqueteDesconexion) p;
                    manejarDesconexionServidor(pd.getRazon());
                }

            } catch (Exception e) {
                if(conectado && !servidorDesconectado) {
                    long tiempoSinRespuesta = System.currentTimeMillis() - ultimoEstadoRecibido;
                    if (tiempoSinRespuesta > TIMEOUT_SERVIDOR) {
                        manejarDesconexionServidor("TIMEOUT_SERVIDOR");
                    }
                }
            }
        }
    }

    private void iniciarPing() {
        enviandoPings = true;
        hiloPing = new Thread(() -> {
            while (enviandoPings && conectado) {
                try {
                    Thread.sleep(1000);
                    enviarPaquete(new PaquetePing(miId));
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.err.println("Error enviando ping: " + e.getMessage());
                }
            }
        }, "Ping-Thread");
        hiloPing.start();
    }

    private void manejarDesconexionServidor(String razon) {
        if (servidorDesconectado) return;

        servidorDesconectado = true;
        razonDesconexion = razon;
        conectado = false;

        System.out.println("Servidor desconectado: " + razon);

        enviandoPings = false;
        if (hiloPing != null) {
            hiloPing.interrupt();
        }
    }

    public void enviarInput(boolean arriba, boolean abajo, boolean izq, boolean der, boolean atacar, boolean agarrar) {
        if (!conectado || servidorDesconectado) return;
        PaqueteInput input = new PaqueteInput(miId, arriba, izq, der, atacar, agarrar);
        enviarPaquete(input);
    }

    // NUEVOS MÉTODOS PARA SELECCIÓN
    public void enviarPaqueteSeleccion(PaqueteRed paquete) {
        enviarPaquete(paquete);
    }

    private void enviarPaquete(PaqueteRed p) {
        try {
            byte[] data = p.serializar();
            DatagramPacket packet = new DatagramPacket(data, data.length, ipServidor, puertoServidor);
            socket.send(packet);
        } catch (Exception e) {
            if (conectado && !servidorDesconectado) {
                System.err.println("Error enviando paquete: " + e.getMessage());
            }
        }
    }

    public PaqueteEstado getUltimoEstado() { return ultimoEstado; }
    public PaqueteInicioPartida getConfiguracionPartida() { return configuracionPartida; }
    public PaqueteTiempoSeleccion getUltimoTiempoSeleccion() { return ultimoTiempoSeleccion; }
    public int getMiId() { return miId; }
    public boolean isServidorDesconectado() { return servidorDesconectado; }
    public String getRazonDesconexion() { return razonDesconexion; }

    public void desconectar() {
        if (conectado) {
            System.out.println("Desconectando del servidor...");
            enviarPaquete(new PaqueteDesconexion(miId, "VOLUNTARIA"));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        conectado = false;
        enviandoPings = false;

        if (hiloPing != null) {
            hiloPing.interrupt();
        }

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }

        System.out.println("Desconectado");
    }
}
