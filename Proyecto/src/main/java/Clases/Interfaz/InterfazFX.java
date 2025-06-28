package Clases.Interfaz;

import Clases.Cocina.Cocinero;
import Clases.Cocina.JefeCocina;
import Clases.Cocina.Pedido;
import Clases.Herramientas.Horno;
import Clases.Herramientas.Parrilla;
import Clases.Herramientas.Herramienta;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class InterfazFX extends Application {

    private Pane root;
    private static final int MAX_PEDIDOS_VISIBLES = 5;
    private final Queue<Pedido> colaPendientes = new LinkedList<>();
    private final Map<Integer, PedidoCard> tarjetasVisibles = new HashMap<>();


    private final Map<String, Point2D> ubicaciones = Map.of(
            "Horno", new Point2D(300, 100),
            "Parrilla", new Point2D(500, 100),
            "Entrega", new Point2D(650, 250),
            "Jefe", new Point2D(100, 300)
    );

    private final Map<String, StackPane> cocineros = new HashMap<>();
    private final Map<String, Label> etiquetasPedidos = new HashMap<>();
    private final Map<String, ProgressBar> barrasProgreso = new HashMap<>();

    private List<Cocinero> listaCocineros;
    private JefeCocina jefeCocina;
    private Horno horno;
    private Parrilla parrilla;

    private VBox contenedorPedidos;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();

        Image fondo = new Image("cocina.jpg");
        ImageView fondoView = new ImageView(fondo);
        fondoView.setFitWidth(800);
        fondoView.setFitHeight(600);
        root.getChildren().add(fondoView);

        Circle marcadorEntrega = new Circle(650, 250, 10, Color.YELLOW);
        marcadorEntrega.setStroke(Color.BLACK);
        root.getChildren().add(marcadorEntrega);

        ProgressBar barraHorno = agregarHerramienta("Horno", "horno.jpg");
        ProgressBar barraParrilla = agregarHerramienta("Parrilla", "parrilla.png");

        horno = new Horno(barraHorno);
        parrilla = new Parrilla(barraParrilla);

        agregarCocinero("Juan", Color.RED);
        agregarCocinero("Ana", Color.BLUE);
        agregarCocinero("Luis", Color.GREEN);

        // Inicializar contenedorPedidos ANTES de agregar pedidos
        contenedorPedidos = new VBox(10); // espaciado horizontal de 10px
        // espaciado vertical de 10px
        contenedorPedidos.setPadding(new Insets(10));
        contenedorPedidos.setLayoutX(50);
        contenedorPedidos.setLayoutY(400);
        root.getChildren().add(contenedorPedidos);

        // Crear lista vacía y jefe
        List<Cocinero> cocinerosTemp = new java.util.ArrayList<>();
        jefeCocina = new JefeCocina(cocinerosTemp,this);

        // Crear cocineros con referencia al jefe
        Cocinero cocineroJuan = new Cocinero("Juan", this::moverCocineroPorNombreDestino, texto -> actualizarEtiquetaPedido("Juan", texto), jefeCocina);
        Cocinero cocineroAna = new Cocinero("Ana", this::moverCocineroPorNombreDestino, texto -> actualizarEtiquetaPedido("Ana", texto), jefeCocina);
        Cocinero cocineroLuis = new Cocinero("Luis", this::moverCocineroPorNombreDestino, texto -> actualizarEtiquetaPedido("Luis", texto), jefeCocina);

        // Agregar cocineros a la lista
        cocinerosTemp.add(cocineroJuan);
        cocinerosTemp.add(cocineroAna);
        cocinerosTemp.add(cocineroLuis);

        listaCocineros = cocinerosTemp;
        Herramienta[] herramientas= {horno,parrilla};
        // Agregar pedidos al jefe con herramienta aleatoria y mostrar tarjetas
        String[] platos = {"Pizza", "Hamburguesa", "Tacos", "Ensalada", "Pan de ajo"};
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Pedido pedido = new Pedido(platos[random.nextInt(platos.length)],herramientas[random.nextInt(herramientas.length)], random.nextInt(1000,2000),i+1 );
            jefeCocina.agregarPedido(pedido);
        }

        // Iniciar threads de cocineros
        for (Cocinero c : listaCocineros) {
            c.start();
        }

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cocina estilo Overcooked");
        primaryStage.show();
    }

    private ProgressBar agregarHerramienta(String nombre, String rutaImagen) {
        Point2D pos = ubicaciones.get(nombre);
        ImageView img = new ImageView(new Image(rutaImagen));
        img.setFitWidth(64);
        img.setFitHeight(64);

        ProgressBar barra = new ProgressBar(0);
        barra.setPrefWidth(64);
        barra.setPrefHeight(10);
        barra.setTranslateY(-10);
        barra.setStyle("-fx-accent: #ff4500;");

        StackPane stack = new StackPane(img, barra);
        stack.setLayoutX(pos.getX());
        stack.setLayoutY(pos.getY());

        root.getChildren().add(stack);
        barrasProgreso.put(nombre, barra);

        return barra;
    }

    private void agregarCocinero(String nombre, Color color) {
        Circle cocinero = new Circle(15, color);
        cocinero.setStroke(Color.BLACK);
        cocinero.setStrokeWidth(2);

        Label etiqueta = new Label("");
        etiqueta.setTranslateY(-30);
        etiqueta.setStyle("-fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-background-color: rgba(0,0,0,0.6); -fx-padding: 2 5 2 5; " +
                "-fx-background-radius: 5;");

        StackPane stack = new StackPane(cocinero, etiqueta);
        Point2D inicio = ubicaciones.get("Jefe");
        stack.setLayoutX(inicio.getX());
        stack.setLayoutY(inicio.getY());

        root.getChildren().add(stack);
        cocineros.put(nombre, stack);
        etiquetasPedidos.put(nombre, etiqueta);
    }

    public void actualizarEtiquetaPedido(String nombreCocinero, String texto) {
        Platform.runLater(() -> {
            Label etiqueta = etiquetasPedidos.get(nombreCocinero);
            if (etiqueta != null) {
                etiqueta.setText(texto);
            }
        });
    }

    private void moverCocinero(String nombre, String destino, Runnable onFinish) {
        StackPane stack = cocineros.get(nombre);
        Point2D objetivo = ubicaciones.get(destino);

        if (stack != null && objetivo != null) {
            TranslateTransition tt = new TranslateTransition(Duration.seconds(1), stack);
            double actualX = stack.getLayoutX() + stack.getTranslateX();
            double actualY = stack.getLayoutY() + stack.getTranslateY();
            double dx = objetivo.getX() - actualX;
            double dy = objetivo.getY() - actualY;

            tt.setToX(dx);
            tt.setToY(dy);

            tt.setOnFinished(e -> {
                stack.setLayoutX(objetivo.getX());
                stack.setLayoutY(objetivo.getY());
                stack.setTranslateX(0);
                stack.setTranslateY(0);
                if (onFinish != null) onFinish.run();
            });

            if (dx == 0 && dy == 0) {
                if (onFinish != null) onFinish.run();
            } else {
                tt.play();
            }
        }
    }

    public void moverCocineroPorNombreDestino(String mensaje, Runnable onFinish) {
        Platform.runLater(() -> {
            String[] partes = mensaje.split("->");
            if (partes.length == 2) {
                moverCocineroEncadenado(partes[0], partes[1], onFinish);
            } else {
                String[] datos = mensaje.split("-");
                if (datos.length != 2) return;
                String nombre = datos[0];
                String destino = datos[1];
                moverCocinero(nombre, destino, onFinish);
            }
        });
    }

    private void moverCocineroEncadenado(String paso1, String paso2, Runnable onFinish) {
        String[] datos1 = paso1.split("-");
        String[] datos2 = paso2.split("-");
        if (datos1.length != 2 || datos2.length != 2) return;

        String nombre = datos1[0];
        String destino1 = datos1[1];
        String destino2 = datos2[1];

        StackPane stack = cocineros.get(nombre);
        Point2D objetivo1 = ubicaciones.get(destino1);
        Point2D objetivo2 = ubicaciones.get(destino2);

        if (stack == null || objetivo1 == null || objetivo2 == null) return;

        double actualX = stack.getLayoutX() + stack.getTranslateX();
        double actualY = stack.getLayoutY() + stack.getTranslateY();

        double dx1 = objetivo1.getX() - actualX;
        double dy1 = objetivo1.getY() - actualY;

        TranslateTransition tt1 = new TranslateTransition(Duration.seconds(3), stack);
        tt1.setToX(dx1);
        tt1.setToY(dy1);

        tt1.setOnFinished(e1 -> {
            stack.setLayoutX(objetivo1.getX());
            stack.setLayoutY(objetivo1.getY());
            stack.setTranslateX(0);
            stack.setTranslateY(0);

            double dx2 = objetivo2.getX() - stack.getLayoutX();
            double dy2 = objetivo2.getY() - stack.getLayoutY();

            TranslateTransition tt2 = new TranslateTransition(Duration.seconds(1), stack);
            tt2.setToX(dx2);
            tt2.setToY(dy2);

            tt2.setOnFinished(e2 -> {
                stack.setLayoutX(objetivo2.getX());
                stack.setLayoutY(objetivo2.getY());
                stack.setTranslateX(0);
                stack.setTranslateY(0);
                if (onFinish != null) onFinish.run();
            });

            if (dx2 == 0 && dy2 == 0) {
                if (onFinish != null) onFinish.run();
            } else {
                tt2.play();
            }
        });

        if (dx1 == 0 && dy1 == 0) {
            tt1.getOnFinished().handle(null);
        } else {
            tt1.play();
        }
    }

    // Recibe la cola completa de pedidos pendientes y actualiza las tarjetas visibles
    public void actualizarPedidosVisibles(Queue<Pedido> colaPedidos) {
        colaPendientes.clear();
        colaPendientes.addAll(colaPedidos);

        // Limpiar las tarjetas actuales
        contenedorPedidos.getChildren().clear();

        // Mostrar solo hasta 5 pedidos visibles
        int maxVisibles = 5;
        int count = 0;

        for (Pedido pedido : colaPendientes) {
            if (count >= maxVisibles) break;

            StackPane tarjetaPedido = crearTarjetaPedido(pedido);
            contenedorPedidos.getChildren().add(tarjetaPedido);
            count++;
        }
    }


    // Añade una tarjeta visual para un pedido
    private void agregarTarjetaPedido(Pedido pedido) {
        Image imagen = new Image("pizza.jpg"); // O el que corresponda
        PedidoCard nuevaTarjeta = new PedidoCard(pedido.getNumeroPedido(), pedido.getNombre(), "pendiente", imagen);
        contenedorPedidos.getChildren().add(nuevaTarjeta);
        tarjetasVisibles.put(pedido.getNumeroPedido(), nuevaTarjeta);
    }

    // Elimina la tarjeta y actualiza la lista visible para mostrar siguiente si hay
    public void eliminarPedido(int numeroPedido) {
        StackPane tarjetaAEliminar = null;

        for (javafx.scene.Node nodo : contenedorPedidos.getChildren()) {
            if (nodo instanceof StackPane) {
                StackPane tarjeta = (StackPane) nodo;

                Label etiqueta = null;
                for (javafx.scene.Node hijo : tarjeta.getChildren()) {
                    if (hijo instanceof Label) {
                        etiqueta = (Label) hijo;
                        break;
                    }
                }

                if (etiqueta != null && etiqueta.getText().contains("#" + numeroPedido)) {
                    tarjetaAEliminar = tarjeta;
                    break;
                }
            }
        }

        if (tarjetaAEliminar != null) {
            contenedorPedidos.getChildren().remove(tarjetaAEliminar);
        }

        if (!colaPendientes.isEmpty()) {
            colaPendientes.removeIf(p -> p.getNumeroPedido() == numeroPedido);
            actualizarPedidosVisibles(new LinkedList<>(colaPendientes));
        }
    }



    // Llamado desde JefeCocina cuando se asigna un pedido para que no aparezca en la lista visible
    public void quitarPedidoDeCola(int idPedido) {
        colaPendientes.removeIf(p -> p.getNumeroPedido() == idPedido);
    }

    private StackPane crearTarjetaPedido(Pedido pedido) {
        StackPane tarjeta = new StackPane();
        tarjeta.setPrefSize(200, 60);
        tarjeta.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 10;");

        // Imagen (elige la que quieras, aquí ejemplo genérico)
        ImageView imagen = new ImageView(new Image("pizza.jpg"));
        imagen.setFitWidth(40);
        imagen.setFitHeight(40);
        imagen.setTranslateX(-70);

        // Texto con nombre y número de pedido
        Label texto = new Label("#" + pedido.getNumeroPedido() + " - " + pedido.getNombre());
        texto.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        tarjeta.getChildren().addAll(imagen, texto);
        StackPane.setMargin(texto, new Insets(0, 0, 0, 30));

        return tarjeta;
    }

    private Image getImagenPorNombre(String nombre) {
        switch(nombre.toLowerCase()) {
            case "pizza": return new Image("pizza.jpg");
            case "hamburguesa": return new Image("pizza.jpg");
            case "tacos": return new Image("pizza.jpg");
            case "ensalada": return new Image("pizza.jpg");
            case "pan de ajo": return new Image("pizza.jpg");
            default: return new Image("pizza.jpg");
        }
    }







    public static void main(String[] args) {
        launch(args);
    }


}
