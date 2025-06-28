package Clases.Cocina;
import Clases.Herramientas.Herramienta;

import java.util.ArrayList;

public class Pedido {
    public String nombre;
    public Herramienta herramienta;
    public int tiempo;
    public Pedido(String nombre, Herramienta herramienta, int tiempo) {
        this.nombre = nombre;
        this.herramienta = herramienta;
        this.tiempo=tiempo;
    }
    public String getNombre() { return nombre; }
    public Herramienta getHerramienta() { return herramienta; }
}