package view;

import controller.CarreraController;
import model.Carrera;

import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class View extends JFrame {
    public static final SpinnerNumberModel LAPS_NUMBER_SPINNER_MODEL = new SpinnerNumberModel(5, 1, 10, 1);
    public static final SpinnerNumberModel LAP_LENGTH_SPINNER_MODEL = new SpinnerNumberModel(500, 100, 1000, 50);

    // Arrays para los spinners, barras de progreso y etiquetas
    private JProgressBar[] progressBars = new JProgressBar[4]; // Barras de progreso para los 4 coches
    private JLabel[] metrosRecorridosLabel = new JLabel[4]; // Etiquetas para los 4 coches
    private JSpinner[] velocitySpinners = new JSpinner[4]; // Spinners de velocidad para cada coche

    // Componentes para configuración de carrera
    private JSpinner lapsNumberSpinner;
    private JSpinner lapLengthSpinner;

    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;

    Carrera carrera;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                View frame = new View();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public View() {
        // Configura la ventana
        setTitle("Carrera de Coches - Concurrencia");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configura el BorderLayout
        getContentPane().setLayout(new BorderLayout(10, 10));

        // Panel superior con configuración de carrera
        initTopPanel();

        // Panel central con configuración de coches y progreso de carrera
        initCenterPanel();

        // Panel inferior con botón de inicio de carrera
        initBottomPanel();
    }

    private void initTopPanel() {
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Espaciado horizontal y vertical
        // Panel superior con configuración de carrera
        topPanel.setBorder(new EmptyBorder(20, 20, 0, 20));
        getContentPane().add(topPanel, BorderLayout.NORTH);

        // Panel para Número de vueltas
        JPanel lapsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        lapsPanel.setBorder(new EmptyBorder(0, 0, 0, 5)); // Margen
        JLabel label1 = new JLabel("Número de vueltas:");
        lapsPanel.add(label1);
        lapsNumberSpinner = new JSpinner(LAPS_NUMBER_SPINNER_MODEL);
        customizeSpinner(lapsNumberSpinner); // Personalizar Spinner
        lapsPanel.add(lapsNumberSpinner);
        topPanel.add(lapsPanel);

        // Panel para Longitud de vuelta
        JPanel lengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        lengthPanel.setBorder(new EmptyBorder(0, 5, 0, 0)); // Margen
        JLabel label2 = new JLabel("Longitud de vuelta (m):");
        lengthPanel.add(label2);
        lapLengthSpinner = new JSpinner(LAP_LENGTH_SPINNER_MODEL);
        customizeSpinner(lapLengthSpinner); // Personalizar Spinner
        lengthPanel.add(lapLengthSpinner);
        topPanel.add(lengthPanel);
    }

    private void initCenterPanel() {
        // Panel central con el GridLayout inicial (2 columnas)
        centerPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // 5 filas, 2 columnas
        centerPanel.setBorder(new EmptyBorder(0, 150, 0, 150)); // Margen
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        // Títulos de columnas iniciales
        String[] columnLabels = {"Coche", "Velocidad (m/s)"};
        for (String columnLabel : columnLabels) {
            JLabel label = new JLabel(columnLabel);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            centerPanel.add(label);
        }

        // Inicializar los componentes de los coches (solo velocidad y coche)
        for (int numCoche = 0; numCoche < 4; numCoche++) {
            addCarComponentsInitial(centerPanel, numCoche);
        }
    }

    private void initBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        JButton startButton = new JButton("Iniciar Carrera");
        startButton.setPreferredSize(new Dimension(400, 40)); // Tamaño
        bottomPanel.add(startButton);
        startButton.addActionListener(e -> startCarrera()); // Acción del botón
    }

    private void addCarComponentsInitial(JPanel panel, int numCoche) {
        // Imagen del coche
        ImageIcon cocheImage = new ImageIcon(getClass().getResource("/images/coche" + numCoche + ".png"));
        cocheImage = new ImageIcon(cocheImage.getImage().getScaledInstance(100, 33, Image.SCALE_SMOOTH));
        JLabel cocheLabel = new JLabel(cocheImage);
        panel.add(cocheLabel);

        // Spinner para la velocidad de cada coche
        JSpinner velocitySpinner = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
        customizeSpinner(velocitySpinner);
        velocitySpinners[numCoche] = velocitySpinner;  // Guardamos el spinner en el arreglo
        panel.add(velocitySpinner);
    }

    private void updateTopPanelEstadoCarrera(String mensajeEstadoCarrera) {
        topPanel.removeAll(); // Eliminar componentes anteriores
        JLabel label1 = new JLabel(mensajeEstadoCarrera);
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(label1);

        topPanel.revalidate(); // Validar
        topPanel.repaint();    // Repintar
    }

    private void updateCenterPanelCarreraEnProgreso() {
        // Cambiar layout a 4 columnas
        centerPanel.setLayout(new GridLayout(5, 3, 10, 10)); // 5 filas, 4 columnas
        centerPanel.setBorder(new EmptyBorder(0, 100, 0, 100)); // Margen
        centerPanel.removeAll(); // Eliminar componentes anteriores

        // Títulos de columnas actualizados
        String[] columnLabels = {"Coche", "Progreso", "Recorrido"};
        for (String columnLabel : columnLabels) {
            JLabel label = new JLabel(columnLabel);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            centerPanel.add(label);
        }

        // Añadir componentes de información de cada coche en la carrera
        for (int numCoche = 0; numCoche < 4; numCoche++) {
            addCarStats(centerPanel, numCoche);
        }

        centerPanel.revalidate(); // Validar
        centerPanel.repaint();    // Repintar
    }

    private void clearBottomPanel() {
        bottomPanel.removeAll(); // Eliminar componentes anteriores
    }

    private void addCarStats(JPanel gridPanel, int numCoche) {
        // Imagen del coche
        ImageIcon cocheImage = new ImageIcon(getClass().getResource("/images/coche" + numCoche + ".png"));
        cocheImage = new ImageIcon(cocheImage.getImage().getScaledInstance(100, 33, Image.SCALE_SMOOTH));
        JLabel cocheLabel = new JLabel(cocheImage);
        gridPanel.add(cocheLabel);

        // Barra de progreso
        JProgressBar progressBar = new JProgressBar();
        progressBars[numCoche] = progressBar;  // Guardamos la barra de progreso en el arreglo
        gridPanel.add(progressBar);

        // Etiqueta de información
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER); // Centrar el texto
        metrosRecorridosLabel[numCoche] = label;  // Guardamos la etiqueta en el arreglo
        gridPanel.add(label);
    }

    private void startCarrera() {
        // Actualizar panel superior
        updateTopPanelEstadoCarrera("La carrera está en curso...");

        // Actualizar panel central con las nuevas columnas
        updateCenterPanelCarreraEnProgreso();

        // Actualizar panel inferior (sin contenido)
        clearBottomPanel();

        // Obtener valores de los spinners
        int numVueltas = (int) lapsNumberSpinner.getValue();
        int longitudVuelta = (int) lapLengthSpinner.getValue();

        carrera = new Carrera(numVueltas, longitudVuelta);

        // Valores máximos de los progressbar en función de los metros de carrera
        for (int numCoche = 0; numCoche < 4; numCoche++) {
            progressBars[numCoche].setMaximum(carrera.getLongitudTotal());
            metrosRecorridosLabel[numCoche].setText(progressBars[numCoche].getValue() + "m");
        }

        CarreraController.startCarrera(carrera, this);
    }

    public void showStatsFinalCarrera(int[] numCocheClasificacion) {
        int numCocheGanador = numCocheClasificacion[0];
        int[] otrosNumCochesClasificacion = Arrays.copyOfRange(numCocheClasificacion, 1, numCocheClasificacion.length); // Array omitiendo el ganador
        // Mostrar mensaje de estado de la carrera en panel superior
        updateTopPanelEstadoCarrera("La carrera ha finalizado");

        // Mostrar ganador en panel central
        updateCenterPanelShowGanadorInfo(numCocheGanador);

        // Mostrar info del resto de coches en el panel inferior
        updateBottonPanelShowOtrosCochesInfo(otrosNumCochesClasificacion);
    }

    private void updateCenterPanelShowGanadorInfo(int numCocheGanador) {
        centerPanel.removeAll();
        centerPanel.setLayout(new GridLayout(3, 1, 0, 0));

        // Título de ganador
        JLabel label1 = new JLabel("GANADOR:");
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(label1);

        // Imagen del coche ganador
        ImageIcon cocheImage = new ImageIcon(getClass().getResource("/images/coche" + numCocheGanador + ".png"));
        cocheImage = new ImageIcon(cocheImage.getImage().getScaledInstance(300, 100, Image.SCALE_SMOOTH));
        JLabel cocheLabel = new JLabel(cocheImage);
        centerPanel.add(cocheLabel);

        // Progreso total del ganador
        JLabel label = new JLabel(String.format("%dm / %dm", progressBars[numCocheGanador].getValue(), carrera.getLongitudTotal()));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(label);

        centerPanel.revalidate(); // Validar
        centerPanel.repaint();    // Repintar
    }


    private void updateBottonPanelShowOtrosCochesInfo(int[] otrosNumCochesClasificacion) {
        bottomPanel.removeAll(); // Limpiar el panel

        // Número de coches restantes
        int numCochesRestantes = otrosNumCochesClasificacion.length;

        bottomPanel.setLayout(new GridLayout(3, numCochesRestantes, 10, 0)); // 3 filas y tantas columnas como otros coches haya

        // Fila 1: Mostrar las posiciones del resto de coches
        for (int posicion = 1; posicion <= numCochesRestantes; posicion++) {
            JLabel labelPosicion = new JLabel(String.format("%dº", posicion +1));
            labelPosicion.setHorizontalAlignment(SwingConstants.CENTER);
            bottomPanel.add(labelPosicion);
        }

        // Fila 2: Mostrar las imágenes de los coches restantes
        for (int numCoche : otrosNumCochesClasificacion) {
            ImageIcon cocheImage = new ImageIcon(getClass().getResource("/images/coche" + numCoche + ".png"));
            cocheImage = new ImageIcon(cocheImage.getImage().getScaledInstance(100, 33, Image.SCALE_SMOOTH));
            JLabel cocheLabel = new JLabel(cocheImage);
            cocheLabel.setHorizontalAlignment(SwingConstants.CENTER);
            bottomPanel.add(cocheLabel);
        }

        // Fila 3: Mostrar los metros recorridos de cada coche
        for (int numCoche : otrosNumCochesClasificacion) {
            JLabel label = new JLabel(String.format("%dm / %dm", progressBars[numCoche].getValue(), carrera.getLongitudTotal()));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            bottomPanel.add(label);
        }

        // Actualizar la vista
        bottomPanel.revalidate(); // Validar
        bottomPanel.repaint();    // Repintar
    }



    private void customizeSpinner(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setBackground(new Color(238, 238, 238));
            textField.setBorder(null); // Quita el borde
            textField.setHorizontalAlignment(SwingConstants.CENTER); // Centrar el texto
        }
    }

    public JProgressBar[] getProgressBars() {
        return progressBars;
    }

    public JLabel[] getMetrosRecorridosLabel() {
        return metrosRecorridosLabel;
    }

    public JSpinner[] getVelocitySpinners() {
        return velocitySpinners;
    }

}
