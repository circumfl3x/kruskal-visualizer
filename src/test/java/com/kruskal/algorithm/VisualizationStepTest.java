package com.kruskal.algorithm;

import com.kruskal.model.Edge;
import com.kruskal.model.Node;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class VisualizationStepTest {


    private Node createNode(int id) {
        return new Node(id, id * 100, id * 100);
    }


    private Edge createEdge(Node n1, Node n2, int weight) {
        return new Edge(n1, n2, weight);
    }


    @Test
    void testConstructorAndGetters() {

        // Arrange
        Node node1 = createNode(1);
        Node node2 = createNode(2);

        Edge edge = createEdge(node1, node2, 5);

        List<Edge> mstEdges = List.of(edge);

        Map<Edge, EdgeStatus> statuses = new HashMap<>();
        statuses.put(edge, EdgeStatus.ADDED);

        Map<Node, Color> colors = new HashMap<>();
        colors.put(node1, Color.RED);
        colors.put(node2, Color.BLUE);


        VisualizationStep step = new VisualizationStep(
                mstEdges,
                statuses,
                colors,
                edge,
                5,
                "Ребро добавлено"
        );


        // Assert
        assertEquals(mstEdges, step.getMstEdges());
        assertEquals(statuses, step.getEdgeStatuses());
        assertEquals(colors, step.getNodeColors());
        assertEquals(edge, step.getCurrentEdge());
        assertEquals(5, step.getTotalWeight());
        assertEquals("Ребро добавлено", step.getDescription());
    }


    @Test
    void testIsCurrentEdgeReturnsTrueForCurrentEdge() {

        // Arrange
        Node node1 = createNode(1);
        Node node2 = createNode(2);

        Edge edge = createEdge(node1, node2, 10);

        VisualizationStep step = new VisualizationStep(
                List.of(),
                Map.of(),
                Map.of(),
                edge,
                0,
                "Текущее ребро"
        );


        // Assert
        assertTrue(step.isCurrentEdge(edge));
    }


    @Test
    void testIsCurrentEdgeReturnsFalseForDifferentEdge() {

        // Arrange
        Node node1 = createNode(1);
        Node node2 = createNode(2);
        Node node3 = createNode(3);


        Edge current = createEdge(node1, node2, 5);
        Edge other = createEdge(node2, node3, 7);


        VisualizationStep step = new VisualizationStep(
                List.of(),
                Map.of(),
                Map.of(),
                current,
                0,
                "Проверка"
        );


        // Assert
        assertFalse(step.isCurrentEdge(other));
    }


    @Test
    void testGetStatusReturnsSavedStatus() {

        // Arrange
        Node node1 = createNode(1);
        Node node2 = createNode(2);

        Edge edge = createEdge(node1, node2, 3);

        Map<Edge, EdgeStatus> statuses = Map.of(
                edge,
                EdgeStatus.REJECTED
        );


        VisualizationStep step = new VisualizationStep(
                List.of(),
                statuses,
                Map.of(),
                null,
                0,
                "Ребро отклонено"
        );


        // Assert
        assertEquals(
                EdgeStatus.REJECTED,
                step.getStatus(edge)
        );
    }


    @Test
    void testGetStatusReturnsDefaultForUnknownEdge() {

        // Arrange
        Node node1 = createNode(1);
        Node node2 = createNode(2);

        Edge edge = createEdge(node1, node2, 4);


        VisualizationStep step = new VisualizationStep(
                List.of(),
                Map.of(),
                Map.of(),
                null,
                0,
                "Нет статуса"
        );


        // Assert
        assertEquals(
                EdgeStatus.UNPROCESSED,
                step.getStatus(edge)
        );
    }


    @Test
    void testGetColorReturnsSavedColor() {

        // Arrange
        Node node = createNode(1);

        VisualizationStep step = new VisualizationStep(
                List.of(),
                Map.of(),
                Map.of(node, Color.GREEN),
                null,
                0,
                "Цвет вершины"
        );


        // Assert
        assertEquals(
                Color.GREEN,
                step.getColor(node)
        );
    }


    @Test
    void testGetColorReturnsGrayForUnknownNode() {

        // Arrange
        Node node = createNode(1);

        VisualizationStep step = new VisualizationStep(
                List.of(),
                Map.of(),
                Map.of(),
                null,
                0,
                "Цвет отсутствует"
        );


        // Assert
        assertEquals(
                Color.GRAY,
                step.getColor(node)
        );
    }


    @Test
    void testCollectionsAreImmutable() {

        // Arrange
        Node node1 = createNode(1);
        Node node2 = createNode(2);

        Edge edge = createEdge(node1, node2, 1);


        VisualizationStep step = new VisualizationStep(
                new ArrayList<>(List.of(edge)),
                new HashMap<>(),
                new HashMap<>(),
                null,
                1,
                "Проверка"
        );


        // Assert
        assertThrows(
                UnsupportedOperationException.class,
                () -> step.getMstEdges().add(edge)
        );


        assertThrows(
                UnsupportedOperationException.class,
                () -> step.getEdgeStatuses().put(edge, EdgeStatus.ADDED)
        );


        assertThrows(
                UnsupportedOperationException.class,
                () -> step.getNodeColors().put(node1, Color.RED)
        );
    }
}