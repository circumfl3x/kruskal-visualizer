package com.kruskal.editor;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.scene.Group;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;


class GraphEditorTest {


    // Заглушка Logger без JavaFX TextArea
    static class TestLogger extends Logger {

        public TestLogger() {
            super(null);
        }


        @Override
        public void log(String message) {
        }


        @Override
        public void clear() {
        }


        @Override
        public void logError(String errorMessage) {
        }
    }



    private GraphEditor createEditor(Graph graph) {

        return new GraphEditor(
                graph,
                new GraphRenderer(),
                new Group(),
                new TestLogger()
        );
    }



    private Graph emptyGraph(){

        return new Graph(
                new ArrayList<>(),
                new ArrayList<>()
        );
    }




    private Graph createGraph(){


        Node n1 =
                new Node(
                        0,
                        100,
                        100
                );


        Node n2 =
                new Node(
                        1,
                        200,
                        200
                );


        Edge edge =
                new Edge(
                        n1,
                        n2,
                        5
                );


        return new Graph(
                new ArrayList<>(
                        List.of(n1,n2)
                ),
                new ArrayList<>(
                        List.of(edge)
                )
        );
    }





    @Test
    void initialModeShouldBeNone(){

        GraphEditor editor =
                createEditor(emptyGraph());


        assertEquals(
                EditMode.NONE,
                editor.getMode()
        );
    }




    @Test
    void setModeShouldChangeMode(){

        GraphEditor editor =
                createEditor(emptyGraph());


        editor.setMode(
                EditMode.ADD_NODE
        );


        assertEquals(
                EditMode.ADD_NODE,
                editor.getMode()
        );
    }




    @Test
    void disableModeShouldResetMode(){

        GraphEditor editor =
                createEditor(emptyGraph());


        editor.setMode(
                EditMode.ADD_NODE
        );


        editor.disableMode();


        assertEquals(
                EditMode.NONE,
                editor.getMode()
        );
    }




    @Test
    void addNodeShouldCreateVertex(){

        Graph graph =
                emptyGraph();


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
    void deleteNodeShouldRemoveVertex(){

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
    void deleteEdgeShouldRemoveEdge(){

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
    void deleteEdgeModeClickOnNodeKeepsEdge(){

        Graph graph =
                createGraph();


        GraphEditor editor =
                createEditor(graph);


        editor.setMode(
                EditMode.DELETE_EDGE
        );


        editor.handleClick(
                100,
                100
        );


        assertEquals(
                1,
                graph.getEdgeCount()
        );
    }





    @Test
    void setGraphShouldReplaceGraph(){


        GraphEditor editor =
                createEditor(
                        emptyGraph()
                );


        Graph graph =
                createGraph();


        editor.setGraph(
                graph
        );


        assertEquals(
                2,
                graph.getNodeCount()
        );
    }





    @Test
    void graphChangedCallbackShouldRun(){


        Graph graph =
                emptyGraph();


        GraphEditor editor =
                createEditor(graph);



        AtomicBoolean changed =
                new AtomicBoolean(false);



        editor.setOnGraphChanged(
                () ->
                        changed.set(true)
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
    void nodesShouldHaveDifferentIds(){


        Graph graph =
                emptyGraph();


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


        assertNotEquals(
                graph.getNodes()
                        .get(0)
                        .getId(),

                graph.getNodes()
                        .get(1)
                        .getId()
        );
    }

}