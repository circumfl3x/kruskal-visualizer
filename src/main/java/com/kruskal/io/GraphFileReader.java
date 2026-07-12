package com.kruskal.io;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;

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
    public Graph read(String fileName) throws IOException {
        Graph graph = new Graph(new ArrayList<>(), new ArrayList<>());

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Читаем первую строку (количество вершин)
            String firstLine = reader.readLine();

            if (firstLine == null || firstLine.isBlank()) {
                throw new IOException("Файл пуст.");
            }

            int vertexCount = Integer.parseInt(firstLine);
            if (vertexCount < 0) {
                throw new IOException("Количество вершин не может быть отрицательным.");
            }

            // Читаем вершины
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
                graph.addNode(new Node(id, x, y));
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