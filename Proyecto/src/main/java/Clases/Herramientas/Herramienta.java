package Clases.Herramientas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public abstract class Herramienta implements IHerramienta {
    protected final String nombre;
    protected final java.util.concurrent.Semaphore disponibilidad;
    protected final ProgressBar progressBar;

    public Herramienta(String nombre, int cantidadDisponible, ProgressBar progressBar) {
        this.nombre = nombre;
        this.disponibilidad = new java.util.concurrent.Semaphore(cantidadDisponible);
        this.progressBar = progressBar;
    }

    @Override
    public void pedir() throws InterruptedException {
        disponibilidad.acquire();
    }

    @Override
    public void liberar() {
        disponibilidad.release();
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    /**
     * Simula el proceso con la barra de progreso durante un tiempo fijo (ej. 3 segundos)
     */
    @Override
    public void dibujarProceso(int duracionMs) throws InterruptedException {
        final Object lock = new Object();
        final boolean[] terminado = {false};

        Platform.runLater(() -> {
            progressBar.setProgress(0);
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> progressBar.setProgress(0)),
                    new KeyFrame(Duration.millis(duracionMs), e -> {
                        progressBar.setProgress(1);
                        synchronized (lock) {
                            terminado[0] = true;
                            lock.notify(); // ⚠️ IMPORTANTE: despierta al cocinero
                        }
                    })
            );
            timeline.setCycleCount(1);
            timeline.play();
        });

        synchronized (lock) {
            while (!terminado[0]) {
                lock.wait(); // ⏳ El cocinero se queda esperando acá
            }
        }
    }






    /**
     * Ejecuta la animación de la barra de progreso de forma asíncrona.
     *
     * @param barra     La ProgressBar a animar.
     * @param duracionMs Duración en milisegundos del proceso.
     * @param onFinished Callback que se ejecuta cuando termina la animación.
     */
    public void ejecutarProcesoConBarra(ProgressBar barra, int duracionMs, Runnable onFinished) {
        Platform.runLater(() -> {
            barra.setProgress(0);
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> barra.setProgress(0)),
                    new KeyFrame(Duration.millis(duracionMs), e -> {
                        barra.setProgress(1);
                        if (onFinished != null) onFinished.run();
                    })
            );
            timeline.setCycleCount(1);
            timeline.play();
        });
    }
}
