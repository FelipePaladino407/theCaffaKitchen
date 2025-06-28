package Clases.Herramientas;

import javafx.scene.control.ProgressBar;

public class Chorizera extends Herramienta {
    private final ProgressBar barra;

    public Chorizera(ProgressBar barra) {
        super("Horno", 1);
        this.barra = barra;
    }

    @Override
    public void dibujarProceso() {
        new Thread(() -> {
            ejecutarProcesoConBarra(barra, 8000, () ->
                    System.out.println("Proceso horno terminado"));
        }).start();
    }
}

