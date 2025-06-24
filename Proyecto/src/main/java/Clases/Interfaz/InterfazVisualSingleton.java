package Clases.Interfaz;

//Se usa singleton porque solo querermos una sola intancia de es básicamente un menú con las clases / librería de interfaces
public class InterfazVisualSingleton {
    private static InterfazVisual instancia;

    public static InterfazVisual get() {
        if (instancia == null) {
            instancia = new InterfazVisual();
        }
        return instancia;
    }
}
