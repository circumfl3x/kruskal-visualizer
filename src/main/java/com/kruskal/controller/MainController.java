package com.kruskal.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private Canvas graphCanvas;

    @FXML
    private Canvas mstCanvas;

    @FXML
    private TextArea stepsTextArea;

    @FXML
    private TextField speedTextField;

    @FXML
    public void initialize() {
        System.out.println("Контроллер инициализирован");

        drawPlaceholder(graphCanvas, "Original Graph\n");
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
}