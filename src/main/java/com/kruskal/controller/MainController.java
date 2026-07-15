package com.kruskal.controller;

import com.kruskal.algorithm.KruskalAlgorithm;
import com.kruskal.algorithm.VisualizationStep;
import com.kruskal.algorithm.AlgorithmStep;
import com.kruskal.editor.EditMode;
import com.kruskal.editor.GraphEditor;
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
    private GraphEditor editor;
    private List<Edge> lastMSTEdges;

    // Для визуализации
    private List<VisualizationStep> steps;
    private int currentStepIndex;

    @FXML
    public void initialize() {
        renderer = new GraphRenderer();
        logger = new Logger(stepsTextArea);
        algorithm = new KruskalAlgorithm();
        generator = new GraphGenerator();
        fileReader = new GraphFileReader();
        fileWriter = new GraphFileWriter();
        currentGraph = new Graph(new ArrayList<>(), new ArrayList<>());
        editor = new GraphEditor(currentGraph, renderer, graphGroup);

        graphContainer.setOnMouseClicked(event -> {
            javafx.geometry.Point2D point = graphGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
            editor.handleClick(point.getX(), point.getY());
        });
        steps = new ArrayList<>();
        currentStepIndex = -1;

        graphGroup.getChildren().clear();
        mstGroup.getChildren().clear();
        logger.clear();

    }

    @FXML
    private void onAddNode() {
        switchEditMode(EditMode.ADD_NODE);
        stepsTextArea.appendText("\n[Действие] Добавление вершины");
    }

    @FXML
    private void onDeleteNode() {
        switchEditMode(EditMode.DELETE_NODE);
        stepsTextArea.appendText("\n[Действие] Удаление вершины");
    }

    @FXML
    private void onAddEdge() {
        switchEditMode(EditMode.ADD_EDGE);
        stepsTextArea.appendText("\n[Действие] Добавление ребра");
    }

    @FXML
    private void onDeleteEdge() {
        switchEditMode(EditMode.DELETE_EDGE);
        stepsTextArea.appendText("\n[Действие] Удаление ребра");
    }

    @FXML
    private void onEditWeight() {
        switchEditMode(EditMode.EDIT_WEIGHT);
        stepsTextArea.appendText("\n[Действие] Изменение веса ребра");
    }

    @FXML
    private void onSaveGraph() {
        editor.disableMode();
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
        editor.disableMode();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить граф");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );

        File file = fileChooser.showOpenDialog(graphContainer.getScene().getWindow());
        if (file != null) {
            try {
                currentGraph = fileReader.read(file.getAbsolutePath());
                editor.setGraph(currentGraph);
                steps = null;
                currentStepIndex = -1;
                renderer.renderGraph(currentGraph, graphGroup);
                mstGroup.getChildren().clear();
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
        editor.disableMode();
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
            editor.setGraph(currentGraph);
            renderer.renderGraph(currentGraph, graphGroup);
            mstGroup.getChildren().clear();
            logger.logGraphGenerated(vertexCount, edgeCount);
            steps = null;
            currentStepIndex = -1;
        } catch (IllegalArgumentException e) {
            logger.logError("Ошибка генерации: " + e.getMessage());
            showErrorAlert("Ошибка генерации", e.getMessage());
        }
    }

    @FXML
    private void onRunKruskalAuto() {
        editor.disableMode();

        System.out.println("MODE = " + editor.getMode());
        System.out.println("NODES = " + currentGraph.getNodeCount());
        System.out.println("EDGES = " + currentGraph.getEdgeCount());

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
        editor.disableMode();
        if (currentGraph == null || currentGraph.isEmpty()) {
            logger.logError("Граф пуст. Загрузите или сгенерируйте граф перед запуском алгоритма.");
            return;
        }
        try {
            steps = algorithm.executeWithStates(currentGraph);
            logger.logSortedEdges(currentGraph.getEdges().stream().sorted().toList());
            currentStepIndex = 0;
            renderStep(currentStepIndex);
            logger.log("Пошаговый режим запущен. Используйте кнопки Prev/Next для навигации.");
        } catch (IllegalArgumentException e) {
            logger.logError(e.getMessage());
            showErrorAlert("Ошибка алгоритма", e.getMessage());
        }
    }

    @FXML
    private void onPrevStep() {
        editor.disableMode();
        if (steps == null || steps.isEmpty()) {
            logger.log("Сначала запустите алгоритм (Run Kruskal Manual).");
            return;
        }
        if (currentStepIndex > 0) {
            currentStepIndex--;
            renderStep(currentStepIndex);
        } else {
            logger.log("Это первый шаг.");
        }
    }

    @FXML
    private void onNextStep() {
        editor.disableMode();
        if (steps == null || steps.isEmpty()) {
            logger.log("Сначала запустите алгоритм (Run Kruskal Manual).");
            return;
        }
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            renderStep(currentStepIndex);
        } else {
            logger.log("Достигнут последний шаг.");
        }
    }

    @FXML
    private void onClean() {
        editor.disableMode();
        if (currentGraph != null) {
            currentGraph.clear();
        }
        graphGroup.getChildren().clear();
        mstGroup.getChildren().clear();
        logger.clear();
        steps = null;
        currentStepIndex = -1;
    }

    @FXML
    private void onInfo() {
        System.out.println("Info clicked");
        stepsTextArea.appendText("\n[Действие] Информация");
    }

    private void switchEditMode(EditMode newMode) {
        if (editor.getMode() == newMode) {
            editor.disableMode();
            logger.log("Режим редактирования выключен");
        } else {
            editor.setMode(newMode);
            logger.log("Включен режим: " + newMode);
        }
    }

    // Вспомогательный метод:
    private void renderStep(int index) {
        VisualizationStep step = steps.get(index);
        renderer.renderStep(currentGraph, step, graphGroup);
        renderer.renderMSTStep(currentGraph, step, mstGroup);
        logger.log(step.getDescription());
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}