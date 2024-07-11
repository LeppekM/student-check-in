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
 * Controller for the edit worker popup that appears when double-clicking a worker in manage workers screen
 */
public class EditWorkerController implements IController {

    @FXML
    private AnchorPane main = new AnchorPane();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private JFXTextField email, workerName, eRFIDw;

    @FXML
    private JFXTextField unmasked;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXCheckBox admin, editParts, workers, removeParts;

    @FXML
    private JFXCheckBox showPass;

    private static Worker worker, loggedWorker;
    private Database database;
    private static String name, workerEmail, password;
    private static int rfid;
    private static boolean priv, edit, work, remove;
    private final StageUtils stageUtils = StageUtils.getInstance();

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

    public void setWorker(Worker w) {
        worker = w;
        database = Database.getInstance();
        workerName.setText(w.getName());
        email.setText(w.getEmail());
        pass.setText(w.getPass());
        eRFIDw.setText(w.getWorkerRFID() + "");
        editParts.selectedProperty().setValue(w.canEditParts());
        removeParts.selectedProperty().setValue(w.canRemoveParts());
        workers.selectedProperty().setValue(w.canEditWorkers());
        if (w.canEditParts() || w.canRemoveParts() || w.canEditWorkers()){
            admin.selectedProperty().setValue(true);
            editParts.setDisable(false);
            workers.setDisable(false);
            removeParts.setDisable(false);

        }
        name = workerName.getText();
        workerEmail = email.getText();
        password = pass.getText();
        rfid = Integer.parseInt(eRFIDw.getText());
        priv = admin.isSelected();
        edit = w.canEditParts();
        work = w.canEditWorkers();
        remove = w.canRemoveParts();
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

    public boolean changed(){
        return !name.equals(workerName.getText()) || !password.equals(pass.getText()) ||
                !workerEmail.equals(email.getText()) || priv != admin.isSelected() || edit != editParts.isSelected() ||
                work != workers.isSelected() || remove != removeParts.isSelected() ||
                rfid != Integer.parseInt(eRFIDw.getText());
    }

    public void save() {
        if (!changed()) {
            stageUtils.informationAlert("No Edits Made", "No changes detected, so no edits made");
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
            if (priv != admin.isSelected()){
                contentText += "\t Admin: " + priv + " --> Admin: " + admin.isSelected() + "\n";
            }
            if (edit != editParts.isSelected()){
                contentText += "\t Edit Parts: " + edit + " --> Edit Parts: " + editParts.isSelected() + "\n";
            }
            if (work != workers.isSelected()){
                contentText += "\t Manage Workers: " + work + " --> Manage Workers: " + workers.isSelected() + "\n";
            }
            if (remove != removeParts.isSelected()){
                contentText += "\t Remove Parts: " + remove + " --> Remove Parts: " + removeParts.isSelected() + "\n";
            }
            if (rfid != Integer.parseInt(eRFIDw.getText())) {
                contentText += "\t" + rfid + " --> " + eRFIDw.getText() + "\n";
            }
            if (stageUtils.confirmationAlert("Edit Success", "Student worker info changing...",
                    contentText)){
                worker.setName(workerName.getText());
                worker.setEmail(email.getText());
                worker.setPass(pass.getText());
                worker.setAdmin(false);
                worker.setWorkerRFID(Integer.parseInt(eRFIDw.getText()));
                worker.setEdit(editParts.isSelected());
                worker.setRemove(removeParts.isSelected());
                worker.setWorker(workers.isSelected());
                database.initWorker(loggedWorker);
                database.updateWorker(worker);
                stageUtils.informationAlert("Edit Success", "Worker updated");
                main.getScene().getWindow().hide();
            } else {
                workerName.setText(name);
                email.setText(workerEmail);
                pass.setText(password);
                eRFIDw.setText(rfid + "");
            }
        }
    }

    public void unblock() {
        if (admin.isSelected()){
            editParts.setDisable(false);
            workers.setDisable(false);
            removeParts.setDisable(false);
        } else {
            editParts.selectedProperty().setValue(false);
            removeParts.selectedProperty().setValue(false);
            workers.selectedProperty().setValue(false);
            editParts.setDisable(true);
            workers.setDisable(true);
            removeParts.setDisable(true);
        }
    }
}
