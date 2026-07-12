package com.kruskal.io;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GraphFileReaderTest {

    // Проверка успешного чтения графа из файла
    @Test
    void shouldReadGraphFromFile() throws IOException {

        Path file = Files.createTempFile("graph", ".txt");

        Files.writeString(file, """
                3
                0 100 100
                1 200 200
                2 300 300
                0 1 5
                1 2 10
                """);

        GraphFileReader reader = new GraphFileReader();

        Graph graph = reader.read(file.toString());

        assertEquals(3, graph.getNodeCount());
        assertEquals(2, graph.getEdgeCount());
    }

    // Проверка чтения координат вершины
    @Test
    void shouldReadNodeCoordinates() throws IOException {

        Path file = Files.createTempFile("graph", ".txt");

        Files.writeString(file, """
                2
                0 150 250
                1 400 500
                0 1 7
                """);

        Graph graph = new GraphFileReader().read(file.toString());

        Node node = graph.getNodeById(0);

        assertEquals(150, node.getX());
        assertEquals(250, node.getY());
    }

    // Проверка чтения веса ребра
    @Test
    void shouldReadEdgeWeight() throws IOException {

        Path file = Files.createTempFile("graph", ".txt");

        Files.writeString(file, """
                2
                0 100 100
                1 200 200
                0 1 25
                """);

        Graph graph = new GraphFileReader().read(file.toString());

        Edge edge = graph.getEdges().get(0);

        assertEquals(25, edge.getWeight());
    }

    // Проверка обработки пустого файла
    @Test
    void shouldThrowExceptionForEmptyFile() throws IOException {

        Path file = Files.createTempFile("graph", ".txt");

        Files.writeString(file, "");

        GraphFileReader reader = new GraphFileReader();

        assertThrows(
                IOException.class,
                () -> reader.read(file.toString())
        );
    }

    // Проверка обработки ребра с несуществующей вершиной
    @Test
    void shouldThrowExceptionForUnknownNode() throws IOException {

        Path file = Files.createTempFile("graph", ".txt");

        Files.writeString(file, """
                2
                0 100 100
                1 200 200
                0 5 10
                """);

        GraphFileReader reader = new GraphFileReader();

        assertThrows(
                IOException.class,
                () -> reader.read(file.toString())
        );
    }

    // Проверка обработки некорректного формата строки
    @Test
    void shouldThrowExceptionForInvalidFormat() throws IOException {

        Path file = Files.createTempFile("graph", ".txt");

        Files.writeString(file, """
                2
                0 100
                1 200 300
                """);

        GraphFileReader reader = new GraphFileReader();

        assertThrows(
                IOException.class,
                () -> reader.read(file.toString())
        );
    }
}