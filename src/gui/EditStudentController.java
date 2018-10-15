package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.scene.control.TextField;

public class EditStudentController {

    @FXML
    TextField studentName, studentID, studentEmail, dateOfRental;

    @FXML
    ListView checkedOut, savedItems, overdueItems;

    @FXML
    private void checkDate(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)){
            if(!dateOfRental.getText().matches("[0-1][0-9]\\/[0-3][0-9]\\/[0-9]{4}")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, invalid date was entered");
                alert.showAndWait();
            }
        }
    }

    public void checkEmail(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)){
            if(!studentEmail.getText().matches("^(.+)@msoe\\.edu$")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, invalid email was entered.\nNeeds to be an MSOE email");
                alert.showAndWait();
            }
        }
    }
}
