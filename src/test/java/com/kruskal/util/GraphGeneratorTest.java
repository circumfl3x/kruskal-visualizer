package com.kruskal.util;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphGeneratorTest {

    @Test
    void shouldGenerateCorrectNumberOfNodes() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        assertEquals(10, graph.getNodeCount());
    }


    @Test
    void shouldGenerateCorrectNumberOfEdges() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        assertEquals(15, graph.getEdgeCount());
    }


    @Test
    void generatedEdgesShouldHavePositiveWeight() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        for (Edge edge : graph.getEdges()) {
            assertTrue(edge.getWeight() > 0);
        }
    }


    @Test
    void shouldNotGenerateLoops() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        for (Edge edge : graph.getEdges()) {
            assertNotEquals(
                    edge.getNode1(),
                    edge.getNode2()
            );
        }
    }


    @Test
    void shouldNotGenerateDuplicateEdges() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        for (Edge edge1 : graph.getEdges()) {
            for (Edge edge2 : graph.getEdges()) {

                if (edge1 != edge2) {
                    assertFalse(
                            edge1.connects(
                                    edge2.getNode1(),
                                    edge2.getNode2()
                            )
                                    &&
                                    edge1.getWeight() == edge2.getWeight()
                    );
                }
            }
        }
    }


    @Test
    void shouldThrowExceptionForInvalidNodeCount() {
        GraphGenerator generator = new GraphGenerator();

        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(0, 5)
        );
    }


    @Test
    void shouldThrowExceptionWhenTooManyEdgesRequested() {
        GraphGenerator generator = new GraphGenerator();

        // Для 3 вершин максимум 3 ребра
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(3, 10)
        );
    }

    @Test
    void shouldThrowExceptionForNegativeEdgeCount() {
        GraphGenerator generator = new GraphGenerator();

        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(5, -1)
        );
    }
}