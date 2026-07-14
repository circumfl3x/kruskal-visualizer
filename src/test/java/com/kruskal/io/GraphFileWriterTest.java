package com.kruskal.io;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphFileWriterTest {

    @TempDir
    Path tempDir;

    // Проверка создания файла
    @Test
    void shouldCreateFile() throws IOException {
        GraphFileWriter writer = new GraphFileWriter();

        Graph graph = createGraph();

        Path file = tempDir.resolve("graph.txt");

        writer.write(graph, file.toString());

        assertTrue(Files.exists(file));
    }

    // Проверка записи количества вершин
    @Test
    void shouldWriteCorrectVertexCount() throws IOException {
        GraphFileWriter writer = new GraphFileWriter();

        Graph graph = createGraph();

        Path file = tempDir.resolve("graph.txt");
        writer.write(graph, file.toString());

        List<String> lines = Files.readAllLines(file);

        assertEquals("COORDS", lines.get(0));
        assertEquals("2", lines.get(1));
    }

    // Проверка записи координат вершин
    @Test
    void shouldWriteNodeCoordinates() throws IOException {
        GraphFileWriter writer = new GraphFileWriter();

        Graph graph = createGraph();

        Path file = tempDir.resolve("graph.txt");
        writer.write(graph, file.toString());

        List<String> lines = Files.readAllLines(file);

        assertEquals("0 100.0 100.0", lines.get(2));
        assertEquals("1 200.0 200.0", lines.get(3));
    }

    // Проверка записи рёбер
    @Test
    void shouldWriteEdges() throws IOException {
        GraphFileWriter writer = new GraphFileWriter();

        Graph graph = createGraph();

        Path file = tempDir.resolve("graph.txt");
        writer.write(graph, file.toString());

        List<String> lines = Files.readAllLines(file);

        assertTrue(lines.contains("0 1 15"));
    }

    // Проверка null-графа
    @Test
    void shouldThrowExceptionWhenGraphIsNull() {
        GraphFileWriter writer = new GraphFileWriter();

        assertThrows(
                IllegalArgumentException.class,
                () -> writer.write(null, "graph.txt")
        );
    }

    // Проверка null имени файла
    @Test
    void shouldThrowExceptionWhenFileNameIsNull() {
        GraphFileWriter writer = new GraphFileWriter();

        Graph graph = createGraph();

        assertThrows(
                IllegalArgumentException.class,
                () -> writer.write(graph, null)
        );
    }

    // Проверка пустого имени файла
    @Test
    void shouldThrowExceptionWhenFileNameIsBlank() {
        GraphFileWriter writer = new GraphFileWriter();

        Graph graph = createGraph();

        assertThrows(
                IllegalArgumentException.class,
                () -> writer.write(graph, "   ")
        );
    }

    // Создание тестового графа
    private Graph createGraph() {
        Graph graph = new Graph(new ArrayList<>(), new ArrayList<>());

        Node node0 = new Node(0, 100, 100);
        Node node1 = new Node(1, 200, 200);

        graph.addNode(node0);
        graph.addNode(node1);

        graph.addEdge(new Edge(node0, node1, 15));

        return graph;
    }
}