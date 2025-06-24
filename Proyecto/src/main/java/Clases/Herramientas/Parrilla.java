package Clases.Herramientas;

import Clases.Interfaz.InterfazVisualSingleton;

public class Parrilla extends Herramienta {
    public Parrilla() {
        super("La Parrilla de Hugo", 1);
    }
    @Override
    public void dibujarProceso() throws InterruptedException {
        ejecutarProceso(1000);  // 5 segundos de duración
    }
}