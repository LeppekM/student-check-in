package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerRemoveItem implements Initializable {

    @FXML
    private VBox sceneRemovePart;

    @FXML
    private Button submit, cancel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void goBack(){
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    private void removeItem(){
    }

}
