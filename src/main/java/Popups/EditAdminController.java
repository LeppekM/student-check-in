package Popups;

import Database.Database;
import Database.ObjectClasses.Worker;
import Controllers.IController;
import HelperClasses.StageUtils;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the edit admin popup that appears when double-clicking an admin in manage workers screen
 */
public class EditAdminController implements IController {

    @FXML
    private AnchorPane main = new AnchorPane();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private JFXTextField email, workerName, eRFIDa;

    @FXML
    private JFXTextField unmasked, unmaskedPin;

    @FXML
    private JFXPasswordField pass, pin;

    @FXML
    private JFXCheckBox showPass, showPin;

    private static Worker worker, loggedWorker;
    private Database database;
    private final StageUtils stageUtils = StageUtils.getInstance();
    private static String name, workerEmail, password;
    private static int adminPin, rfid;

    /**
     * Initializes the window and copies initial values
     * @param w worker to edit
     */
    public void setAdmin(Worker w) {
        worker = w;
        database = Database.getInstance();
        workerName.setText(w.getName());
        email.setText(w.getEmail());
        pass.setText(w.getPass());
        pin.setText(w.getPin() + "");
        eRFIDa.setText(w.getWorkerRFID() + "");
        name = workerName.getText();
        workerEmail = email.getText();
        password = pass.getText();
        rfid = Integer.parseInt(eRFIDa.getText());
        adminPin = Integer.parseInt(pin.getText());
        unmasked.setManaged(false);
        unmaskedPin.setManaged(false);
        unmasked.setVisible(false);
        unmaskedPin.setVisible(false);
        unmasked.managedProperty().bind(showPass.selectedProperty());
        unmaskedPin.managedProperty().bind(showPin.selectedProperty());
        unmasked.visibleProperty().bind(showPass.selectedProperty());
        unmaskedPin.visibleProperty().bind(showPin.selectedProperty());
        pass.managedProperty().bind(showPass.selectedProperty().not());
        pin.managedProperty().bind(showPin.selectedProperty().not());
        pass.visibleProperty().bind(showPass.selectedProperty().not());
        pin.visibleProperty().bind(showPin.selectedProperty().not());
        unmasked.textProperty().bindBidirectional(pass.textProperty());
        unmaskedPin.textProperty().bindBidirectional(pin.textProperty());
        unmasked.setText(w.getPass());
        unmaskedPin.setText(w.getPin() + "");
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(5);
    }

    /**
     * Helper method for saving
     * @return true if nothing changed
     */
    public boolean changed(){
        return !name.equals(workerName.getText()) || !password.equals(pass.getText()) ||
                !workerEmail.equals(email.getText()) || adminPin != Integer.parseInt(pin.getText())
                || rfid != Integer.parseInt(eRFIDa.getText());
    }

    /**
     * Saves the edits to a worker
     */
    public void save() {
        if (!changed()){
            stageUtils.informationAlert("No changes were made", "No changes detected, so no edits made");
        } else {
            String contentText = "Are you sure you want to make the following changes?\n";
            if (!name.equals(workerName.getText())) {
                contentText += "\t" + name + " --> " + workerName.getText() + "\n";
            }
            if (!workerEmail.equals(email.getText())) {
                contentText += "\t" + workerEmail + " --> " + email.getText() + "\n";
            }
            if (!password.equals(pass.getText())){
                contentText += "\t" + password + " --> " + pass.getText() + "\n";
            }
            if (adminPin != Integer.parseInt(pin.getText())){
                contentText += "\t" + adminPin + " --> " + pin.getText() + "\n";
            }
            if (rfid != Integer.parseInt(eRFIDa.getText())) {
                contentText += "\t" + rfid + " --> " + eRFIDa.getText() + "\n";
            }
            if (stageUtils.confirmationAlert("Edit Success", "Student worker info changing...",
                    contentText)){
                worker.setName(workerName.getText());
                worker.setEmail(email.getText());
                worker.setPass(pass.getText());
                worker.setPin(Integer.parseInt(pin.getText()));
                worker.setWorkerRFID(Integer.parseInt(eRFIDa.getText()));
                database.initWorker(loggedWorker);
                database.updateWorker(worker);
                stageUtils.informationAlert("Admin updated", "Admin updated successfully");
                main.getScene().getWindow().hide();
            } else {
                workerName.setText(name);
                email.setText(workerEmail);
                pass.setText(password);
                pin.setText(adminPin + "");
                eRFIDa.setText(rfid + "");
            }
        }
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (loggedWorker == null){
            loggedWorker = worker;
        }
    }
}
