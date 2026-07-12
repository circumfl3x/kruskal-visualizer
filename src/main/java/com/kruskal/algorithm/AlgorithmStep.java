package com.kruskal.algorithm;

import com.kruskal.model.Edge;

/**
 * Один шаг выполнения алгоритма, для пошаговой визуализации в будущем.
 */

public class AlgorithmStep {

    private final Edge edge;
    private final boolean added;
    private final String description;
    private final int totalWeight;

    /**
     * @param edge         ребро, которое рассматривается на этом шаге
     * @param added        true, если ребро добавлено в MST; false, если отклонено
     * @param description  текстовое описание шага
     * @param totalWeight  суммарный вес MST после этого шага
     */
    public AlgorithmStep(Edge edge, boolean added, String description, int totalWeight) {
        this.edge = edge;
        this.added = added;
        this.description = description;
        this.totalWeight = totalWeight;
    }

    public Edge getEdge() {
        return edge;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public boolean isAdded() {
        return added;
    }

    @Override
    public String toString() {
        return description;
    }
}
