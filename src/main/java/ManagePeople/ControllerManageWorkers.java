package ManagePeople;

import Database.*;
import Database.ObjectClasses.Worker;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

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
    private AnchorPane manageWorkersScene;

    @FXML
    private JFXTreeTableView<ManageWorkersTabTableRow> manageWorkersTable;
    private TreeItem<ManageWorkersTabTableRow> root;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private Button addWorker, addAdmin, deleteWorker;

    @FXML
    private JFXTreeTableColumn<ManageWorkersTabTableRow, String> nameCol, emailCol;

    @FXML
    private JFXTreeTableColumn<ManageWorkersTabTableRow, Boolean> adminCol;

    private String name, email, admin;

    private static ObservableList<Worker> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.worker = null;

        Label emptyTableLabel = new Label("No workers found.");
        emptyTableLabel.setFont(new Font(18));
        manageWorkersTable.setPlaceholder(emptyTableLabel);

        nameCol = new JFXTreeTableColumn<>("Name");
        nameCol.setPrefWidth(800/3);
        nameCol.setResizable(false);
        nameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, String> param) {
                return param.getValue().getValue().getName();
            }
        });

        emailCol = new JFXTreeTableColumn<>("Email");
        emailCol.setPrefWidth(800/3);
        emailCol.setResizable(false);
        emailCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, String> param) {
                return param.getValue().getValue().getEmail();
            }
        });

        adminCol = new JFXTreeTableColumn<>("Admin");
        adminCol.setPrefWidth(800/3);
        adminCol.setResizable(false);
        adminCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, Boolean>,
                ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, Boolean> param) {
                TreeItem<ManageWorkersTabTableRow> treeItem = param.getValue();
                ManageWorkersTabTableRow tRow = treeItem.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(tRow.getIsAdmin().get());
                booleanProp.addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                        Boolean newValue) {
                        tRow.setIsAdmin(newValue);
                    }
                });
                return booleanProp;
            }
        });

        adminCol.setCellFactory(new Callback<TreeTableColumn<ManageWorkersTabTableRow, Boolean>, TreeTableCell<ManageWorkersTabTableRow, Boolean>>() {
            @Override
            public TreeTableCell<ManageWorkersTabTableRow, Boolean> call( TreeTableColumn<ManageWorkersTabTableRow, Boolean> p ) {
                CheckBoxTreeTableCell<ManageWorkersTabTableRow,Boolean> cell = new CheckBoxTreeTableCell<>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        tableRows = FXCollections.observableArrayList();
        searchInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                manageWorkersTable.setPredicate(new Predicate<TreeItem<ManageWorkersTabTableRow>>() {
                    @Override
                    public boolean test(TreeItem<ManageWorkersTabTableRow> tableRow) {
                        String input = newValue.toLowerCase();
                        name = tableRow.getValue().getName().getValue();
                        email = tableRow.getValue().getEmail().getValue();

                        return ((name != null && name.toLowerCase().contains(input))
                                || (email != null && email.toLowerCase().contains(input)));
                    }
                });
            }
        });

//        manageWorkersTable.setRowFactory(new Callback<TreeTableView<ManageWorkersTabTableRow>, TreeTableRow<ManageWorkersTabTableRow>>() {
//            @Override
//            public TreeTableRow<ManageWorkersTabTableRow> call(TreeTableView<ManageWorkersTabTableRow> param) {
//                final TreeTableRow<ManageWorkersTabTableRow> row = new TreeTableRow<>();
//                row.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent event) {
//                        final int index = row.getIndex();
//                        if (index >= 0 && index < manageWorkersTable.getCurrentItemsCount() && manageWorkersTable.getSelectionModel().isSelected(index)) {
//                            manageWorkersTable.getSelectionModel().clearSelection();
//                            event.consume();
//                        }
//                    }
//                });
//                return row;
//            }
//        });

        populateTable();
    }

    private void populateTable(){
        tableRows.clear();
        manageWorkersTable.getColumns().clear();
        data.clear();
        database = new Database();
        data = database.getWorkers();

        for (int i = 0; i < data.size(); i++){
            tableRows.add(new ManageWorkersTabTableRow(data.get(i).getName(), data.get(i).getEmail(),
                    data.get(i).isAdmin()));
        }

        root = new RecursiveTreeItem<>(
                tableRows, RecursiveTreeObject::getChildren
        );

        manageWorkersTable.getColumns().setAll(nameCol, emailCol, adminCol);
        manageWorkersTable.setRoot(root);
        manageWorkersTable.setShowRoot(false);

    }

    public void addWorker(ActionEvent actionEvent) {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/addWorker.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            Scene scene = new Scene(root, 300, 260);
            stage.setTitle("Add a New Worker");
            stage.initOwner(manageWorkersScene.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.showAndWait();
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load add worker page");
            alert.initStyle(StageStyle.UTILITY);
            StudentCheckIn.logger.error("IOException: Couldn't load add worker page.");
            alert.showAndWait();
            e.printStackTrace();
        }
        populateTable();
    }

    public void addAdmin(ActionEvent actionEvent) {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/addAdmin.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            Scene scene = new Scene(root, 300, 260);
            stage.setTitle("Add a New Worker");
            stage.initOwner(manageWorkersScene.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.showAndWait();
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load add admin page");
            alert.initStyle(StageStyle.UTILITY);
            StudentCheckIn.logger.error("IOException: Couldn't load add admin page.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * This will allow a user to delete a worker/admin until there is only one admin left
     *
     * @param actionEvent null
     */
    public void deleteWorker(ActionEvent actionEvent) {
        int admins = 0;
        boolean lastOne = false;
        boolean self = false;
        for (Worker w : data){
            if (w.isAdmin()){
                admins++;
            }
        }
        if (manageWorkersTable.getSelectionModel().getSelectedCells().size() != 0){
            int row = manageWorkersTable.getSelectionModel().getFocusedIndex();
            if (admins == 1 && data.get(row).isAdmin()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot delete the last admin.");
                StudentCheckIn.logger.error("Manage Workers: Unable to delete last admin.");
                alert.showAndWait();
                lastOne = true;
            }
            if (worker.getID() == data.get(row).getID()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot delete your own account.");
                StudentCheckIn.logger.error("Manage Workers: Cannot delete own account.");
                alert.showAndWait();
                self = true;
            }
            if (!lastOne && !self) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete this worker?");
                alert.setTitle("Delete This Worker?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK){
                    database.deleteWorker(data.get(row).getName());
                    data.remove(row);
                }
            }
        }
        populateTable();
    }

    public void goBack(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Menu.fxml"));
            Parent root = loader.load();
            IController controller = loader.<IController>getController();
            controller.initWorker(worker);
            manageWorkersScene.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
            manageWorkersScene.getChildren().clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            StudentCheckIn.logger.error("IOException: No valid stage was found to load");
            alert.showAndWait();
        }
    }

    public void edit(MouseEvent event) {
        if (event.getClickCount() == 2){
            Stage stage = new Stage();
            int f = manageWorkersTable.getSelectionModel().getSelectedIndex();
            ManageWorkersTabTableRow r = manageWorkersTable.getSelectionModel().getModelItem(f).getValue();
            Worker w = database.getWorker(r.getEmail().get());
            if (w.isAdmin()){
                try {
                    URL myFxmlURL = ClassLoader.getSystemResource("fxml/EditAdmin.fxml");
                    FXMLLoader loader = new FXMLLoader(myFxmlURL);
                    Parent root = loader.load();
                    EditAdmin ea = loader.getController();
                    ea.setAdmin(w);
                    Scene scene = new Scene(root, 790, 500);
                    stage.setTitle("Edit " + w.getName());
                    stage.initOwner(manageWorkersScene.getScene().getWindow());
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setScene(scene);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.setOnCloseRequest(event1 -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close?");
                        alert.setTitle("Confirm Close");
                        alert.setHeaderText("If you leave now, unsaved changes could be lost.");
                        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                        alert.showAndWait().ifPresent(buttonType -> {
                            if (buttonType == ButtonType.YES){
                                stage.close();
                            }else if (buttonType == ButtonType.NO){
                                event1.consume();
                            }
                        });
                    });
                    stage.show();
                    populateTable();
                }catch (IOException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load admin info page");
                    alert.initStyle(StageStyle.UTILITY);
                    StudentCheckIn.logger.error("IOException: Couldn't load admin info page.");
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }else {
                try {
                    URL myFxmlURL = ClassLoader.getSystemResource("fxml/EditWorker.fxml");
                    FXMLLoader loader = new FXMLLoader(myFxmlURL);
                    Parent root = loader.load();
                    EditWorker ew = loader.getController();
                    ew.setWorker(w);
                    Scene scene = new Scene(root, 790, 620);
                    stage.setTitle("Edit " + w.getName());
                    stage.initOwner(manageWorkersScene.getScene().getWindow());
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setScene(scene);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.setOnCloseRequest(event1 -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close?");
                        alert.setTitle("Confirm Close");
                        alert.setHeaderText("If you leave now, unsaved changes could be lost.");
                        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                        alert.showAndWait().ifPresent(buttonType -> {
                            if (buttonType == ButtonType.YES){
                                stage.close();
                            }else if (buttonType == ButtonType.NO){
                                event1.consume();
                            }
                        });
                    });
                    stage.show();
                    populateTable();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load worker info page");
                    alert.initStyle(StageStyle.UTILITY);
                    StudentCheckIn.logger.error("IOException: Couldn't load worker info page.");
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }
}
