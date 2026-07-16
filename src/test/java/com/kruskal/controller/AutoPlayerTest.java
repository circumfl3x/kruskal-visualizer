package com.kruskal.controller;

import com.kruskal.algorithm.VisualizationStep;
import com.kruskal.algorithm.KruskalAlgorithm;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import com.kruskal.model.Edge;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class AutoPlayerTest {


    @BeforeAll
    static void initJavaFX() {
        Platform.startup(() -> {});
    }


    private AutoPlayer createPlayer() {

        GraphRenderer renderer = new GraphRenderer();

        Logger logger = new Logger(
                new javafx.scene.control.TextArea()
        );

        Button button = new Button();

        TextField speed =
                new TextField("1000");

        Group graphGroup =
                new Group();

        Group mstGroup =
                new Group();


        return new AutoPlayer(
                renderer,
                logger,
                button,
                speed,
                graphGroup,
                mstGroup,
                new KruskalAlgorithm(),
                null
        );
    }


    private Graph createGraph() {

        Node n1 = new Node(1,100,100);
        Node n2 = new Node(2,200,200);

        Edge edge =
                new Edge(n1,n2,5);


        return new Graph(
                new ArrayList<>(List.of(n1,n2)),
                new ArrayList<>(List.of(edge))
        );
    }



    @Test
    void testInitialState() {

        AutoPlayer player = createPlayer();


        assertFalse(player.isPlaying());
        assertFalse(player.isPaused());
        assertNull(player.getSteps());
    }



    @Test
    void testSetSteps() {

        AutoPlayer player = createPlayer();


        List<VisualizationStep> steps =
                new ArrayList<>();


        player.setSteps(
                steps,
                2
        );


        assertEquals(
                steps,
                player.getSteps()
        );

        assertEquals(
                2,
                player.getCurrentIndex()
        );
    }



    @Test
    void testResetClearsSteps() {

        AutoPlayer player = createPlayer();


        player.setSteps(
                new ArrayList<>(),
                5
        );


        player.reset();


        assertNull(
                player.getSteps()
        );


        assertEquals(
                -1,
                player.getCurrentIndex()
        );


        assertFalse(
                player.isPlaying()
        );

        assertFalse(
                player.isPaused()
        );
    }



    @Test
    void testStopStopsPlaying() {

        AutoPlayer player = createPlayer();


        player.stop();


        assertFalse(
                player.isPlaying()
        );


        assertFalse(
                player.isPaused()
        );
    }



    @Test
    void testSetGraph() {

        AutoPlayer player = createPlayer();

        Graph graph = createGraph();


        player.setGraph(graph);


        // Проверяем косвенно:
        // если граф установлен, togglePlay не должен завершиться сразу из-за null

        assertDoesNotThrow(() ->
                player.togglePlay(() -> {})
        );
    }



    @Test
    void testTogglePlayWithEmptyGraphDoesNotStart() {

        AutoPlayer player = createPlayer();


        player.togglePlay(() -> {});


        assertFalse(
                player.isPlaying()
        );


        assertFalse(
                player.isPaused()
        );
    }



    @Test
    void testTogglePlayStartsAnimation() {

        AutoPlayer player = createPlayer();

        player.setGraph(
                createGraph()
        );


        player.togglePlay(() -> {});


        assertTrue(
                player.isPlaying()
        );


        assertFalse(
                player.isPaused()
        );


        player.stop();
    }



    @Test
    void testTogglePlayPausesAnimation() throws InterruptedException {

        AutoPlayer player = createPlayer();

        player.setGraph(
                createGraph()
        );


        player.togglePlay(() -> {});


        assertTrue(
                player.isPlaying()
        );


        player.togglePlay(() -> {});


        assertTrue(
                player.isPaused()
        );


        player.stop();
    }



    @Test
    void testTogglePlayResumeAfterPause() {

        AutoPlayer player = createPlayer();

        player.setGraph(
                createGraph()
        );


        player.togglePlay(() -> {});

        player.togglePlay(() -> {});


        assertTrue(
                player.isPaused()
        );


        player.togglePlay(() -> {});


        assertFalse(
                player.isPaused()
        );


        player.stop();
    }

}