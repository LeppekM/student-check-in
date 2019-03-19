package ManagePeople;

import CheckItemsController.CheckoutPopUp;
import CheckItemsController.SavedPopUp;
import Database.Database;
import Database.Objects.SavedPart;
import Database.Objects.Student;
import Database.Objects.Worker;
import Database.OverdueItem;
import InventoryController.CheckedOutItems;
import InventoryController.OverduePopUpController;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Optional;

public class EditWorker {

    @FXML
    private AnchorPane main = new AnchorPane();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private JFXTextField email, workerName;

    @FXML
    private JFXTextField unmasked;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXCheckBox admin, parts, overdue, workers, students;

    @FXML
    private JFXCheckBox showPass;

    private static Worker worker;
    private Database database = new Database();
    private static String name;
    private static String workerEmail;
    private static String password;
    private static boolean priv;
    private static boolean allParts;
    private static boolean over;
    private static boolean work;
    private static boolean stu;

    public void setWorker(Worker w) {
        worker = w;
        workerName.setText(w.getName());
        email.setText(w.getEmail());
        pass.setText(w.getPass());
        name = workerName.getText();
        workerEmail = email.getText();
        password = pass.getText();
        priv = admin.isSelected();
        allParts = parts.isSelected();
        over = overdue.isSelected();
        work = workers.isSelected();
        stu = students.isSelected();
        unmasked.setManaged(false);
        unmasked.setVisible(false);
        unmasked.managedProperty().bind(showPass.selectedProperty());
        unmasked.visibleProperty().bind(showPass.selectedProperty());
        pass.managedProperty().bind(showPass.selectedProperty().not());
        pass.visibleProperty().bind(showPass.selectedProperty().not());
        unmasked.textProperty().bindBidirectional(pass.textProperty());
        unmasked.setText(w.getPass());
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(5);
    }


    public void save(ActionEvent actionEvent) {
        if (name.equals(workerName.getText()) && password.equals(pass.getText()) && workerEmail.equals(email.getText()) &&
                priv == admin.isSelected() && allParts == parts.isSelected() && over == overdue.isSelected() &&
                work == workers.isSelected() && stu == students.isSelected()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No changes detected...");
            alert.setTitle("Edit Failure");
            alert.setHeaderText("No changes were made.");
            alert.showAndWait();
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to make the following changes?\n");
            alert.setTitle("Edit Success");
            alert.setHeaderText("Student worker info changing...");
            if (!name.equals(workerName.getText())) {
                alert.setContentText(alert.getContentText() + "\t" + name + " --> " + workerName.getText() + "\n");
            }
            if (!workerEmail.equals(email.getText())) {
                alert.setContentText(alert.getContentText() + "\t" + workerEmail + " --> " + email.getText() + "\n");
            }
            if (!password.equals(pass.getText())){
                alert.setContentText(alert.getContentText() + "\t" + password + " --> " + pass.getText() + "\n");
            }
            if (priv != admin.isSelected()){
                alert.setContentText(alert.getContentText() + "\t Admin: " + priv + " --> Admin: " + admin.isSelected() + "\n");
            }
            if (allParts != parts.isSelected()){
                alert.setContentText(alert.getContentText() + "\t Remove/Edit Parts: " + allParts + " --> Remove/Edit Parts: " + parts.isSelected() + "\n");
            }
            if (over != overdue.isSelected()){
                alert.setContentText(alert.getContentText() + "\t Override Overdue: " + over + " --> Override Overdue: " + overdue.isSelected() + "\n");
            }
            if (work != workers.isSelected()){
                alert.setContentText(alert.getContentText() + "\t Manage Workers: " + work + " --> Manage Workers: " + workers.isSelected() + "\n");
            }
            if (stu != students.isSelected()){
                alert.setContentText(alert.getContentText() + "\t Remove Students: " + stu + " --> Remove Students: " + students.isSelected() + "\n");
            }
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                worker.setName(workerName.getText());
                worker.setEmail(email.getText());
                worker.setPass(pass.getText());
                worker.setAdmin(admin.isSelected());
                worker.setOver(overdue.isSelected());
                worker.setParts(parts.isSelected());
                worker.setStudent(students.isSelected());
                worker.setWorker(workers.isSelected());
                database.updateWorker(worker);
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Worker updated");
                alert1.showAndWait();
            }else if (result.isPresent() && result.get() == ButtonType.CANCEL){
                workerName.setText(name);
                email.setText(workerEmail);
                pass.setText(password);
            }
        }
    }

    public void unblock(ActionEvent actionEvent) {
        if (admin.isSelected()){
            parts.setDisable(false);
            overdue.setDisable(false);
            workers.setDisable(false);
            students.setDisable(false);
        }else {
            parts.setDisable(true);
            overdue.setDisable(true);
            workers.setDisable(true);
            students.setDisable(true);
        }
    }
}
