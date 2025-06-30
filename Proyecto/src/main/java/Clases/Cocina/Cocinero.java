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

    public void run() {
        while (true) {
            // 1) esperamos a que el jefe nos asigne un permiso
            try {
                pedidoSem.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // 2) recogemos el pedido (seguimos marcados como ocupados)
            Pedido p;
            synchronized(this) {
                p = pedidoActual;
            }

            // 3) lo procesamos (moverCallback, pedir herramienta, animar, liberar)
            procesarPedido(p);

            // 4) sólo ahora marcamos libre y borramos el pedido
            synchronized (this) {
                pedidoActual = null;
                ocupado = false;
            }

            // 5) avisamos al jefe
            jefeCocina.notificarCocineroLibre(p);
        }
    }




    private void procesarPedido(Pedido pedido) {
        try {
            // Paso 1: mover del jefe a la herramienta
            CountDownLatch latch1 = new CountDownLatch(1);
            moverCallback.accept(
                    nombre + "-Jefe->" + nombre + "-" + pedido.getHerramienta().getNombre(),
                    () -> {
                        actualizarEtiqueta.accept("Preparando " + pedido.getNombre());
                        latch1.countDown();
                    }
            );
            latch1.await();

            // Paso 2: usar la herramienta con timeout
            boolean ok = pedido.getHerramienta().pedir(3, TimeUnit.SECONDS);
            if (!ok) {
                actualizarEtiqueta.accept("Cliente impaciente: cancelado " + pedido.getNombre());
                return;
            }
            try {
                pedido.getHerramienta().dibujarProceso(pedido.getTiempo());
            } finally {
                pedido.getHerramienta().liberar();
            }

            // --- ***** AQUÍ VA LA FASE QUE FALTABA ***** ---
            // Paso 3: mover de la herramienta a la entrega
            CountDownLatch latch2 = new CountDownLatch(1);
            moverCallback.accept(
                    nombre
                            + "-"
                            + pedido.getHerramienta().getNombre()
                            + "->"
                            + nombre
                            + "-Entrega",
                    () -> {
                        actualizarEtiqueta.accept("Entregado " + pedido.getNombre());
                        latch2.countDown();
                    }
            );
            latch2.await();
            // -----------------------------------------------

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
