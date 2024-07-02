package Tables;

import App.StudentCheckIn;
import Controllers.IController;
import Database.ObjectClasses.Worker;
import HelperClasses.ExportToExcel;
import Controllers.TableScreensController;
import Popups.EditAdminController;
import Popups.EditWorkerController;
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
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class ManageWorkersTable extends TSCTable {

    private JFXTreeTableColumn<MERow, String> nameCol, emailCol;
    private JFXTreeTableColumn<MERow, Boolean> adminCol;

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
            TreeItem<MERow> treeItem = param.getValue();
            MERow tRow = treeItem.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(tRow.getIsAdmin().get());
            booleanProp.addListener((observable, oldValue, newValue) -> tRow.setIsAdmin(newValue));
            return booleanProp;
        });
        adminCol.setCellFactory(p -> {
            CheckBoxTreeTableCell<MERow, Boolean> cell = new CheckBoxTreeTableCell<>();
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
            rows.add(new MERow(datum.getName(), datum.getEmail(), datum.isAdmin()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> nameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) nameCol;
        TreeTableColumn<TableRow, String> emailTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) emailCol;
        TreeTableColumn<TableRow, String> adminTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) adminCol;

        table.getColumns().setAll(nameTemp, emailTemp, adminTemp);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    @Override
    protected boolean isMatch(TableRow value, String filter) {
        MERow val = (MERow) value;
        String input = filter.toLowerCase();
        String name = val.getName().getValue();
        String email = val.getEmail().getValue();

        return ((name != null && name.toLowerCase().contains(input))
                || (email != null && email.toLowerCase().contains(input)));
    }

    @Override
    protected void popupRow(int index) {
        Stage stage = new Stage();
        MERow r = (MERow) table.getSelectionModel().getModelItem(index).getValue();
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load admin info page");
            alert.initStyle(StageStyle.UTILITY);
            StudentCheckIn.logger.error("IOException: Couldn't load worker info page.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public Worker getSelectedWorker() {
        int index = table.getSelectionModel().getFocusedIndex();
        String email = emailCol.getCellData(index);
        return database.getWorker(email);
    }

    public void addWorker() {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/addWorker.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            IController controller = loader.getController();
            controller.initWorker(worker);
            Scene scene = new Scene(root, 350, 370);
            stage.setTitle("Add a New Worker");
            stage.initOwner(this.controller.getScene().getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load add worker page");
            alert.initStyle(StageStyle.UTILITY);
            StudentCheckIn.logger.error("IOException: Couldn't load add worker page.");
            alert.showAndWait();
            e.printStackTrace();
        }
        populateTable();
    }

    public void addAdmin() {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/addAdmin.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            IController controller = loader.getController();
            controller.initWorker(worker);
            Scene scene = new Scene(root, 350, 420);
            stage.setTitle("Add a New Worker");
            stage.initOwner(this.controller.getScene().getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.showAndWait();
        } catch (IOException e) {
            stageUtils.errorAlert("Couldn't load add admin page");
            e.printStackTrace();
        }
        populateTable();
    }

    public void deleteWorker() {
        int admins = database.getNumAdmins();
        if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
            Worker w = getSelectedWorker();
            if (admins == 1 && w.isAdmin()) {
                stageUtils.errorAlert("Cannot delete the last admin.");
            } else if (worker.getID() == w.getID()) {
                stageUtils.errorAlert("Cannot delete your own account.");
            } else  {
                if (stageUtils.confirmationAlert("Delete This Worker?",
                        "Are you sure you want to delete this worker?")) {
                    database.deleteWorker(w.getName());
                }
            }
        }
        populateTable();
    }

    public class MERow extends TableRow {

        private final StringProperty name;
        private final StringProperty email;
        private BooleanProperty isAdmin;

        public MERow(String name, String email, boolean isAdmin) {
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
            isAdmin = new SimpleBooleanProperty(value);
        }
    }
}
