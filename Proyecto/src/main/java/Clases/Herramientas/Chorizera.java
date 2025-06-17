package Clases.Herramientas;

public class Chorizera extends Herramienta {
    public Chorizera() {
        super("Chorizera", 2);
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