package com.kruskal.controller;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Обрабатывает события мыши на холсте графа.
 *
 * Отвечает за клики, перетаскивание вершин и определение порога
 * для различения клика от драга. Все действия делегирует
 * {@link GraphManager} и GraphEditor.
 *
 */
public class GraphInteractionHandler {
    private final GraphManager graphManager;
    private final Pane graphPane;
    private boolean dragged = false;
    private double mousePressX, mousePressY;
    private boolean dragging;

    public GraphInteractionHandler(GraphManager graphManager, Pane graphPane) {
        this.graphManager = graphManager;
        this.graphPane = graphPane;
        attachHandlers();
    }

    private void attachHandlers() {
        graphPane.setOnMouseClicked(this::onMouseClicked);
        graphPane.setOnMousePressed(this::onMousePressed);
        graphPane.setOnMouseDragged(this::onMouseDragged);
        graphPane.setOnMouseReleased(this::onMouseReleased);
    }

    private void onMouseClicked(MouseEvent event) {
        if (dragged) {
            dragged = false;
            return;
        }
        double x = event.getX();
        double y = event.getY();
        double radius = 20;
        double maxX = graphPane.getWidth() - radius;
        double maxY = graphPane.getHeight() - radius;
        x = Math.clamp(x, radius, maxX);
        y = Math.clamp(y, radius, maxY);
        graphManager.handleEditorClick(x, y);
    }

    private void onMousePressed(MouseEvent event) {
        mousePressX = event.getX();
        mousePressY = event.getY();
        dragging = false;
        graphManager.handleEditorMousePressed(event.getX(), event.getY());
    }

    private void onMouseDragged(MouseEvent event) {
        double dx = event.getX() - mousePressX;
        double dy = event.getY() - mousePressY;
        double dragThreshold = 5.0;
        if (Math.sqrt(dx*dx + dy*dy) > dragThreshold) {
            dragging = true;
            graphManager.handleEditorMouseDragged(event.getX(), event.getY());
        }
    }

    private void onMouseReleased(MouseEvent event) {
        graphManager.handleEditorMouseReleased();
        // Флаг dragged сбрасывается в onMouseClicked
    }
}