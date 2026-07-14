package com.kruskal.io;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Класс для сохранения графа в текстовый файл.
 */
public class GraphFileWriter {

    public void write(Graph graph, String fileName) throws IOException {
        if (graph == null) {
            throw new IllegalArgumentException("Граф не может быть null.");
        }

        if (fileName == null) {
            throw new IllegalArgumentException("Имя файла не может быть null.");
        }

        if (fileName.isBlank()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым.");
        }

        if (!fileName.toLowerCase().endsWith(".txt")) {
            fileName += ".txt";
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Маркер нового формата
            writer.write("COORDS");
            writer.newLine();

            // Количество вершин
            writer.write(String.valueOf(graph.getNodeCount()));
            writer.newLine();

            // Вершины
            for (Node node : graph.getNodes()) {
                writer.write(
                        node.getId() + " "
                                + node.getX() + " "
                                + node.getY()
                );
                writer.newLine();
            }

            writer.newLine();

            // Рёбра
            for (Edge edge : graph.getEdges()) {
                if (edge.getNode1() == null || edge.getNode2() == null) {
                    throw new IllegalArgumentException(
                            "Ребро содержит null-вершину."
                    );
                }

                writer.write(
                        edge.getNode1().getId() + " "
                                + edge.getNode2().getId() + " "
                                + edge.getWeight()
                );
                writer.newLine();
            }
        }
    }
}