package com.kruskal.algorithm;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;

import java.util.*;

/**
 * Реализация алгоритма Краскала.
 */

public class KruskalAlgorithm {

    public List<VisualizationStep> executeWithStates(Graph graph) {
        Map<Integer, Integer> nodeIndex = new HashMap<>();
        int index = 0;
        for (var node : graph.getNodes()) {
            nodeIndex.put(node.getId(), index++);
        }

        if (graph.isEmpty()) {
            throw new IllegalArgumentException("Граф пуст");
        }

        List<VisualizationStep> steps = new ArrayList<>();
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        Collections.sort(sortedEdges);

        DSU dsu = new DSU(graph.getNodeCount());
        List<Edge> mstEdges = new ArrayList<>();
        Map<Edge, EdgeStatus> statuses = new HashMap<>();
        for (Edge edge : graph.getEdges()) {
            statuses.put(edge, EdgeStatus.UNPROCESSED);
        }
        int totalWeight = 0;

        // Начальное состояние
        steps.add(new VisualizationStep(
                List.of(),
                statuses,
                dsu.getNodeColors(graph),
                null,
                0,
                "Алгоритм Краскала начат. Рёбра отсортированы по весу."
        ));

        // Основной цикл
        for (Edge edge : sortedEdges) {
            int node1Id = nodeIndex.get(edge.getNode1().getId());
            int node2Id = nodeIndex.get(edge.getNode2().getId());

            // Получаем корни до объединения
            int root1 = dsu.find(node1Id);
            int root2 = dsu.find(node2Id);

            // Подсветка ребра с информацией о корнях
            Map<Edge, EdgeStatus> highlightStatuses = new HashMap<>(statuses);
            highlightStatuses.put(edge, EdgeStatus.CURRENT);

            String highlightDesc = "Рассматривается ребро " + edge.getNode1().getId() + " - " + edge.getNode2().getId() +
                    " (вес " + edge.getWeight() + "). Корень " + node1Id + ": " + root1 +
                    ", корень " + node2Id + ": " + root2 + ".";

            steps.add(new VisualizationStep(
                    mstEdges,
                    highlightStatuses,
                    dsu.getNodeColors(graph),
                    edge,
                    totalWeight,
                    highlightDesc
            ));

            // Проверка DSU
            if (dsu.union(node1Id, node2Id)) {
                // Ребро добавлено
                mstEdges.add(edge);
                totalWeight += edge.getWeight();
                statuses.put(edge, EdgeStatus.ADDED);

                steps.add(new VisualizationStep(
                        mstEdges,
                        statuses,
                        dsu.getNodeColors(graph),
                        null,
                        totalWeight,
                        "Ребро " + edge.getNode1().getId() + " - " + edge.getNode2().getId() +
                                " добавлено в MST. Текущий вес: " + totalWeight
                ));
            } else {
                // Ребро отклонено
                statuses.put(edge, EdgeStatus.REJECTED);

                steps.add(new VisualizationStep(
                        mstEdges,
                        statuses,
                        dsu.getNodeColors(graph),
                        null,
                        totalWeight,
                        "Ребро " + edge.getNode1().getId() + " - " + edge.getNode2().getId() +
                                " отклонено (образует цикл)"
                ));
            }
        }

        StringBuilder finalDesc = new StringBuilder();
        finalDesc.append("Алгоритм Краскала завершён.\n");
        finalDesc.append("Минимальное остовное дерево содержит ").append(mstEdges.size()).append(" рёбер.\n");
        finalDesc.append("Общий вес дерева: ").append(totalWeight).append(".\n");
        if (!mstEdges.isEmpty()) {
            finalDesc.append("Рёбра, вошедшие в остов:\n");
            for (Edge e : mstEdges) {
                finalDesc.append("  ").append(e.getNode1().getId()).append(" - ")
                        .append(e.getNode2().getId()).append(" (вес ").append(e.getWeight()).append(")\n");
            }
        }
        boolean isConnected = mstEdges.size() == graph.getNodeCount() - 1;
        finalDesc.append(isConnected ? "Граф связный, остовное дерево построено полностью."
                : "Граф несвязный, построен минимальный остовный лес.");

        steps.add(new VisualizationStep(
                mstEdges,
                statuses,
                dsu.getNodeColors(graph),
                null,
                totalWeight,
                finalDesc.toString()
        ));

        return steps;
    }
}