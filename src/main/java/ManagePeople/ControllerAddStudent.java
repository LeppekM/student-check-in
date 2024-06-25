package ManagePeople;

import Database.Database;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import HelperClasses.StageUtils;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
        database = Database.getInstance();
        StageUtils sw = StageUtils.getInstance();
        sw.acceptIntegerOnly(rfid);
        // only allows user to enter 5 digits for rfid
        rfidFilter(rfid);
    }

    /**
     * Checks whether the inputs are valid, in which case it adds the inputs to the
     * database as a new student.
     */
    public void submit() {
        database.initWorker(worker);
        boolean isValid = true;
        if (!first.getText().isEmpty() && !last.getText().isEmpty() &&
                !email.getText().isEmpty() && !rfid.getText().isEmpty()) {
            if (rfid.getText().matches("[0-9]{4,}")) {
                if (email.getText().matches("^\\w+[+.\\w'-]*@msoe\\.edu$")) {
                    if (database.getStudentEmails().contains(email.getText())) {
                        isValid = false;
                        Alert alert = new Alert(Alert.AlertType.ERROR, "A student with that email already exists.");
                        StudentCheckIn.logger.warn("A student with that email already exists.");
                        alert.showAndWait();
                    } else if (database.studentRFIDExists(Integer.parseInt(rfid.getText()))) {
                        isValid = false;
                        Alert alert = new Alert(Alert.AlertType.ERROR, "A student with that rfid already exists.");
                        StudentCheckIn.logger.warn("A student with that rfid already exists.");
                        alert.showAndWait();
                    }
                    if (isValid) {
                        database.addStudent(new Student(first.getText() + " " + last.getText(), Integer.parseInt(rfid.getText()), email.getText()));
                        ((Stage) main.getScene().getWindow()).close();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid msoe email.");
                    StudentCheckIn.logger.warn("Please enter a valid msoe email.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The rfid must be a validly formatted student RFID. Scan the student ID.");
                StudentCheckIn.logger.warn("The rfid must be a validly formatted student RFID. Scan the student ID.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled in.");
            StudentCheckIn.logger.warn("All fields must be filled in.");
            alert.showAndWait();
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

    /**
     * Filters for rfid
     *
     * @param textField TextField to be filtered
     */
    private void rfidFilter(JFXTextField textField) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String id = textField.getText();
                if (textField.getText().contains("rfid:")) {
                    textField.setText(id.substring(5));
                }
            }
        });

    }


}
