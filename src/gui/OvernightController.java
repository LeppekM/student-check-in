package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class OvernightController implements Initializable {
    @FXML
    TextField courseID;
    @FXML
    TextField profName;
    @FXML
    TextField dueBy;

    @FXML
    VBox root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void submit(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Thank you, information was submit to database");
        alert.showAndWait();
        root.getScene().getWindow().hide();
    }
}
