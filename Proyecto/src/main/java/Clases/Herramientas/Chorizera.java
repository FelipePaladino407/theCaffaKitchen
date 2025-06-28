package Clases.Herramientas;

import javafx.scene.control.ProgressBar;

public class Chorizera extends Herramienta {

    public Chorizera(ProgressBar barra) {
        super("Chorizera", 1, barra);  // nombre, cantidad disponible y barra
    }

    @Override
    public void dibujarProceso() {
        new Thread(() -> {
            try {
                ejecutarProcesoConBarra(progressBar, 8000, () ->
                        System.out.println("Proceso chorizera terminado"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
