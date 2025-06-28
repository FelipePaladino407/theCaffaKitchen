package Clases.Herramientas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public abstract class Herramienta implements IHerramienta {
    protected final String nombre;
    protected final java.util.concurrent.Semaphore disponibilidad;

    public Herramienta(String nombre, int cantidadDisponible) {
        this.nombre = nombre;
        this.disponibilidad = new java.util.concurrent.Semaphore(cantidadDisponible);
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

    public abstract void dibujarProceso() throws InterruptedException;

    public void ejecutarProcesoConBarra(ProgressBar barra, int duracionMs, Runnable onFinished) {
        Platform.runLater(() -> barra.setProgress(0));

        Timeline timeline = new Timeline();
        int steps = 100;
        double intervalo = duracionMs / (double) steps;

        for (int i = 1; i <= steps; i++) {
            final double progreso = i / 100.0;
            KeyFrame frame = new KeyFrame(Duration.millis(i * intervalo), e ->
                    barra.setProgress(progreso));
            timeline.getKeyFrames().add(frame);
        }

        timeline.setOnFinished(e -> {
            barra.setProgress(0); // Reiniciar barra para próximo uso
            if (onFinished != null) onFinished.run();
        });

        Platform.runLater(timeline::play);
    }
}
