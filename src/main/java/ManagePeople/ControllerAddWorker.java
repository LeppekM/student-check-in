package ManagePeople;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAddWorker implements Initializable {

    @FXML
    private JFXTextField email, first, last;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXButton submit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        submit.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
    }
}
