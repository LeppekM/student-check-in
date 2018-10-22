package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.util.ResourceBundle;

public class AdminPopupController implements Initializable {
    private final String pinID = "1234";

    @FXML
    TextField pin;

    @FXML
    VBox root;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void submit(){
        if(pin.getText().equals(pinID)){
            root.getScene().getWindow().hide();
        }
        else{
            failureAlert();
        }

    }

    public void failureAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Incorrect Pin");
        alert.setContentText("You entered an incorrect pin.");

        alert.showAndWait();
    }


}
