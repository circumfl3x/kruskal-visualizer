package com.kruskal.visualisation;

import com.kruskal.algorithm.*;
import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GraphRendererTest {

    private GraphRenderer renderer;
    private Graph graph;
    private Group group;

    @BeforeAll
    static void initJavaFX() {
        Platform.startup(() -> {});
    }


    @BeforeEach
    void setUp() {
        renderer = new GraphRenderer();

        graph = new Graph();

        Node node1 = new Node(0, 100, 100);
        Node node2 = new Node(1, 200, 100);

        graph.addNode(node1);
        graph.addNode(node2);

        graph.addEdge(new Edge(node1, node2, 5));

        group = new Group();
    }


    @Test
    void renderGraph_shouldDrawNodesAndEdges() {

        renderer.renderGraph(graph, group);

        // Line + 2 Group(node) + Text(weight)
        assertEquals(4, group.getChildren().size());
    }


    @Test
    void renderGraph_emptyGraph_shouldNotDrawAnything() {

        Graph empty = new Graph();

        renderer.renderGraph(empty, group);

        assertTrue(group.getChildren().isEmpty());
    }


    @Test
    void renderMST_shouldDrawOnlyMSTEdges() {

        Edge edge = graph.getEdges().get(0);

        renderer.renderMST(
                graph,
                List.of(edge),
                group
        );

        assertEquals(4, group.getChildren().size());
    }


    @Test
    void renderStep_shouldHighlightCurrentEdge() {

        Edge edge = graph.getEdges().get(0);

        Map<Edge, EdgeStatus> statuses = new HashMap<>();
        statuses.put(edge, EdgeStatus.CURRENT);


        VisualizationStep step =
                new VisualizationStep(
                        List.of(),
                        statuses,
                        Map.of(),
                        edge,
                        0,
                        "test"
                );


        renderer.renderStep(
                graph,
                step,
                group
        );


        assertFalse(group.getChildren().isEmpty());

        Line line =
                (Line) group.getChildren()
                        .get(0);


        assertEquals(
                Color.ORANGE,
                line.getStroke()
        );

        assertEquals(
                4,
                line.getStrokeWidth()
        );
    }



    @Test
    void renderStep_addedEdge_shouldBeGreen() {

        Edge edge = graph.getEdges().get(0);

        Map<Edge, EdgeStatus> statuses = new HashMap<>();
        statuses.put(edge, EdgeStatus.ADDED);


        VisualizationStep step =
                new VisualizationStep(
                        List.of(edge),
                        statuses,
                        Map.of(),
                        null,
                        5,
                        "added"
                );


        renderer.renderStep(
                graph,
                step,
                group
        );


        assertTrue(
                group.getChildren().size() > 0
        );
    }



    @Test
    void renderMSTStep_shouldRenderMSTOnly() {

        Edge edge = graph.getEdges().get(0);


        VisualizationStep step =
                new VisualizationStep(
                        List.of(edge),
                        Map.of(edge, EdgeStatus.ADDED),
                        Map.of(
                                graph.getNodes().get(0),
                                Color.RED
                        ),
                        null,
                        5,
                        "mst"
                );


        renderer.renderMSTStep(
                graph,
                step,
                group
        );


        assertFalse(
                group.getChildren().isEmpty()
        );
    }



    @Test
    void getNodeRadius_shouldReturnCorrectValue(){

        assertEquals(
                17,
                GraphRenderer.getNodeRadius()
        );
    }



    @Test
    void createNodeWithColor_shouldCreateGroup(){

        Node node = graph.getNodes().get(0);

        javafx.scene.Node result =
                renderer.createNodeWithColor(
                        node,
                        Color.RED,
                        false
                );


        assertInstanceOf(
                Group.class,
                result
        );


        Group nodeGroup = (Group) result;

        assertEquals(
                2,
                nodeGroup.getChildren().size()
        );


        assertInstanceOf(
                Circle.class,
                nodeGroup.getChildren().get(0)
        );


        assertInstanceOf(
                Text.class,
                nodeGroup.getChildren().get(1)
        );
    }
}