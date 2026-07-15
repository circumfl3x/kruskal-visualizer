package com.kruskal.algorithm;

import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DSUTest {


    @Test
    void testInitialFindReturnsOwnIndex() {
        // Arrange
        DSU dsu = new DSU(5);

        // Assert
        assertEquals(0, dsu.find(0));
        assertEquals(1, dsu.find(1));
        assertEquals(2, dsu.find(2));
        assertEquals(3, dsu.find(3));
        assertEquals(4, dsu.find(4));
    }


    @Test
    void testUnionConnectsTwoDifferentSets() {
        // Arrange
        DSU dsu = new DSU(5);

        // Act
        boolean result = dsu.union(0, 1);

        // Assert
        assertTrue(result);
        assertTrue(dsu.connected(0, 1));
        assertEquals(dsu.find(0), dsu.find(1));
    }


    @Test
    void testUnionReturnsFalseForSameSet() {
        // Arrange
        DSU dsu = new DSU(5);

        dsu.union(0, 1);

        // Act
        boolean result = dsu.union(0, 1);

        // Assert
        assertFalse(result);
    }


    @Test
    void testMultipleUnionsCreateOneSet() {
        // Arrange
        DSU dsu = new DSU(6);

        // Act
        dsu.union(0, 1);
        dsu.union(1, 2);
        dsu.union(2, 3);

        // Assert
        assertTrue(dsu.connected(0, 3));
        assertTrue(dsu.connected(1, 2));
        assertFalse(dsu.connected(0, 5));
    }


    @Test
    void testDifferentSetsAreNotConnected() {
        // Arrange
        DSU dsu = new DSU(4);

        dsu.union(0, 1);
        dsu.union(2, 3);

        // Assert
        assertFalse(dsu.connected(0, 2));
        assertFalse(dsu.connected(1, 3));
    }


    @Test
    void testPathCompression() {
        // Arrange
        DSU dsu = new DSU(5);

        dsu.union(0, 1);
        dsu.union(1, 2);

        // Act
        int rootBefore = dsu.find(2);
        int rootAfter = dsu.find(2);

        // Assert
        assertEquals(rootBefore, rootAfter);
        assertEquals(dsu.find(0), dsu.find(2));
    }


    @Test
    void testGetNodeColorsReturnsColorsForAllNodes() {
        // Arrange
        DSU dsu = new DSU(3);

        Node node1 = new Node(0, 10, 10);
        Node node2 = new Node(1, 20, 20);
        Node node3 = new Node(2, 30, 30);

        Graph graph = new Graph(
                new ArrayList<>(java.util.List.of(node1, node2, node3)),
                new ArrayList<>()
        );

        // Act
        Map<Node, Color> colors = dsu.getNodeColors(graph);

        // Assert
        assertEquals(3, colors.size());

        assertNotNull(colors.get(node1));
        assertNotNull(colors.get(node2));
        assertNotNull(colors.get(node3));
    }


    @Test
    void testUnionByRankKeepsConnectivity() {
        // Arrange
        DSU dsu = new DSU(8);

        // Act
        dsu.union(0, 1);
        dsu.union(2, 3);
        dsu.union(0, 2);

        // Assert
        assertTrue(dsu.connected(0, 3));
        assertTrue(dsu.connected(1, 2));
        assertEquals(dsu.find(0), dsu.find(3));
    }
}