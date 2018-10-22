package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class FaultController implements Initializable {

    @FXML
    VBox root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void submit(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fault Information");
        alert.setHeaderText("");
        alert.setContentText("Thank you, your description has been submitted");
        alert.showAndWait();
        root.getScene().getWindow().hide();
    }
}
