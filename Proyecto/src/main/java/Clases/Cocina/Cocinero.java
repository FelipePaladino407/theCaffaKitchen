package Clases.Cocina;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class Cocinero extends Thread {
    private final String nombre;
    private final java.util.function.BiConsumer<String, Runnable> moverCallback;
    private final java.util.function.Consumer<String> actualizarEtiqueta;
    private final JefeCocina jefeCocina;


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
        pedidoSem.release();   // Despierto al hilo si esta esperando.
    }


    @Override
    public void run() {
        while (true) {
            // 1) Espero a que el jefe me libere un permiso de pedido
            try {
                pedidoSem.acquire();
            } catch (InterruptedException e) {
                // Si me interrumpen aquí, limpio el flag y salgo del loop
                Thread.currentThread().interrupt();
                break;
            }

            // 2) Copio el pedido y marco libre
            Pedido p = pedidoActual;
            pedidoActual = null;
            ocupado = false;

            // 3) Proceso el pedido (tu método existente)
            procesarPedido(p);

            // 4) Aviso al jefe de que terminé
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
// AHORA: timeout = "cliente se fue", no re‑encolamos
            boolean obtuvo;
            try {
                obtuvo = pedido.getHerramienta().pedir(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (!obtuvo) {
                // No consiguió recurso en 3s: descartamos el pedido
                actualizarEtiqueta.accept("Cliente impaciente: cancelado " + pedido.getNombre());
                return;
            }

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
