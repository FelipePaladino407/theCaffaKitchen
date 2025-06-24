package Clases.Herramientas;

import Clases.Interfaz.InterfazVisualSingleton;

public class Chorizera extends Herramienta {
    public Chorizera() {
        super("Chorizera", 2);
    }
    @Override
    public void dibujarProceso() throws InterruptedException {
        ejecutarProceso(8000);  // 8 segundos de duración
    }
}