package CheckItemsController;

import Database.Database;
import Database.ObjectClasses.SavedPart;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import InventoryController.CheckedOutItems;
import InventoryController.IController;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class CheckoutPopUp extends StudentPage implements IController {

    @FXML
    private JFXTextField name, part, barcode, coDate, dueDate;

    @FXML
    private Label cID;

    @FXML
    private AnchorPane main;

    private Database database;
    private Worker worker;

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker){
        if (this.worker == null){
            this.worker = worker;
        }
    }

    public void populate(CheckedOutItems checked){
        name.setText(checked.getStudentName().get());
        part.setText(checked.getPartName().get());
        barcode.setText(checked.getBarcode().get() + "");
        coDate.setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(checked.getCheckedOutDate().get()));
        dueDate.setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(checked.getDueDate().get()));
        cID.setText("Checkout ID: " + checked.getCheckoutID().get());
    }

    public void savePart(ActionEvent actionEvent) {
        database = new Database();
        Student s = StudentPage.getStudent();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean result = false;
        boolean date = false;
        boolean curse = false;
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
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Date Input");
                dialog.setHeaderText("Please enter a date on which the item will \nbe taken back out (in the form yyyy-mm-dd)");
                Optional<String> dateResult = dialog.showAndWait();
                if (dateResult.isPresent()) {
                    returnDate = dateResult.get();
                }
                if (returnDate != null && !returnDate.matches("\\s*")) {
                    if (returnDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                        dataCheck = false;
                        date = true;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Date must be of the form: yyyy-mm-dd");
                        alert.showAndWait();
                        date = false;
                    }
                }else {
                    date = false;
                    break;
                }
            }
            dataCheck = true;
            while (dataCheck) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Course Code Input");
                dialog.setHeaderText("Please enter a course code (i.e. CS3840)");
                Optional<String> courseCodeResult = dialog.showAndWait();
                if (courseCodeResult.isPresent()) {
                    course = courseCodeResult.get();
                }
                if (course != null && !course.matches("\\s*")) {
                    if (course.matches("^[A-Za-z]{2}\\d{3,4}$")) {
                        dataCheck = false;
                        curse = true;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Please enter a valid course code.");
                        alert.showAndWait();
                        curse = false;
                    }
                }else {
                    curse = false;
                    break;
                }
            }
            if (date && curse) {
                s.getSavedItems().add(new SavedPart(name.getText(), part.getText(), coDate.getText(), Integer.parseInt(barcode.getText()),
                        sdf.format(new Date(System.currentTimeMillis())), dueDate.getText(), cID.getText(), returnDate, course));
                try {
                    Connection connection = database.getConnection();
                    Statement statement = connection.createStatement();
                    long time = System.currentTimeMillis();
                    java.sql.Date d = new java.sql.Date(time);
                    statement.executeUpdate("UPDATE checkout SET  reservedAt = date('" + d.toString() + "'), returnDate = '" +
                            returnDate + "', course = '" + course + "'" + ", updatedAt = date('" + d.toString() + "'), updatedBy = '"
                            + worker + "' WHERE studentID = " + s.getRFID() + " and checkoutID = " + cID.getText().substring(13) + ";");
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Could not update database");
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Part has already been saved");
            alert.showAndWait();
        }
        main.getScene().getWindow().hide();
    }
}
