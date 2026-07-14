package com.kruskal.util;

import com.kruskal.model.Edge;
import javafx.scene.control.TextArea;

import java.util.List;

/**
 * Логирование шагов алгоритма в панель Steps.
 */
public class Logger {

    private final TextArea textArea;

    public Logger(TextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Добавляет сообщение в лог.
     */
    public void log(String message) {
        textArea.appendText(message + "\n");
    }

    /**
     * Очищает панель логов и выводит приветственное сообщение.
     */
    public void clear() {
        textArea.clear();
        textArea.setText("Добро пожаловать! Загрузите граф или создайте его вручную.\n");
    }

    /**
     * Выводит отсортированный список рёбер.
     */
    public void logSortedEdges(List<Edge> sortedEdges) {
        textArea.appendText("\nРёбра отсортированы по весу:\n");
        for (int i = 0; i < sortedEdges.size(); i++) {
            Edge edge = sortedEdges.get(i);
            textArea.appendText(String.format("  %d. %d - %d (вес %d)\n",
                    i + 1,
                    edge.getNode1().getId(),
                    edge.getNode2().getId(),
                    edge.getWeight()));
        }
    }

    /**
     * Выводит итоговый результат работы алгоритма.
     */
    public void logResult(int totalWeight, List<Edge> mstEdges, int vertexCount) {
        textArea.appendText("\nАлгоритм Краскала завершён.\n");
        textArea.appendText("Минимальное остовное дерево содержит " + mstEdges.size() + " рёбер.\n");
        textArea.appendText("Общий вес дерева: " + totalWeight + ".\n");

        textArea.appendText("Рёбра, вошедшие в остов:\n");
        for (Edge edge : mstEdges) {
            textArea.appendText(String.format("  %d - %d (вес %d)\n",
                    edge.getNode1().getId(),
                    edge.getNode2().getId(),
                    edge.getWeight()));
        }

        if (mstEdges.size() == vertexCount - 1) {
            textArea.appendText("Граф связный, остовное дерево построено полностью.\n");
        } else {
            textArea.appendText("Граф несвязный, построен минимальный остовный лес.\n");
        }
    }

    /**
     * Выводит информацию о генерации графа.
     */
    public void logGraphGenerated(int vertexCount, int edgeCount) {
        textArea.appendText("\nСгенерирован случайный граф.\n");
        textArea.appendText("Количество вершин: " + vertexCount + ", рёбер: " + edgeCount + ".\n");
    }

    /**
     * Выводит информацию о загрузке графа из файла.
     */
    public void logGraphLoaded(String fileName, int vertexCount, int edgeCount) {
        textArea.appendText("\nГраф загружен из файла " + fileName + ".\n");
        textArea.appendText("Количество вершин: " + vertexCount + ", рёбер: " + edgeCount + ".\n");
    }

    /**
     * Выводит информацию о сохранении графа в файл.
     */
    public void logGraphSaved(String fileName) {
        textArea.appendText("\nГраф успешно сохранён: " + fileName);
    }

    /**
     * Выводит сообщение о начале выполнения алгоритма.
     */
    public void logAlgorithmStarted() {
        textArea.appendText("\nЗапуск алгоритма Краскала.\n");
    }

    /**
     * Выводит сообщение об ошибке.
     */
    public void logError(String errorMessage) {
        textArea.appendText("\nОшибка: " + errorMessage + "\n");
    }

    /**
     * Выводит сообщение об очистке рабочей области.
     */
    public void logCleared() {
        textArea.appendText("\nРабочая область очищена.\n");
    }
}
