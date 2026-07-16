package com.kruskal.util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GenerateGraphDialog {
    public static int[] showAndWait() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Генерация графа");
        dialog.setHeaderText("Введите параметры графа");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType generateButtonType = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(generateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField vertexCountField = new TextField("4");
        TextField edgeCountField = new TextField("5");

        grid.add(new Label("Количество вершин (2–20):"), 0, 0);
        grid.add(vertexCountField, 1, 0);
        grid.add(new Label("Количество рёбер (минимум n - 1, максимум (n*(n-1)) / 2:"), 0, 1);
        grid.add(edgeCountField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == generateButtonType) {
                try {
                    int vertices = Integer.parseInt(vertexCountField.getText().trim());
                    int edges = Integer.parseInt(edgeCountField.getText().trim());
                    int maxEdges = vertices * (vertices - 1) / 2;
                    if (vertices < 2 || vertices > 20) {
                        showError("Количество вершин должно быть от 2 до 20.");
                        return null;
                    }
                    if (edges < vertices - 1 || edges > maxEdges) {
                        showError("Количество рёбер должно быть от " + (vertices - 1) + " до " + maxEdges + ".");
                        return null;
                    }
                    return new int[]{vertices, edges};
                } catch (NumberFormatException e) {
                    showError("Введите целые числа.");
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}