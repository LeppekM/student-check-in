package InventoryController;

import Database.Database;
import Database.ObjectClasses.Part;
import Database.ObjectClasses.Worker;
import HelperClasses.ExportToExcel;
import HelperClasses.StageUtils;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class TableScreensController extends ControllerMenu implements IController, Initializable {

    @FXML
    public Label titleLabel;

    @FXML
    public JFXTabPane tabPane;

    @FXML
    private StackPane scene;

    @FXML
    TextField searchInput;

    @FXML
    public JFXTreeTableView<TSCTable.TableRow> table;

    @FXML
    private Button backButton, excelButton, clearHistoryButton, addPartButton, editPartButton, editManyPartButton,
            deletePartButton, deleteManyPartButton, importStudentsButton, addStudentButton, deleteStudentButton,
            addEmployeeButton, addAdminButton, deleteEmployeeButton;

    protected static Database database = Database.getInstance();
    private final StageUtils stageUtils = StageUtils.getInstance();
    private final ExportToExcel export = new ExportToExcel();
    private Worker worker;
    private TSCTable tscTable;
    private TableScreen screen = TableScreen.COMPLETE_INVENTORY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // init tabPane listeners
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.getText().equals("Total Inventory")) {
                    screen = TableScreen.COMPLETE_INVENTORY;
                } else if (newValue.getText().equals("Transaction History")) {
                    screen = TableScreen.HISTORY;
                } else if (newValue.getText().equals("Checked Out")) {
                    screen = TableScreen.CHECKED_OUT;
                } else {
                    screen = TableScreen.OVERDUE;
                }
            }
            showCorrectButtons();
            updateTable();
        });
        tabPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            tabPane.setTabMinWidth(tabPane.getWidth() / 4.5);
            tabPane.setTabMaxWidth(tabPane.getWidth() / 4.5);
        });
        //  todo setup getters for what screen this is

        updateTable();
        backButton.getStylesheets().add("/css/CheckButton.css");  // set button style
        showCorrectButtons();  // show buttons

        // Updates the search if the user presses enter with the cursor in the search field
        searchInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search();
            }
        });
    }

    private void updateTable() {
        switch (screen) {
            case COMPLETE_INVENTORY:
                tscTable = new CompleteInventoryTable(this);
                break;
            case HISTORY:
                tscTable = new HistoryInventoryTable(this);
                break;
            case CHECKED_OUT:
                tscTable = new CheckedOutInventoryTable(this);
                break;
            case OVERDUE:
                break;
            case STUDENTS:
                break;
            case EMPLOYEES:
                break;
        }  // todo: finish this once the other classes are set up
        tscTable.initialize();
        tscTable.populateTable();
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }

    /**
     * Updates the table based on the input text for the search
     */
    @FXML
    private void search() {
        String filter = searchInput.getText();
        String[] filters = filter.split(" ");
        if (!filter.isEmpty()) {
            TreeItem<TSCTable.TableRow> filteredRoot = new TreeItem<>();
            for (String f : filters) {
                f = f.trim();
                tscTable.filter(f, filteredRoot);
                table.setRoot(filteredRoot);
            }
        }
    }

    /**
     * This method shows/hides the buttons on the bottom bar depending on what screen the user is on.
     * The back button is always shown so its value is unchanged
     */
    private void showCorrectButtons(){
        excelButton.setVisible(false);
        addPartButton.setVisible(false);
        editPartButton.setVisible(false);
        editManyPartButton.setVisible(false);
        deletePartButton.setVisible(false);
        deleteManyPartButton.setVisible(false);
        clearHistoryButton.setVisible(false);
        addEmployeeButton.setVisible(false);
        addAdminButton.setVisible(false);
        deleteEmployeeButton.setVisible(false);
        importStudentsButton.setVisible(false);
        addStudentButton.setVisible(false);
        deleteStudentButton.setVisible(false);
        switch (screen) {
            case COMPLETE_INVENTORY:
                excelButton.setVisible(true);
                addPartButton.setVisible(true);
                editPartButton.setVisible(true);
                editManyPartButton.setVisible(true);
                deletePartButton.setVisible(true);
                deleteManyPartButton.setVisible(true);
                break;
            case HISTORY:
                excelButton.setVisible(true);
                clearHistoryButton.setVisible(true);
                break;
            case CHECKED_OUT:  // both cases only have export as Excel document button enabled
            case OVERDUE:
                excelButton.setVisible(true);
                break;
            case EMPLOYEES:
                addEmployeeButton.setVisible(true);
                addAdminButton.setVisible(true);
                deleteEmployeeButton.setVisible(true);
                break;
            case STUDENTS:
                importStudentsButton.setVisible(true);
                addStudentButton.setVisible(true);
                deleteStudentButton.setVisible(true);
                break;

        }
    }

    @FXML
    public void goBack() {
        stageUtils.goBack(scene, worker);
    }

    /**
     * This exports the current table of the inventory as an Excel file
     */
    @FXML
    public void export() {
        tscTable.export(export);
    }

    @FXML
    public void addPart(ActionEvent e) {
        if (screen == TableScreen.COMPLETE_INVENTORY) {
            Stage stage = StageUtils.getInstance().createPopupStage("fxml/AddPart.fxml", scene, "Add a Part");
            stage.setOnCloseRequest(event -> {
                tscTable.populateTable();
                stage.close();
            });
            stage.show();
        } else {
            impossibleOperation("add part");
        }
    }

    @FXML
    public void deleteManyParts(ActionEvent event) {
        if (screen == TableScreen.COMPLETE_INVENTORY) {
            if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
                int row = table.getSelectionModel().getFocusedIndex();
                CompleteInventoryTable temp = (CompleteInventoryTable) tscTable;
                int partID = temp.getRowPartID(row);
                Part part = database.selectPart(partID);

                if ((worker != null && (worker.canRemoveParts() || worker.isAdmin())) || stageUtils.requestAdminPin("delete parts", scene)) {
                    boolean typeHasOneCheckedOut = false;
                    ArrayList<String> partIDs = database.getAllPartIDsForPartName("" + part.getPartID());
                    for (String id : partIDs) {
                        if (database.getIsCheckedOut(id)) {
                            typeHasOneCheckedOut = true;
                        }
                    }
                    String partName = part.getPartName();
                    if (!typeHasOneCheckedOut) {
                        database.initWorker(worker);
                        try {
                            if (database.hasPartName(partName)) {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete all parts named: " + partName + "?", ButtonType.YES, ButtonType.NO);
                                alert.showAndWait();
                                if (alert.getResult() == ButtonType.YES) {
                                    database.deleteParts(partName);
                                    tscTable.populateTable();
                                }
                            }
                        } catch (Exception e) {
                            StudentCheckIn.logger.error("Exception while deleting part type.");
                            e.printStackTrace();
                        }
                    } else {
                        stageUtils.errorAlert("At least one " + partName + " is currently checked out, so "
                                + partName + " parts cannot be deleted.");
                    }
                }
            }
        } else {
            impossibleOperation("delete many parts");
        }
    }

    @FXML
    public void deletePart() {
        if (screen == TableScreen.COMPLETE_INVENTORY) {
            if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
                int row = table.getSelectionModel().getFocusedIndex();
                CompleteInventoryTable temp = (CompleteInventoryTable) tscTable;
                int partID = temp.getRowPartID(row);
                Part part = database.selectPart(partID);

                if ((worker != null && (worker.canRemoveParts() || worker.isAdmin())) || stageUtils.requestAdminPin("Delete a Part", scene) ) {
                    if (!part.getCheckedOut()) {
                        database.initWorker(worker);
                        try {
                            if (database.selectPart(partID) != null) {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete the part with ID = " + partID + "?", ButtonType.YES, ButtonType.NO);
                                alert.showAndWait();
                                if (alert.getResult() == ButtonType.YES) {
                                    database.deleteItem(partID);
                                    tscTable.populateTable();
                                }
                            }
                        } catch (Exception e) {
                            StudentCheckIn.logger.error("Exception while deleting part.");
                            e.printStackTrace();
                        }
                    } else {
                        deleteCheckedOutPartAlert();
                    }
                }
            }
        } else {
            impossibleOperation("delete part");
        }
    }

    /**
     * Alert that the part is currently checked out, so it cannot be deleted
     */
    private void deleteCheckedOutPartAlert() {
        stageUtils.errorAlert("This part is currently checked out and cannot be deleted.");
    }

    @FXML
    public void editManyParts(ActionEvent event) {
        if (screen == TableScreen.COMPLETE_INVENTORY) {
            if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
                int row = table.getSelectionModel().getFocusedIndex();
                CompleteInventoryTable temp = (CompleteInventoryTable) tscTable;
                int partID = temp.getRowPartID(row);
                Part part = database.selectPart(partID);

                if ((worker != null && (worker.canEditParts() || worker.isAdmin()))
                        || StageUtils.getInstance().requestAdminPin("edit all parts named " + part.getPartName(), scene)) {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditPartType.fxml"));
                    try {
                        Parent root = loader.load();
                        ((ControllerEditPart) loader.getController()).initPart(part);
                        Scene scene = new Scene(root, 400, 500);
                        Stage stage = new Stage();
                        stage.setMinWidth(400);
                        stage.setMaxWidth(400);
                        stage.setMaxHeight(550);
                        stage.setMinHeight(550);
                        stage.setTitle("Edit all " + part.getPartName());
                        stage.initOwner(this.scene.getScene().getWindow());
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.setScene(scene);
                        stage.getIcons().add(new Image("images/msoe.png"));
                        stage.setOnCloseRequest(ev -> {
                            tscTable.populateTable();
                            stage.close();
                        });
                        stage.show();
                    } catch (IOException e) {
                        StudentCheckIn.logger.error("IOException: Loading Edit Part.");
                        e.printStackTrace();
                    }
                }
            }
        } else {
            impossibleOperation("edit many parts");
        }
    }

    @FXML
    public void editPart(ActionEvent event) {
        if (screen == TableScreen.COMPLETE_INVENTORY) {
            if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
                if ((worker != null && (worker.canEditParts() || worker.isAdmin()))
                        || StageUtils.getInstance().requestAdminPin("edit a part", scene)) {

                    int row = table.getSelectionModel().getFocusedIndex();
                    CompleteInventoryTable temp = (CompleteInventoryTable) tscTable;
                    int partID = temp.getRowPartID(row);
                    Part part = database.selectPart(partID);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditOnePart.fxml"));

                    try {
                        Parent root = loader.load();
                        ((ControllerEditPart) loader.getController()).initPart(part);
                        Scene scene = new Scene(root, 400, 500);
                        Stage stage = new Stage();
                        stage.setMinWidth(400);
                        stage.setMaxWidth(400);
                        stage.setMaxHeight(550);
                        stage.setMinHeight(550);
                        String partName = part.getPartName();
                        if (part.getPartName().endsWith("s")) {
                            partName = part.getPartName().substring(0, part.getPartName().length() - 1);
                        }
                        stage.setTitle("Edit a " + partName);
                        stage.initOwner(this.scene.getScene().getWindow());
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.setScene(scene);
                        stage.getIcons().add(new Image("images/msoe.png"));
                        stage.setOnCloseRequest(ev -> {
                            tscTable.populateTable();
                            stage.close();
                        });
                        stage.show();
                    } catch (IOException e) {
                        StudentCheckIn.logger.error("IOException: Loading Edit Part.");
                        e.printStackTrace();
                    }
                }
            }
        } else {
            impossibleOperation("edit part");
        }
    }

    @FXML
    public void clearHistory(ActionEvent event) {
        if (screen == TableScreen.HISTORY) {
            if (this.worker != null && this.worker.isAdmin()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Delete Old History");
                alert.setContentText("Are you sure you want to clear the transaction history for parts older than 2 years?");
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    database.clearOldHistory();
                    tscTable.populateTable();
                }
            }

        } else {
            impossibleOperation("clear history");
        }
    }

    @FXML
    public void importStudents(ActionEvent event) {
        if (screen == TableScreen.STUDENTS) {

        } else {
            impossibleOperation("import students");
        }
    }

    @FXML
    public void addStudent(ActionEvent event) {
        if (screen == TableScreen.STUDENTS) {

        } else {
            impossibleOperation("add student");
        }
    }

    @FXML
    public void deleteStudent(ActionEvent event) {
        if (screen == TableScreen.STUDENTS) {

        } else {
            impossibleOperation("delete student");
        }
    }

    @FXML
    public void addEmployee(ActionEvent event) {
        if (screen == TableScreen.EMPLOYEES) {

        } else {
            impossibleOperation("add employee");
        }
    }

    @FXML
    public void addAdmin(ActionEvent event) {
        if (screen == TableScreen.EMPLOYEES) {

        } else {
            impossibleOperation("add admin");
        }
    }

    @FXML
    public void deleteEmployee(ActionEvent event) {
        if (screen == TableScreen.EMPLOYEES) {

        } else {
            impossibleOperation("delete employee");
        }
    }

    private void impossibleOperation(String operation) {
        stageUtils.errorAlert("It should be impossible to access " + operation + " from " + screen);
    }
}
