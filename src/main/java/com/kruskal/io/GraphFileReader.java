package com.kruskal.io;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import com.kruskal.util.GraphGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Класс для чтения графа из текстового файла.
 * Поля:
 *  отсутствуют
 *
 * Содержит метод для загрузки графа из файла,
 * проверки корректности входных данных и
 * создания объекта Graph на основе считанной информации.
 */

public class GraphFileReader {
    private static final double DEFAULT_CANVAS_WIDTH = 470;
    private static final double DEFAULT_CANVAS_HEIGHT = 470;
    public Graph read(String fileName) throws IOException {
        Graph graph = new Graph(new ArrayList<>(), new ArrayList<>());
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Читаем первую строку (количество вершин)
            String firstLine = reader.readLine();
            firstLine = firstLine.trim().replace("\uFEFF", "");

            if (firstLine == null || firstLine.isBlank()) {
                throw new IOException("Файл пуст.");
            }

            /* Определяем, с каким файлом работаем:
             *  1. Если первая строка "COORDS", тогда идентифицируем файл,
             *  как файл с координатами вершин.
             *  2. Иначе идентифицируем файл, как файл без координат.
             */
            boolean hasCoordinates = false;
            if (firstLine.equalsIgnoreCase("COORDS")) {
                hasCoordinates = true;
                firstLine = reader.readLine();
                if (firstLine == null || firstLine.isBlank()) {
                    throw new IOException("Отсутствует количество вершин.");
                }
            }

            int vertexCount = Integer.parseInt(firstLine);
            if (vertexCount < 0) {
                throw new IOException("Количество вершин не может быть отрицательным.");
            }

            // Читаем вершины
            if (hasCoordinates) {
                for (int i = 0; i < vertexCount; i++) {
                    String line = reader.readLine();
                    if (line == null) {
                        throw new IOException("Недостаточно данных о вершинах.");
                    }

                    String[] parts = line.trim().split("\\s+");
                    if (parts.length != 3) {
                        throw new IOException("Неверный формат строки вершины: " + line);
                    }

                    int id = Integer.parseInt(parts[0]);
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);

                    if (graph.getNodeById(id) != null) {
                        throw new IOException("Повторяющийся идентификатор вершины: " + id);
                    }

                    graph.addNode(new Node(id, x, y));
                }
            } else {
                for (int i = 0; i < vertexCount; i++) {
                    graph.addNode(new Node(i, 0, 0));
                }
                GraphGenerator generator = new GraphGenerator();
                generator.generateNodePositions(graph, DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);
            }

            // Читаем рёбра
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.trim().split("\\s+");
                if (parts.length != 3) {
                    throw new IOException("Неверный формат строки ребра: " + line);
                }

                int node1Id = Integer.parseInt(parts[0]);
                int node2Id = Integer.parseInt(parts[1]);
                int weight = Integer.parseInt(parts[2]);

                if (weight <= 0) {
                    throw new IOException("Вес ребра должен быть положительным.");
                }

                Node node1 = graph.getNodeById(node1Id);
                Node node2 = graph.getNodeById(node2Id);

                if (node1 == null || node2 == null) {
                    throw new IOException(
                            "Ребро содержит несуществующую вершину: " + line
                    );
                }

                graph.addEdge(new Edge(node1, node2, weight));
            }
        }
        return graph;
    }
}