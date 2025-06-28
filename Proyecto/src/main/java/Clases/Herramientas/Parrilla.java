package Clases.Herramientas;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

public class Parrilla extends Herramienta {
    private final ProgressBar barra;

    public Parrilla(ProgressBar barra) {
        super("Parrilla", 1);
        this.barra = barra;
    }

    @Override
    public void dibujarProceso() throws InterruptedException {
        final Object lock = new Object();

        Platform.runLater(() -> {
            ejecutarProcesoConBarra(barra, 3000, () -> {
                synchronized (lock) {
                    lock.notify();
                }
            });
        });

        synchronized (lock) {
            lock.wait(); // El cocinero se queda esperando aquí hasta que termine la barra
        }
    }

}

