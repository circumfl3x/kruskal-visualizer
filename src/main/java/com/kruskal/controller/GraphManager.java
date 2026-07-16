package com.kruskal.controller;

import com.kruskal.editor.EditMode;
import com.kruskal.editor.GraphEditor;
import com.kruskal.io.GraphFileReader;
import com.kruskal.io.GraphFileWriter;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import com.kruskal.util.GraphGenerator;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.scene.Group;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphManager {
    private Graph currentGraph;
    private final GraphRenderer renderer;
    private final GraphEditor editor;
    private AutoPlayer autoPlayer;
    private final Logger logger;
    private final GraphGenerator generator;
    private final GraphFileReader fileReader;
    private final GraphFileWriter fileWriter;
    private final Group graphGroup;
    private final Group mstGroup;
    private Runnable onGraphChanged;

    public GraphManager(GraphRenderer renderer, GraphEditor editor, AutoPlayer autoPlayer,
                        Logger logger, Group graphGroup, Group mstGroup) {
        this.renderer = renderer;
        this.editor = editor;
        this.autoPlayer = autoPlayer;
        this.logger = logger;
        this.graphGroup = graphGroup;
        this.mstGroup = mstGroup;
        this.generator = new GraphGenerator();
        this.fileReader = new GraphFileReader();
        this.fileWriter = new GraphFileWriter();
        this.currentGraph = new Graph(new ArrayList<>(), new ArrayList<>());
    }

    public void setOnGraphChanged(Runnable onGraphChanged) {
        this.onGraphChanged = onGraphChanged;
    }

    public Graph getCurrentGraph() {
        return currentGraph;
    }

    public void setCurrentGraph(Graph graph) {
        this.currentGraph = graph;
        editor.setGraph(graph);
        autoPlayer.setGraph(graph);
        if (onGraphChanged != null) onGraphChanged.run();
        renderGraph();
    }

    public void loadGraph(File file) throws IOException {
        Graph graph = fileReader.read(file.getAbsolutePath());
        setCurrentGraph(graph);
        logger.logGraphLoaded(file.getName(), graph.getNodeCount(), graph.getEdgeCount());
        mstGroup.getChildren().clear();
    }

    public void generateRandom() {
        Random random = new Random();
        int vertexCount = random.nextInt(3) + 4;
        int maxEdges = (vertexCount * (vertexCount - 1)) / 2;
        int minEdges = vertexCount - 1;
        int maxAllowedEdges = (int) (maxEdges * 0.6);
        int edgeCount;
        if (maxAllowedEdges < minEdges + 2) {
            edgeCount = Math.min(minEdges + 2, maxEdges);
        } else {
            int minEdgesWithExtra = Math.min(minEdges + 2, maxEdges);
            edgeCount = random.nextInt(maxAllowedEdges - minEdgesWithExtra + 1) + minEdgesWithExtra;
        }
        generateWithParams(vertexCount, edgeCount);
    }

    public void generateWithParams(int vertexCount, int edgeCount) {
        try {
            Graph graph = generator.generate(vertexCount, edgeCount);
            setCurrentGraph(graph);
            logger.logGraphGenerated(vertexCount, edgeCount);
            mstGroup.getChildren().clear();
        } catch (IllegalArgumentException e) {
            logger.logError("Ошибка генерации: " + e.getMessage());
        }
    }

    public void saveGraph(File file) throws IOException {
        if (currentGraph == null || currentGraph.isEmpty()) {
            throw new IOException("Нет графа для сохранения.");
        }
        fileWriter.write(currentGraph, file.getAbsolutePath());
        logger.logGraphSaved(file.getName());
    }

    public void clear() {
        if (currentGraph != null) {
            currentGraph.clear();
        }
        graphGroup.getChildren().clear();
        mstGroup.getChildren().clear();
        logger.clear();
        editor.disableMode();
        autoPlayer.reset();
        if (onGraphChanged != null) onGraphChanged.run();
    }

    public void renderGraph() {
        renderer.renderGraph(currentGraph, graphGroup, List.of());
    }

    public void setAutoPlayer(AutoPlayer autoPlayer) {
        this.autoPlayer = autoPlayer;
    }

    public void renderGraphWithHighlight(List<Node> highlighted) {
        renderer.renderGraph(currentGraph, graphGroup, highlighted);
    }

    public GraphEditor getEditor() {
        return editor;
    }

    public AutoPlayer getAutoPlayer() {
        return autoPlayer;
    }

    public void handleEditorClick(double x, double y) {
        editor.handleClick(x, y);
    }

    public void handleEditorMousePressed(double x, double y) {
        editor.handleMousePressed(x, y);
    }

    public void handleEditorMouseDragged(double x, double y) {
        editor.handleMouseDragged(x, y);
    }

    public void handleEditorMouseReleased() {
        editor.handleMouseReleased();
    }

    public void switchEditMode(EditMode mode) {
        editor.setMode(mode);
    }

    public void disableEditorMode() {
        editor.disableMode();
    }

    public void setCanvasSize(double width, double height) {
        editor.setCanvasSize(width, height);
    }
}