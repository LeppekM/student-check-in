package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.IOException;
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


    public void launchAdminPin() {
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("AdminPopup.fxml"));
            Scene scene = new Scene(pane, 250, 200);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Admin Credentials Needed");
            diffStage.showAndWait();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

}
