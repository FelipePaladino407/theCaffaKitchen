package Clases.Cocina;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Cocinero extends Thread {
    private volatile boolean ocupado = false;
    private Pedido pedidoActual;
    private String nombre;
    private BiConsumer<String, Runnable> moverCallback;
    private Consumer<String> etiquetaCallback;
    private JefeCocina jefeCocina; // 👉 NUEVO

    public Cocinero(String nombre,
                    BiConsumer<String, Runnable> moverCallback,
                    Consumer<String> etiquetaCallback,
                    JefeCocina jefeCocina) { // 👉 Constructor actualizado
        this.nombre = nombre;
        this.moverCallback = moverCallback;
        this.etiquetaCallback = etiquetaCallback;
        this.jefeCocina = jefeCocina;
    }

    public boolean estaOcupado() {
        return ocupado;
    }

    public synchronized void asignarPedido(Pedido pedido) {
        if (!ocupado) {
            this.pedidoActual = pedido;
            this.ocupado = true;
            notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                while (!ocupado) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            try {
                etiquetaCallback.accept("Cocinando: " + pedidoActual.getNombre());
                moverCocinero(nombre, pedidoActual.getHerramienta().getNombre());
                pedidoActual.getHerramienta().dibujarProceso();
                moverCocinero(nombre, "Entrega");
                etiquetaCallback.accept("");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            synchronized (this) {
                pedidoActual = null;
                ocupado = false;
            }

            // ✅ Notificar al jefe que este cocinero ya está libre
            jefeCocina.notificarCocineroLibre();
        }
    }

    private final Object lockMovimiento = new Object();
    private boolean movimientoTerminado = false;

    private void moverCocinero(String nombre, String destino) throws InterruptedException {
        movimientoTerminado = false;

        moverCallback.accept(nombre + "-" + destino, () -> {
            synchronized (lockMovimiento) {
                movimientoTerminado = true;
                lockMovimiento.notify();
            }
        });

        synchronized (lockMovimiento) {
            while (!movimientoTerminado) {
                lockMovimiento.wait();
            }
        }
    }
}
