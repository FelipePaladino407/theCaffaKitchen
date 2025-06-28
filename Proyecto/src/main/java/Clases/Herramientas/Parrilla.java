package Clases.Herramientas;

import javafx.scene.control.ProgressBar;

public class Parrilla extends Herramienta {

    public Parrilla(ProgressBar barra) {
        super("Parrilla", 1, barra);  // nombre, cantidad disponible y barra
    }

    @Override
    public void dibujarProceso() {
        new Thread(() -> {
            try {
                ejecutarProcesoConBarra(progressBar, 8000, () ->
                        System.out.println("Proceso parrilla terminado"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
