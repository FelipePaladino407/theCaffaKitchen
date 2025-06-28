package Clases.Herramientas;

import javafx.scene.control.ProgressBar;

public class Horno extends Herramienta {

    public Horno(ProgressBar barra) {
        super("Horno", 1, barra);  // nombre, cantidad disponible y barra
    }

    @Override
    public void dibujarProceso() {
        new Thread(() -> {
            try {
                ejecutarProcesoConBarra(progressBar, 8000, () ->
                        System.out.println("Proceso horno terminado"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
