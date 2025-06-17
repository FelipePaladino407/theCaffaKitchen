package Clases.Cocina;

import Clases.Herramientas.Herramienta;

import java.util.concurrent.Semaphore;


public class Cocinero extends Thread {
    private final String nombre;
    private Pedido pedidoActual;
    private final Semaphore semaforo = new Semaphore(0); // para esperar pedidos
    private boolean ocupado = false;

    public Cocinero(String nombre) {
        this.nombre = nombre;
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
                semaforo.acquire();  // espera a que le asignen un pedido
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            System.out.println(nombre + " está preparando " + pedidoActual.nombre);

            try {
                for (Herramienta h : pedidoActual.herramientas) {
                    h.pedir();
                    h.dibujarProceso();
                    h.liberar();
                }
                System.out.println(nombre + " terminó " + pedidoActual.nombre);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Marcar como libre para el jefe
            synchronized (this) {
                pedidoActual = null;
                ocupado = false;
            }
        }
    }
}
