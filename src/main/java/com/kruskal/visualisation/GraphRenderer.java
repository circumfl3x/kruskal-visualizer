package com.kruskal.visualisation;

import com.kruskal.algorithm.EdgeStatus;
import com.kruskal.algorithm.VisualizationStep;
import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.util.List;

/**
 * Отрисовка графов с использованием javafx-узлов.
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
     * Рендер исходного графа в указанную группу.
     */
    public void renderGraph(Graph graph, Group group, List<Node> highlightedNodes, Edge highlightedEdge) {
        group.getChildren().clear();
        if (graph == null || graph.isEmpty()) return;

        for (Edge edge : graph.getEdges()) {
            boolean isHighlighted = edge.equals(highlightedEdge);
            Color color = isHighlighted ? Color.ORANGE : EDGE_COLOR;
            double width = isHighlighted ? 4 : 2;
            group.getChildren().add(createEdgeWithStyle(edge, color, width, false));
        }

        for (Node node : graph.getNodes()) {
            boolean isHighlighted = highlightedNodes != null && highlightedNodes.contains(node);
            group.getChildren().add(createNodeWithColor(node, NODE_COLOR, isHighlighted));
        }

        for (Edge edge : graph.getEdges()) {
            group.getChildren().add(createEdgeWeight(edge));
        }
    }

    /**
     * Чуток перегрузок
     */
    public void renderGraph(Graph graph, Group group) {
        renderGraph(graph, group, List.of(), null);
    }

    public void renderGraph(Graph graph, Group group, List<Node> highlightedNodes) {
        renderGraph(graph, group, highlightedNodes, null);
    }

    /**
     * Рендер минимального остовного дерева в указанную группу.
     */
    public void renderMST(Graph graph, List<Edge> mstEdges, Group group) {
        group.getChildren().clear();
        if (graph == null || graph.isEmpty()) return;

        for (Edge edge : mstEdges) {
            group.getChildren().add(createEdge(edge, MST_EDGE_COLOR, true));
        }
        for (Node node : graph.getNodes()) {
            group.getChildren().add(createNode(node, NODE_COLOR, NODE_STROKE));
        }
        for (Edge edge : mstEdges) {
            group.getChildren().add(createEdgeWeight(edge));
        }
    }

    public void renderStep(Graph graph, VisualizationStep step, Group group) {
        group.getChildren().clear();
        if (graph == null || graph.isEmpty()) return;

        // Рёбра
        for (Edge edge : graph.getEdges()) {
            EdgeStatus status = step.getStatus(edge);
            Color color;
            double width;
            boolean isMST = false;
            switch (status) {
                case CURRENT:
                    color = Color.ORANGE;
                    width = 4;
                    break;
                case ADDED:
                    color = MST_EDGE_COLOR;
                    width = 3;
                    isMST = true;
                    break;
                case REJECTED:
                    color = Color.RED;
                    width = 2;
                    break;
                default:
                    color = EDGE_COLOR;
                    width = 2;
                    break;
            }
            group.getChildren().add(createEdgeWithStyle(edge, color, width, isMST));
        }

        // Вершины: всегда серые, подсветка жёлтым для текущего ребра
        Edge currentEdge = step.getCurrentEdge();
        for (Node node : graph.getNodes()) {
            boolean highlighted = currentEdge != null &&
                    (currentEdge.getNode1().equals(node) || currentEdge.getNode2().equals(node));
            group.getChildren().add(createNodeWithColor(node, NODE_COLOR, highlighted));
        }

        // Веса
        for (Edge edge : graph.getEdges()) {
            group.getChildren().add(createEdgeWeight(edge));
        }
    }

    /**
     * Рендер текущего шага на правом холсте.
     */
    public void renderMSTStep(Graph graph, VisualizationStep step, Group group) {
        group.getChildren().clear();
        if (graph == null || graph.isEmpty()) return;

        // 1. Если есть текущее ребро, рисуем его пунктиром
        Edge currentEdge = step.getCurrentEdge();
        if (currentEdge != null) {
            group.getChildren().add(createDashedEdge(currentEdge, Color.GRAY, 1.5));
        }

        // 2. Рёбра MST (зелёные)
        for (Edge edge : step.getMstEdges()) {
            group.getChildren().add(createEdge(edge, MST_EDGE_COLOR, true));
        }

        // 3. Вершины (с цветами компонент)
        for (Node node : graph.getNodes()) {
            group.getChildren().add(createNodeWithColor(node, step.getColor(node), false));
        }

        // 4. Веса рёбер MST
        for (Edge edge : step.getMstEdges()) {
            group.getChildren().add(createEdgeWeight(edge));
        }
    }

    private javafx.scene.Node createNode(Node node, Color fill, Color stroke) {
        double x = node.getX();
        double y = node.getY();

        Circle circle = new Circle(x, y, NODE_RADIUS);
        circle.setFill(fill);
        circle.setStroke(stroke);
        circle.setStrokeWidth(2);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setRadius(3);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        circle.setEffect(shadow);

        Text text = new Text(String.valueOf(node.getId()));
        text.setFill(getContrastColor(fill));
        text.setFont(Font.font(FONT_SIZE));
        text.setBoundsType(TextBoundsType.VISUAL);
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        text.setX(x - textWidth / 2);
        text.setY(y + textHeight / 4);

        return new Group(circle, text);
    }

    private javafx.scene.Node createEdge(Edge edge, Color color, boolean isMSTEdge) {
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

        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(isMSTEdge ? 4 : 2);

        if (isMSTEdge) {
            Line outline = new Line(startX, startY, endX, endY);
            outline.setStroke(MST_EDGE_STROKE);
            outline.setStrokeWidth(1);
            return new Group(outline, line);
        }
        return line;
    }

    /**
     * Создаёт ребро с произвольным цветом, шириной и возможной обводкой.
     */
    private javafx.scene.Node createEdgeWithStyle(Edge edge, Color color, double width, boolean isMSTEdge) {
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

        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(width);

        if (isMSTEdge) {
            Line outline = new Line(startX, startY, endX, endY);
            outline.setStroke(MST_EDGE_STROKE);
            outline.setStrokeWidth(1);
            return new Group(outline, line);
        }
        return line;
    }

    private javafx.scene.Node createEdgeWeight(Edge edge) {
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

        Text weightText = new Text(String.valueOf(edge.getWeight()));
        weightText.setFill(EDGE_WEIGHT_COLOR);
        weightText.setFont(Font.font("Arial", FontWeight.BOLD, FONT_SIZE));
        weightText.setBoundsType(TextBoundsType.VISUAL);
        double w = weightText.getLayoutBounds().getWidth();
        double h = weightText.getLayoutBounds().getHeight();
        weightText.setX(midX + offsetX - w / 2);
        weightText.setY(midY + offsetY + h / 4);
        return weightText;
    }

    /**
     * Вершина с произвольным цветом и подсветкой.
     */
    public javafx.scene.Node createNodeWithColor(Node node, Color color, boolean isHighlighted) {
        double x = node.getX();
        double y = node.getY();

        Circle circle = new Circle(x, y, NODE_RADIUS);
        circle.setFill(color);
        circle.setStroke(isHighlighted ? Color.GOLD : NODE_STROKE);
        circle.setStrokeWidth(isHighlighted ? 4 : 2);

        Text text = new Text(String.valueOf(node.getId()));
        text.setFill(getContrastColor(color));
        text.setFont(Font.font(FONT_SIZE));
        text.setBoundsType(TextBoundsType.VISUAL);
        double tw = text.getLayoutBounds().getWidth();
        double th = text.getLayoutBounds().getHeight();
        text.setX(x - tw / 2);
        text.setY(y + th / 4);

        return new Group(circle, text);
    }

    /**
     * Создаёт пунктирное ребро (для отображения текущего ребра на MST).
     */
    private javafx.scene.Node createDashedEdge(Edge edge, Color color, double width) {
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

        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(width);
        line.getStrokeDashArray().addAll(6.0, 6.0); // пунктир: 6 пикселей черта, 6 пробел
        return line;
    }

    private Color getContrastColor(Color color) {
        double brightness = (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) / 1000;
        return brightness > 0.6 ? Color.BLACK : Color.WHITE;
    }

    public static double getNodeRadius() {
        return NODE_RADIUS;
    }
}