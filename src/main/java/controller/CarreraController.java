package controller;

import model.Carrera;
import view.View;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class CarreraController {
    public static void startCarrera(Carrera carrera, View view) {
        // Obtener componentes necesarios
        JProgressBar[] progressBars = view.getProgressBars();
        JLabel[] metrosRecorridosLabels = view.getMetrosRecorridosLabel();
        JSpinner[] velocitySpinners = view.getVelocitySpinners();

        // Atómico para detener todos los hilos cuando un coche termine la carrera
        AtomicBoolean carreraTerminada = new AtomicBoolean(false);

        // Crear hilo para cara coche
        for (int numCoche = 0; numCoche < progressBars.length; numCoche++) {
            int finalNumCoche = numCoche;
            new Thread(() -> {
                JProgressBar progressBar = progressBars[finalNumCoche];
                JLabel metrosLabel = metrosRecorridosLabels[finalNumCoche];
                int velocidad = (int) velocitySpinners[finalNumCoche].getValue(); // Obtener velocidad del spinner

                int metrosRecorridos = 0;
                int longitudTotal = carrera.getLongitudTotal();

                // Avanzar el coche mientras no termine la carrera y no haya ganador
                while (metrosRecorridos < longitudTotal && !carreraTerminada.get()) {
                    try {
                        Thread.sleep(1000); // Intervalo de actualización
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    // Avanzar metros según velocidad en un rango variable de 60m/s
                    metrosRecorridos += new Random().nextInt(velocidad -30, velocidad +30);

                    // Actualizar la barra de progreso y el texto de metros recorridos
                    int progreso = Math.min(metrosRecorridos, longitudTotal); // Evita valor mayor a la longitud de la carrera
                    int metros = Math.min(metrosRecorridos, longitudTotal); // Evita valor mayor a la longitud de la carrera

                    // Utilizar invokeLater para evitar problemas en la vista
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progreso);
                        metrosLabel.setText(metros + " m");
                    });
                }

                // Si este hilo llegó primero, se termina la carrera
                if (!carreraTerminada.getAndSet(true)) {
                    int[] numCochesClasificacion = IntStream.range(0, progressBars.length) // Generar índices del 0 al número de barras de progreso (número de coches)
                            .boxed() // Convertir a Stream<Integer>
                            .sorted((i1, i2) -> Integer.compare(progressBars[i2].getValue(), progressBars[i1].getValue())) // Ordenar por valores de barras
                            .mapToInt(i -> i) // Convertir de Stream<Integer> a IntStream
                            .toArray(); // Convertir a array de int

                    SwingUtilities.invokeLater(() -> {
                        view.showStatsFinalCarrera(numCochesClasificacion); // Se pasa Array ordenado con los números de coches ordenados por posición (metros recorridos)
                    });
                }
            }).start();
        }
    }
}
