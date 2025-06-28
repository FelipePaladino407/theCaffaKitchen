package Clases.Cocina;

import Clases.Herramientas.Herramienta;
import Clases.Interfaz.InterfazVisualSingleton;
import java.util.function.Consumer;


import java.util.concurrent.Semaphore;


public class Cocinero extends Thread {
    private final String nombre;
    private Pedido pedidoActual;
    private final Semaphore semaforo = new Semaphore(0); // para esperar pedidos
    private boolean ocupado = false;
    private final Consumer<String> movimientoCallback;

    public Cocinero(String nombre, Consumer<String> movimientoCallback) {

        this.nombre = nombre;
        this.movimientoCallback = movimientoCallback;
    }

    public synchronized boolean estaOcupado() {
        return ocupado;
    }

    // Llamado por el jefe cuando hay un pedido
    public synchronized void asignarPedido(Pedido pedido) {
        this.pedidoActual = pedido;
        this.ocupado = true;
        semaforo.release();  // despierta al cocinero
    }

    @Override
    public void run() {
        while (true) {
            try {
                semaforo.acquire();

                // Mover al cocinero al lugar de la herramienta
                movimientoCallback.accept(nombre + "-" + pedidoActual.herramienta.getNombre());
                Thread.sleep(1000);
                // Simular el trabajo (usar la herramienta)
                pedidoActual.herramienta.pedir();

                Thread proceso = new Thread(() -> {
                    try {
                        pedidoActual.herramienta.dibujarProceso();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                proceso.start();
                proceso.join();


                pedidoActual.herramienta.liberar();


                // Mover al cocinero de vuelta al jefe
                movimientoCallback.accept(nombre + "-Jefe");
                Thread.sleep(1000);

                synchronized (this) {
                    pedidoActual = null;
                    ocupado = false;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
