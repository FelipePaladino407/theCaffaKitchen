package Clases.Cocina;
import Clases.Herramientas.Herramienta;

import java.util.ArrayList;

public class Pedido {
    public String nombre;
    public Herramienta herramienta;
    public Pedido(String nombre, Herramienta herramienta) {
        this.nombre = nombre;
        this.herramienta = herramienta;
    }
    public String getNombre() { return nombre; }
    public Herramienta getHerramienta() { return herramienta; }
}