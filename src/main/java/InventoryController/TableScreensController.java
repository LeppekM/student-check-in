package InventoryController;

import Database.Database;
import Database.ObjectClasses.Part;
import Database.ObjectClasses.Worker;
import HelperClasses.AdminPinRequestController;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class TableScreensController extends ControllerMenu implements IController, Initializable {

    @FXML
    public Label titleLabel;
    public JFXTabPane tabPane;

    @FXML
    private StackPane scene;

    @FXML
    TextField searchInput;

    private TSCTable tscTable;
    private TreeItem<TSCTable.TableRow> root;
    @FXML
    private JFXTreeTableView<TSCTable.TableRow> table;
    private final ArrayList<String> selectedFilters = new ArrayList<>();

    @FXML
    private Button backButton, excelButton, clearHistoryButton, addPartButton, editPartButton, editManyPartButton,
            deletePartButton, deleteManyPartButton, importStudentsButton, addStudentButton, deleteStudentButton,
            addEmployeeButton, addAdminButton, deleteEmployeeButton;

    protected static Database database = new Database();
    private StageUtils stageUtils = StageUtils.getInstance();
    private Worker worker;
    ExportToExcel export = new ExportToExcel();
    private String screen = "completeInventory";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // init tabPane listeners
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.getText().equals("Total Inventory")) {
                    screen = "completeInventory";
                } else if (newValue.getText().equals("Transaction History")) {
                    screen = "history";
                } else if (newValue.getText().equals("Checked Out")) {
                    screen = "checkedOut";
                } else {
                    screen = "overdue";
                }
            }
            showCorrectButtons();
        });
        tabPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            tabPane.setTabMinWidth(tabPane.getWidth() / 5.05);
            tabPane.setTabMaxWidth(tabPane.getWidth() / 5.05);
        });
        //  todo setup getters for what screen this is

        //updateTable();
        backButton.getStylesheets().add("/css/CheckButton.css");  // set button style
        //tscTable.initialize();  // set up table
        showCorrectButtons();  // show buttons

    }

    private void updateTable() {
        switch (screen) {
            case "completeInventory":
                tscTable = new CompleteInventoryTab(this);
                break;
            case "history":
                break;
        }  // todo: finish this once the other classes are set up
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
     * Asks the student worker to enter an admin pin if they try to do something they do
     * not have the privilege to do
     *
     * @param action the privileged action that the worker tried to do
     * @return true if the inputted admin pin is correct; false otherwise
     */
    public boolean requestAdminPin(String action, Window owner) {
        AtomicBoolean isValid = new AtomicBoolean(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminPinRequest.fxml"));
            Parent root = loader.load();
            ((AdminPinRequestController) loader.getController()).setAction(action);
            Scene scene = new Scene(root, 350, 250);
            Stage stage = new Stage();
            stage.setTitle("Admin Pin Required");
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.setResizable(false);
            stage.setOnCloseRequest(e -> {
                // checks to see whether the pin was submitted or the window was just closed
                if (((AdminPinRequestController) loader.getController()).isSubmitted()) {
                    // checks to see if the input pin is empty. if empty, close pop up
                    if (((AdminPinRequestController) loader.getController()).isNotEmpty()) {
                        // checks to see whether the submitted pin matches one of the admins' pins
                        if (((AdminPinRequestController) loader.getController()).isValid()) {
                            stage.close();
                            isValid.set(true);
                        } else {
                            stage.close();
                            invalidAdminPinAlert();
                            isValid.set(false);
                        }
                    } else {
                        stage.close();
                        isValid.set(false);
                    }
                }
            });
            stage.showAndWait();
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Loading Admin Pin Request.");
            e.printStackTrace();
        }
        return isValid.get();
    }


    /**
     * Alert that the pin entered does not match one of the admin pins.
     */
    private void invalidAdminPinAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("The pin entered is invalid.");
        StudentCheckIn.logger.error("The pin entered is invalid.");
        alert.showAndWait();
    }


    /**
     * Updates the table based on the input text for the search
     */
    @FXML
    private void search() {
        String filter = searchInput.getText();
        String[] filters = filter.split(",");
        if (filter.isEmpty()) {
            table.setRoot(root);
        } else {
            TreeItem<TSCTable.TableRow> filteredRoot = new TreeItem<>();
            selectedFilters.removeAll(selectedFilters.subList(0, selectedFilters.size()));
            for (String f : filters) {
                f = f.trim();
                if (f.equalsIgnoreCase("overdue") || f.equalsIgnoreCase("checked out")
                        || f.equalsIgnoreCase("all")) {
                    f = f.substring(0, 1).toUpperCase() + f.substring(1);
                    if (f.length() > 7) {
                        f = f.substring(0, 8) + f.substring(8, 9).toUpperCase() + f.substring(9);
                    }
                    selectedFilters.add(f);
                    tscTable.populateTable();
                } else {
                    tscTable.filter(root, f, filteredRoot);
                    table.setRoot(filteredRoot);
                }
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
            case "completeInventory":
                excelButton.setVisible(true);
                addPartButton.setVisible(true);
                editPartButton.setVisible(true);
                editManyPartButton.setVisible(true);
                deletePartButton.setVisible(true);
                deleteManyPartButton.setVisible(true);
                break;
            case "history":
                excelButton.setVisible(true);
                clearHistoryButton.setVisible(true);
                break;
            case "checkedOut":  // both cases only have export as Excel document button enabled
            case "overdue":
                excelButton.setVisible(true);
                break;
            case "employees":
                addEmployeeButton.setVisible(true);
                addAdminButton.setVisible(true);
                deleteEmployeeButton.setVisible(true);
                break;
            case "students":
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
    public void addPart(ActionEvent event) {

    }

    @FXML
    public void deleteManyParts(ActionEvent event) {

    }

    @FXML
    public void deletePart() {

    }

    @FXML
    public void editManyParts(ActionEvent event) {

    }

    @FXML
    public void editPart(ActionEvent event) {

    }

    @FXML
    public void clearHistory(ActionEvent event) {

    }

    @FXML
    public void importStudents(ActionEvent event) {

    }

    @FXML
    public void addStudent(ActionEvent event) {

    }

    @FXML
    public void deleteStudent(ActionEvent event) {

    }

    @FXML
    public void addEmployee(ActionEvent event) {

    }

    @FXML
    public void addAdmin(ActionEvent event) {

    }

    @FXML
    public void deleteEmployee(ActionEvent event) {

    }
}
