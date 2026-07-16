package com.kruskal.controller;

import com.kruskal.algorithm.KruskalAlgorithm;
import com.kruskal.algorithm.VisualizationStep;
import com.kruskal.model.Graph;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.scene.Group;

import java.util.List;

public class PlaybackCoordinator {
    private final KruskalAlgorithm algorithm;
    private final GraphRenderer renderer;
    private final Logger logger;
    private final GraphManager graphManager;
    private final AutoPlayer autoPlayer;
    private final Group graphGroup;
    private final Group mstGroup;

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
    }

    public void reset() {
        autoPlayer.reset();
    }

    public void runManual() {
        Graph graph = graphManager.getCurrentGraph();
        if (graph.isEmpty()) {
            logger.logError("Граф пуст.");
            return;
        }
        try {
            List<VisualizationStep> steps = algorithm.executeWithStates(graph);
            logger.logSortedEdges(graph.getEdges().stream().sorted().toList());
            autoPlayer.setSteps(steps, 0);
            renderStep(0);
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

        // Если авто уже активен (играет или на паузе) – просто переключаем
        if (autoPlayer.isPlaying() || autoPlayer.isPaused()) {
            autoPlayer.togglePlay(() -> {});
            return;
        }

        // Если шагов нет – вычисляем и записываем в авто-плеер
        if (autoPlayer.getSteps() == null || autoPlayer.getSteps().isEmpty()) {
            List<VisualizationStep> steps = algorithm.executeWithStates(graph);
            autoPlayer.setSteps(steps, 0);
        }

        logger.logSortedEdges(graph.getEdges().stream().sorted().toList());
        autoPlayer.togglePlay(() -> {});
    }

    public void nextStep() {
        if (autoPlayer.isPlaying() && !autoPlayer.isPaused()) {
            autoPlayer.stop();
            logger.log("Автоматическое воспроизведение остановлено.");
        }

        List<VisualizationStep> steps = autoPlayer.getSteps();
        if (steps == null || steps.isEmpty()) {
            logger.log("Сначала запустите алгоритм.");
            return;
        }

        int index = autoPlayer.getCurrentIndex();
        if (index < steps.size() - 1) {
            index++;
            autoPlayer.setSteps(steps, index);
            renderStep(index);
        } else {
            logger.log("Достигнут последний шаг.");
        }
    }

    public void prevStep() {
        if (autoPlayer.isPlaying() && !autoPlayer.isPaused()) {
            autoPlayer.stop();
            logger.log("Автоматическое воспроизведение остановлено.");
        }

        List<VisualizationStep> steps = autoPlayer.getSteps();
        if (steps == null || steps.isEmpty()) {
            logger.log("Сначала запустите алгоритм.");
            return;
        }

        int index = autoPlayer.getCurrentIndex();
        if (index > 0) {
            index--;
            autoPlayer.setSteps(steps, index);
            renderStep(index);
        } else {
            logger.log("Это первый шаг.");
        }
    }

    private void renderStep(int index) {
        VisualizationStep step = autoPlayer.getSteps().get(index);
        Graph graph = graphManager.getCurrentGraph();
        renderer.renderStep(graph, step, graphGroup);
        renderer.renderMSTStep(graph, step, mstGroup);
        logger.log(step.getDescription());
    }

    public void setAutoPlayer(AutoPlayer autoPlayer) {
        // Здесь ничего не делаем, т.к. autoPlayer передаётся в конструкторе
    }
}