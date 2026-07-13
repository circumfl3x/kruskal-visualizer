package com.kruskal.controller;

import com.kruskal.algorithm.KruskalAlgorithm;
import com.kruskal.algorithm.AlgorithmStep;
import com.kruskal.io.GraphFileReader;
import com.kruskal.model.Edge;
import com.kruskal.model.Graph;
import com.kruskal.util.GraphGenerator;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
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
    private List<Edge> lastMSTEdges;

    @FXML
    public void initialize() {
        renderer = new GraphRenderer();
        logger = new Logger(stepsTextArea);
        algorithm = new KruskalAlgorithm();
        generator = new GraphGenerator();
        fileReader = new GraphFileReader();
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
        System.out.println("Save Graph clicked");
        stepsTextArea.appendText("\n[Действие] Сохранение графа в файл");
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
        System.out.println("Info clicked");
        stepsTextArea.appendText("\n[Действие] Информация");
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}