package com.kruskal.algorithm;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;

import java.util.*;

/**
 * Реализация алгоритма Краскала.
 */

public class KruskalAlgorithm {

    /**
     * Выполняет алгоритм Краскала на заданном графе.
     *
     * @param graph входной граф
     * @return список шагов алгоритма для визуализации
     * @throws IllegalArgumentException если граф пустой или несвязный
     */
    public List<AlgorithmStep> execute(Graph graph) {
        if (graph.isEmpty()) {
            throw new IllegalArgumentException("Граф пуст");
        }

        List<AlgorithmStep> steps = new ArrayList<>();
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        Collections.sort(sortedEdges); // сортировка Timsort'ом - O(nlogn)

        // создание dsu по всем вершинам
        DSU dsu = new DSU(graph.getNodeCount());
        int totalWeigh = 0;

        for (Edge edge : sortedEdges) {
            int nodeId1 = edge.getNode1().getId();
            int nodeId2 = edge.getNode2().getId();

            if (dsu.union(nodeId1, nodeId2)) {
                totalWeigh += edge.getWeight();
                String description = "Ребро " + nodeId1 + "-" + nodeId2 + "("
                        + edge.getWeight() + ") добавлено в остовное дерево. Текущий вес: " + totalWeigh;
                steps.add(new AlgorithmStep(edge, true, description, totalWeigh));
            } else {
                String description = "Ребро " + nodeId1 + "-" + nodeId2 + "("
                        + edge.getWeight() + ") отклонено (образует цикл)";
                steps.add(new AlgorithmStep(edge, false, description, totalWeigh));
            }
        }
        return steps;
    }

    /**
     * Выполняет алгоритм и возвращает только рёбра MST.
     *
     * @param graph входной граф
     * @return список рёбер минимального остовного дерева
     */
    public List<Edge> findMST(Graph graph) {
        List<AlgorithmStep> steps = execute(graph);
        List<Edge> mst = new ArrayList<>();
        for (AlgorithmStep step : steps) {
            if (step.isAdded()) {mst.add(step.getEdge());}
        }
        return mst;
    }

    public int calculateMSTWeight(Graph graph) {
        List<AlgorithmStep> steps = execute(graph);
        if (steps.isEmpty()) {return 0;}
        return steps.get(steps.size() - 1).getTotalWeight();
    }

    public List<VisualizationStep> executeWithStates(Graph graph) {
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
            int node1Id = edge.getNode1().getId();
            int node2Id = edge.getNode2().getId();

            // Получаем корни до объединения
            int root1 = dsu.find(node1Id);
            int root2 = dsu.find(node2Id);

            // Подсветка ребра с информацией о корнях
            Map<Edge, EdgeStatus> highlightStatuses = new HashMap<>(statuses);
            highlightStatuses.put(edge, EdgeStatus.CURRENT);

            String highlightDesc = "Рассматривается ребро " + node1Id + " - " + node2Id +
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
                        "Ребро " + node1Id + " - " + node2Id +
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
                        "Ребро " + node1Id + " - " + node2Id +
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