package CheckItemsController;

import Database.*;
import Database.ObjectClasses.SavedPart;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import InventoryController.IController;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import java.nio.file.Watchable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SavedPopUp extends StudentPage implements IController {

    @FXML
    private JFXTextField name, part, quantity, coDate, dueDate, saved, returnDate, course;

    @FXML
    private AnchorPane main;

    private Database database;
    private Worker worker;

    @Override
    public void initWorker(Worker worker){
        if (this.worker == null){
            this.worker = worker;
        }
    }

    public void populate(SavedPart part){
        name.setText(part.getStudentName().get());
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
            if (s.getSavedItems().get(i).getStudentName().get().equals(name.getText())) {
                index = i;
            }
            if (s.getCheckedOut().get(i).getStudentName().get().equals(name.getText())){
                index1 = i;
            }
        }
        s.getSavedItems().remove(index);
        int id = s.getCheckedOut().get(index1).getCheckoutID().get();
        try {
            Connection connection = database.getConnection();
            Statement statement = connection.createStatement();
            long time = System.currentTimeMillis();
            java.sql.Date d = new java.sql.Date(time);
            String query = "UPDATE checkout SET reservedAt = NULL, returnDate = NULL, course = NULL, updatedAt = date('"
                    + d.toString() + "'), updatedBy = '" + worker + "' WHERE studentID = " + s.getRFID() + " and checkoutID = " + id + ";";
            statement.executeUpdate(query);
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot update database");
            alert.showAndWait();
            e.printStackTrace();
        }
        main.getScene().getWindow().hide();
    }
}
