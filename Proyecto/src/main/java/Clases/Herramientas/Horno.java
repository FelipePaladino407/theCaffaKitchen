package Clases.Herramientas;

public class Horno extends Herramienta {
    public Horno() {
        super("Horno", 2);
        // Horno: capacidad 2. Osea hay dos hornetes.
    }

    @Override
    public void dibujarProceso() throws InterruptedException {
        String[] frames = {"[=   ]", "[==  ]", "[=== ]", "[ ===]", "[  ==]", "[   =]"};
        for (int i = 0; i < 10; i++) {
            System.out.print("\r" + nombre + " cocinando " + frames[i % frames.length]);
            Thread.sleep(300);
        }
        System.out.println("\r" + nombre + " listo!  ");
    }
}