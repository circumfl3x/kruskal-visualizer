package com.kruskal.controller;

import com.kruskal.algorithm.KruskalAlgorithm;
import com.kruskal.editor.EditMode;
import com.kruskal.editor.GraphEditor;
import com.kruskal.model.Graph;
import com.kruskal.util.GenerateGraphDialog;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private StackPane graphContainer;
    @FXML private Pane graphPane;
    @FXML private Group graphGroup;
    @FXML private Group mstGroup;
    @FXML private TextArea stepsTextArea;
    @FXML private TextField speedTextField;
    @FXML private Button runKruskalAutoButton;
    @FXML private Button addNodeButton, addEdgeButton, editWeightButton, deleteNodeButton, deleteEdgeButton, runKruskalManualButton;

    private GraphManager graphManager;
    private Logger logger;
    private GraphRenderer renderer;
    private KruskalAlgorithm algorithm;
    private AutoPlayer autoPlayer;
    private UIStateManager uiStateManager;
    private PlaybackCoordinator playbackCoordinator;
    private GraphInteractionHandler interactionHandler;

    @FXML
    public void initialize() {
        logger = new Logger(stepsTextArea);
        renderer = new GraphRenderer();
        algorithm = new KruskalAlgorithm();

        uiStateManager = new UIStateManager(addNodeButton, addEdgeButton, editWeightButton,
                deleteNodeButton, deleteEdgeButton, runKruskalManualButton);

        // 1. GraphManager (пока без autoPlayer)
        GraphEditor editor = new GraphEditor(new Graph(new ArrayList<>(), new ArrayList<>()), renderer, graphGroup, logger);
        graphManager = new GraphManager(renderer, editor, null, logger, graphGroup, mstGroup);
        graphManager.setOnGraphChanged(() -> {
            if (playbackCoordinator != null) playbackCoordinator.reset();
        });

        // 2. PlaybackCoordinator (пока без autoPlayer)
        playbackCoordinator = new PlaybackCoordinator(algorithm, renderer, logger, graphManager, null, graphGroup, mstGroup);

        // 3. AutoPlayer с колбэком, синхронизирующим playbackCoordinator
        autoPlayer = new AutoPlayer(
                renderer, logger, runKruskalAutoButton, speedTextField,
                graphGroup, mstGroup, algorithm,
                () -> {
                    if (autoPlayer.getSteps() != null && !autoPlayer.getSteps().isEmpty()) {
                        playbackCoordinator.syncFromAuto();
                        logger.log("Состояние алгоритма синхронизировано для ручного управления.");
                    }
                }
        );
        autoPlayer.setOnComplete(uiStateManager::unlockControls);

        // 4. Устанавливаем autoPlayer в менеджеры
        graphManager.setAutoPlayer(autoPlayer);
        playbackCoordinator.setAutoPlayer(autoPlayer);

        // Подписка на размеры
        graphPane.widthProperty().addListener((obs, old, val) ->
                graphManager.setCanvasSize(val.doubleValue(), graphPane.getHeight()));
        graphPane.heightProperty().addListener((obs, old, val) ->
                graphManager.setCanvasSize(graphPane.getWidth(), val.doubleValue()));

        // Обработчики мыши (GraphInteractionHandler уже создан в отдельном классе)
        interactionHandler = new GraphInteractionHandler(graphManager, graphPane);

        graphGroup.setMouseTransparent(true);
        mstGroup.setMouseTransparent(true);

        uiStateManager.unlockControls();
        graphGroup.getChildren().clear();
        mstGroup.getChildren().clear();
        logger.clear();
    }

    // ---- Действия кнопок ----

    @FXML private void onAddNode() {
        switchEditMode(EditMode.ADD_NODE);
    }

    @FXML private void onDeleteNode() {
        switchEditMode(EditMode.DELETE_NODE);
    }

    @FXML private void onAddEdge() {
        switchEditMode(EditMode.ADD_EDGE);
    }

    @FXML private void onDeleteEdge() {
        switchEditMode(EditMode.DELETE_EDGE);
    }

    @FXML private void onEditWeight() {
        switchEditMode(EditMode.EDIT_WEIGHT);
    }

    private void switchEditMode(EditMode mode) {
        if (graphManager.getEditor().getMode() == mode) {
            graphManager.disableEditorMode();
            logger.log("Режим редактирования выключен");
        } else {
            graphManager.switchEditMode(mode);
            logger.log("Включен режим: " + mode);
        }
    }

    @FXML private void onSaveGraph() {
        graphManager.disableEditorMode();
        if (graphManager.getCurrentGraph().isEmpty()) {
            logger.logError("Нет графа для сохранения.");
            showErrorAlert("Ошибка", "Нет графа для сохранения.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранить граф");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        File file = chooser.showSaveDialog(graphContainer.getScene().getWindow());
        if (file == null) return;
        try {
            graphManager.saveGraph(file);
        } catch (IOException e) {
            logger.logError("Ошибка сохранения: " + e.getMessage());
            showErrorAlert("Ошибка сохранения", e.getMessage());
        }
    }

    @FXML private void onInsertGraph() {
        graphManager.disableEditorMode();
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.stop();
            uiStateManager.unlockControls();
            logger.log("Автоматическое воспроизведение остановлено.");
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Загрузить граф");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        File file = chooser.showOpenDialog(graphContainer.getScene().getWindow());
        if (file == null) return;
        try {
            graphManager.loadGraph(file);
            playbackCoordinator.reset();
        } catch (IOException | NumberFormatException e) {
            logger.logError("Ошибка загрузки: " + e.getMessage());
            showErrorAlert("Ошибка загрузки", e.getMessage());
        }
    }

    @FXML private void onGenerateGraph() {
        graphManager.disableEditorMode();
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.stop();
            uiStateManager.unlockControls();
            logger.log("Автоматическое воспроизведение остановлено.");
        }
        graphManager.generateRandom();
        playbackCoordinator.reset();
        uiStateManager.unlockControls();
    }

    @FXML private void onGenerateGraphParams() {
        int[] params = GenerateGraphDialog.showAndWait();
        if (params != null) {
            graphManager.generateWithParams(params[0], params[1]);
            playbackCoordinator.reset();
        }
        uiStateManager.unlockControls();
    }

    @FXML private void onRunKruskalAuto() {
        uiStateManager.lockControls();
        playbackCoordinator.runAuto();
        // разблокировка будет через onComplete в autoPlayer
    }

    @FXML private void onRunKruskalManual() {
        graphManager.disableEditorMode();
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.stop();
            uiStateManager.unlockControls();
            logger.log("Автоматическое воспроизведение остановлено.");
        }
        playbackCoordinator.runManual();
    }

    @FXML private void onPrevStep() {
        playbackCoordinator.prevStep();
    }

    @FXML private void onNextStep() {
        playbackCoordinator.nextStep();
    }

    @FXML private void onClean() {
        graphManager.disableEditorMode();
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.stop();
            logger.log("Автоматическое воспроизведение остановлено.");
        }
        graphManager.clear();
        playbackCoordinator.reset();
        uiStateManager.unlockControls();
    }

    @FXML private void onInfo() {
        System.out.println("Info clicked");
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
}