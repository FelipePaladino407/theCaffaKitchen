package Clases.Cocina;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class JefeCocina {
    private final List<Cocinero> cocineros;
    private final Queue<Pedido> pedidosPendientes = new LinkedList<>();

    public JefeCocina(List<Cocinero> cocineros) {
        this.cocineros = cocineros;
    }

    // Método sincronizado que agrega pedido y asigna en la misma operación
    public synchronized void agregarPedido(Pedido pedido) {
        pedidosPendientes.add(pedido);
        asignarPedidos();
    }

    // Método privado sincronizado para asignar pedidos a cocineros libres
    private synchronized void asignarPedidos() {
        for (Cocinero cocinero : cocineros) {
            if (!cocinero.estaOcupado() && !pedidosPendientes.isEmpty()) {
                Pedido pedido = pedidosPendientes.poll();
                cocinero.asignarPedido(pedido);
            }
        }
    }

    // Podrías agregar un método para que los cocineros notifiquen cuando terminan y
    // se pueda intentar asignar nuevos pedidos
    public synchronized void notificarCocineroLibre() {
        asignarPedidos();
    }
}
