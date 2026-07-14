package com.kruskal.algorithm;

import com.kruskal.model.Edge;
import com.kruskal.model.Node;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

/**
 * Состояние алгоритма на одном шаге визуализации.
 */
public class VisualizationStep {

    private final List<Edge> mstEdges;                   // рёбра, уже добавленные в MST
    private final Map<Edge, EdgeStatus> edgeStatuses;    // статус каждого ребра
    private final Map<Node, Color> nodeColors;           // цвет каждой вершины
    private final Edge currentEdge;                      // ребро, которое сейчас рассматривается (может быть null)
    private final int totalWeight;                       // текущий суммарный вес MST
    private final String description;                    // текстовое пояснение шага

    public VisualizationStep(List<Edge> mstEdges,
                             Map<Edge, EdgeStatus> edgeStatuses,
                             Map<Node, Color> nodeColors,
                             Edge currentEdge,
                             int totalWeight,
                             String description) {
        this.mstEdges = List.copyOf(mstEdges);
        this.edgeStatuses = Map.copyOf(edgeStatuses);
        this.nodeColors = Map.copyOf(nodeColors);
        this.currentEdge = currentEdge;
        this.totalWeight = totalWeight;
        this.description = description;
    }

    public List<Edge> getMstEdges() {
        return mstEdges;
    }

    public Map<Edge, EdgeStatus> getEdgeStatuses() {
        return edgeStatuses;
    }

    public Map<Node, Color> getNodeColors() {
        return nodeColors;
    }

    public Edge getCurrentEdge() {
        return currentEdge;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCurrentEdge(Edge edge) {
        return edge.equals(currentEdge);
    }

    public EdgeStatus getStatus(Edge edge) {
        return edgeStatuses.getOrDefault(edge, EdgeStatus.UNPROCESSED);
    }

    public Color getColor(Node node) {
        return nodeColors.getOrDefault(node, Color.GRAY);
    }
}

