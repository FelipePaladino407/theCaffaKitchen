package Clases.Herramientas;

import javafx.scene.control.ProgressBar;

public class Freidora extends Herramienta {

    public Freidora(ProgressBar barra) {
        super("Freidora", 1, barra);  // nombre, cantidad disponible y barra
    }

    @Override
    public void dibujarProceso() {
        new Thread(() -> {
            try {
                ejecutarProcesoConBarra(progressBar, 8000, () ->
                        System.out.println("Proceso freidora terminado"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
