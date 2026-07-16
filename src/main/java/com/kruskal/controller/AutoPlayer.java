package com.kruskal.controller;

import com.kruskal.algorithm.KruskalAlgorithm;
import com.kruskal.algorithm.VisualizationStep;
import com.kruskal.model.Graph;
import com.kruskal.util.Logger;
import com.kruskal.visualisation.GraphRenderer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.List;

public class AutoPlayer {
    private final GraphRenderer renderer;
    private final Logger logger;
    private final Button autoButton;
    private final TextField speedField;
    private final Group graphGroup;
    private final Group mstGroup;
    private final KruskalAlgorithm algorithm;

    private Graph currentGraph;
    private List<VisualizationStep> stepsRef;
    private int currentIndexRef;

    private Timeline timeline;
    private boolean isPlaying;
    private boolean isPaused;
    private Runnable onFinish;
    private Runnable onPauseCallback;
    private Runnable onComplete; // новый callback для завершения

    public AutoPlayer(GraphRenderer renderer, Logger logger,
                      Button autoButton, TextField speedField,
                      Group graphGroup, Group mstGroup,
                      KruskalAlgorithm algorithm, Runnable onPauseCallback) {
        this.renderer = renderer;
        this.logger = logger;
        this.autoButton = autoButton;
        this.speedField = speedField;
        this.graphGroup = graphGroup;
        this.mstGroup = mstGroup;
        this.algorithm = algorithm;
        this.isPlaying = false;
        this.isPaused = false;
        this.onPauseCallback = onPauseCallback;
        resetButton();
    }

    public void setGraph(Graph graph) {
        this.currentGraph = graph;
    }

    public void setSteps(List<VisualizationStep> steps, int currentIndex) {
        this.stepsRef = steps;
        this.currentIndexRef = currentIndex;
    }

    public void togglePlay(Runnable onFinish) {
        if (currentGraph == null || currentGraph.isEmpty()) {
            logger.logError("Граф пуст. Загрузите или сгенерируйте граф.");
            return;
        }

        this.onFinish = onFinish;

        if (isPlaying && !isPaused) {
            pause();
            return;
        }
        if (isPaused) {
            resume();
            return;
        }
        start();
    }

    private void start() {
        if (stepsRef == null || stepsRef.isEmpty()) {
            try {
                stepsRef = algorithm.executeWithStates(currentGraph);
                currentIndexRef = 0;
            } catch (IllegalArgumentException e) {
                logger.logError(e.getMessage());
                resetButton();
                return;
            }
        } else {
            if (currentIndexRef < 0 || currentIndexRef >= stepsRef.size()) {
                currentIndexRef = 0;
            } else if (currentIndexRef >= stepsRef.size() - 1) {
                currentIndexRef = 0;
            }
        }

        renderStep(currentIndexRef);
        createAndPlayTimeline();
        isPlaying = true;
        isPaused = false;
        autoButton.setText("Pause");
        logger.log("Автоматическое воспроизведение начато с шага " + (currentIndexRef + 1) + ", скорость " + parseSpeed() + " мс.");
    }

    private void pause() {
        if (isPlaying && !isPaused) {
            timeline.pause();
            isPaused = true;
            autoButton.setText("Resume");
            logger.log("Автоматическое воспроизведение поставлено на паузу.");
            if (onPauseCallback != null) {
                onPauseCallback.run();
            }
        }
    }

    private void resume() {
        if (isPaused) {
            if (timeline != null) {
                timeline.stop();
                timeline = null;
            }
            createAndPlayTimeline();
            isPaused = false;
            isPlaying = true;
            autoButton.setText("Pause");
            logger.log("Автоматическое воспроизведение продолжено со скоростью " + parseSpeed() + " мс.");
        }
    }

    private void createAndPlayTimeline() {
        int speed = parseSpeed();
        timeline = new Timeline(
                new KeyFrame(Duration.millis(speed), e -> {
                    if (currentIndexRef < stepsRef.size() - 1) {
                        currentIndexRef++;
                        renderStep(currentIndexRef);
                    } else {
                        stop();
                        if (onFinish != null) onFinish.run();
                        autoButton.setText("Run Kruskal Auto");
                        logger.log("Автоматическое воспроизведение завершено.");
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        isPlaying = false;
        isPaused = false;
        resetButton();

        // Вызываем callback завершения, если он установлен
        if (onComplete != null) {
            onComplete.run();
            onComplete = null;
        }
    }

    public void reset() {
        stop();
        stepsRef = null;
        currentIndexRef = -1;
        resetButton();
    }

    private void renderStep(int index) {
        VisualizationStep step = stepsRef.get(index);
        renderer.renderStep(currentGraph, step, graphGroup);
        renderer.renderMSTStep(currentGraph, step, mstGroup);
        logger.log(step.getDescription());
    }

    private int parseSpeed() {
        try {
            String text = speedField.getText().trim();
            if (text.isEmpty()) {
                speedField.setText("2000");
                return 2000;
            }
            int speed = Integer.parseInt(text);
            if (speed < 100) {
                speedField.setText("100");
                return 100;
            }
            return speed;
        } catch (NumberFormatException e) {
            speedField.setText("2000");
            return 2000;
        }
    }

    private void resetButton() {
        autoButton.setText("Run Kruskal Auto");
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public List<VisualizationStep> getSteps() {
        return stepsRef;
    }

    public int getCurrentIndex() {
        return currentIndexRef;
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }
}