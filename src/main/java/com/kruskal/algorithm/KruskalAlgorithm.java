package com.kruskal.algorithm;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            throw new IllegalArgumentException("Graph is empty");
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
                        + edge.getWeight() + ") добавлено в MST. Текущий вес: " + totalWeigh;
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
}
