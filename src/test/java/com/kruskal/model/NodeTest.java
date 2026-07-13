package com.kruskal.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    // Проверка корректного создания вершины и получения значений
    @Test
    void shouldCreateNodeAndReturnValues() {

        Node node = new Node(1, 100.5, 200.5);

        assertEquals(1, node.getId());
        assertEquals(100.5, node.getX());
        assertEquals(200.5, node.getY());
    }


    // Проверка изменения координаты X
    @Test
    void shouldChangeXCoordinate() {

        Node node = new Node(1, 100, 200);

        node.setX(300);

        assertEquals(300, node.getX());
    }


    // Проверка изменения координаты Y
    @Test
    void shouldChangeYCoordinate() {

        Node node = new Node(1, 100, 200);

        node.setY(400);

        assertEquals(400, node.getY());
    }


    // Вершины с одинаковым id должны считаться равными
    @Test
    void shouldReturnTrueForNodesWithSameId() {

        Node node1 = new Node(1, 100, 200);
        Node node2 = new Node(1, 500, 600);

        assertEquals(node1, node2);
    }


    // Вершины с разными id не должны считаться равными
    @Test
    void shouldReturnFalseForNodesWithDifferentIds() {

        Node node1 = new Node(1, 100, 200);
        Node node2 = new Node(2, 100, 200);

        assertNotEquals(node1, node2);
    }


    // Проверка сравнения с null
    @Test
    void shouldReturnFalseWhenComparingWithNull() {

        Node node = new Node(1, 100, 200);

        assertNotEquals(null, node);
    }


    // Проверка hashCode для одинаковых вершин
    @Test
    void equalNodesShouldHaveSameHashCode() {

        Node node1 = new Node(1, 100, 200);
        Node node2 = new Node(1, 300, 400);

        assertEquals(
                node1.hashCode(),
                node2.hashCode()
        );
    }


    // Проверка строкового представления
    @Test
    void shouldReturnCorrectStringRepresentation() {

        Node node = new Node(1, 100, 200);

        assertEquals(
                "Node {id = 1, x=100.0, y=200.0}",
                node.toString()
        );
    }


    // Проверка, что id нельзя изменить
    @Test
    void shouldKeepSameIdAfterCoordinateChanges() {

        Node node = new Node(5, 100, 200);

        node.setX(300);
        node.setY(400);

        assertEquals(5, node.getId());
    }
}