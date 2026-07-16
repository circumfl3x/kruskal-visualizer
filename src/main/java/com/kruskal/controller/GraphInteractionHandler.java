package com.kruskal.controller;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class GraphInteractionHandler {
    private final GraphManager graphManager;
    private final Pane graphPane;
    private final double dragThreshold = 5.0;
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
        x = Math.max(radius, Math.min(x, maxX));
        y = Math.max(radius, Math.min(y, maxY));
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