package Clases.Cocina;

import Clases.Herramientas.Herramienta;

import java.util.concurrent.Semaphore;


public class Cocinero extends Thread {
    private final String nombre;
    private Pedido pedidoActual;
    private final Semaphore semaforo = new Semaphore(0); // empieza bloqueado

    public Cocinero(String nombre) {
        this.nombre = nombre;
    }



    // El jefe llama a esto cuando tiene un pedido para este cocinero
    public void asignarPedido(Pedido pedido) {
        this.pedidoActual = pedido;
        semaforo.release();
    }

    public boolean estaOcupado()
    {
        if (semaforo.tryAcquire()) {
            semaforo.release();
            return true;
        }
        return false;
    }
    @Override
    public void run() {
        while (true) {
            try {
                semaforo.acquire();
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

            synchronized (this) {
                pedidoActual = null;
                semaforo.release();
            }
        }
    }
}