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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class InterfazFX extends Application {

    private Pane root;

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

        // 1. Crear la lista vacía de cocineros
        List<Cocinero> cocinerosTemp = new java.util.ArrayList<>();

// 2. Crear el jefe primero (aunque reciba una lista vacía)
        jefeCocina = new JefeCocina(cocinerosTemp);

// 3. Crear cocineros con referencia al jefe
        Cocinero cocineroJuan = new Cocinero("Juan", this::moverCocineroPorNombreDestino, texto -> actualizarEtiquetaPedido("Juan", texto), jefeCocina);
        Cocinero cocineroAna = new Cocinero("Ana", this::moverCocineroPorNombreDestino, texto -> actualizarEtiquetaPedido("Ana", texto), jefeCocina);
        Cocinero cocineroLuis = new Cocinero("Luis", this::moverCocineroPorNombreDestino, texto -> actualizarEtiquetaPedido("Luis", texto), jefeCocina);

// 4. Agregar a la lista
        cocinerosTemp.add(cocineroJuan);
        cocinerosTemp.add(cocineroAna);
        cocinerosTemp.add(cocineroLuis);

// 5. Guardar lista para uso general
        listaCocineros = cocinerosTemp;


        // Agregar pedidos al jefe, asignando herramienta aleatoria
        String[] platos = {"Pizza", "Hamburguesa", "Tacos", "Ensalada", "Pan de ajo"};
        for (int i = 0; i < 10; i++) {
            Herramienta herramienta = ThreadLocalRandom.current().nextBoolean() ? horno : parrilla;
            Pedido pedido = new Pedido(platos[i % platos.length] + " #" + (i + 1), herramienta);
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
        etiqueta.setTranslateY(-30);  // etiqueta arriba del círculo
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
            // Elimina esta línea que llama a onFinish inmediatamente:
            // onFinish.run();
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

    public static void main(String[] args) {
        launch(args);
    }
}
