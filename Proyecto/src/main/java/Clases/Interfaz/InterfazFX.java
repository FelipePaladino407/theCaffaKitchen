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
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class InterfazFX extends Application {

    private Pane root;
    private Map<String, Point2D> ubicaciones = Map.of(
            "Horno", new Point2D(300, 100),
            "Parrilla", new Point2D(500, 100),
            "Jefe", new Point2D(100, 300)
    );
    private Map<String, Circle> cocineros = new HashMap<>();
    private Map<String, ProgressBar> barrasProgreso = new HashMap<>();

    private List<Cocinero> listaCocineros;
    private JefeCocina jefeCocina;
    private Horno horno;
    private Parrilla parrilla;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();

        // Fondo de la cocina
        Image fondo = new Image("cocina.jpg");
        ImageView fondoView = new ImageView(fondo);
        fondoView.setFitWidth(800);
        fondoView.setFitHeight(600);
        root.getChildren().add(fondoView);

        // Agregar herramientas visuales con barras de progreso y obtener esas barras
        ProgressBar barraHorno = agregarHerramienta("Horno", "horno.jpg");
        ProgressBar barraParrilla = agregarHerramienta("Parrilla", "parrilla.png");

        // Crear herramientas con sus barras asociadas
        horno = new Horno(barraHorno);
        parrilla = new Parrilla(barraParrilla);

        // Agregar cocineros visuales
        agregarCocinero("Juan", Color.RED);
        agregarCocinero("Ana", Color.BLUE);
        agregarCocinero("Luis", Color.GREEN);

        // Crear cocineros con callback visual para moverlos
        listaCocineros = new ArrayList<>();
        listaCocineros.add(new Cocinero("Juan", this::moverCocineroPorNombreDestino));
        listaCocineros.add(new Cocinero("Ana", this::moverCocineroPorNombreDestino));
        listaCocineros.add(new Cocinero("Luis", this::moverCocineroPorNombreDestino));

        // Crear jefe de cocina con lista de cocineros
        jefeCocina = new JefeCocina(listaCocineros);

        // Crear y asignar pedidos aleatorios
        String[] platos = {"Pizza", "Hamburguesa", "Tacos", "Ensalada", "Pan de ajo"};
        for (int i = 0; i < 10; i++) {
            Herramienta herramienta = ThreadLocalRandom.current().nextBoolean() ? horno : parrilla;
            Pedido pedido = new Pedido(platos[i % platos.length] + " #" + (i + 1), herramienta);
            jefeCocina.agregarPedido(pedido);
        }

        // Lanzar cocineros
        for (Cocinero c : listaCocineros) {
            c.start();
        }

        // Mostrar escena al final
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cocina estilo Overcooked");
        primaryStage.show();

    }

    /**
     * Crea la imagen de la herramienta con su barra de progreso arriba y la añade al root.
     * Devuelve la barra creada para asignarla a la herramienta.
     */
    private ProgressBar agregarHerramienta(String nombre, String rutaImagen) {
        Point2D pos = ubicaciones.get(nombre);

        ImageView img = new ImageView(new Image(rutaImagen));
        img.setFitWidth(64);
        img.setFitHeight(64);

        ProgressBar barraProgreso = new ProgressBar(0);
        barraProgreso.setPrefWidth(64);
        barraProgreso.setPrefHeight(10);
        barraProgreso.setTranslateY(-10);
        barraProgreso.setStyle("-fx-accent: #ff4500;");

        StackPane stack = new StackPane(img, barraProgreso);
        stack.setLayoutX(pos.getX());
        stack.setLayoutY(pos.getY());

        root.getChildren().add(stack);
        barrasProgreso.put(nombre, barraProgreso);

        return barraProgreso;
    }

    private void agregarCocinero(String nombre, Color color) {
        Circle cocinero = new Circle(15, color);
        cocinero.setStroke(Color.BLACK);
        cocinero.setStrokeWidth(2);
        Point2D inicio = ubicaciones.get("Jefe");
        cocinero.setLayoutX(inicio.getX());
        cocinero.setLayoutY(inicio.getY());
        root.getChildren().add(cocinero);
        cocineros.put(nombre, cocinero);
    }

    private void moverCocinero(String nombre, String destino) {
        Circle cocinero = cocineros.get(nombre);
        Point2D objetivo = ubicaciones.get(destino);

        if (cocinero != null && objetivo != null) {
            TranslateTransition tt = new TranslateTransition(Duration.seconds(1), cocinero);

            double dx = objetivo.getX() - cocinero.getLayoutX();
            double dy = objetivo.getY() - cocinero.getLayoutY();

            tt.setToX(dx);
            tt.setToY(dy);

            tt.setOnFinished(e -> {
                // Actualizar la posición base y resetear la animación
                cocinero.setLayoutX(objetivo.getX());
                cocinero.setLayoutY(objetivo.getY());
                cocinero.setTranslateX(0);
                cocinero.setTranslateY(0);
            });

            tt.play(); // 👈 esto es lo que hacía falta
        }
    }


    /**
     * Recibe mensajes como "Juan-Horno" o "Ana-Jefe" y mueve el cocinero correspondiente.
     */
    public void moverCocineroPorNombreDestino(String mensaje) {
        String[] partes = mensaje.split("-");
        if (partes.length != 2) return;
        String nombreCocinero = partes[0];
        String destino = partes[1];

        Platform.runLater(() -> moverCocinero(nombreCocinero, destino));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
