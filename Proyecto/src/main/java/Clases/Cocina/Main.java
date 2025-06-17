// Main.java
package Clases.Cocina;

import Clases.Herramientas.Herramienta;
import Clases.Herramientas.Horno;
import Clases.Herramientas.Parrilla;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Crear herramientas compartidas
        ArrayList<Herramienta> herramientas = new ArrayList<>();
        herramientas.add(new Horno());
        herramientas.add(new Parrilla());

        // Crear cocineros
        ArrayList<Cocinero> cocineros = new ArrayList<>();
        cocineros.add(new Cocinero("Juan"));
        cocineros.add(new Cocinero("Ana"));
        cocineros.add(new Cocinero("Camila"));
        cocineros.add(new Cocinero("Walter"));
        cocineros.add(new Cocinero("Angel"));


        for (Cocinero c : cocineros) {
            c.start();
        }

        // Crear jefe de cocina
        JefeCocina jefe = new JefeCocina(cocineros);

        // Lista de platos disponibles
        String[] platos = {"panchos", "milanesa", "asado", "pizza", "hamburguesa"};
        Random random = new Random();

        int i;
        for (i = 0; i<5; i++)
        {
            ArrayList<Herramienta> herramientasUsar = new ArrayList<>();
            if (random.nextBoolean()) herramientasUsar.add(herramientas.get(0)); // horno
            if (random.nextBoolean()) herramientasUsar.add(herramientas.get(1)); // parrilla

            if (herramientasUsar.isEmpty()) {
                herramientasUsar.add(herramientas.get(random.nextInt(2))); // asegurar al menos una herramienta
            }
            String plato = platos[i];
            Pedido pedido = new Pedido(plato, herramientasUsar);
            jefe.agregarPedido(pedido);
            try {
                Thread.sleep(1000); // Cada segundo llega un nuevo pedido
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}