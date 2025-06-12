package Clases.Herramientas;

import java.util.concurrent.Semaphore;

public abstract class Herramienta implements IHerramienta {

    protected final String nombre;
    protected final Semaphore disponibilidad;

    public Herramienta(String nombre, int cantidadDisponible) {
        this.nombre = nombre;
        this.disponibilidad = new Semaphore(cantidadDisponible);
    }

    /**
     * usar() es el equivalente a "p" que haciamos a papel.
     */
    @Override
    public void pedir() throws InterruptedException {
        disponibilidad.acquire();
        System.out.println(nombre + " se esta utilizando");

    }

    /**
     * liberar() es el equivalente a "v" que haciamos a papel.
     */
    @Override
    public void liberar(){
        disponibilidad.release();
        System.out.println(nombre + " se ha liberado");
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public abstract void dibujarProceso() throws InterruptedException;
}
