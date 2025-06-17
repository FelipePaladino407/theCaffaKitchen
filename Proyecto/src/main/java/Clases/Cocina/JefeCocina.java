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

    public synchronized void agregarPedido(Pedido pedido) {
        pedidosPendientes.add(pedido);
        asignarPedidos();
    }

    private synchronized void asignarPedidos() {
        for (Cocinero cocinero : cocineros) {
            if (!cocinero.estaOcupado() && !pedidosPendientes.isEmpty()) {
                Pedido pedido = pedidosPendientes.poll();
                cocinero.asignarPedido(pedido);
            }
        }
    }
}