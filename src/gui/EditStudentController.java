package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.awt.*;

public class EditStudentController {

    @FXML
    TextField studentName, studentID, studentEmail, dateOfRental;

    @FXML
    ListView checkedOut, savedItems, overdueItems;
}
