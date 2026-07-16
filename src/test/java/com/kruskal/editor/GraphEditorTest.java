package com.kruskal.editor;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import com.kruskal.visualisation.GraphRenderer;
import javafx.scene.Group;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;


class GraphEditorTest {


    private GraphEditor createEditor(Graph graph) {

        return new GraphEditor(
                graph,
                new GraphRenderer(),
                new Group()
        );
    }


    private Graph createGraph() {

        Node n1 = new Node(0, 100, 100);
        Node n2 = new Node(1, 200, 200);

        Edge edge = new Edge(
                n1,
                n2,
                5
        );

        return new Graph(
                new ArrayList<>(List.of(n1,n2)),
                new ArrayList<>(List.of(edge))
        );
    }



    @Test
    void testInitialModeIsNone() {

        GraphEditor editor =
                createEditor(new Graph(
                        new ArrayList<>(),
                        new ArrayList<>()
                ));


        assertEquals(
                EditMode.NONE,
                editor.getMode()
        );
    }



    @Test
    void testSetModeChangesMode() {

        GraphEditor editor =
                createEditor(
                        new Graph(
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                );


        editor.setMode(
                EditMode.ADD_NODE
        );


        assertEquals(
                EditMode.ADD_NODE,
                editor.getMode()
        );
    }



    @Test
    void testDisableModeSetsNone() {

        GraphEditor editor =
                createEditor(
                        new Graph(
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                );


        editor.setMode(EditMode.ADD_NODE);

        editor.disableMode();


        assertEquals(
                EditMode.NONE,
                editor.getMode()
        );
    }



    @Test
    void testAddNode() {

        Graph graph =
                new Graph(
                        new ArrayList<>(),
                        new ArrayList<>()
                );


        GraphEditor editor =
                createEditor(graph);


        editor.setMode(
                EditMode.ADD_NODE
        );


        editor.handleClick(
                150,
                150
        );


        assertEquals(
                1,
                graph.getNodeCount()
        );


        Node node =
                graph.getNodes().get(0);


        assertEquals(
                150,
                node.getX()
        );

        assertEquals(
                150,
                node.getY()
        );
    }



    @Test
    void testDeleteNode() {

        Graph graph =
                createGraph();


        GraphEditor editor =
                createEditor(graph);


        editor.setMode(
                EditMode.DELETE_NODE
        );


        editor.handleClick(
                100,
                100
        );


        assertEquals(
                1,
                graph.getNodeCount()
        );
    }



    @Test
    void testDeleteEdgeByClickOnEdge() {

        Graph graph =
                createGraph();


        GraphEditor editor =
                createEditor(graph);


        editor.setMode(
                EditMode.DELETE_EDGE
        );


        editor.handleClick(
                150,
                150
        );


        assertEquals(
                0,
                graph.getEdgeCount()
        );
    }



    @Test
    void testClickOnNodeDoesNotDeleteEdge() {

        Graph graph =
                createGraph();


        GraphEditor editor =
                createEditor(graph);


        editor.setMode(
                EditMode.DELETE_EDGE
        );


        // клик по вершине
        editor.handleClick(
                100,
                100
        );


        // ребро должно остаться

        assertEquals(
                1,
                graph.getEdgeCount()
        );
    }



    @Test
    void testSetGraphReplacesGraph() {

        GraphEditor editor =
                createEditor(
                        new Graph(
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                );


        Graph newGraph =
                createGraph();


        editor.setGraph(
                newGraph
        );


        editor.setMode(
                EditMode.DELETE_NODE
        );


        editor.handleClick(
                100,
                100
        );


        assertEquals(
                1,
                newGraph.getNodeCount()
        );
    }



    @Test
    void testGraphChangedCallbackCalled() {

        Graph graph =
                new Graph(
                        new ArrayList<>(),
                        new ArrayList<>()
                );


        GraphEditor editor =
                createEditor(graph);


        AtomicBoolean changed =
                new AtomicBoolean(false);


        editor.setOnGraphChanged(
                () -> changed.set(true)
        );


        editor.setMode(
                EditMode.ADD_NODE
        );


        editor.handleClick(
                50,
                50
        );


        assertTrue(
                changed.get()
        );
    }



    @Test
    void testNodeIdsAreUnique() {

        Graph graph =
                new Graph(
                        new ArrayList<>(),
                        new ArrayList<>()
                );


        GraphEditor editor =
                createEditor(graph);


        editor.setMode(
                EditMode.ADD_NODE
        );


        editor.handleClick(
                50,
                50
        );


        editor.handleClick(
                100,
                100
        );


        assertEquals(
                0,
                graph.getNodes().get(0).getId()
        );


        assertEquals(
                1,
                graph.getNodes().get(1).getId()
        );
    }
}