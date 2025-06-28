package Clases.Herramientas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class Parrilla extends Herramienta {

    public Parrilla(ProgressBar barra) {
        super("Parrilla", 1, barra);  // nombre, cantidad disponible y barra
    }

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
                            lock.notify();
                        }
                    })
            );
            timeline.setCycleCount(1);
            timeline.play();
        });

        synchronized (lock) {
            while (!terminado[0]) {
                lock.wait();
            }
        }
    }
}
