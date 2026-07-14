package com.kruskal.controller;

import com.kruskal.algorithm.KruskalAlgorithm;
import com.kruskal.algorithm.AlgorithmStep;
import com.kruskal.io.GraphFileReader;
import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.util.GraphGenerator;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import com.kruskal.io.GraphFileWriter;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainController {

    @FXML
    private StackPane graphContainer;

    @FXML
    private StackPane mstContainer;

    @FXML
    private Group graphGroup;

    @FXML
    private Group mstGroup;

    @FXML
    private TextArea stepsTextArea;

    @FXML
    private TextField speedTextField;

    private Graph currentGraph;
    private GraphRenderer renderer;
    private Logger logger;
    private KruskalAlgorithm algorithm;
    private GraphGenerator generator;
    private GraphFileReader fileReader;
    private GraphFileWriter fileWriter;
    private List<Edge> lastMSTEdges;

    @FXML
    public void initialize() {
        renderer = new GraphRenderer();
        logger = new Logger(stepsTextArea);
        algorithm = new KruskalAlgorithm();
        generator = new GraphGenerator();
        fileReader = new GraphFileReader();
        fileWriter = new GraphFileWriter();
        currentGraph = new Graph(new ArrayList<>(), new ArrayList<>());
        lastMSTEdges = new ArrayList<>();

        graphGroup.getChildren().clear();
        mstGroup.getChildren().clear();
        logger.clear();

    }

    private void redrawGraph() {
        if (graphContainer.getWidth() <= 0 || graphContainer.getHeight() <= 0) return;
        if (currentGraph != null && !currentGraph.isEmpty()) {
            renderer.renderGraph(currentGraph, graphGroup);
        } else {
            graphGroup.getChildren().clear();
        }
    }

    private void redrawMST() {
        if (mstContainer.getWidth() <= 0 || mstContainer.getHeight() <= 0) return;
        if (currentGraph != null && !lastMSTEdges.isEmpty()) {
            renderer.renderMST(currentGraph, lastMSTEdges, mstGroup);
        } else {
            mstGroup.getChildren().clear();
        }
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
        if (currentGraph == null || currentGraph.isEmpty()) {
            logger.logError("Нет графа для сохранения.");
            showErrorAlert("Ошибка", "Нет графа для сохранения.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить граф");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );

        File file = fileChooser.showSaveDialog(graphContainer.getScene().getWindow());
        if (file == null) {
            return;
        } else if (file != null && !file.getName().toLowerCase().endsWith(".txt")) {
            file = new File(file.getAbsolutePath() + ".txt");
        }

        try {
            fileWriter.write(currentGraph, file.getAbsolutePath());
            logger.logGraphSaved(file.getName());
        } catch (IOException e) {
            logger.logError("Ошибка сохранения: " + e.getMessage());
            showErrorAlert("Ошибка сохранения", e.getMessage());
        }
    }

    @FXML
    private void onInsertGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить граф");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );

        File file = fileChooser.showOpenDialog(graphContainer.getScene().getWindow());
        if (file != null) {
            try {
                currentGraph = fileReader.read(file.getAbsolutePath());
                renderer.renderGraph(currentGraph, graphGroup);
                mstGroup.getChildren().clear();
                lastMSTEdges.clear();
                logger.logGraphLoaded(file.getName(), currentGraph.getNodeCount(), currentGraph.getEdgeCount());
            } catch (IOException e) {
                logger.logError("Ошибка при загрузке графа: " + e.getMessage());
                showErrorAlert("Ошибка загрузки", e.getMessage());
            } catch (NumberFormatException e) {
                logger.logError("Ошибка формата файла: " + e.getMessage());
                showErrorAlert("Ошибка формата", e.getMessage());
            }
        }
    }

    @FXML
    private void onGenerateGraph() {
        try {
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

            currentGraph = generator.generate(vertexCount, edgeCount);
            renderer.renderGraph(currentGraph, graphGroup);
            mstGroup.getChildren().clear();
            lastMSTEdges.clear();
            logger.logGraphGenerated(vertexCount, edgeCount);
        } catch (IllegalArgumentException e) {
            logger.logError("Ошибка генерации: " + e.getMessage());
            showErrorAlert("Ошибка генерации", e.getMessage());
        }
    }

    @FXML
    private void onRunKruskalAuto() {
        if (currentGraph == null || currentGraph.isEmpty()) {
            logger.logError("Граф пуст. Загрузите или сгенерируйте граф перед запуском алгоритма.");
            return;
        }

        try {
            List<AlgorithmStep> steps = algorithm.execute(currentGraph);

            List<Edge> mstEdges = new ArrayList<>();
            int totalWeight = 0;
            for (AlgorithmStep step : steps) {
                if (step.isAdded()) {
                    mstEdges.add(step.getEdge());
                    totalWeight = step.getTotalWeight();
                }
            }

            lastMSTEdges = mstEdges;
            renderer.renderMST(currentGraph, mstEdges, mstGroup);
            logger.logSortedEdges(currentGraph.getEdges().stream().sorted().toList());
            logger.logResult(totalWeight, mstEdges, currentGraph.getNodeCount());
        } catch (IllegalArgumentException e) {
            logger.logError(e.getMessage());
            showErrorAlert("Ошибка алгоритма", e.getMessage());
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
        if (currentGraph != null) {
            currentGraph.clear();
        }
        graphGroup.getChildren().clear();
        mstGroup.getChildren().clear();
        lastMSTEdges.clear();
        logger.clear();
    }

    @FXML
    private void onInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Справка");
        alert.setHeaderText("Визуализатор алгоритма Краскала");

        String text = """
            Назначение программы
            --------------------
            Программа предназначена для построения минимального остовного дерева
            неориентированного взвешенного графа с помощью алгоритма Краскала.
            Поддерживается создание, загрузка, сохранение и визуализация графов.
            
            Работа с программой
            --------------------
            
            Add Node
                Добавляет новую вершину в граф.
                (Функция находится в разработке.)
            
            Delete Node
                Удаляет выбранную вершину вместе со всеми
                инцидентными ей рёбрами.
                (Функция находится в разработке.)
            
            Add Edge
                Добавляет ребро между двумя вершинами.
                (Функция находится в разработке.)
            
            Delete Edge
                Удаляет выбранное ребро.
                (Функция находится в разработке.)
            
            Edit Weight
                Изменяет вес выбранного ребра.
                (Функция находится в разработке.)
            
            Generate Graph
                Генерирует случайный связный взвешенный граф.
            
            Insert Graph
                Загружает граф из текстового файла.
            
            Save Graph
                Сохраняет текущий граф в текстовый файл.
            
            Run Kruskal Auto
                Автоматически выполняет алгоритм Краскала
                и отображает полученное минимальное
                остовное дерево.
            
            Speed (ms)
                Позволяет задать скорость автоматической
                визуализации алгоритма.
                (Используется для автоматического режима.)
            
            Run Kruskal Manual
                Запускает пошаговое выполнение алгоритма.
                (Функция находится в разработке.)
            
            Prev Step
                Возвращает к предыдущему шагу алгоритма.
                (Функция находится в разработке.)
            
            Next Step
                Выполняет следующий шаг алгоритма.
                (Функция находится в разработке.)
            
            Clean
                Очищает оба окна графов и журнал действий.
            
            Info
                Открывает данную справку.
            
            Форматы файлов
            --------------------
            Программа поддерживает два формата файлов.
            
            1. Формат без координат
            
            Первая строка:
                количество вершин

            Далее:
                вершина1 вершина2 вес
                
            Пример:
            5
            0 1 5
            2 1 10
            3 2 6
            4 2 8
            
            Координаты вершин автоматически
            генерируются программой.
            
            2. Формат с координатами
            
            Первая строка:
                COORDS
            
            Вторая строка:
                количество вершин
            
            Далее:
                id x y
            
            После пустой строки:
                вершина1 вершина2 вес
            
            Пример:
            COORDS
            5
            0 100 120
            1 200 300
            2 350 170
            3 400 500
            4 600 250
                
            0 1 5
            1 2 7
            2 4 3
            3 4 10
            
            Алгоритм Краскала
            --------------------
            Алгоритм используется для построения
            минимального остовного дерева связного
            взвешенного неориентированного графа.
            
            Порядок работы алгоритма:
            
            1. Все рёбра сортируются по возрастанию веса.
            
            2. Рёбра рассматриваются по одному,
               начиная с ребра минимального веса.
            
            3. Если очередное ребро не образует цикл,
               оно включается в минимальное остовное дерево.
            
            4. Если добавление ребра приводит к образованию
               цикла, оно пропускается.
            
            5. Алгоритм завершается после добавления
               (N − 1) рёбер, где N — количество вершин графа.
            
            Результатом работы является минимальное
            остовное дерево, содержащее все вершины
            графа и имеющее минимальную суммарную
            стоимость.
            """;

        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        alert.getDialogPane().setContent(textArea);

        alert.getDialogPane().setPrefWidth(700);
        alert.getDialogPane().setPrefHeight(650);

        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}