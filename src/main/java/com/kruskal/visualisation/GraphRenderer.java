package com.kruskal.visualisation;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.FontWeight;


import java.util.List;

/**
 * Отрисовка графов на холстах.
 */
public class GraphRenderer {

    private static final double NODE_RADIUS = 17;
    private static final double EDGE_WEIGHT_OFFSET = 2;
    private static final double FONT_SIZE = 18;
    private static final Color NODE_COLOR = Color.DARKGRAY;
    private static final Color NODE_STROKE = Color.BLACK;
    private static final Color EDGE_COLOR = Color.BLACK;
    private static final Color EDGE_WEIGHT_COLOR = Color.DARKBLUE;
    private static final Color MST_EDGE_COLOR = Color.GREEN;
    private static final Color MST_EDGE_STROKE = Color.DARKGREEN;

    /**
     * Очищает холст.
     */
    public void clearCanvas(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Рисует полный граф: вершины и рёбра с весами.
     */
    public void drawGraph(Graph graph, Canvas canvas) {
        clearCanvas(canvas);

        if (graph == null || graph.isEmpty()) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Рисуем рёбра
        for (Edge edge : graph.getEdges()) {
            drawEdge(gc, edge, EDGE_COLOR, false);
        }

        // Рисуем вершины поверх рёбер
        for (Node node : graph.getNodes()) {
            drawNode(gc, node, NODE_COLOR, NODE_STROKE);
        }

        // Рисуем веса поверх всего
        for (Edge edge : graph.getEdges()) {
            drawEdgeWeight(gc, edge);
        }
    }

    /**
     * Рисует только вершины графа (без рёбер).
     */
    public void drawNodesOnly(Graph graph, Canvas canvas) {
        clearCanvas(canvas);

        if (graph == null || graph.isEmpty()) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        for (Node node : graph.getNodes()) {
            drawNode(gc, node, NODE_COLOR, NODE_STROKE);
        }
    }

    /**
     * Рисует минимальное остовное дерево.
     */
    public void drawMST(Graph graph, List<Edge> mstEdges, Canvas canvas) {
        clearCanvas(canvas);

        if (graph == null || graph.isEmpty()) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Рисуем рёбра MST
        for (Edge edge : mstEdges) {
            drawEdge(gc, edge, MST_EDGE_COLOR, true);
        }

        // Рисуем вершины поверх рёбер
        for (Node node : graph.getNodes()) {
            drawNode(gc, node, NODE_COLOR, NODE_STROKE);
        }

        // Рисуем веса поверх всего
        for (Edge edge : mstEdges) {
            drawEdgeWeight(gc, edge);
        }
    }

    /**
     * Рисует вершину.
     */
    public void drawNode(GraphicsContext gc, Node node, Color fill, Color stroke) {
        double x = node.getX();
        double y = node.getY();

        // Тень
        gc.setFill(Color.rgb(0, 0, 0, 0.1));
        gc.fillOval(x - NODE_RADIUS + 3, y - NODE_RADIUS + 3,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setFill(fill);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setStroke(stroke);
        gc.setLineWidth(2);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setFill(getContrastColor(fill));
        gc.setFont(Font.font(FONT_SIZE));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(node.getId()), x, y + 5);
    }


    /**
     * Рисует ребро (без веса).
     */
    public void drawEdge(GraphicsContext gc, Edge edge, Color color, boolean isMSTEdge) {
        Node node1 = edge.getNode1();
        Node node2 = edge.getNode2();

        double x1 = node1.getX();
        double y1 = node1.getY();
        double x2 = node2.getX();
        double y2 = node2.getY();

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double startX = x1 + NODE_RADIUS * Math.cos(angle);
        double startY = y1 + NODE_RADIUS * Math.sin(angle);
        double endX = x2 - NODE_RADIUS * Math.cos(angle);
        double endY = y2 - NODE_RADIUS * Math.sin(angle);

        // Тень для MST
        if (isMSTEdge) {
            gc.setStroke(Color.rgb(0, 100, 0, 0.2));
            gc.setLineWidth(6);
            gc.strokeLine(startX, startY, endX, endY);
        }

        gc.setStroke(color);
        gc.setLineWidth(isMSTEdge ? 4 : 2);
        gc.strokeLine(startX, startY, endX, endY);

        if (isMSTEdge) {
            gc.setStroke(MST_EDGE_STROKE);
            gc.setLineWidth(1);
            gc.strokeLine(startX, startY, endX, endY);
        }
    }

    /**
     * Рисует вес ребра поверх всего.
     */
    private void drawEdgeWeight(GraphicsContext gc, Edge edge) {
        Node node1 = edge.getNode1();
        Node node2 = edge.getNode2();

        double x1 = node1.getX();
        double y1 = node1.getY();
        double x2 = node2.getX();
        double y2 = node2.getY();

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;
        double perpAngle = angle + Math.PI / 2;
        double offsetX = EDGE_WEIGHT_OFFSET * Math.cos(perpAngle);
        double offsetY = EDGE_WEIGHT_OFFSET * Math.sin(perpAngle);

        gc.setFill(EDGE_WEIGHT_COLOR);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, FONT_SIZE));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(edge.getWeight()), midX + offsetX, midY + offsetY);
    }

    /**
     * Рисует вершину с произвольным цветом.
     */
    public void drawNodeWithColor(GraphicsContext gc, Node node, Color color, boolean isHighlighted) {
        double x = node.getX();
        double y = node.getY();

        gc.setFill(color);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setStroke(isHighlighted ? Color.GOLD : NODE_STROKE);
        gc.setLineWidth(isHighlighted ? 4 : 2);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setFill(getContrastColor(color));
        gc.setFont(Font.font(FONT_SIZE));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(node.getId()), x, y);
    }

    /**
     * Рисует ребро с произвольным стилем.
     */
    public void drawEdgeWithStyle(GraphicsContext gc, Edge edge, Color color,
                                  double width, boolean showWeight) {
        Node node1 = edge.getNode1();
        Node node2 = edge.getNode2();

        double x1 = node1.getX();
        double y1 = node1.getY();
        double x2 = node2.getX();
        double y2 = node2.getY();

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double startX = x1 + NODE_RADIUS * Math.cos(angle);
        double startY = y1 + NODE_RADIUS * Math.sin(angle);
        double endX = x2 - NODE_RADIUS * Math.cos(angle);
        double endY = y2 - NODE_RADIUS * Math.sin(angle);

        gc.setStroke(color);
        gc.setLineWidth(width);
        gc.strokeLine(startX, startY, endX, endY);

        if (showWeight) {
            double midX = (x1 + x2) / 2;
            double midY = (y1 + y2) / 2;
            double perpAngle = angle + Math.PI / 2;
            double offsetX = EDGE_WEIGHT_OFFSET * Math.cos(perpAngle);
            double offsetY = EDGE_WEIGHT_OFFSET * Math.sin(perpAngle);

            gc.setFill(EDGE_WEIGHT_COLOR);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, FONT_SIZE));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(edge.getWeight()), midX + offsetX, midY + offsetY);
        }
    }

    private Color getContrastColor(Color color) {
        double brightness = (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) / 1000;
        return brightness > 0.6 ? Color.BLACK : Color.WHITE;
    }

    public static double getNodeRadius() {
        return NODE_RADIUS;
    }
}
