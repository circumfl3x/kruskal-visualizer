package com.kruskal.model;

import java.util.Objects;

/**
 * Класс - ребро графа.
 * Поля:
 *  Node node1 - первая инцидентная вершина
 *  Node node2 - вторая инцидентная вершина
 *  int weight - вес ребра
 *
 * Содержит геттеры и сеттеры для полей, конструктор;
 * переопределяет методы сравнения, получения хэша, возврата строкового
 * представления
 */

public class Edge implements Comparable<Edge> {

    private final Node node1;
    private final Node node2;
    private final int weight;

    public Edge(Node node1, Node node2, int weight) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public int getWeight() {
        return weight;
    }

    // Соединяет ли ребро две указанные вершины
    public boolean connects(Node n1, Node n2) {
        return (node1.equals(n1) && node2.equals(n2)) || (node1.equals(n2) && node2.equals(n1));
    }

    // Содержит ли ребро указанную вершину
    public boolean contains(Node node) {
        return node1.equals(node) || node2.equals(node);
    }

    /**
     * Сравнение двух ребер по весу, возвращает:
     * -1, если this.weight < other.weight
     * 0, если равны
     * 1, если this.weight > other.weight
     */
    @Override
    public int compareTo(Edge other){
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return weight == edge.weight &&
                ((node1.equals(edge.node1) && node2.equals(edge.node2)) ||
                        (node1.equals(edge.node2) && node2.equals(edge.node1)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(node1.getId() + node2.getId(), weight);
    }

    @Override
    public String toString() {
        return "Edge{" + node1.getId() + " - " + node2.getId() + ", weight=" + weight + "}";
    }

}
