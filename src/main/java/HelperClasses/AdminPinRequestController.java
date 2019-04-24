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

    private StageWrapper stageWrapper;

    private boolean submitted;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stageWrapper = new StageWrapper();
        submitted = false;
        stageWrapper.acceptIntegerOnly(adminPinInputAdminPinRequest);
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
        sceneAdminPinRequest.fireEvent(new WindowEvent(((Node) sceneAdminPinRequest).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public boolean isNotEmpty() {
        return !adminPinInputAdminPinRequest.getText().equals("");
    }

    public boolean isValid() {
        Database database = new Database();
        return database.isValidPin(Integer.parseInt(adminPinInputAdminPinRequest.getText()));
    }

}