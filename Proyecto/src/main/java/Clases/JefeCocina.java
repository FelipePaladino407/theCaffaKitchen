package Clases;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JefeCocina {
    LinkedList<Cocinero> cocineros;
    LinkedList<Pedido> pedidosPendientes;

    public JefeCocina(ArrayList<Cocinero> chefs){
        cocineros=new LinkedList<>(chefs);
        pedidosPendientes=new LinkedList<>();
    }

    public void agregarPedido(Pedido nuevoPedido){
        pedidosPendientes.add(nuevoPedido);
    }

    public void comenzar(){
        while(true){
            for(Pedido pedido : pedidosPendientes){
                for(Cocinero cocinero: cocineros){
                    if(!cocinero.getOcupado()){
                        cocinero.setOcupado();
                        cocinero.cocinar(pedido);
                    }
                }
            }
        }
    }
}
