package CheckItemsController;

import Database.*;
import Database.Objects.SavedPart;
import Database.Objects.Student;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SavedPopUp extends StudentPage {

    @FXML
    private JFXTextField studentName, part, quantity, coDate, dueDate, saved, returnDate, course;

    @FXML
    private AnchorPane main;

    private Database database;

    public void populate(SavedPart part){
        studentName.setText(part.getStudentName().get());
        this.part.setText(part.getPartName().get());
        quantity.setText(part.getQuantity().get() + "");
        coDate.setText(part.getCheckedOutAt().get());
        dueDate.setText(part.getDueAt().get());
        saved.setText(part.getSavedAt().get());
        returnDate.setText(part.getReturnDate());
        course.setText(part.getCourse());
    }

    public void undoSave(ActionEvent actionEvent) {
        database = new Database();
        Student s = StudentPage.getStudent();
        int index = -1;
        int index1 = -1;
        for (int i = 0; i < s.getSavedItems().size(); i++){
            if (s.getSavedItems().get(i).getStudentName().get().equals(studentName.getText())) {
                index = i;
            }
            if (s.getCheckedOut().get(i).getStudentName().get().equals(studentName.getText())){
                index1 = i;
            }
        }
        s.getSavedItems().remove(index);
        int id = s.getCheckedOut().get(index1).getCheckID().get();
        try {
            Connection connection = database.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE checkout SET reservedAt = NULL, returnDate = NULL, course = NULL WHERE studentID = " +
                    s.getID() + " and checkoutID = " + id + ";");
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot update database");
            alert.showAndWait();
            e.printStackTrace();
        }
        main.getScene().getWindow().hide();
    }
}
