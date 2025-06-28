package Clases.Cocina;

import Clases.Interfaz.InterfazFX;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class JefeCocina {
    private final List<Cocinero> cocineros;
    private final Queue<Pedido> pedidosPendientes = new LinkedList<>();
    private InterfazFX interfaz;  // Añadido

    public JefeCocina(List<Cocinero> cocineros, InterfazFX interfaz) {
        this.cocineros = cocineros;
        this.interfaz = interfaz;
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

    public synchronized void notificarCocineroLibre(Pedido pedidoTerminado) {
        // Eliminar el pedido visualmente
        interfaz.eliminarPedido(pedidoTerminado.getNumeroPedido());

        // Intentar asignar nuevos pedidos
        asignarPedidos();
    }
}

