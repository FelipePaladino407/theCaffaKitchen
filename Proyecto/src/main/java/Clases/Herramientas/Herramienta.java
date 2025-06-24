package Clases.Herramientas;

import Clases.Interfaz.InterfazVisualSingleton;

import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public abstract class Herramienta implements IHerramienta {

    protected final String nombre;
    protected final Semaphore disponibilidad;

    // Atributos visuales individuales por herramienta
    private final JLabel estadoLabel;
    private final JProgressBar barraProgreso;

    public Herramienta(String nombre, int cantidadDisponible) {
        this.nombre = nombre;
        this.disponibilidad = new Semaphore(cantidadDisponible);
        this.estadoLabel = InterfazVisualSingleton.get().crearEstadoLabel(nombre);
        this.barraProgreso = InterfazVisualSingleton.get().crearBarraProgreso();
        InterfazVisualSingleton.get().agregarHerramientaVisual(nombre, estadoLabel, barraProgreso);
    }

    @Override
    public void pedir() throws InterruptedException {
        disponibilidad.acquire();

        SwingUtilities.invokeLater(() -> estadoLabel.setText(nombre + ": Ocupado"));
        InterfazVisualSingleton.get().log(nombre + " se está utilizando"); //Aviso que voy a usar
    }

    @Override
    public void liberar() {
        disponibilidad.release();
        SwingUtilities.invokeLater(() -> {
            estadoLabel.setText(nombre + ": Libre");
            barraProgreso.setValue(0);
        });
        InterfazVisualSingleton.get().log(nombre + " se ha liberado"); //Aviso por la interfaz
    }

    public void ejecutarProceso(int duracionTotalMs) throws InterruptedException {
        for (int i = 1; i <= 100; i++) {
            final int progreso = i;
            SwingUtilities.invokeLater(() -> barraProgreso.setValue(progreso)); //Actualizo la barra de cada herramienta
            Thread.sleep(duracionTotalMs / 100);
        }
        InterfazVisualSingleton.get().log(nombre + " terminó de cocinar.");
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public abstract void dibujarProceso() throws InterruptedException;
}