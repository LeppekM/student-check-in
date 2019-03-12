package CheckItemsController;

import Database.Database;
import Database.Objects.SavedPart;
import Database.Objects.Student;
import InventoryController.CheckedOutItems;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckoutPopUp extends StudentPage {

    @FXML
    private JFXTextField student, part, barcode, coDate, dueDate;

    @FXML
    private Label cID;

    @FXML
    private AnchorPane main;

    private Database database;

    public void populate(CheckedOutItems checked){
        student.setText(checked.getStudentName().get());
        part.setText(checked.getPartName().get());
        barcode.setText(checked.getBarcode().get() + "");
        coDate.setText(checked.getCheckedOutAt().get());
        dueDate.setText(checked.getDueDate().get());
        cID.setText("Checkout ID: " + checked.getCheckID().get());
    }

    public void savePart(ActionEvent actionEvent) {
        database = new Database();
        Student s = StudentPage.getStudent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean result = false;
        for (int j = 0; j < s.getSavedItems().size(); j++) {
            int sID = Integer.parseInt(s.getSavedItems().get(j).getCheckID());
            result = (sID == Integer.parseInt(cID.getText().substring(13)));
            if (result){
                break;
            }
        }
        if (!result){
            boolean dataCheck = true;
            String returnDate = "";
            String course = "";
            while (dataCheck) {
                returnDate = JOptionPane.showInputDialog(null, "Please enter a date on which the item will be taken back out");
                if (returnDate.matches("^\\d{4}-\\d{2}-\\d{2}$")){
                    dataCheck = false;
                }else {
                    JOptionPane.showMessageDialog(null, "Date must be of the form: yyyy-mm-dd");
                }
            }
            dataCheck = true;
            while (dataCheck) {
                course = JOptionPane.showInputDialog(null, "Please enter a course code (i.e. CS3840)");
                if (course.matches("^[A-Za-z]{2}\\d{3,4}$")){
                    dataCheck = false;
                }else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid course code.");
                }
            }
            s.getSavedItems().add(new SavedPart(student.getText(), part.getText(), coDate.getText(), Integer.parseInt(barcode.getText()),
                    sdf.format(new Date(System.currentTimeMillis())), dueDate.getText(), cID.getText(), returnDate, course));
            try {
                Connection connection = database.getConnection();
                Statement statement = connection.createStatement();
                long date = System.currentTimeMillis();
                java.sql.Date d = new java.sql.Date(date);
                statement.executeUpdate("UPDATE checkout SET  reservedAt = date('" + d.toString() + "'), returnDate = '" + returnDate + "', course = '" + course + "'" +
                        " WHERE studentID = " + s.getID() + " and checkoutID = " + cID.getText().substring(13) + ";");
            }catch (SQLException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not update database");
                alert.showAndWait();
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Part has already been saved");
            alert.showAndWait();
        }
        main.getScene().getWindow().hide();
    }
}
