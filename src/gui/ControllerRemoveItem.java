package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerRemoveItem implements Initializable {

    @FXML
    private Button submit;

    @FXML
    private Hyperlink cancel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void goBack(){
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void removeItem(){
        System.out.println("Part Removed!");
    }

}
