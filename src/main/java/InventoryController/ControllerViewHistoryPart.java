package InventoryController;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class ControllerViewHistoryPart {

    @FXML
    private VBox sceneViewHistoryPart;

    public void populate(HistoryTabTableRow row) {

    }

    public void goBack() {
        sceneViewHistoryPart.fireEvent(new WindowEvent(((Node) sceneViewHistoryPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}