package com.kruskal.algorithm;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KruskalAlgorithmTest {


    private Graph createConnectedGraph() {

        Node n1 = new Node(1, 100, 100);
        Node n2 = new Node(2, 200, 100);
        Node n3 = new Node(3, 300, 100);

        Edge e1 = new Edge(n1, n2, 1);
        Edge e2 = new Edge(n2, n3, 2);
        Edge e3 = new Edge(n1, n3, 10);

        return new Graph(
                new ArrayList<>(List.of(n1, n2, n3)),
                new ArrayList<>(List.of(e1, e2, e3))
        );
    }


    @Test
    void testExecuteBuildsMST() {

        // Arrange
        KruskalAlgorithm algorithm = new KruskalAlgorithm();
        Graph graph = createConnectedGraph();

        // Act
        List<AlgorithmStep> steps = algorithm.execute(graph);

        // Assert
        assertFalse(steps.isEmpty());

        long addedEdges = steps.stream()
                .filter(AlgorithmStep::isAdded)
                .count();

        assertEquals(2, addedEdges);
    }


    @Test
    void testFindMSTReturnsCorrectEdgesCount() {

        // Arrange
        KruskalAlgorithm algorithm = new KruskalAlgorithm();
        Graph graph = createConnectedGraph();

        // Act
        List<Edge> mst = algorithm.findMST(graph);

        // Assert
        assertEquals(2, mst.size());

        assertEquals(1, mst.get(0).getWeight());
        assertEquals(2, mst.get(1).getWeight());
    }


    @Test
    void testCalculateMSTWeight() {

        // Arrange
        KruskalAlgorithm algorithm = new KruskalAlgorithm();
        Graph graph = createConnectedGraph();

        // Act
        int weight = algorithm.calculateMSTWeight(graph);

        // Assert
        assertEquals(3, weight);
    }


    @Test
    void testCycleEdgeIsRejected() {

        // Arrange
        KruskalAlgorithm algorithm = new KruskalAlgorithm();
        Graph graph = createConnectedGraph();

        // Act
        List<AlgorithmStep> steps = algorithm.execute(graph);

        // Assert
        boolean hasRejectedEdge = steps.stream()
                .anyMatch(step -> !step.isAdded());

        assertTrue(hasRejectedEdge);
    }


    @Test
    void testEdgesProcessedInIncreasingWeightOrder() {

        // Arrange
        KruskalAlgorithm algorithm = new KruskalAlgorithm();
        Graph graph = createConnectedGraph();

        // Act
        List<AlgorithmStep> steps = algorithm.execute(graph);


        List<Integer> addedWeights = steps.stream()
                .filter(AlgorithmStep::isAdded)
                .map(step -> step.getEdge().getWeight())
                .toList();


        // Assert
        assertEquals(List.of(1, 2), addedWeights);
    }


    @Test
    void testEmptyGraphThrowsException() {

        // Arrange
        KruskalAlgorithm algorithm = new KruskalAlgorithm();

        Graph emptyGraph = new Graph(
                new ArrayList<>(),
                new ArrayList<>()
        );


        // Act + Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> algorithm.execute(emptyGraph)
                );


        assertEquals("Граф пуст", exception.getMessage());
    }


    @Test
    void testExecuteWithStatesCreatesSteps() {

        // Arrange
        KruskalAlgorithm algorithm = new KruskalAlgorithm();
        Graph graph = createConnectedGraph();


        // Act
        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);


        // Assert
        assertFalse(steps.isEmpty());

        assertNotNull(steps.get(0));

        assertEquals(
                "Алгоритм Краскала начат. Рёбра отсортированы по весу.",
                steps.get(0).getDescription()
        );
    }


    @Test
    void testFinalStateContainsMSTInformation() {

        // Arrange
        KruskalAlgorithm algorithm = new KruskalAlgorithm();
        Graph graph = createConnectedGraph();


        // Act
        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);

        VisualizationStep lastStep =
                steps.get(steps.size() - 1);


        // Assert
        assertTrue(
                lastStep.getDescription()
                        .contains("Минимальное остовное дерево содержит")
        );

        assertTrue(
                lastStep.getDescription()
                        .contains("Общий вес дерева: 3")
        );
    }
}