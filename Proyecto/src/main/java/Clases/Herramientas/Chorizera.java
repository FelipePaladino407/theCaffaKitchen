package Clases.Herramientas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class Chorizera extends Herramienta {

    public Chorizera(ProgressBar barra) {
        super("Chorizera", 1, barra);
    }

    @Override
    public void dibujarProceso(int duracionMs) throws InterruptedException {
        super.dibujarProceso(duracionMs); // Usa la versión bloqueante
    }

}
