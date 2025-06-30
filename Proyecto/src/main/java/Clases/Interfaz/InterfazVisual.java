package Clases.Interfaz;

import javax.swing.*;
import java.awt.*;


public class InterfazVisual {
    private final JFrame frame; //Es la ventana de la aplicación
    private final JTextArea logArea; //Tiene una muestra de registos, es básicamente una consola.
    // COn esta podemos liberar los mensajes en pantalla
    private final JPanel herramientasPanel; //Panel de herramientas

    public InterfazVisual() {
        frame = new JFrame("Simulación de Cocina");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Panel para herramientas
        herramientasPanel = new JPanel();
        herramientasPanel.setLayout(new BoxLayout(herramientasPanel, BoxLayout.Y_AXIS));
        herramientasPanel.setBorder(BorderFactory.createTitledBorder("Herramientas"));
        frame.add(herramientasPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    public void log(String mensaje) {
        SwingUtilities.invokeLater(() -> logArea.append(mensaje + "\n")); //Escribe en pantalla
    }

    public JLabel crearEstadoLabel(String nombre) {
        return new JLabel(nombre + ": Libre"); //Imprime que determinada herramienta está libre, avisa cuando la herramienta no es pedida por nadie
    }

    public JProgressBar crearBarraProgreso() {
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue(0);
        barra.setStringPainted(true);
        return barra; //Crea una barra de progreso y la retornamos para que la herramienta se la pueda guardar
    }

    public void agregarHerramientaVisual(String nombre, JLabel estado, JProgressBar barra) {
        //Agrega el estado y la barra a la ventana principal al panel de la derecha.

        SwingUtilities.invokeLater(() -> {
            herramientasPanel.add(estado);
            herramientasPanel.add(barra);
            herramientasPanel.add(Box.createVerticalStrut(10));
            herramientasPanel.revalidate();
            herramientasPanel.repaint();
        });
    }
}
