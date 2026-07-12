package com.kruskal.util;

import com.kruskal.model.Graph;
import com.kruskal.model.Edge;
import com.kruskal.model.Node;

import java.util.ArrayList;
import java.util.Random;

/**
 * Класс для генерации случайных графов.
 */

public class GraphGenerator {
    public Graph generate(int vertexCount, int edgeCount) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("Количество вершин должно быть больше 0");
        }

        int maxEdges = (vertexCount * (vertexCount - 1)) / 2;
        if (edgeCount < 0 || edgeCount > maxEdges) {
            throw new IllegalArgumentException("Некорректное количество рёбер. Максимум: " + maxEdges);
        }

        Graph graph = new Graph(new ArrayList<>(), new ArrayList<>());
        Random random = new Random();

        for (int i = 0; i < vertexCount; i++) {
            Node node = new Node(i,
                    random.nextDouble() * 800,
                    random.nextDouble() * 600);

            graph.addNode(node);
        }

        while (graph.getEdgeCount() < edgeCount) {
            Node node1 = graph.getNodes().get(random.nextInt(vertexCount));
            Node node2 = graph.getNodes().get(random.nextInt(vertexCount));
            if (node1.equals(node2)) {
                continue;
            }

            boolean exists = graph.getEdges().stream().anyMatch(edge -> edge.connects(node1, node2));
            if (exists) {
                continue;
            }

            int weight = random.nextInt(100) + 1;
            Edge edge = new Edge(node1, node2, weight);
            graph.addEdge(edge);
        }

        return graph;
    }
}
