package Clases;

import Clases.Herramientas.Herramienta;

import java.util.List;

public class Cocinero {
    private String nombre;
    private Boolean ocupado;
    private Pedido pedidoActual;

    public Cocinero(String nombre, Boolean ocupado) {
        this.nombre = nombre;
        this.ocupado = ocupado;
    }

    public Boolean estaOcupado() {
        return ocupado;
    }

    public void ocupar() {
        ocupado = true;
    }

    public void liberar(){
        ocupado = false;
    }

    public void cocinar()
    {
        List<Herramienta> herramientas = pedidoActual.herramientas;
        for (Herramienta h : herramientas)
        {
            //estacion.adquirir()
            //Muestro como cocino()
            //Pasa x tiempo
            //Entrego la comida (animación)
            //estacion.liberar()
        }
    }
    public void asignarPedidos(Pedido pedido)
    {
        pedidoActual = pedido;
        //usar threat de cocinar.start()
    }
}
