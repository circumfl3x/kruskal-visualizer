package com.kruskal.controller;

import com.kruskal.algorithm.KruskalAlgorithm;
import com.kruskal.algorithm.VisualizationStep;
import com.kruskal.algorithm.AlgorithmStep;
import com.kruskal.editor.EditMode;
import com.kruskal.editor.GraphEditor;
import com.kruskal.io.GraphFileReader;
import com.kruskal.io.GraphFileWriter;
import com.kruskal.model.Graph;
import com.kruskal.model.Edge;
import com.kruskal.util.GraphGenerator;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainController {

    @FXML private StackPane graphContainer;
    @FXML private StackPane mstContainer;
    @FXML private Pane graphPane;
    @FXML private Group graphGroup;
    @FXML private Group mstGroup;
    @FXML private TextArea stepsTextArea;
    @FXML private TextField speedTextField;
    @FXML private Button runKruskalAutoButton;

    private Graph currentGraph;
    private GraphRenderer renderer;
    private Logger logger;
    private KruskalAlgorithm algorithm;
    private GraphGenerator generator;
    private GraphFileReader fileReader;
    private GraphFileWriter fileWriter;
    private GraphEditor editor;
    private List<Edge> lastMSTEdges;

    private AutoPlayer autoPlayer;
    private List<VisualizationStep> steps;       // для ручного режима
    private int currentStepIndex;                // для ручного режима

    @FXML private Button addNodeButton;
    @FXML private Button addEdgeButton;
    @FXML private Button editWeightButton;
    @FXML private Button deleteNodeButton;
    @FXML private Button deleteEdgeButton;
    @FXML private Button runKruskalManualButton;

    @FXML
    public void initialize() {
        unlockControls();

        renderer = new GraphRenderer();
        logger = new Logger(stepsTextArea);
        algorithm = new KruskalAlgorithm();
        generator = new GraphGenerator();
        fileReader = new GraphFileReader();
        fileWriter = new GraphFileWriter();
        currentGraph = new Graph(new ArrayList<>(), new ArrayList<>());
        editor = new GraphEditor(currentGraph, renderer, graphGroup);
        graphGroup.setMouseTransparent(true);
        mstGroup.setMouseTransparent(true);

        // Инициализация AutoPlayer
        autoPlayer = new AutoPlayer(
                renderer, logger, runKruskalAutoButton, speedTextField,
                graphGroup, mstGroup, algorithm,
                () -> {
                    // Синхронизируем шаги и индекс при паузе
                    if (autoPlayer.getSteps() != null && !autoPlayer.getSteps().isEmpty()) {
                        steps = autoPlayer.getSteps();
                        currentStepIndex = autoPlayer.getCurrentIndex();
                        logger.log("Состояние алгоритма синхронизировано для ручного управления.");
                    }
                }
        );
        autoPlayer.setGraph(currentGraph);

        graphPane.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();
            double radius = 20;
            double maxX = graphPane.getWidth() - radius;
            double maxY = graphPane.getHeight() - radius;
            // ограничиваем координаты
            x = Math.max(radius, Math.min(x, maxX));
            y = Math.max(radius, Math.min(y, maxY));
            editor.handleClick(x, y);
        });
        steps = new ArrayList<>();
        currentStepIndex = -1;

        graphGroup.getChildren().clear();
        mstGroup.getChildren().clear();
        logger.clear();
    }

    @FXML private void onAddNode() {
        switchEditMode(EditMode.ADD_NODE);
        System.out.println("Add Node clicked");
    }

    @FXML private void onDeleteNode() {
        switchEditMode(EditMode.DELETE_NODE);
        System.out.println("Delete Node clicked");
    }

    @FXML private void onAddEdge() {
        switchEditMode(EditMode.ADD_EDGE);
        System.out.println("Add Edge clicked");
    }

    @FXML private void onDeleteEdge() {
        switchEditMode(EditMode.DELETE_EDGE);
        System.out.println("Delete Edge clicked");
    }

    @FXML private void onEditWeight() {
        switchEditMode(EditMode.EDIT_WEIGHT);
        System.out.println("Edit Weight clicked");
    }

    @FXML private void onSaveGraph() {
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

    @FXML private void onInsertGraph() {
        editor.disableMode();
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.stop();
            unlockControls();
            logger.log("Автоматическое воспроизведение остановлено.");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить граф");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );
        File file = fileChooser.showOpenDialog(graphContainer.getScene().getWindow());
        if (file == null) return;
        try {
            currentGraph = fileReader.read(file.getAbsolutePath());
            editor.setGraph(currentGraph);
            autoPlayer.setGraph(currentGraph);
            autoPlayer.reset(); // сброс шагов
            steps = null;
            currentStepIndex = -1;
            renderer.renderGraph(currentGraph, graphGroup);
            mstGroup.getChildren().clear();
            logger.logGraphLoaded(file.getName(), currentGraph.getNodeCount(), currentGraph.getEdgeCount());
        } catch (IOException | NumberFormatException e) {
            logger.logError("Ошибка при загрузке графа: " + e.getMessage());
            showErrorAlert("Ошибка загрузки", e.getMessage());
        }
    }

    @FXML private void onGenerateGraph() {
        editor.disableMode();
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.stop();
            unlockControls();
            logger.log("Автоматическое воспроизведение остановлено.");
        }

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
            autoPlayer.setGraph(currentGraph);
            autoPlayer.reset();
            steps = null;
            currentStepIndex = -1;
            renderer.renderGraph(currentGraph, graphGroup);
            mstGroup.getChildren().clear();
            logger.logGraphGenerated(vertexCount, edgeCount);
        } catch (IllegalArgumentException e) {
            logger.logError("Ошибка генерации: " + e.getMessage());
            showErrorAlert("Ошибка генерации", e.getMessage());
        }
    }

    @FXML
    private void onRunKruskalAuto() {
        editor.disableMode();
        if (currentGraph == null || currentGraph.isEmpty()) {
            logger.logError("Граф пуст.");
            return;
        }

        // Случай 1: Авто на паузе – обновляем индекс и перезапускаем
        if (autoPlayer.isPaused()) {
            // Синхронизируем шаги и индекс из контроллера (пользователь мог изменить вручную)
            if (steps != null && !steps.isEmpty()) {
                autoPlayer.setSteps(steps, currentStepIndex);
            }
            // Останавливаем авто (сброс состояния паузы)
            autoPlayer.stop();

            lockControls();

            autoPlayer.togglePlay(() -> {
                syncStepsFromAuto();
                unlockControls();
            });
            return;
        }

        // Случай 2: Авто играет (не на паузе) – ставим на паузу
        if (autoPlayer.isPlaying()) {
            autoPlayer.togglePlay(() -> {});
            return;
        }

        // Случай 3: Авто не запущен – запускаем с текущего индекса (если есть шаги)
        if (steps != null && !steps.isEmpty()) {
            autoPlayer.setSteps(steps, currentStepIndex);
        } else {
            autoPlayer.setSteps(null, -1);
        }

        lockControls();
        autoPlayer.togglePlay(() -> {
            syncStepsFromAuto();
            unlockControls();
        });
    }

    @FXML private void onRunKruskalManual() {
        editor.disableMode();
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.stop();
            unlockControls();
            logger.log("Автоматическое воспроизведение остановлено.");
        }

        if (currentGraph == null || currentGraph.isEmpty()) {
            logger.logError("Граф пуст.");
            return;
        }
        try {
            steps = algorithm.executeWithStates(currentGraph);
            logger.logSortedEdges(currentGraph.getEdges().stream().sorted().toList());
            currentStepIndex = 0;
            renderStep(currentStepIndex);
            logger.log("Пошаговый режим запущен. Используйте Prev/Next.");
        } catch (IllegalArgumentException e) {
            logger.logError(e.getMessage());
            showErrorAlert("Ошибка алгоритма", e.getMessage());
        }
    }

    @FXML private void onPrevStep() {
        editor.disableMode();
        if (autoPlayer.isPlaying() && !autoPlayer.isPaused()) {
            autoPlayer.stop();
            unlockControls();
            logger.log("Автоматическое воспроизведение остановлено.");
        }

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

    @FXML private void onNextStep() {
        editor.disableMode();
        if (autoPlayer.isPlaying() && !autoPlayer.isPaused()) {
            autoPlayer.stop();
            unlockControls();
            logger.log("Автоматическое воспроизведение остановлено.");
        }

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

    @FXML private void onClean() {
        editor.disableMode();
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.stop();
            unlockControls();
            logger.log("Автоматическое воспроизведение остановлено.");
        }
        if (currentGraph != null) {
            currentGraph.clear();
        }
        graphGroup.getChildren().clear();
        mstGroup.getChildren().clear();
        logger.clear();
        steps = null;
        currentStepIndex = -1;
        autoPlayer.reset();
    }

    @FXML private void onInfo() {
        System.out.println("Info clicked");
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

    private void renderStep(int index) {
        VisualizationStep step = steps.get(index);
        renderer.renderStep(currentGraph, step, graphGroup);
        renderer.renderMSTStep(currentGraph, step, mstGroup);
        logger.log(step.getDescription());
    }

    private void syncStepsFromAuto() {
        if (autoPlayer.getSteps() != null && !autoPlayer.getSteps().isEmpty()) {
            steps = autoPlayer.getSteps();
            currentStepIndex = autoPlayer.getCurrentIndex();
        }
    }

    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setButtonsDisabled(boolean disable) {
        addNodeButton.setDisable(disable);
        addEdgeButton.setDisable(disable);
        editWeightButton.setDisable(disable);
        deleteNodeButton.setDisable(disable);
        deleteEdgeButton.setDisable(disable);
        runKruskalManualButton.setDisable(disable);
    }

    private void lockControls() {
        setButtonsDisabled(true);
    }

    private void unlockControls() {
        setButtonsDisabled(false);
    }
}