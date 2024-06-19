package ManagePeople;

import Database.Database;
import Database.ObjectClasses.Worker;
import HelperClasses.StageUtils;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ControllerManageWorkers implements IController, Initializable {
    private ObservableList<ManageWorkersTabTableRow> tableRows;

    private Database database;

    private Worker worker;

    @FXML
    private VBox manageWorkersScene;

    @FXML
    private JFXTreeTableView<ManageWorkersTabTableRow> manageWorkersTable;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private JFXTreeTableColumn<ManageWorkersTabTableRow, String> nameCol, emailCol;

    @FXML
    private JFXTreeTableColumn<ManageWorkersTabTableRow, Boolean> adminCol;

    private String name, email;

    private StageUtils stageUtils = StageUtils.getInstance();

    private static ObservableList<Worker> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.worker = null;

        Label emptyTableLabel = new Label("No workers found.");
        emptyTableLabel.setFont(new Font(18));
        manageWorkersTable.setPlaceholder(emptyTableLabel);

        nameCol = new JFXTreeTableColumn<>("Name");
        nameCol.prefWidthProperty().bind(manageWorkersTable.widthProperty().divide(3));
        nameCol.setStyle("-fx-font-size: 18px");
        nameCol.setResizable(false);
        nameCol.setCellValueFactory(param -> param.getValue().getValue().getName());

        emailCol = new JFXTreeTableColumn<>("Email");
        emailCol.prefWidthProperty().bind(manageWorkersTable.widthProperty().divide(3));
        emailCol.setStyle("-fx-font-size: 18px");
        emailCol.setResizable(false);
        emailCol.setCellValueFactory(param -> param.getValue().getValue().getEmail());

        adminCol = new JFXTreeTableColumn<>("Admin");
        adminCol.prefWidthProperty().bind(manageWorkersTable.widthProperty().divide(3));
        adminCol.setResizable(false);
        adminCol.setCellValueFactory(param -> {
            TreeItem<ManageWorkersTabTableRow> treeItem = param.getValue();
            ManageWorkersTabTableRow tRow = treeItem.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(tRow.getIsAdmin().get());
            booleanProp.addListener((observable, oldValue, newValue) -> tRow.setIsAdmin(newValue));
            return booleanProp;
        });

        adminCol.setCellFactory(p -> {
            CheckBoxTreeTableCell<ManageWorkersTabTableRow, Boolean> cell = new CheckBoxTreeTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        tableRows = FXCollections.observableArrayList();
        searchInput.textProperty().addListener((observable, oldValue, newValue) -> manageWorkersTable.setPredicate(new Predicate<TreeItem<ManageWorkersTabTableRow>>() {
            @Override
            public boolean test(TreeItem<ManageWorkersTabTableRow> tableRow) {
                String input = newValue.toLowerCase();
                name = tableRow.getValue().getName().getValue();
                email = tableRow.getValue().getEmail().getValue();

                return ((name != null && name.toLowerCase().contains(input))
                        || (email != null && email.toLowerCase().contains(input)));
            }
        }));

        manageWorkersTable.setRowFactory(param -> {
            final TreeTableRow<ManageWorkersTabTableRow> row = new TreeTableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getClickCount() == 2) {
                    edit(row.getIndex());
                } else {
                    final int index = row.getIndex();
                    if (index >= 0 && index < manageWorkersTable.getCurrentItemsCount() && manageWorkersTable.getSelectionModel().isSelected(index)) {
                        manageWorkersTable.getSelectionModel().clearSelection();
                        event.consume();
                    }
                }
            });
            return row;
        });
        populateTable();
    }

    private void populateTable() {
        tableRows.clear();
        manageWorkersTable.getColumns().clear();
        data.clear();
        database = new Database();
        data = database.getWorkers();

        for (Worker datum : data) {
            tableRows.add(new ManageWorkersTabTableRow(datum.getName(), datum.getEmail(),
                    datum.isAdmin()));
        }

        TreeItem<ManageWorkersTabTableRow> root = new RecursiveTreeItem<>(
                tableRows, RecursiveTreeObject::getChildren
        );

        manageWorkersTable.getColumns().setAll(nameCol, emailCol, adminCol);
        manageWorkersTable.setRoot(root);
        manageWorkersTable.setShowRoot(false);

    }

    @FXML
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
            stage.initOwner(manageWorkersScene.getScene().getWindow());
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

    @FXML
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
            stage.initOwner(manageWorkersScene.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load add admin page");
            alert.initStyle(StageStyle.UTILITY);
            StudentCheckIn.logger.error("IOException: Couldn't load add admin page.");
            alert.showAndWait();
            e.printStackTrace();
        }
        populateTable();
    }

    /**
     * This will allow a user to delete a worker/admin until there is only one admin left
     */
    @FXML
    public void deleteWorker() {
        int admins = 0;
        boolean lastOne = false;
        boolean self = false;
        for (Worker w : data) {
            if (w.isAdmin()) {
                admins++;
            }
        }
        if (!manageWorkersTable.getSelectionModel().getSelectedCells().isEmpty()) {
            int row = manageWorkersTable.getSelectionModel().getFocusedIndex();
            if (admins == 1 && data.get(row).isAdmin()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot delete the last admin.");
                StudentCheckIn.logger.error("Manage Workers: Unable to delete last admin.");
                alert.showAndWait();
                lastOne = true;
            }
            if (worker.getID() == data.get(row).getID()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot delete your own account.");
                StudentCheckIn.logger.error("Manage Workers: Cannot delete own account.");
                alert.showAndWait();
                self = true;
            }
            if (!lastOne && !self) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete this worker?");
                alert.setTitle("Delete This Worker?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    database.deleteWorker(data.get(row).getName());
                    data.remove(row);
                }
            }
        }
        populateTable();
    }

    @FXML
    public void goBack() {
        StageUtils.getInstance().goBack(manageWorkersScene, worker);
    }

    public void edit(int row) {
        Stage stage = new Stage();
        ManageWorkersTabTableRow r = manageWorkersTable.getSelectionModel().getModelItem(row).getValue();
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
                EditAdmin ea = loader.getController();
                ea.setAdmin(w);
                ea.initWorker(worker);
                if (ea.changed()) {
                    stageUtils.unsavedChangesAlert(stage);
                }
            } else {
                EditWorker ew = loader.getController();
                ew.setWorker(w);
                ew.initWorker(worker);
                if (ew.changed()) {
                    stageUtils.unsavedChangesAlert(stage);
                }
            }
            Scene scene = new Scene(root, 790, 600);
            stage.setTitle("Edit " + w.getName());
            stage.initOwner(manageWorkersScene.getScene().getWindow());
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

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     *
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }
}
