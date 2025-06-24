package Clases.Herramientas;

import Clases.Interfaz.InterfazVisualSingleton;

public class Freidora extends Herramienta {
    public Freidora() {
        super("Freidora", 3);
    }
    @Override
    public void dibujarProceso() throws InterruptedException {
        ejecutarProceso(2000);  // 2 segundos de duración
    }
}