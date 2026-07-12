package com.kruskal.util;

import com.kruskal.model.Graph;
import com.kruskal.model.Edge;
import com.kruskal.model.Node;

import java.util.ArrayList;
import java.util.Random;

/**
 * Класс для генерации случайных графов.
 * Поля:
 *  double MIN_DISTANCE - минимальное расстояние между вершинами
 *
 * Содержит метод для создания случайного графа
 * с заданным количеством вершин и рёбер,
 * генерации координат вершин без наложения
 * и случайного назначения весов рёбрам.
 */

public class GraphGenerator {

    // Минимальное расстояние между вершинами
    private static final double MIN_DISTANCE = 50;

    public Graph generate(int vertexCount, int edgeCount) {

        // Проверка корректности количества вершин
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("Количество вершин должно быть больше 0");
        }

        // Максимально возможное количество ребер в неориентированном графе
        int maxEdges = (vertexCount * (vertexCount - 1)) / 2;

        // Проверка корректности количества ребер
        if (edgeCount < 0 || edgeCount > maxEdges) {
            throw new IllegalArgumentException(
                    "Некорректное количество рёбер. Максимум: " + maxEdges
            );
        }

        // Создаем пустой граф
        Graph graph = new Graph(new ArrayList<>(), new ArrayList<>());
        Random random = new Random();

        // Генерация вершин
        for (int i = 0; i < vertexCount; i++) {

            double x;
            double y;

            // Подбираем координаты, пока они не окажутся достаточно далеко
            // от уже существующих вершин
            do {
                x = random.nextDouble() * 800;
                y = random.nextDouble() * 600;

            } while (!isPositionAvailable(x, y, graph));

            Node node = new Node(i, x, y);
            graph.addNode(node);
        }

        // Генерация случайных ребер
        while (graph.getEdgeCount() < edgeCount) {

            Node node1 = graph.getNodes().get(random.nextInt(vertexCount));
            Node node2 = graph.getNodes().get(random.nextInt(vertexCount));

            // Не допускаем наличие петель
            if (node1.equals(node2)) {
                continue;
            }

            // Проверяем, существует ли такое ребро
            boolean exists = graph.getEdges()
                    .stream()
                    .anyMatch(edge -> edge.connects(node1, node2));

            if (exists) {
                continue;
            }

            // Генерируем случайный вес ребра
            int weight = random.nextInt(100) + 1;

            Edge edge = new Edge(node1, node2, weight);
            graph.addEdge(edge);
        }

        return graph;
    }

    // Проверяет, можно ли разместить вершину в указанных координатах
    private boolean isPositionAvailable(double x, double y, Graph graph) {

        for (Node node : graph.getNodes()) {

            double dx = node.getX() - x;
            double dy = node.getY() - y;

            // Вычисляем расстояние между вершинами
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Если вершины расположены слишком близко - позиция не подходит
            if (distance < MIN_DISTANCE) {
                return false;
            }
        }

        return true;
    }
}