package com.kruskal.editor;

import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.scene.Group;
import javafx.scene.control.TextInputDialog;

import java.util.List;
import java.util.Optional;

/**
 * Редактор графа.
 *
 * Отвечает за ручное создание и изменение графа пользователем:
 * - добавление вершин;
 * - удаление вершин;
 * - добавление ребер;
 * - удаление ребер;
 * - изменение веса ребер.
 */
public class GraphEditor {
    private Graph graph;
    private final GraphRenderer renderer;
    private final Group graphGroup;
    private EditMode mode = EditMode.NONE;
    private Runnable onGraphChanged;
    // Выбранные вершины при создании ребра
    private Node firstSelected;
    private Node secondSelected;
    // Перетаскиваемая вершина
    private Node draggedNode;
    private double canvasWidth;
    private double canvasHeight;
    private final Logger logger;
    private List<Node> highlightedNodes = List.of();
    private Edge highlightedEdge = null;

    public GraphEditor(Graph graph, GraphRenderer renderer, Group graphGroup, Logger logger) {
        this.graph = graph;
        this.renderer = renderer;
        this.graphGroup = graphGroup;
        this.logger = logger;
    }

    /**
     * Устанавливает текущий режим редактирования.
     */
    public void setMode(EditMode mode) {
        this.mode = mode;
        this.firstSelected = null;
        this.secondSelected = null;
        this.highlightedNodes = List.of();
        this.highlightedEdge = null;
        refresh();
    }

    /**
     * Обработка клика мыши по области графа.
     */
    public void handleClick(double x, double y) {
        switch (mode) {
            case ADD_NODE -> addNode(x, y);
            case DELETE_NODE -> deleteNode(x, y);
            case ADD_EDGE -> addEdge(x, y);
            case DELETE_EDGE -> deleteEdge(x, y);
            case EDIT_WEIGHT -> editWeight(x, y);
            default -> {}
        }
    }

    /**
     * Добавление новой вершины.
     */
    private void addNode(double x, double y) {
        int id = 0;
        while (isIdExists(id)) {
            id++;
        }
        graph.addNode(new Node(id, x, y));
        refresh();
    }

    /**
     * Удаление вершины.
     */
    private void deleteNode(double x, double y) {
        Node node = findNode(x, y);
        if (node == null) return;
        graph.removeNode(node);
        refresh();
    }

    /**
     * Добавление ребра между двумя вершинами.
     */
    private void addEdge(double x, double y) {
        Node node = findNode(x, y);
        if (node == null) return;

        // Если уже есть две выбранные вершины — сбрасываем (начинаем заново)
        if (firstSelected != null && secondSelected != null) {
            firstSelected = null;
            secondSelected = null;
            highlightedNodes = List.of();
            refresh();
            return;
        }

        // Первая вершина
        if (firstSelected == null) {
            firstSelected = node;
            highlightedNodes = List.of(firstSelected);
            refresh();
            return;
        }

        // Если кликнули ту же вершину — сбрасываем выбор первой
        if (firstSelected.equals(node)) {
            firstSelected = null;
            secondSelected = null;
            highlightedNodes = List.of();
            refresh();
            return;
        }

        // Вторая вершина — другая
        secondSelected = node;
        highlightedNodes = List.of(firstSelected, secondSelected);
        refresh();
        finishAddEdge();
    }

    /**
     * Завершает создание ребра после выбора второй вершины.
     */
    private void finishAddEdge() {
        if (firstSelected == null || secondSelected == null) {
            return;
        }

        // Проверка на существование ребра между вершинами
        for (Edge edge : graph.getEdges()) {
            if (edge.connects(firstSelected, secondSelected)) {
                logger.log("Ребро между вершинами " + firstSelected.getId() + " и " + secondSelected.getId() + " уже существует.");
                firstSelected = null;
                secondSelected = null;
                highlightedNodes = List.of();
                refresh();
                return;
            }
        }

        int weight = askWeight();
        Edge edge = new Edge(firstSelected, secondSelected, weight);
        graph.addEdge(edge);
        logger.log("Добавлено ребро. Всего ребер: " + graph.getEdgeCount());

        firstSelected = null;
        secondSelected = null;
        highlightedNodes = List.of();
        refresh();
    }

    /**
     * Удаление ребра.
     */
    private void deleteEdge(double x, double y) {
        Edge edge = findEdge(x, y);
        if (edge == null) return;
        graph.removeEdge(edge);
        refresh();
    }

    /**
     * Изменение веса ребра. Подсветка редактируемого ребра.
     */
    private void editWeight(double x, double y) {
        Edge oldEdge = findEdge(x, y);
        if (oldEdge == null) return;

        // Подсветка ребра
        highlightedEdge = oldEdge;
        refresh();

        int newWeight = askWeight();
        Edge newEdge = new Edge(oldEdge.getNode1(), oldEdge.getNode2(), newWeight);
        graph.removeEdge(oldEdge);
        graph.addEdge(newEdge);

        // Сброс подсветки
        highlightedEdge = null;
        refresh();
    }

    /**
     * Поиск вершины рядом с координатой клика.
     */
    private Node findNode(double x, double y) {
        for (Node node : graph.getNodes()) {
            double dx = node.getX() - x;
            double dy = node.getY() - y;
            if (Math.sqrt(dx*dx + dy*dy) <= GraphRenderer.getNodeRadius()) {
                return node;
            }
        }
        return null;
    }

    /**
     * Поиск ребра рядом с координатой клика.
     */
    private Edge findEdge(double x, double y) {
        if (findNode(x, y) != null) return null;
        for (Edge edge : graph.getEdges()) {
            double d = distanceToSegment(x, y,
                    edge.getNode1().getX(), edge.getNode1().getY(),
                    edge.getNode2().getX(), edge.getNode2().getY());
            if (d < 10) return edge;
        }
        return null;
    }

    private double distanceToSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1);
        }
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx*dx + dy*dy);
        t = Math.clamp(t, 0, 1);
        double nearX = x1 + t * dx;
        double nearY = y1 + t * dy;
        return Math.hypot(px - nearX, py - nearY);
    }

    /**
     * Диалог ввода веса ребра.
     */
    private int askWeight() {
        while (true) {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Вес ребра");
            dialog.setHeaderText("Вес ребра > 0");
            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) return 1;
            try {
                int w = Integer.parseInt(result.get());
                if (w > 0) return w;
            } catch (NumberFormatException ignored) {}
        }
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        this.firstSelected = null;
        this.secondSelected = null;
        this.highlightedNodes = List.of();
        this.highlightedEdge = null;
        refresh();
    }

    public void disableMode() {
        this.mode = EditMode.NONE;
        this.firstSelected = null;
        this.secondSelected = null;
        this.highlightedNodes = List.of();
        this.highlightedEdge = null;
        refresh();
    }

    public EditMode getMode() {
        return mode;
    }

    public void setOnGraphChanged(Runnable callback) {
        this.onGraphChanged = callback;
    }

    private boolean isIdExists(int id) {
        for (Node node : graph.getNodes()) {
            if (node.getId() == id) return true;
        }
        return false;
    }

    public void handleMousePressed(double x, double y) {
        draggedNode = findNode(x, y);
    }

    public void handleMouseDragged(double x, double y) {
        if (draggedNode == null) return;
        double radius = GraphRenderer.getNodeRadius();
        x = Math.clamp(x, radius, canvasWidth - radius);
        y = Math.clamp(y, radius, canvasHeight - radius);
        draggedNode.setX(x);
        draggedNode.setY(y);
        refresh();
    }

    public void handleMouseReleased() {
        draggedNode = null;
    }

    public void setCanvasSize(double width, double height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
    }

    private void refresh() {
        if (onGraphChanged != null) onGraphChanged.run();
    }

    public Graph getGraph() {
        return graph;
    }

    public List<Node> getHighlightedNodes() {
        return highlightedNodes;
    }

    public Edge getHighlightedEdge() {
        return highlightedEdge;
    }
}