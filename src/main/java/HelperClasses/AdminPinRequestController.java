package HelperClasses;

import Database.Database;
import com.jfoenix.controls.JFXPasswordField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminPinRequestController implements Initializable {

    @FXML
    private VBox sceneAdminPinRequest;

    @FXML
    private Label adminPinRequestTitle;

    @FXML
    private JFXPasswordField adminPinInputAdminPinRequest;

    private boolean submitted;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StageUtils stageUtils = StageUtils.getInstance();
        submitted = false;
        stageUtils.acceptIntegerOnly(adminPinInputAdminPinRequest);
        adminPinInputAdminPinRequest.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                submit();
            }
        });
    }

    public void setAction(String action) {
        adminPinRequestTitle.setText("You do not have permission to " + action + ".\n" +
                "To override, enter an administrator pin.");
    }

    @FXML
    private void submit() {
        submitted = true;
        sceneAdminPinRequest.fireEvent(new WindowEvent(sceneAdminPinRequest.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public boolean isNotEmpty() {
        return !adminPinInputAdminPinRequest.getText().isEmpty();
    }

    public boolean isValid() {
        Database database = Database.getInstance();
        return database.isValidPin(Integer.parseInt(adminPinInputAdminPinRequest.getText()));
    }

}