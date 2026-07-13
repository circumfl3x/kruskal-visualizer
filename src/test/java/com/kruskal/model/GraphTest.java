package com.kruskal.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    // Проверка создания пустого графа
    @Test
    void shouldCreateEmptyGraph() {

        Graph graph = new Graph(
                new ArrayList<>(),
                new ArrayList<>()
        );

        assertTrue(graph.isEmpty());
        assertEquals(0, graph.getNodeCount());
        assertEquals(0, graph.getEdgeCount());
    }


    // Проверка добавления вершины
    @Test
    void shouldAddNode() {

        Graph graph = new Graph(
                new ArrayList<>(),
                new ArrayList<>()
        );

        Node node = new Node(1, 100, 200);

        graph.addNode(node);

        assertEquals(1, graph.getNodeCount());
        assertTrue(graph.getNodes().contains(node));
    }


    // Проверка, что одинаковая вершина не добавляется дважды
    @Test
    void shouldNotAddDuplicateNode() {

        Graph graph = new Graph(
                new ArrayList<>(),
                new ArrayList<>()
        );

        Node node1 = new Node(1, 100, 200);
        Node node2 = new Node(1, 300, 400);

        graph.addNode(node1);
        graph.addNode(node2);

        assertEquals(1, graph.getNodeCount());
    }


    // Проверка удаления вершины
    @Test
    void shouldRemoveNode() {

        Graph graph = new Graph(
                new ArrayList<>(),
                new ArrayList<>()
        );

        Node node = new Node(1, 100, 200);

        graph.addNode(node);
        graph.removeNode(node);

        assertEquals(0, graph.getNodeCount());
    }


    // Проверка добавления ребра между существующими вершинами
    @Test
    void shouldAddEdge() {

        Graph graph = new Graph(
                new ArrayList<>(),
                new ArrayList<>()
        );

        Node node1 = new Node(1, 100, 200);
        Node node2 = new Node(2, 300, 400);

        Edge edge = new Edge(node1, node2, 5);

        graph.addNode(node1);
        graph.addNode(node2);

        graph.addEdge(edge);

        assertEquals(1, graph.getEdgeCount());
        assertTrue(graph.getEdges().contains(edge));
    }


    // Проверка, что нельзя добавить ребро с отсутствующей вершиной
    @Test
    void shouldThrowExceptionWhenAddingEdgeWithMissingNode() {

        Graph graph = new Graph(
                new ArrayList<>(),
                new ArrayList<>()
        );

        Node node1 = new Node(1, 100, 200);
        Node node2 = new Node(2, 300, 400);

        Edge edge = new Edge(node1, node2, 5);

        graph.addNode(node1);

        assertThrows(
                IllegalArgumentException.class,
                () -> graph.addEdge(edge)
        );
    }


    // Проверка удаления ребра
    @Test
    void shouldRemoveEdge() {

        Graph graph = createGraph();

        Edge edge = graph.getEdges().get(0);

        graph.removeEdge(edge);

        assertEquals(0, graph.getEdgeCount());
    }


    // Проверка поиска вершины по id
    @Test
    void shouldFindNodeById() {

        Graph graph = createGraph();

        Node result = graph.getNodeById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }


    // Проверка поиска отсутствующей вершины
    @Test
    void shouldReturnNullWhenNodeDoesNotExist() {

        Graph graph = createGraph();

        assertNull(graph.getNodeById(100));
    }


    // Проверка получения рёбер вершины
    @Test
    void shouldReturnEdgesOfNode() {

        Graph graph = createGraph();

        Node node = graph.getNodeById(1);

        assertEquals(
                1,
                graph.getEdgesOf(node).size()
        );
    }


    // Проверка очистки графа
    @Test
    void shouldClearGraph() {

        Graph graph = createGraph();

        graph.clear();

        assertTrue(graph.isEmpty());
        assertEquals(0, graph.getEdgeCount());
    }


    // Проверка строки графа
    @Test
    void shouldReturnCorrectStringRepresentation() {

        Graph graph = createGraph();

        assertEquals(
                "Graph{nodes=2, edges=1}",
                graph.toString()
        );
    }


    // Вспомогательный метод создания тестового графа
    private Graph createGraph() {

        Node node1 = new Node(1, 100, 200);
        Node node2 = new Node(2, 300, 400);

        Edge edge = new Edge(node1, node2, 5);

        Graph graph = new Graph(
                new ArrayList<>(),
                new ArrayList<>()
        );

        graph.addNode(node1);
        graph.addNode(node2);
        graph.addEdge(edge);

        return graph;
    }
}