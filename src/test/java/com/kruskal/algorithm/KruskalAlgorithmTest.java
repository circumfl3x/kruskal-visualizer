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
    void executeWithStatesShouldCreateSteps() {

        KruskalAlgorithm algorithm =
                new KruskalAlgorithm();


        Graph graph =
                createConnectedGraph();


        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);


        assertNotNull(steps);

        assertFalse(
                steps.isEmpty()
        );
    }



    @Test
    void firstStepShouldContainStartMessage() {

        KruskalAlgorithm algorithm =
                new KruskalAlgorithm();


        Graph graph =
                createConnectedGraph();


        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);


        VisualizationStep first =
                steps.get(0);


        assertEquals(
                "Алгоритм Краскала начат. Рёбра отсортированы по весу.",
                first.getDescription()
        );
    }




    @Test
    void mstShouldContainTwoEdges() {

        KruskalAlgorithm algorithm =
                new KruskalAlgorithm();


        Graph graph =
                createConnectedGraph();


        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);



        VisualizationStep last =
                steps.get(
                        steps.size() - 1
                );


        assertEquals(
                2,
                last.getMstEdges().size()
        );
    }





    @Test
    void mstShouldHaveCorrectWeight() {

        KruskalAlgorithm algorithm =
                new KruskalAlgorithm();


        Graph graph =
                createConnectedGraph();



        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);



        VisualizationStep last =
                steps.get(
                        steps.size()-1
                );



        assertEquals(
                3,
                last.getTotalWeight()
        );
    }





    @Test
    void heavyEdgeShouldBeRejected() {

        KruskalAlgorithm algorithm =
                new KruskalAlgorithm();


        Graph graph =
                createConnectedGraph();



        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);



        boolean rejectedFound =
                steps.stream()
                        .anyMatch(step ->
                                step.getDescription()
                                        .contains("отклонено")
                        );



        assertTrue(
                rejectedFound
        );
    }





    @Test
    void edgesShouldBeProcessedByWeight() {

        KruskalAlgorithm algorithm =
                new KruskalAlgorithm();


        Graph graph =
                createConnectedGraph();



        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);



        VisualizationStep last =
                steps.get(
                        steps.size()-1
                );



        List<Edge> mst =
                last.getMstEdges();



        assertEquals(
                1,
                mst.get(0).getWeight()
        );


        assertEquals(
                2,
                mst.get(1).getWeight()
        );
    }





    @Test
    void emptyGraphShouldThrowException() {

        KruskalAlgorithm algorithm =
                new KruskalAlgorithm();


        Graph graph =
                new Graph(
                        new ArrayList<>(),
                        new ArrayList<>()
                );



        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                algorithm.executeWithStates(graph)
                );



        assertEquals(
                "Граф пуст",
                exception.getMessage()
        );
    }





    @Test
    void finalStepShouldContainResultInformation() {

        KruskalAlgorithm algorithm =
                new KruskalAlgorithm();


        Graph graph =
                createConnectedGraph();



        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);



        VisualizationStep last =
                steps.get(
                        steps.size()-1
                );



        String description =
                last.getDescription();



        assertTrue(
                description.contains(
                        "Алгоритм Краскала завершён"
                )
        );


        assertTrue(
                description.contains(
                        "Общий вес дерева: 3"
                )
        );
    }





    @Test
    void everyStepShouldHaveDescription() {

        KruskalAlgorithm algorithm =
                new KruskalAlgorithm();


        Graph graph =
                createConnectedGraph();



        List<VisualizationStep> steps =
                algorithm.executeWithStates(graph);



        for (VisualizationStep step : steps) {

            assertNotNull(
                    step.getDescription()
            );

            assertFalse(
                    step.getDescription().isEmpty()
            );
        }
    }

}