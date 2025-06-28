package Clases.Cocina;
import java.util.concurrent.CountDownLatch;


public class Cocinero extends Thread {
    private final String nombre;
    private final java.util.function.BiConsumer<String, Runnable> moverCallback;
    private final java.util.function.Consumer<String> actualizarEtiqueta;
    private final JefeCocina jefeCocina;

    private volatile boolean ocupado = false;
    private Pedido pedidoActual;

    public Cocinero(String nombre,
                    java.util.function.BiConsumer<String, Runnable> moverCallback,
                    java.util.function.Consumer<String> actualizarEtiqueta,
                    JefeCocina jefeCocina) {
        this.nombre = nombre;
        this.moverCallback = moverCallback;
        this.actualizarEtiqueta = actualizarEtiqueta;
        this.jefeCocina = jefeCocina;
    }

    public synchronized boolean estaOcupado() {
        return ocupado;
    }

    public synchronized void asignarPedido(Pedido pedido) {
        this.pedidoActual = pedido;
        this.ocupado = true;
        notify();  // Despierta al cocinero si está esperando
    }

    @Override
    public void run() {
        while (true) {
            Pedido pedidoAProcesar;

            synchronized (this) {
                while (pedidoActual == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                pedidoAProcesar = pedidoActual;
            }

            procesarPedido(pedidoAProcesar);

            synchronized (this) {
                pedidoActual = null;
                ocupado = false;
            }

            jefeCocina.notificarCocineroLibre(pedidoAProcesar);
        }
    }

    private void procesarPedido(Pedido pedido) {
        try {
            // Paso 1: Ir del cocinero al jefe y luego a la herramienta
            CountDownLatch latch1 = new CountDownLatch(1);
            moverCallback.accept(nombre + "-Jefe->" + nombre + "-" + pedido.getHerramienta().getNombre(), () -> {
                actualizarEtiqueta.accept("Preparando " + pedido.getNombre());
                latch1.countDown();
            });
            latch1.await(); // Esperar a que termine el movimiento

            // Paso 2: Usar herramienta
            pedido.getHerramienta().pedir();
            try {
                pedido.getHerramienta().dibujarProceso(pedido.getTiempo());
            } finally {
                pedido.getHerramienta().liberar();
            }

            // Paso 3: Ir a entregar
            CountDownLatch latch2 = new CountDownLatch(1);
            moverCallback.accept(nombre + "-" + pedido.getHerramienta().getNombre() + "->" + nombre + "-Entrega", () -> {
                actualizarEtiqueta.accept("Entregado " + pedido.getNombre());
                latch2.countDown();
            });
            latch2.await();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
