package com.kruskal.controller;


import com.kruskal.editor.EditMode;
import com.kruskal.editor.GraphEditor;
import com.kruskal.model.Graph;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.kruskal.util.JavaFXTestUtil;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


class GraphManagerTest {


    @BeforeAll
    static void initJavaFX() throws Exception {

        CountDownLatch latch =
                new CountDownLatch(1);


        JavaFXTestUtil.init();


        latch.await(
                5,
                TimeUnit.SECONDS
        );
    }



    private GraphManager createManager() {


        Graph graph =
                new Graph(
                        new ArrayList<>(),
                        new ArrayList<>()
                );


        TextArea area =
                new TextArea();


        Logger logger =
                new Logger(area);


        Group graphGroup =
                new Group();


        Group mstGroup =
                new Group();


        GraphRenderer renderer =
                new GraphRenderer();


        GraphEditor editor =
                new GraphEditor(
                        graph,
                        renderer,
                        graphGroup,
                        logger
                );


        return new GraphManager(
                renderer,
                editor,
                null,
                logger,
                graphGroup,
                mstGroup
        );
    }



    @Test
    void getCurrentGraphShouldReturnEditorGraph() {

        GraphManager manager =
                createManager();


        assertNotNull(
                manager.getCurrentGraph()
        );
    }



    @Test
    void setCurrentGraphShouldReplaceGraph() {


        GraphManager manager =
                createManager();


        Graph newGraph =
                new Graph(
                        new ArrayList<>(),
                        new ArrayList<>()
                );


        manager.setCurrentGraph(
                newGraph
        );


        assertSame(
                newGraph,
                manager.getCurrentGraph()
        );
    }



    @Test
    void switchEditModeShouldChangeEditorMode() {


        GraphManager manager =
                createManager();


        manager.switchEditMode(
                EditMode.ADD_NODE
        );


        assertEquals(
                EditMode.ADD_NODE,
                manager.getEditor().getMode()
        );
    }



    @Test
    void disableEditorModeShouldSetNone() {


        GraphManager manager =
                createManager();


        manager.switchEditMode(
                EditMode.DELETE_NODE
        );


        manager.disableEditorMode();


        assertEquals(
                EditMode.NONE,
                manager.getEditor().getMode()
        );
    }



    @Test
    void clearShouldRemoveGraphData() {


        GraphManager manager =
                createManager();


        com.kruskal.model.Node node =
                new com.kruskal.model.Node(
                        0,
                        100,
                        100
                );


        Graph graph =
                new Graph(
                        new ArrayList<>(
                                java.util.List.of(node)
                        ),
                        new ArrayList<>()
                );


        manager.setCurrentGraph(
                graph
        );


        assertEquals(
                1,
                manager.getCurrentGraph()
                        .getNodeCount()
        );


        manager.clear();


        assertEquals(
                0,
                manager.getCurrentGraph()
                        .getNodeCount()
        );
    }


    @Test
    void setOnGraphChangedShouldCallCallback() {


        GraphManager manager =
                createManager();


        boolean[] changed =
                {false};


        manager.setOnGraphChanged(
                () -> changed[0] = true
        );


        manager.setCurrentGraph(
                new Graph(
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );


        assertTrue(
                changed[0]
        );
    }



    @Test
    void generateWithValidParamsShouldCreateGraph() {


        GraphManager manager =
                createManager();


        manager.generateWithParams(
                5,
                6
        );


        assertNotNull(
                manager.getCurrentGraph()
        );


        assertEquals(
                5,
                manager.getCurrentGraph()
                        .getNodeCount()
        );


        assertEquals(
                6,
                manager.getCurrentGraph()
                        .getEdgeCount()
        );
    }



    @Test
    void generateWithInvalidParamsShouldNotThrowException() {


        GraphManager manager =
                createManager();


        assertDoesNotThrow(
                () ->
                        manager.generateWithParams(
                                2,
                                100
                        )
        );
    }



    @Test
    void setCanvasSizeShouldNotThrowException() {


        GraphManager manager =
                createManager();


        assertDoesNotThrow(
                () ->
                        manager.setCanvasSize(
                                800,
                                600
                        )
        );
    }



    @Test
    void handleEditorClickShouldNotThrowException() {


        GraphManager manager =
                createManager();


        assertDoesNotThrow(
                () ->
                        manager.handleEditorClick(
                                200,
                                200
                        )
        );
    }



    @Test
    void handleMouseEventsShouldNotThrowException() {


        GraphManager manager =
                createManager();


        assertDoesNotThrow(() -> {

            manager.handleEditorMousePressed(
                    100,
                    100
            );

            manager.handleEditorMouseDragged(
                    120,
                    120
            );

            manager.handleEditorMouseReleased();

        });
    }
}