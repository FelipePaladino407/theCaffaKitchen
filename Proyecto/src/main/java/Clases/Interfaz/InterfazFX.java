package Clases.Interfaz;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.scene.paint.Color;





public class InterfazFX extends Application {

    private Pane root = new Pane();
    private Map<String, Point2D> ubicaciones = Map.of(
            "Horno", new Point2D(300, 100),
            "Parrilla", new Point2D(500, 100),
            "Jefe", new Point2D(100, 300)
    );

    private Map<String, ImageView> herramientaViews = new HashMap<>();
    private Map<String, Circle> cocineros = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();

        // Fondo de cocina
        Image fondo = new Image("cocina.jpg");
        ImageView fondoView = new ImageView(fondo);
        fondoView.setFitWidth(800);
        fondoView.setFitHeight(600);
        root.getChildren().add(fondoView);


        // Herramientas (pueden ser reemplazadas por imágenes si querés)
        agregarHerramienta("Horno", "horno.jpg");
        agregarHerramienta("Parrilla", "parrilla.png");

        // Cocineros como círculos de colores
        agregarCocinero("Juan", Color.RED);
        agregarCocinero("Ana", Color.BLUE);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cocina estilo Overcooked");
        primaryStage.show();

        // Ejemplo: mover a Juan al horno
        moverCocinero("Juan", "Horno");
    }

    private void agregarHerramienta(String nombre, String rutaImagen) {
        Point2D pos = ubicaciones.get(nombre);
        ImageView img = new ImageView(new Image(rutaImagen));
        img.setFitWidth(64);
        img.setFitHeight(64);
        img.setLayoutX(pos.getX());
        img.setLayoutY(pos.getY());
        root.getChildren().add(img);
        herramientaViews.put(nombre, img);
    }

    private void agregarCocinero(String nombre, Color color) {
        Circle cocinero = new Circle(15, color);
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
            cocinero.setLayoutX(objetivo.getX());
            cocinero.setLayoutY(objetivo.getY());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}



