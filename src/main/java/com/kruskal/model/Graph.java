package com.kruskal.model;

import java.util.Objects;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс, представляющий граф.
 * Поля:
 *  List<Node> nodes - список вершин
 *  List<Edges> edges - список ребер
 *
 * Содержит методы для создания и редактирования графа:
 * добавление и удаление вершин и ребер, поиск вершины по id,
 * получение ребер, связанных с заданной вершиной.
 */

public class Graph {

    private final List<Node> nodes;
    private final List<Edge> edges;

    // Конструктор
    public Graph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    // Добавить вершину
    public void addNode(Node node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
        }
    }

    // Удалить вершину
    public void removeNode(Node node) {
        edges.removeIf(edge -> edge.contains(node));
        nodes.remove(node);
    }

    // Добавить ребро
    public void addEdge(Edge edge) {
        if (nodes.contains(edge.getNode1()) && nodes.contains(edge.getNode2())) {
            if (!edges.contains(edge)) {
                edges.add(edge);
            }
        } else {
            throw new IllegalArgumentException("Both nodes must be in the graph");
        }
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    // Возвращает вершину по id
    public Node getNodeById(int id) {
        return nodes.stream().filter(node -> node.getId() == id).findFirst().orElse(null);
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    // Возвращает ребра, связанные с указанной вершиной
    public List<Edge> getEdgesOf(Node node) {
        return edges.stream().filter(edge -> edge.contains(node)).collect(Collectors.toList());
    }

    // Полная очистка графа
    public void clear() {
        nodes.clear();
        edges.clear();
    }

    // Проверяет, пуст ли граф
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public String toString() {
        return "Graph{nodes=" + nodes.size() + ", edges=" + edges.size() + "}";
    }
}
