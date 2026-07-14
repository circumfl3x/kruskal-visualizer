package com.kruskal.visualisation;

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
    public void renderGraph(Graph graph, Group group) {
        group.getChildren().clear();
        if (graph == null || graph.isEmpty()) return;

        for (Edge edge : graph.getEdges()) {
            group.getChildren().add(createEdge(edge, EDGE_COLOR, false));
        }
        for (Node node : graph.getNodes()) {
            group.getChildren().add(createNode(node, NODE_COLOR, NODE_STROKE));
        }
        for (Edge edge : graph.getEdges()) {
            group.getChildren().add(createEdgeWeight(edge));
        }
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

        Group nodeGroup = new Group(circle, text);
        return nodeGroup;
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
     * Вершина с произвольным цветом. (Для визуализации шагов)
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

    private Color getContrastColor(Color color) {
        double brightness = (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) / 1000;
        return brightness > 0.6 ? Color.BLACK : Color.WHITE;
    }

    public static double getNodeRadius() {
        return NODE_RADIUS;
    }

    /**
     * Отрисовывает граф на левом холсте с подсветкой текущего ребра.
     *
     * @param graph            граф для отрисовки
     * @param group            группа JavaFX для добавления узлов
     * @param highlightedEdge  ребро, которое нужно подсветить (может быть null)
     * @param isAdded          true, если ребро добавляется в MST (зелёный), false если отклоняется (красный)
     */
    public void renderGraphWithHighlight(Graph graph, Group group, Edge highlightedEdge, boolean isAdded) {
        group.getChildren().clear();
        if (graph == null || graph.isEmpty()) return;

        for (Edge edge : graph.getEdges()) {
            Color color;
            if (edge.equals(highlightedEdge)) {
                color = isAdded ? Color.LIME : Color.ORANGE;
            } else {
                color = EDGE_COLOR;
            }
            group.getChildren().add(createEdge(edge, color, false));
        }

        for (Node node : graph.getNodes()) {
            boolean isHighlighted = highlightedEdge != null &&
                    (highlightedEdge.getNode1().equals(node) ||
                            highlightedEdge.getNode2().equals(node));
            group.getChildren().add(createNodeWithHighlight(node, isHighlighted));
        }

        for (Edge edge : graph.getEdges()) {
            group.getChildren().add(createEdgeWeight(edge));
        }
    }

    /**
     * Отрисовывает частично построенное минимальное остовное дерево на правом холсте.
     *
     * @param graph       исходный граф (нужен для получения вершин)
     * @param addedEdges  список рёбер, уже добавленных в MST
     * @param group       группа JavaFX для добавления узлов
     */
    public void renderMSTPartial(Graph graph, List<Edge> addedEdges, Group group) {
        group.getChildren().clear();
        if (graph == null || graph.isEmpty()) return;

        for (Node node : graph.getNodes()) {
            group.getChildren().add(createNode(node, NODE_COLOR, NODE_STROKE));
        }

        for (Edge edge : addedEdges) {
            group.getChildren().add(createEdge(edge, MST_EDGE_COLOR, true));
        }

        for (Edge edge : addedEdges) {
            group.getChildren().add(createEdgeWeight(edge));
        }
    }

    /**
     * Создаёт узел (вершину) с возможностью подсветки.
     *
     * @param node           вершина
     * @param isHighlighted  true, если вершину нужно подсветить золотой обводкой
     * @return JavaFX-узел (группа из круга и текста)
     */
    private javafx.scene.Node createNodeWithHighlight(Node node, boolean isHighlighted) {
        double x = node.getX();
        double y = node.getY();

        Circle circle = new Circle(x, y, NODE_RADIUS);
        circle.setFill(NODE_COLOR);
        circle.setStroke(isHighlighted ? Color.GOLD : NODE_STROKE);
        circle.setStrokeWidth(isHighlighted ? 4 : 2);

        Text text = new Text(String.valueOf(node.getId()));
        text.setFill(getContrastColor(NODE_COLOR));
        text.setFont(Font.font(FONT_SIZE));
        text.setBoundsType(TextBoundsType.VISUAL);
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        text.setX(x - textWidth / 2);
        text.setY(y + textHeight / 4);

        return new Group(circle, text);
    }
}