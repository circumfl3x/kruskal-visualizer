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
    private Logger logger;
    private Edge editingEdge;

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
        this.editingEdge = null;
        refresh();
    }

    /**
     * Обработка клика мыши по области графа.
     */
    public void handleClick(double x, double y) {
        switch (mode) {
            case ADD_NODE:
                addNode(x, y);
                break;
            case DELETE_NODE:
                deleteNode(x, y);
                break;
            case ADD_EDGE:
                addEdge(x, y);
                break;
            case DELETE_EDGE:
                deleteEdge(x, y);
                break;
            case EDIT_WEIGHT:
                editWeight(x, y);
                break;
            case NONE:
            default:
                break;
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
        Node node = new Node(id, x, y);
        graph.addNode(node);
        refresh();
    }

    /**
     * Удаление вершины.
     */
    private void deleteNode(double x, double y) {
        Node node = findNode(x, y);
        if (node == null) {
            return;
        }
        graph.removeNode(node);
        refresh();
    }

    /**
     * Добавление ребра между двумя вершинами.
     */
    private void addEdge(double x, double y) {
        Node node = findNode(x, y);
        if (node == null) {
            return;
        }

        // Если уже есть две выбранные вершины — сбрасываем (начинаем заново)
        if (firstSelected != null && secondSelected != null) {
            firstSelected = null;
            secondSelected = null;
            refresh();
            return;
        }

        // Первая вершина
        if (firstSelected == null) {
            firstSelected = node;
            refresh(List.of(firstSelected));
            return;
        }

        // Вторая вершина (firstSelected уже есть)
        // Если кликнули ту же вершину — сбрасываем выбор первой
        if (firstSelected.equals(node)) {
            firstSelected = null;
            refresh();
            return;
        }

        // Вторая вершина — другая
        secondSelected = node;
        refresh(List.of(firstSelected, secondSelected));
        // Завершаем создание ребра
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
        refresh();
    }

    /**
     * Удаление ребра.
     */
    private void deleteEdge(double x, double y) {
        Edge edge = findEdge(x, y);
        if (edge == null) {
            return;
        }
        graph.removeEdge(edge);
        refresh();
    }

    /**
     * Изменение веса ребра. Подсветка! редактируемого ребра
     */
    private void editWeight(double x, double y) {
        Edge oldEdge = findEdge(x, y);
        if (oldEdge == null) return;

        // Подсветка ребра
        editingEdge = oldEdge;
        refresh(editingEdge);

        int newWeight = askWeight();
        Edge newEdge = new Edge(oldEdge.getNode1(), oldEdge.getNode2(), newWeight);
        graph.removeEdge(oldEdge);
        graph.addEdge(newEdge);

        // Сброс подсветки
        editingEdge = null;
        refresh();
    }

    /**
     * Поиск вершины рядом с координатой клика.
     */
    private Node findNode(double x, double y) {
        for (Node node : graph.getNodes()) {
            double dx = node.getX() - x;
            double dy = node.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance <= GraphRenderer.getNodeRadius()) {
                return node;
            }
        }
        return null;
    }

    /**
     * Поиск ребра рядом с координатой клика.
     */
    private Edge findEdge(double x, double y) {
        if (findNode(x, y) != null) {
            return null;
        }
        for (Edge edge : graph.getEdges()) {
            Node node1 = edge.getNode1();
            Node node2 = edge.getNode2();
            double distance = distanceToSegment(x, y, node1.getX(), node1.getY(), node2.getX(), node2.getY());
            if (distance < 10) {
                return edge;
            }
        }
        return null;
    }

    private double distanceToSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2));
        }
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        double nearestX = x1 + t * dx;
        double nearestY = y1 + t * dy;
        return Math.sqrt(Math.pow(px - nearestX, 2) + Math.pow(py - nearestY, 2));
    }

    private double distanceToLine(double px, double py, double x1, double y1, double x2, double y2) {
        double numerator = Math.abs((y2 - y1) * px - (x2 - x1) * py + x2 * y1 - y2 * x1);
        double denominator = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
        if (denominator == 0) {
            return Double.MAX_VALUE;
        }
        return numerator / denominator;
    }

    /**
     * Диалог ввода веса ребра.
     */
    private int askWeight() {
        while (true) {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Вес ребра");
            dialog.setHeaderText("Вес ребра должен быть больше нуля");
            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) {
                return 1;
            }
            try {
                int weight = Integer.parseInt(result.get());
                if (weight > 0) {
                    return weight;
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        this.firstSelected = null;
        this.secondSelected = null;
        refresh();
    }

    public void disableMode() {
        this.mode = EditMode.NONE;
        this.firstSelected = null;
        this.secondSelected = null;
        this.editingEdge = null;
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
            if (node.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public void handleMousePressed(double x, double y) {
        draggedNode = findNode(x, y);
    }

    public void handleMouseDragged(double x, double y) {
        if (draggedNode == null) {
            return;
        }
        double radius = GraphRenderer.getNodeRadius();
        x = Math.max(radius, Math.min(canvasWidth - radius, x));
        y = Math.max(radius, Math.min(canvasHeight - radius, y));
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

    private void refresh(Edge highlightedEdge) {
        renderer.renderGraph(graph, graphGroup, List.of(), highlightedEdge);
        if (onGraphChanged != null) onGraphChanged.run();
    }

    private void refresh() {
        renderer.renderGraph(graph, graphGroup, List.of(), null);
        if (onGraphChanged != null) onGraphChanged.run();
    }

    private void refresh(List<Node> highlightedNodes) {
        renderer.renderGraph(graph, graphGroup, highlightedNodes, null);
        if (onGraphChanged != null) onGraphChanged.run();
    }



    /**
     * Метод для завершения создания ребра извне (не требуется, т.к. finishAddEdge вызывается автоматически).
     */
    public void confirmEdgeCreation() {
        finishAddEdge();
    }
}