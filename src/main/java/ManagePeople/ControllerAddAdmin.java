package ManagePeople;

import Database.*;
import Database.Objects.Worker;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerAddAdmin implements Initializable {

    @FXML
    private AnchorPane main;

    @FXML
    private JFXTextField email, first, last;

    @FXML
    private JFXPasswordField pass, pin;

    @FXML
    private JFXButton submit;

    private Database database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        submit.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        database = new Database();
    }

    public void submit(ActionEvent actionEvent) {
        Pattern p = Pattern.compile("[0-9]*");
        StringBuilder n = new StringBuilder();
        Matcher m;
        boolean emailValid = false;
        boolean fValid = false;
        boolean lValid = false;
        boolean passValid = false;
        boolean pinValid = false;
        if (!email.getText().equals("") && !first.getText().equals("") && !last.getText().equals("") &&
                !pass.getText().equals("") && !pin.getText().equals("")){
            ObservableList<Worker> workers = database.getWorkers();
            for (Worker w : workers) {
                if (w.getEmail().equals(email.getText())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Admin is already in the database!");
                    StudentCheckIn.logger.warn("Add Admin: Admin is already in the database.");
                    alert.showAndWait();
                    emailValid = false;
                    break;
                }
            }
            if (!email.getText().matches("^\\w+[+.\\w-]*@msoe\\.edu$")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email must be an MSOE email.");
                StudentCheckIn.logger.warn("Add Admin: Email isn't @msoe.edu");
                alert.showAndWait();
                emailValid = false;
            }else {
                emailValid = true;
            }
            m = p.matcher(first.getText());
            if (!m.matches() && !first.getText().matches("\\s*")){
                String temp = first.getText().substring(0, 1).toUpperCase() + first.getText().substring(1);
                n = new StringBuilder(temp);
                fValid = true;
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Admins first name is invalid");
                StudentCheckIn.logger.warn("Add Admin: First name matches and invalid regex");
                alert.showAndWait();
                fValid = false;
            }
            m = p.matcher(last.getText());
            if (!m.matches() && !last.getText().matches("\\s*")){
                String temp = last.getText().substring(0, 1).toUpperCase() + last.getText().substring(1);
                n.append(" ").append(temp);
                lValid = true;
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Admins last name is invalid");
                StudentCheckIn.logger.warn("Add Admin: Last name matches and invalid regex");
                alert.showAndWait();
                lValid = false;
            }
            if (pass.getText().length() < 8){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Password must be at least eight characters in length.");
                StudentCheckIn.logger.warn("Add Admin: Password not at least 8 characters.");
                alert.showAndWait();
                passValid = false;
            }else {
                passValid = true;
            }
            if (pin.getText().matches("[0-9]{4}")){
                pinValid = true;
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Pin must be 4 digits.");
                StudentCheckIn.logger.warn("Add Admin: Pin isn't 4 digits.");
                alert.showAndWait();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled in.");
            StudentCheckIn.logger.warn("Add Admin: All fields not filled.");
            alert.showAndWait();
        }
        if (emailValid && fValid && lValid && passValid && pinValid){
            ObservableList<Worker> w = database.getWorkers();
            database.addWorker(new Worker(n.toString(), w.get(w.size() - 1).getID() + 1, email.getText(), pass.getText(), Integer.parseInt(pin.getText()), true));
            main.getScene().getWindow().hide();
        }
    }
}
