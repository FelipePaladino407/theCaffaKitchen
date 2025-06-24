package Clases.Herramientas;

public class Horno extends Herramienta {
    public Horno() {
        super("Horno", 2);
        // Horno: capacidad 2. Osea hay dos hornetes.
    }
    @Override
    public void dibujarProceso() throws InterruptedException {
        ejecutarProceso(5000);  // 5 segundos de duración
    }
}