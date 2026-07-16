package com.kruskal.controller;

import javafx.scene.control.Button;

public class UIStateManager {
    private final Button addNodeButton, addEdgeButton, editWeightButton, deleteNodeButton, deleteEdgeButton, runKruskalManualButton;

    public UIStateManager(Button addNode, Button addEdge, Button editWeight,
                          Button deleteNode, Button deleteEdge, Button runManual) {
        this.addNodeButton = addNode;
        this.addEdgeButton = addEdge;
        this.editWeightButton = editWeight;
        this.deleteNodeButton = deleteNode;
        this.deleteEdgeButton = deleteEdge;
        this.runKruskalManualButton = runManual;
    }

    public void lockControls() {
        setButtonsDisabled(true);
    }

    public void unlockControls() {
        setButtonsDisabled(false);
    }

    private void setButtonsDisabled(boolean disable) {
        addNodeButton.setDisable(disable);
        addEdgeButton.setDisable(disable);
        editWeightButton.setDisable(disable);
        deleteNodeButton.setDisable(disable);
        deleteEdgeButton.setDisable(disable);
        runKruskalManualButton.setDisable(disable);
    }
}