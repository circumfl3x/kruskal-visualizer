package com.kruskal.io;


import com.kruskal.model.Graph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class GraphFileReaderTest {


    @TempDir
    Path tempDir;



    private File createFile(String content) throws IOException {

        Path file =
                tempDir.resolve("graph.txt");

        Files.writeString(
                file,
                content
        );

        return file.toFile();
    }



    @Test
    void readGraphWithoutCoordinatesShouldCreateGraph()
            throws Exception {


        File file =
                createFile(
                        """
                        3
                        0 1 5
                        1 2 7
                        """
                );


        GraphFileReader reader =
                new GraphFileReader();


        Graph graph =
                reader.read(
                        file.getAbsolutePath()
                );


        assertEquals(
                3,
                graph.getNodeCount()
        );


        assertEquals(
                2,
                graph.getEdgeCount()
        );
    }



    @Test
    void readGraphWithCoordinatesShouldPreserveCoordinates()
            throws Exception {


        File file =
                createFile(
                        """
                        COORDS
                        2
                        10 100 200
                        20 300 400
                        10 20 5
                        """
                );


        Graph graph =
                new GraphFileReader()
                        .read(file.getAbsolutePath());


        assertEquals(
                2,
                graph.getNodeCount()
        );


        assertEquals(
                100,
                graph.getNodes()
                        .get(0)
                        .getX()
        );


        assertEquals(
                200,
                graph.getNodes()
                        .get(0)
                        .getY()
        );


        assertEquals(
                1,
                graph.getEdgeCount()
        );
    }



    @Test
    void emptyFileShouldThrowException()
            throws Exception {


        File file =
                createFile(
                        ""
                );


        assertThrows(
                IOException.class,
                () ->
                        new GraphFileReader()
                                .read(
                                        file.getAbsolutePath()
                                )
        );
    }



    @Test
    void negativeVertexCountShouldThrowException()
            throws Exception {


        File file =
                createFile(
                        "-5"
                );


        IOException exception =
                assertThrows(
                        IOException.class,
                        () ->
                                new GraphFileReader()
                                        .read(
                                                file.getAbsolutePath()
                                        )
                );


        assertEquals(
                "Количество вершин не может быть отрицательным.",
                exception.getMessage()
        );
    }



    @Test
    void duplicateNodeIdShouldThrowException()
            throws Exception {


        File file =
                createFile(
                        """
                        COORDS
                        2
                        1 100 100
                        1 200 200
                        """
                );


        assertThrows(
                IOException.class,
                () ->
                        new GraphFileReader()
                                .read(
                                        file.getAbsolutePath()
                                )
        );
    }



    @Test
    void invalidVertexFormatShouldThrowException()
            throws Exception {


        File file =
                createFile(
                        """
                        COORDS
                        1
                        1 100
                        """
                );


        assertThrows(
                IOException.class,
                () ->
                        new GraphFileReader()
                                .read(
                                        file.getAbsolutePath()
                                )
        );
    }



    @Test
    void invalidEdgeFormatShouldThrowException()
            throws Exception {


        File file =
                createFile(
                        """
                        2
                        0 1
                        """
                );


        assertThrows(
                IOException.class,
                () ->
                        new GraphFileReader()
                                .read(
                                        file.getAbsolutePath()
                                )
        );
    }



    @Test
    void negativeEdgeWeightShouldThrowException()
            throws Exception {


        File file =
                createFile(
                        """
                        2
                        0 1 -10
                        """
                );


        IOException exception =
                assertThrows(
                        IOException.class,
                        () ->
                                new GraphFileReader()
                                        .read(
                                                file.getAbsolutePath()
                                        )
                );


        assertEquals(
                "Вес ребра должен быть положительным.",
                exception.getMessage()
        );
    }



    @Test
    void edgeWithUnknownNodeShouldThrowException()
            throws Exception {


        File file =
                createFile(
                        """
                        2
                        0 5 10
                        """
                );


        IOException exception =
                assertThrows(
                        IOException.class,
                        () ->
                                new GraphFileReader()
                                        .read(
                                                file.getAbsolutePath()
                                        )
                );


        assertTrue(
                exception.getMessage()
                        .contains(
                                "несуществующую вершину"
                        )
        );
    }



    @Test
    void bomAtStartShouldBeIgnored()
            throws Exception {


        File file =
                createFile(
                        "\uFEFF2\n0 1 5"
                );


        Graph graph =
                new GraphFileReader()
                        .read(
                                file.getAbsolutePath()
                        );


        assertEquals(
                2,
                graph.getNodeCount()
        );


        assertEquals(
                1,
                graph.getEdgeCount()
        );
    }
}