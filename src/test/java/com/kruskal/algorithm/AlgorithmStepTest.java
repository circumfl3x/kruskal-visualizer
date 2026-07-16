package com.kruskal.algorithm;

import com.kruskal.model.Edge;
import com.kruskal.model.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmStepTest {
    @Test
    void testConstructorAndGetters() {
        // Arrange
        Node node1 = new Node(1, 100, 100);
        Node node2 = new Node(2, 200, 200);
        Edge edge = new Edge(node1, node2, 5);
        AlgorithmStep step = new AlgorithmStep(
                edge,
                true,
                "Ребро добавлено в MST",
                5
        );

        // Act & Assert
        assertEquals(edge, step.getEdge());
        assertTrue(step.isAdded());
        assertEquals("Ребро добавлено в MST", step.getDescription());
        assertEquals(5, step.getTotalWeight());
    }


    @Test
    void testStepWithRejectedEdge() {
        // Arrange
        Node node1 = new Node(1, 50, 50);
        Node node2 = new Node(2, 150, 150);
        Edge edge = new Edge(node1, node2, 10);
        AlgorithmStep step = new AlgorithmStep(
                edge,
                false,
                "Ребро отклонено (цикл)",
                7
        );

        // Assert
        assertEquals(edge, step.getEdge());
        assertFalse(step.isAdded());
        assertEquals("Ребро отклонено (цикл)", step.getDescription());
        assertEquals(7, step.getTotalWeight());
    }


    @Test
    void testToStringReturnsDescription() {
        // Arrange
        Node node1 = new Node(1, 0, 0);
        Node node2 = new Node(2, 100, 100);
        Edge edge = new Edge(node1, node2, 3);
        String description = "Рассмотрено ребро";
        AlgorithmStep step = new AlgorithmStep(
                edge,
                true,
                description,
                3
        );

        // Act
        String result = step.toString();
        // Assert
        assertEquals(description, result);
    }


    @Test
    void testZeroWeight() {
        // Arrange
        Node node1 = new Node(1, 0, 0);
        Node node2 = new Node(2, 10, 10);

        Edge edge = new Edge(node1, node2, 0);

        AlgorithmStep step = new AlgorithmStep(
                edge,
                true,
                "Ребро с нулевым весом",
                0
        );

        // Assert
        assertEquals(0, step.getTotalWeight());
        assertEquals(edge, step.getEdge());
    }
}