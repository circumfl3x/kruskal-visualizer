package com.kruskal.util;

import com.kruskal.model.Graph;
import com.kruskal.model.Edge;
import com.kruskal.model.Node;

import java.util.ArrayList;
import java.util.Random;

public class GraphGenerator {

    private static final double MIN_DISTANCE = 50;
    private static final double CANVAS_WIDTH = 500;
    private static final double CANVAS_HEIGHT = 600;
    private static final double MARGIN = 40;

    public Graph generate(int vertexCount, int edgeCount) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("Количество вершин должно быть больше 0");
        }

        int maxEdges = (vertexCount * (vertexCount - 1)) / 2;
        if (edgeCount < 0 || edgeCount > maxEdges) {
            throw new IllegalArgumentException(
                    "Некорректное количество рёбер. Максимум: " + maxEdges
            );
        }

        Graph graph = new Graph(new ArrayList<>(), new ArrayList<>());
        Random random = new Random();

        for (int i = 0; i < vertexCount; i++) {
            double x;
            double y;
            do {
                x = MARGIN + random.nextDouble() * (CANVAS_WIDTH - 2 * MARGIN);
                y = MARGIN + random.nextDouble() * (CANVAS_HEIGHT - 2 * MARGIN);
            } while (!isPositionAvailable(x, y, graph));

            graph.addNode(new Node(i, x, y));
        }

        // Генерация рёбер
        while (graph.getEdgeCount() < edgeCount) {
            Node node1 = graph.getNodes().get(random.nextInt(vertexCount));
            Node node2 = graph.getNodes().get(random.nextInt(vertexCount));

            if (node1.equals(node2)) continue;

            boolean exists = graph.getEdges().stream()
                    .anyMatch(edge -> edge.connects(node1, node2));
            if (exists) continue;

            int weight = random.nextInt(100) + 1;
            graph.addEdge(new Edge(node1, node2, weight));
        }

        return graph;
    }

    private boolean isPositionAvailable(double x, double y, Graph graph) {
        for (Node node : graph.getNodes()) {
            double dx = node.getX() - x;
            double dy = node.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) < MIN_DISTANCE) {
                return false;
            }
        }
        return true;
    }
}