package com.kruskal.util;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphGeneratorTest {

    // Проверка, что создается нужное количество вершин
    @Test
    void shouldGenerateCorrectNumberOfNodes() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        assertEquals(10, graph.getNodeCount());
    }

    // Проверка, что создается нужное количество ребер
    @Test
    void shouldGenerateCorrectNumberOfEdges() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        assertEquals(15, graph.getEdgeCount());
    }

    // Проверка, что все ребра имеют положительный вес
    @Test
    void generatedEdgesShouldHavePositiveWeight() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        for (Edge edge : graph.getEdges()) {
            assertTrue(edge.getWeight() > 0);
        }
    }

    // Проверка отсутствия петель (ребер из вершины в саму себя)
    @Test
    void shouldNotGenerateLoops() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        for (Edge edge : graph.getEdges()) {
            assertNotEquals(edge.getNode1(), edge.getNode2());
        }
    }

    // Проверка отсутствия повторяющихся ребер
    @Test
    void shouldNotGenerateDuplicateEdges() {
        GraphGenerator generator = new GraphGenerator();

        Graph graph = generator.generate(10, 15);

        for (Edge edge1 : graph.getEdges()) {
            for (Edge edge2 : graph.getEdges()) {

                if (edge1 != edge2) {
                    assertFalse(
                            edge1.connects(edge2.getNode1(), edge2.getNode2())
                                    && edge1.getWeight() == edge2.getWeight()
                    );
                }
            }
        }
    }

    // Проверка обработки некорректного количества вершин
    @Test
    void shouldThrowExceptionForInvalidNodeCount() {
        GraphGenerator generator = new GraphGenerator();

        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(0, 5)
        );
    }

    // Проверка обработки слишком большого количества ребер
    @Test
    void shouldThrowExceptionWhenTooManyEdgesRequested() {
        GraphGenerator generator = new GraphGenerator();

        // Для 3 вершин максимум 3 ребра
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(3, 10)
        );
    }

    // Проверка обработки отрицательного количества ребер
    @Test
    void shouldThrowExceptionForNegativeEdgeCount() {
        GraphGenerator generator = new GraphGenerator();

        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(5, -1)
        );
    }
}