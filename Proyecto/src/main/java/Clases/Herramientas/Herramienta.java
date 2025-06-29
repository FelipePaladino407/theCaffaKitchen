package Clases.Herramientas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class Herramienta implements IHerramienta {
    protected final String nombre;
    protected final Semaphore disponibilidad;
    protected final ProgressBar progressBar;

    public Herramienta(String nombre, int cantidadDisponible, ProgressBar progressBar) {
        this.nombre = nombre;
        /**
         * Activo el fairness en el semáforo.
         */
        this.disponibilidad = new Semaphore(cantidadDisponible, true);
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
     * Devuelve cuantos recursos, ya sea "Horno", "Parrilla"... Hay disponibles en el momento.
     */
    public int getPermitsAvailable() {
        return disponibilidad.availablePermits();
    }
    /**
     * Devuelve cuántos hilos están actualmente en cola esperando un permiso.
     */
    public int getQueueLength() {
        return disponibilidad.getQueueLength();
    }

    public boolean pedir(long timeout, TimeUnit unit) throws InterruptedException {
        return disponibilidad.tryAcquire(timeout, unit);
    }



    /**
     * Simula el proceso con la barra de progreso durante un tiempo fijo (ej. 3 segundos)
     */
    @Override
    public void dibujarProceso(int duracionMs) throws InterruptedException {
        final Object lock = new Object();  // Lock de espera
        final boolean[] terminado = {false};  // Bandera de finalización

        Platform.runLater(() -> {
            progressBar.setProgress(0);

            int steps = 100;
            double increment = 1.0 / steps;
            double msPerStep = duracionMs / (double) steps;

            Timeline timeline = new Timeline();
            for (int i = 1; i <= steps; i++) {
                final double progress = increment * i;
                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(i * msPerStep), e -> progressBar.setProgress(progress))
                );
            }

            timeline.setCycleCount(1);
            timeline.setOnFinished(e -> {
                synchronized (lock) {
                    terminado[0] = true;
                    lock.notify();  // Notificamos al hilo del cocinero
                }
            });
            timeline.play();
        });

        synchronized (lock) {
            while (!terminado[0]) {
                lock.wait();  // Esperamos hasta que se termine la animación
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
