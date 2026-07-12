package com.kruskal.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import com.kruskal.model.Edge;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class MainController {

    @FXML
    private Canvas graphCanvas;

    @FXML
    private Canvas mstCanvas;

    @FXML
    private TextArea stepsTextArea;

    @FXML
    private TextField speedTextField;

    private Graph currentGraph;

    @FXML
    public void initialize() {
        System.out.println("Контроллер инициализирован");

        currentGraph = new Graph(new ArrayList<>(), new ArrayList<>());
        Node n0 = new Node(0, 100, 100);
        Node n1 = new Node(1, 300, 100);
        Node n2 = new Node(2, 200, 300);
        currentGraph.addNode(n0);
        currentGraph.addNode(n1);
        currentGraph.addNode(n2);
        currentGraph.addEdge(new Edge(n0, n1, 4));
        currentGraph.addEdge(new Edge(n0, n2, 3));
        currentGraph.addEdge(new Edge(n1, n2, 1));

        drawGraph(graphCanvas, currentGraph);
        drawPlaceholder(mstCanvas, "MST\n");
        stepsTextArea.setText("Добро пожаловать!\n" +
                "Загрузите граф или создайте его вручную.");
    }

    @FXML
    private void onAddNode() {
        System.out.println("Add Node clicked");
        stepsTextArea.appendText("\n[Действие] Добавление вершины");
    }

    @FXML
    private void onDeleteNode() {
        System.out.println("Delete Node clicked");
        stepsTextArea.appendText("\n[Действие] Удаление вершины");
    }

    @FXML
    private void onAddEdge() {
        System.out.println("Add Edge clicked");
        stepsTextArea.appendText("\n[Действие] Добавление ребра");
    }

    @FXML
    private void onDeleteEdge() {
        System.out.println("Delete Edge clicked");
        stepsTextArea.appendText("\n[Действие] Удаление ребра");
    }

    @FXML
    private void onEditWeight() {
        System.out.println("Edit Weight clicked");
        stepsTextArea.appendText("\n[Действие] Изменение веса ребра");
    }

    @FXML
    private void onSaveGraph() {
        System.out.println("Save Graph clicked");
        stepsTextArea.appendText("\n[Действие] Сохранение графа в файл");
    }

    @FXML
    private void onInsertGraph() {
        System.out.println("Insert Graph clicked");
        stepsTextArea.appendText("\n[Действие] Загрузка графа из файла");
    }

    @FXML
    private void onGenerateGraph() {
        System.out.println("Generate Graph clicked");
        stepsTextArea.appendText("\n[Действие] Генерация случайного графа");
    }

    @FXML
    private void onRunKruskalAuto() {
        System.out.println("Run Kruskal Auto clicked");
        stepsTextArea.appendText("\n[Действие] Запуск алгоритма Краскала (авто)");

        if (currentGraph == null || currentGraph.isEmpty()) {
            stepsTextArea.appendText("\n[Ошибка] Граф пуст. Загрузите или создайте граф.");
            return;
        }

        List<Edge> sortedEdges = new ArrayList<>(currentGraph.getEdges());
        Collections.sort(sortedEdges);

        stepsTextArea.appendText("\nОтсортированные ребра:");
        for (Edge edge : sortedEdges) {
            stepsTextArea.appendText("\n  " + edge);
        }

        List<Edge> mstEdges = runKruskal(currentGraph);

        int totalWeight = 0;
        for (Edge edge : mstEdges) {
            totalWeight += edge.getWeight();
        }

        drawMST(mstCanvas, currentGraph, mstEdges);

        stepsTextArea.appendText("\n\nИтоговый результат:");
        stepsTextArea.appendText("\nОбщий вес: " + totalWeight);
        stepsTextArea.appendText("\nКоличество ребер: " + mstEdges.size());
        stepsTextArea.appendText("\nСписок ребер:");
        for (Edge edge : mstEdges) {
            stepsTextArea.appendText("\n  " + edge);
        }
    }

    @FXML
    private void onRunKruskalManual() {
        System.out.println("Run Kruskal Manual clicked");
        stepsTextArea.appendText("\n[Действие] Запуск алгоритма Краскала (пошагово)");
    }

    @FXML
    private void onPrevStep() {
        System.out.println("Prev Step clicked");
        stepsTextArea.appendText("\n[Действие] Предыдущий шаг");
    }

    @FXML
    private void onNextStep() {
        System.out.println("Next Step clicked");
        stepsTextArea.appendText("\n[Действие] Следующий шаг");
    }

    @FXML
    private void onClean() {
        System.out.println("Clean clicked");
        stepsTextArea.appendText("\n[Действие] Очистить");
    }

    @FXML
    private void onInfo() {
        System.out.println("Info clicked");
        stepsTextArea.appendText("\n[Действие] Информация");
    }

    private void drawPlaceholder(Canvas canvas, String text) {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(javafx.scene.paint.Color.DARKGRAY);
        gc.fillText(text, canvas.getWidth() / 2 - 50, canvas.getHeight() / 2);
    }

    private void drawGraph(Canvas canvas, Graph graph) {
        var gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(2);
        for (Edge edge : graph.getEdges()) {
            Node n1 = edge.getNode1();
            Node n2 = edge.getNode2();
            gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());

            double midX = (n1.getX() + n2.getX()) / 2;
            double midY = (n1.getY() + n2.getY()) / 2;
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillText(String.valueOf(edge.getWeight()), midX, midY);
        }

        for (Node node : graph.getNodes()) {
            gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
            gc.fillOval(node.getX() - 15, node.getY() - 15, 30, 30);
            gc.setStroke(javafx.scene.paint.Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(node.getX() - 15, node.getY() - 15, 30, 30);


            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillText(String.valueOf(node.getId()), node.getX() - 5, node.getY() + 5);
        }
    }

    private void drawMST(Canvas canvas, Graph graph, List<Edge> mstEdges) {
        var gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(javafx.scene.paint.Color.GREEN);
        gc.setLineWidth(3);
        for (Edge edge : mstEdges) {
            Node n1 = edge.getNode1();
            Node n2 = edge.getNode2();
            gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());

            double midX = (n1.getX() + n2.getX()) / 2;
            double midY = (n1.getY() + n2.getY()) / 2;
            gc.setFill(javafx.scene.paint.Color.GREEN);
            gc.fillText(String.valueOf(edge.getWeight()), midX, midY);
        }

        for (Node node : graph.getNodes()) {
            gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
            gc.fillOval(node.getX() - 15, node.getY() - 15, 30, 30);
            gc.setStroke(javafx.scene.paint.Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(node.getX() - 15, node.getY() - 15, 30, 30);

            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillText(String.valueOf(node.getId()), node.getX() - 5, node.getY() + 5);
        }
    }

    private List<Edge> runKruskal(Graph graph) {
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        Collections.sort(sortedEdges);

        int[] parent = new int[graph.getNodeCount()];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }

        List<Edge> mstEdges = new ArrayList<>();
        for (Edge edge : sortedEdges) {
            int root1 = find(parent, edge.getNode1().getId());
            int root2 = find(parent, edge.getNode2().getId());

            if (root1 != root2) {
                mstEdges.add(edge);
                union(parent, root1, root2);
            }
        }

        return mstEdges;
    }

    private int find(int[] parent, int i) {
        if (parent[i] == i) return i;
        return find(parent, parent[i]);
    }

    private void union(int[] parent, int i, int j) {
        parent[i] = j;
    }
}