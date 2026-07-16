package com.kruskal.controller;

import com.kruskal.algorithm.KruskalAlgorithm;
import com.kruskal.algorithm.VisualizationStep;
import com.kruskal.model.Graph;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Координирует пошаговое и автоматическое воспроизведение алгоритма.
 *
 * Хранит список шагов и текущий индекс, управляет ручным переключением
 * (Prev/Next), запуском пошагового режима и синхронизацией с
 * {@link AutoPlayer} при паузе/возобновлении.
 *
 */
public class PlaybackCoordinator {
    private final KruskalAlgorithm algorithm;
    private final GraphRenderer renderer;
    private final Logger logger;
    private final GraphManager graphManager;
    private AutoPlayer autoPlayer;
    private final Group graphGroup;
    private final Group mstGroup;

    private List<VisualizationStep> steps;
    private int currentStepIndex;

    public PlaybackCoordinator(KruskalAlgorithm algorithm, GraphRenderer renderer, Logger logger,
                               GraphManager graphManager, AutoPlayer autoPlayer,
                               Group graphGroup, Group mstGroup) {
        this.algorithm = algorithm;
        this.renderer = renderer;
        this.logger = logger;
        this.graphManager = graphManager;
        this.autoPlayer = autoPlayer;
        this.graphGroup = graphGroup;
        this.mstGroup = mstGroup;
        this.steps = new ArrayList<>();
        this.currentStepIndex = -1;
    }

    public void reset() {
        steps = null;
        currentStepIndex = -1;
        autoPlayer.reset();
    }

    public void runManual() {
        Graph graph = graphManager.getCurrentGraph();
        if (graph.isEmpty()) {
            logger.logError("Граф пуст.");
            return;
        }
        try {
            steps = algorithm.executeWithStates(graph);
            logger.logSortedEdges(graph.getEdges().stream().sorted().toList());
            currentStepIndex = 0;
            renderStep(currentStepIndex);
            logger.log("Пошаговый режим запущен. Используйте Prev/Next.");
        } catch (IllegalArgumentException e) {
            logger.logError(e.getMessage());
        }
    }

    public void runAuto() {
        Graph graph = graphManager.getCurrentGraph();
        if (graph.isEmpty()) {
            logger.logError("Граф пуст.");
            return;
        }
        logger.logSortedEdges(graph.getEdges().stream().sorted().toList());
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.togglePlay(() -> {});
            return;
        }
        if (steps != null && !steps.isEmpty()) {
            autoPlayer.setSteps(steps, currentStepIndex);
        } else {
            autoPlayer.setSteps(null, -1);
        }
        autoPlayer.togglePlay(this::syncFromAuto);
    }

    public void nextStep() {
        if (autoPlayer.isPlaying() && !autoPlayer.isPaused()) {
            autoPlayer.stop();
            logger.log("Автоматическое воспроизведение остановлено.");
        }
        if (steps == null || steps.isEmpty()) {
            logger.log("Сначала запустите алгоритм.");
            return;
        }
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            renderStep(currentStepIndex);
            if (autoPlayer.isPaused() || autoPlayer.isPlaying()) {
                autoPlayer.setSteps(steps, currentStepIndex);
            }
        } else {
            logger.log("Достигнут последний шаг.");
        }
    }

    public void prevStep() {
        if (autoPlayer.isPlaying() && !autoPlayer.isPaused()) {
            autoPlayer.stop();
            logger.log("Автоматическое воспроизведение остановлено.");
        }
        if (steps == null || steps.isEmpty()) {
            logger.log("Сначала запустите алгоритм.");
            return;
        }
        if (currentStepIndex > 0) {
            currentStepIndex--;
            renderStep(currentStepIndex);
            if (autoPlayer.isPaused() || autoPlayer.isPlaying()) {
                autoPlayer.setSteps(steps, currentStepIndex);
            }
        } else {
            logger.log("Это первый шаг.");
        }
    }

    private void renderStep(int index) {
        VisualizationStep step = steps.get(index);
        Graph graph = graphManager.getCurrentGraph();
        renderer.renderStep(graph, step, graphGroup);
        renderer.renderMSTStep(graph, step, mstGroup);
        logger.log(step.getDescription());
    }

    public void syncFromAuto() {
        if (autoPlayer.getSteps() != null && !autoPlayer.getSteps().isEmpty()) {
            steps = autoPlayer.getSteps();
            currentStepIndex = autoPlayer.getCurrentIndex();
        }
    }

    public void setAutoPlayer(AutoPlayer autoPlayer) {
        this.autoPlayer = autoPlayer;
    }
}