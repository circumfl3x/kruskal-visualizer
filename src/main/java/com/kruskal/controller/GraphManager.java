package com.kruskal.controller;

import com.kruskal.editor.EditMode;
import com.kruskal.editor.GraphEditor;
import com.kruskal.io.GraphFileReader;
import com.kruskal.io.GraphFileWriter;
import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import com.kruskal.util.GraphGenerator;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.scene.Group;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Центральный менеджер для управления графом и его состоянием.
 */
public class GraphManager {
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

        editor.setOnGraphChanged(() -> {
            renderGraph();
            if (autoPlayer != null) autoPlayer.setGraph(editor.getGraph());
            if (onGraphChanged != null) onGraphChanged.run();
        });
    }

    public Graph getCurrentGraph() {
        return editor.getGraph();
    }

    public void setCurrentGraph(Graph graph) {
        editor.setGraph(graph);
        if (autoPlayer != null) autoPlayer.setGraph(graph);
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
        Graph graph = getCurrentGraph();
        if (graph == null || graph.isEmpty()) {
            throw new IOException("Нет графа для сохранения.");
        }
        fileWriter.write(graph, file.getAbsolutePath());
        logger.logGraphSaved(file.getName());
    }

    public void clear() {
        Graph graph = getCurrentGraph();
        if (graph != null) {
            graph.clear();
        }
        graphGroup.getChildren().clear();
        mstGroup.getChildren().clear();
        logger.clear();
        editor.disableMode();
        if (autoPlayer != null) autoPlayer.reset();
        if (onGraphChanged != null) onGraphChanged.run();
    }

    public void setOnGraphChanged(Runnable onGraphChanged) {
        this.onGraphChanged = onGraphChanged;
    }

    public void renderGraph() {
        Graph graph = getCurrentGraph();
        List<Node> highlightedNodes = editor.getHighlightedNodes();
        Edge highlightedEdge = editor.getHighlightedEdge();
        renderer.renderGraph(graph, graphGroup, highlightedNodes, highlightedEdge);
    }

    public void setAutoPlayer(AutoPlayer autoPlayer) {
        this.autoPlayer = autoPlayer;
    }

    public GraphEditor getEditor() {
        return editor;
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