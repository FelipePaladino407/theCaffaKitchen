package Clases.Cocina;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class Cocinero extends Thread {
    private final String nombre;
    private final java.util.function.BiConsumer<String, Runnable> moverCallback;
    private final java.util.function.Consumer<String> actualizarEtiqueta;
    private final JefeCocina jefeCocina;

    /** NUEVO: Semaforo que despierta al chef cuando hay pedido*/
    private final Semaphore pedidoSem = new Semaphore(0, true);

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

    public void asignarPedido(Pedido pedido) {
        this.pedidoActual = pedido;
        this.ocupado = true;
        pedidoSem.release();
    }

    @Override
    public void run() {
        while (true) {
            try {
                pedidoSem.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            Pedido p = pedidoActual;
            pedidoActual = null; ocupado = false;
            procesarPedido(p);
            jefeCocina.notificarCocineroLibre(p);
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

            // Paso 2: intentar con timeout (3s)
            boolean ok;
            try {
                ok = pedido.getHerramienta().pedir(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (!ok) {
                actualizarEtiqueta.accept("Cliente impaciente: cancelado " + pedido.getNombre());
                return;
            }
            try {
                pedido.getHerramienta().dibujarProceso(pedido.getTiempo());
            } finally {
                pedido.getHerramienta().liberar();
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
