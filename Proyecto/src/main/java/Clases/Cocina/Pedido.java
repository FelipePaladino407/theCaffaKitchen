package Clases.Cocina;
import Clases.Herramientas.Herramienta;

import java.util.ArrayList;

public class Pedido {
    public String nombre;
    public ArrayList<Herramienta> herramientas;
    public Pedido(String nombre, ArrayList<Herramienta> herramientas) {
        this.nombre = nombre;
        this.herramientas = herramientas;
    }

}
