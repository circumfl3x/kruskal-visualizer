package com.kruskal.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    // Проверка корректного создания ребра и получения данных
    @Test
    void shouldCreateEdgeAndReturnValues() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);

        Edge edge = new Edge(node1, node2, 5);

        assertEquals(node1, edge.getNode1());
        assertEquals(node2, edge.getNode2());
        assertEquals(5, edge.getWeight());
    }


    // Проверка, что ребро соединяет две указанные вершины
    @Test
    void shouldReturnTrueWhenEdgeConnectsNodes() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);

        Edge edge = new Edge(node1, node2, 5);

        assertTrue(edge.connects(node1, node2));
    }


    // Проверка обратного порядка вершин в ребре
    @Test
    void shouldReturnTrueWhenNodesAreReversed() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);

        Edge edge = new Edge(node1, node2, 5);

        assertTrue(edge.connects(node2, node1));
    }


    // Проверка, что ребро не соединяет посторонние вершины
    @Test
    void shouldReturnFalseForDifferentNodes() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);
        Node node3 = new Node(2, 300, 300);

        Edge edge = new Edge(node1, node2, 5);

        assertFalse(edge.connects(node1, node3));
    }


    // Проверка метода contains
    @Test
    void shouldReturnTrueWhenEdgeContainsNode() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);

        Edge edge = new Edge(node1, node2, 5);

        assertTrue(edge.contains(node1));
        assertTrue(edge.contains(node2));
    }


    // Проверка contains для отсутствующей вершины
    @Test
    void shouldReturnFalseWhenEdgeDoesNotContainNode() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);
        Node node3 = new Node(2, 300, 300);

        Edge edge = new Edge(node1, node2, 5);

        assertFalse(edge.contains(node3));
    }


    // Проверка сравнения рёбер по весу
    @Test
    void shouldCompareEdgesByWeight() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);

        Edge edge1 = new Edge(node1, node2, 5);
        Edge edge2 = new Edge(node1, node2, 10);

        assertTrue(edge1.compareTo(edge2) < 0);
        assertTrue(edge2.compareTo(edge1) > 0);
    }


    // Проверка равенства одинаковых рёбер
    @Test
    void shouldReturnTrueForEqualEdges() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);

        Edge edge1 = new Edge(node1, node2, 5);
        Edge edge2 = new Edge(node2, node1, 5);

        assertEquals(edge1, edge2);
    }


    // Проверка неравенства рёбер с разным весом
    @Test
    void shouldReturnFalseForEdgesWithDifferentWeight() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);

        Edge edge1 = new Edge(node1, node2, 5);
        Edge edge2 = new Edge(node1, node2, 10);

        assertNotEquals(edge1, edge2);
    }


    // Проверка hashCode
    @Test
    void equalEdgesShouldHaveSameHashCode() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);

        Edge edge1 = new Edge(node1, node2, 5);
        Edge edge2 = new Edge(node2, node1, 5);

        assertEquals(
                edge1.hashCode(),
                edge2.hashCode()
        );
    }


    // Проверка строкового представления
    @Test
    void shouldReturnCorrectStringRepresentation() {

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 200);

        Edge edge = new Edge(node1, node2, 5);

        assertEquals(
                "Edge{0 - 1, weight=5}",
                edge.toString()
        );
    }
}