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

}
