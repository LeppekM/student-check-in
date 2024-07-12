package Tables;

import Database.ObjectClasses.Worker;
import HelperClasses.ExportToExcel;
import Controllers.TableScreensController;
import Popups.EditAdminController;
import Popups.EditWorkerController;
import Popups.Popup;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static Controllers.CheckOutController.EMAIL_REGEX;
import static Controllers.CheckOutController.RFID_REGEX;

/**
 * Manages the workers in the database from the Manage Workers screen as a table
 */
public class ManageWorkersTable extends TSCTable {

    private JFXTreeTableColumn<MWRow, String> nameCol, emailCol;
    private JFXTreeTableColumn<MWRow, Boolean> adminCol;

    public ManageWorkersTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 3;
        table.setPlaceholder(getEmptyTableLabel());

        nameCol = createNewCol("Name");
        nameCol.setCellValueFactory(col -> col.getValue().getValue().getName());
        emailCol = createNewCol("Email");
        emailCol.setCellValueFactory(col -> col.getValue().getValue().getEmail());
        adminCol = createNewCol("Is an Admin");
        adminCol.setCellValueFactory(param -> {
            TreeItem<MWRow> treeItem = param.getValue();
            MWRow tRow = treeItem.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(tRow.getIsAdmin().get());
            booleanProp.addListener((observable, oldValue, newValue) -> tRow.setIsAdmin(newValue));
            return booleanProp;
        });
        adminCol.setCellFactory(p -> {
            CheckBoxTreeTableCell<MWRow, Boolean> cell = new CheckBoxTreeTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        rows = FXCollections.observableArrayList();

        setDoubleClickBehavior();
    }

    @Override
    public void export(ExportToExcel exportToExcel) {
        // workers do not export
    }

    @Override
    public void populateTable() {
        // clear previous data
        rows.clear();
        table.getColumns().clear();
        // get and add all rows
        ObservableList<Worker> list = database.getWorkers();
        for (Worker datum : list) {
            rows.add(new MWRow(datum.getName(), datum.getEmail(), datum.isAdmin()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> nameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) nameCol;
        TreeTableColumn<TableRow, String> emailTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) emailCol;
        TreeTableColumn<TableRow, String> adminTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) adminCol;

        table.getColumns().setAll(nameTemp, emailTemp, adminTemp);
        table.setRoot(root);
        table.setShowRoot(false);
        controller.repopulatedTableSearch();
    }

    @Override
    protected boolean isMatch(TableRow value, String[] filters) {
        for (String filter : filters) {
            MWRow val = (MWRow) value;
            String input = filter.toLowerCase();
            String name = val.getName().getValue();
            String email = val.getEmail().getValue();
            if (!(name != null && name.toLowerCase().contains(input)
                    || email != null && email.toLowerCase().contains(input))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void popupRow(int index) {
        Stage stage = new Stage();
        MWRow r = (MWRow) table.getSelectionModel().getModelItem(index).getValue();
        Worker w = database.getWorker(r.getEmail().get());
        if (w.isAdmin()) {
            openEditWorker("fxml/EditAdmin.fxml", w, stage);
        } else {
            openEditWorker("fxml/EditWorker.fxml", w, stage);
        }
    }

    private void openEditWorker(String fxml, Worker w, Stage stage) {
        try {
            URL myFxmlURL = ClassLoader.getSystemResource(fxml);
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            if (w.isAdmin()) {
                EditAdminController ea = loader.getController();
                ea.setAdmin(w);
                ea.initWorker(worker);
                if (ea.changed()) {
                    stageUtils.unsavedChangesAlert(stage);
                }
            } else {
                EditWorkerController ew = loader.getController();
                ew.setWorker(w);
                ew.initWorker(worker);
                if (ew.changed()) {
                    stageUtils.unsavedChangesAlert(stage);
                }
            }
            Scene scene = new Scene(root, 790, 600);
            stage.setTitle("Edit " + w.getName());
            stage.initOwner(controller.getScene().getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.setOnHiding(event1 -> populateTable());
            stage.show();
            populateTable();
        } catch (IOException e) {
            stageUtils.errorAlert("Couldn't load admin info page");
        }
    }

    public void addWorker() {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setTitle("Add a New Worker");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);

        new Popup(root) {
            private JFXTextField email, first, last, rfid;
            private JFXPasswordField pass;

            @Override
            public void populate() {
                email = (JFXTextField) add("MSOE Email: ", "", true).getChildren().get(1);
                first = (JFXTextField) add("First Name: ", "", true).getChildren().get(1);
                last = (JFXTextField) add("Last Name: ", "", true).getChildren().get(1);
                pass = addPasswordField("Password: ");
                rfid = (JFXTextField) add("RFID: ", "", true).getChildren().get(1);
                stageUtils.acceptIntegerOnly(rfid);
            }

            @Override
            public void submit() {
                StringBuilder n;
                if (!email.getText().isEmpty() && !first.getText().isEmpty()
                        && !last.getText().isEmpty() && !pass.getText().isEmpty()){
                    ObservableList<Worker> workers = database.getWorkers();
                    for (Worker w : workers) {
                        if (w.getEmail().equals(email.getText())) {
                            stageUtils.errorAlert("Worker is already in the database!");
                            break;
                        }
                    }
                    if (!email.getText().matches(EMAIL_REGEX)){
                        stageUtils.errorAlert("Email must be an MSOE email.");
                    } else if (first.getText().matches("\\s*")){
                        stageUtils.errorAlert("Worker's first name is invalid");
                    } else if (last.getText().matches("\\s*")){
                        stageUtils.errorAlert("Worker's last name is invalid");
                    } else if (pass.getText().length() < 8){
                        stageUtils.errorAlert("Password must be at least eight characters in length.");
                    } else if (!rfid.getText().matches(RFID_REGEX)){
                        stageUtils.errorAlert("RFID must be at least 4 digits long.");
                    } else {
                        String temp = first.getText().substring(0, 1).toUpperCase() + first.getText().substring(1);
                        n = new StringBuilder(temp);
                        temp = last.getText().substring(0, 1).toUpperCase() + last.getText().substring(1);
                        n.append(" ").append(temp);

                        database.initWorker(worker);
                        database.addWorker(new Worker(n.toString(), email.getText(), pass.getText(),
                                Integer.parseInt(rfid.getText())));
                        stage.close();
                    }
                } else {
                    stageUtils.errorAlert("All fields must be filled in.");
                }
            }
        };

        stage.getIcons().add(new Image("images/msoe.png"));
        stage.showAndWait();
        populateTable();
    }

    public void addAdmin() {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setTitle("Add a New Admin");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);

        new Popup(root) {
            private JFXTextField email, first, last, rfid;
            private JFXPasswordField pass, pin;

            @Override
            public void populate() {
                email = (JFXTextField) add("MSOE Email: ", "", true).getChildren().get(1);
                first = (JFXTextField) add("First Name: ", "", true).getChildren().get(1);
                last = (JFXTextField) add("Last Name: ", "", true).getChildren().get(1);
                pass = addPasswordField("Password: ");
                pin = addPasswordField("4-Digit Pin: ");
                rfid = (JFXTextField) add("RFID: ", "", true).getChildren().get(1);
                stageUtils.acceptIntegerOnly(rfid);
                stageUtils.acceptIntegerOnly(pin);
                stageUtils.setMaxTextLength(pin, 4);
            }

            @Override
            public void submit() {
                StringBuilder n;
                if (!email.getText().isEmpty() && !first.getText().isEmpty() &&
                        !last.getText().isEmpty() && !pass.getText().isEmpty()){
                    ObservableList<Worker> workers = database.getWorkers();
                    for (Worker w : workers) {
                        if (w.getEmail().equals(email.getText())) {
                            stageUtils.errorAlert("Admin is already in the database!");
                            break;
                        }
                    }
                    if (!email.getText().matches(EMAIL_REGEX)){
                        stageUtils.errorAlert("Email must be an MSOE email.");
                    } else if (first.getText().matches("\\s*")){
                        stageUtils.errorAlert("Admin's first name is invalid");
                    } else if (last.getText().matches("\\s*")){
                        stageUtils.errorAlert("Admin's last name is invalid");
                    } else if (pass.getText().length() < 8){
                        stageUtils.errorAlert("Password must be at least eight characters in length.");
                    } else if (!rfid.getText().matches(RFID_REGEX)){
                        stageUtils.errorAlert("RFID must be at least 4 digits long.");
                    } else if (!pin.getText().matches("[0-9]{4}")) {
                        stageUtils.errorAlert("Pin must be at exactly 4 digits");
                    } else {
                        String temp = first.getText().substring(0, 1).toUpperCase() + first.getText().substring(1);
                        n = new StringBuilder(temp);
                        temp = last.getText().substring(0, 1).toUpperCase() + last.getText().substring(1);
                        n.append(" ").append(temp);

                        ObservableList<Worker> w = database.getWorkers();
                        database.initWorker(worker);
                        database.addWorker(new Worker(n.toString(), w.get(w.size() - 1).getWorkerID()
                                + 1, email.getText(), pass.getText(), Integer.parseInt(pin.getText()),
                                Integer.parseInt(rfid.getText()), true, true, true, true));
                        stage.close();
                    }
                } else {
                    stageUtils.errorAlert("All fields must be filled in.");
                }
            }
        };

        stage.getIcons().add(new Image("images/msoe.png"));
        stage.showAndWait();
        populateTable();
    }

    public void deleteWorker() {
        int admins = database.getNumAdmins();
        if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
            int index = table.getSelectionModel().getFocusedIndex();
            String email = emailCol.getCellData(index);
            Worker w = database.getWorker(email);
            if (admins == 1 && w.isAdmin()) {
                stageUtils.errorAlert("Cannot delete the last admin.");
            } else if (worker.getWorkerID() == w.getWorkerID()) {
                stageUtils.errorAlert("Cannot delete your own account.");
            } else {
                if (stageUtils.confirmationAlert("Delete This Worker?",
                        "Are you ok with this?",
                        "Are you sure you want to delete this worker?")) {
                    database.deleteWorker(w.getName());
                }
            }
        }
        populateTable();
    }

    public class MWRow extends TableRow {

        private final StringProperty name;
        private final StringProperty email;
        private final BooleanProperty isAdmin;

        public MWRow(String name, String email, boolean isAdmin) {
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.isAdmin = new SimpleBooleanProperty(isAdmin);
        }

        public StringProperty getName() {
            return name;
        }

        public StringProperty getEmail() {
            return email;
        }

        public BooleanProperty getIsAdmin() {
            return isAdmin;
        }

        public void setIsAdmin(boolean value){
            isAdmin.set(value);
        }
    }
}
