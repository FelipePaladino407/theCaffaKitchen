package Clases;
import java.util.ArrayList;

public class Pedido {
    public String nombre;
    public ArrayList<Herramienta> herramientas;
    public int tiempo;
    public Pedido(String nombre, ArrayList<Herramienta> herramientas, int tiempo) {
        this.nombre = nombre;
        this.herramientas = herramientas;
        this.tiempo = tiempo;
    }


}
