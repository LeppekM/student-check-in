package ManagePeople;

import Database.*;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAddStudent implements Initializable, IController {

    @FXML
    private AnchorPane main;

    @FXML
    private JFXTextField email, first, last, rfid;

    private Database database;
    private Worker worker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
        // only allows user to enter 5 digits for rfid
        rfid.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("^\\D*(?:\\d\\D*){0,5}$")) {
                    rfid.setText(oldValue);
                }
            }
        });
    }

    /**
     * Checks whether the inputs are valid, in which case it adds the inputs to the
     * database as a new student.
     */
    public void submit() {
        database.initWorker(worker);
        boolean isValid = true;
        if (database.getStudentEmails().contains(email.getText())) {
            isValid = false;
            Alert alert = new Alert(Alert.AlertType.ERROR, "A student with that email already exists.");
            StudentCheckIn.logger.warn("A student with that email already exists.");
            alert.showAndWait();
        }
        if (database.getStudentRFIDs().contains(rfid.getText())) {
            isValid = false;
            Alert alert = new Alert(Alert.AlertType.ERROR, "A student with that rfid already exists.");
            StudentCheckIn.logger.warn("A student with that rfid already exists.");
            alert.showAndWait();
        }
        if (isValid) {
            database.addStudent(new Student(first.getText() + " " + last.getText(), Integer.parseInt(rfid.getText()), email.getText()));
        }
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null){
            this.worker = worker;
        }
    }


}
