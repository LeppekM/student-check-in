package InventoryController;

import Database.Database;
import Database.ObjectClasses.Part;
import Database.ObjectClasses.Student;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    private TableScreen screen;

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
            reloadScreen();
        });
        tabPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            tabPane.setTabMinWidth(tabPane.getWidth() / 4.5);
            tabPane.setTabMaxWidth(tabPane.getWidth() / 4.5);
        });

        backButton.getStylesheets().add("/css/CheckButton.css");  // set button style

        // Updates the search if the user presses enter with the cursor in the search field
        searchInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search();
            }
        });
    }

    private void reloadScreen() {
        if (screen == TableScreen.STUDENTS || screen == TableScreen.EMPLOYEES) {
            tabPane.setVisible(false);
            tabPane.setMaxHeight(0);
        } else {
            tabPane.setVisible(true);
            tabPane.setMaxHeight(42);
        }

        setDisplay(excelButton, false);
        setDisplay(addPartButton, false);
        setDisplay(editPartButton, false);
        setDisplay(editManyPartButton, false);
        setDisplay(deletePartButton, false);
        setDisplay(deleteManyPartButton, false);
        setDisplay(clearHistoryButton, false);
        setDisplay(addEmployeeButton, false);
        setDisplay(addAdminButton, false);
        setDisplay(deleteEmployeeButton, false);
        setDisplay(importStudentsButton, false);
        setDisplay(addStudentButton, false);
        setDisplay(deleteStudentButton, false);
        switch (screen) {
            case COMPLETE_INVENTORY:
                tscTable = new CompleteInventoryTable(this);
                titleLabel.setText("Total Inventory");
                setDisplay(excelButton, true);
                setDisplay(addPartButton, true);
                setDisplay(editPartButton, true);
                setDisplay(editManyPartButton, true);
                setDisplay(deletePartButton, true);
                setDisplay(deleteManyPartButton, true);
                break;
            case HISTORY:
                tscTable = new HistoryInventoryTable(this);
                titleLabel.setText("Transaction History");
                setDisplay(excelButton, true);
                setDisplay(clearHistoryButton, true);
                break;
            case CHECKED_OUT:
                tscTable = new CheckedOutInventoryTable(this);
                titleLabel.setText("Checked Out");
                setDisplay(excelButton, true);
                break;
            case OVERDUE:
                tscTable = new OverdueInventoryTable(this);
                titleLabel.setText("Overdue Parts");
                setDisplay(excelButton, true);
                break;
            case EMPLOYEES:
                tscTable = new ManageEmployeesTable(this);
                titleLabel.setText("Manage Employees");
                setDisplay(addEmployeeButton, true);
                setDisplay(addAdminButton, true);
                setDisplay(deleteEmployeeButton, true);
                break;
            case STUDENTS:
                tscTable = new ManageStudentsTable(this);
                titleLabel.setText("Manage Students");
                setDisplay(importStudentsButton, true);
                setDisplay(addStudentButton, true);
                setDisplay(deleteStudentButton, true);
                break;
        }
        tscTable.initialize();
        tscTable.populateTable();
    }

    private void setDisplay(Button button, boolean bool) {
        if (bool) {
            button.setVisible(true);
            button.setMinWidth(200);
        } else {
            button.setVisible(false);
            button.setMaxWidth(0);
        }
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
                                    database.deletePart(partID);
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
            database.initWorker(worker);
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Import Students");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));

                File file = fileChooser.showOpenDialog(table.getScene().getWindow());
                FileInputStream fis = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(fis);
                XSSFSheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIt = sheet.iterator();
                // skip the first row, which just has column labels
                if (rowIt.hasNext()) {
                    rowIt.next();
                }

                List<Student> failedImports = new ArrayList<>();
                // parse the rest of the rows
                while (rowIt.hasNext()) {
                    Row row = rowIt.next();
                    if (row.getCell(0) != null && row.getCell(3) != null) {
                        String email = row.getCell(3).toString();
                        String name = row.getCell(0).toString();
                        try {
                            String lastName = name.substring(0, name.indexOf(", "));
                            String restOfName = name.substring(name.indexOf(", ") + 2);
                            String firstName;
                            if (restOfName.contains(" ")) {
                                firstName = restOfName.substring(0, restOfName.indexOf(" "));
                            } else {
                                firstName = restOfName;
                            }
                            if (restOfName.contains(", ")) {
                                lastName += restOfName.substring(restOfName.indexOf(", ") + 1);
                            }
                            if (!email.matches("^\\w+[+.\\w'-]*@msoe\\.edu$")) {
                                failedImports.add(new Student(firstName + " " + lastName, email));
                            } else {
                                if (!database.getStudentEmails().contains(email)) {
                                    if (!database.importStudent(new Student((firstName + " " + lastName), email))) {
                                        failedImports.add(new Student(firstName + " " + lastName, email));
                                    }
                                }
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            failedImports.add(new Student(name, email));
                        }
                    } else {
                        stageUtils.errorAlert("The name must be in the first row and the email must be in the fourth row of the imported excel file.");
                    }

                }
                tscTable.populateTable();

                if (!failedImports.isEmpty()) {
                    List<String> lines = new ArrayList<>();
                    for (Student student : failedImports) {
                        lines.add(student.getName());
                    }
                    Path filePath = Paths.get("failed_students_import.txt");
                    Files.write(filePath, lines);
                    stageUtils.errorAlert("The program failed to import the students listed in the text file: \"" + filePath.getFileName() + "\"");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            impossibleOperation("import students");
        }
    }

    @FXML
    public void addStudent(ActionEvent event) {
        if (screen == TableScreen.STUDENTS) {
            Stage stage = new Stage();
            try {
                URL myFxmlURL = ClassLoader.getSystemResource("fxml/addStudent.fxml");
                FXMLLoader loader = new FXMLLoader(myFxmlURL);
                Parent root = loader.load();
                IController controller = loader.getController();
                controller.initWorker(worker);
                Scene scene = new Scene(root);
                stage.setTitle("Add a New Student");
                stage.initOwner(this.scene.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setScene(scene);
                stage.getIcons().add(new Image("images/msoe.png"));
                stage.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load add student page");
                alert.initStyle(StageStyle.UTILITY);
                StudentCheckIn.logger.error("IOException: Couldn't load add student page.");
                alert.showAndWait();
                e.printStackTrace();
            }
            tscTable.populateTable();
        } else {
            impossibleOperation("add student");
        }
    }

    @FXML
    public void deleteStudent(ActionEvent event) {
        if (screen == TableScreen.STUDENTS) {
            if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
                if ((worker != null && worker.isAdmin())
                        || StageUtils.getInstance().requestAdminPin("delete a student", scene)) {

                    int index = table.getSelectionModel().getFocusedIndex();
                    ManageStudentsTable temp = (ManageStudentsTable) tscTable;
                    String email = temp.getEmail(index);
                    if (stageUtils.confirmationAlert("Delete Student", "Delete this Student?")) {
                        database.deleteStudent(email);
                        tscTable.populateTable();
                    }
                }
            }
        } else {
            impossibleOperation("delete student");
        }
    }

    @FXML
    public void addEmployee(ActionEvent event) {
        if (screen == TableScreen.EMPLOYEES) {
            Stage stage = new Stage();
            try {
                URL myFxmlURL = ClassLoader.getSystemResource("fxml/addWorker.fxml");
                FXMLLoader loader = new FXMLLoader(myFxmlURL);
                Parent root = loader.load();
                IController controller = loader.getController();
                controller.initWorker(worker);
                Scene scene = new Scene(root, 350, 370);
                stage.setTitle("Add a New Worker");
                stage.initOwner(this.scene.getScene().getWindow());
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
            tscTable.populateTable();
        } else {
            impossibleOperation("add employee");
        }
    }

    @FXML
    public void addAdmin(ActionEvent event) {
        if (screen == TableScreen.EMPLOYEES) {
            Stage stage = new Stage();
            try {
                URL myFxmlURL = ClassLoader.getSystemResource("fxml/addAdmin.fxml");
                FXMLLoader loader = new FXMLLoader(myFxmlURL);
                Parent root = loader.load();
                IController controller = loader.getController();
                controller.initWorker(worker);
                Scene scene = new Scene(root, 350, 420);
                stage.setTitle("Add a New Worker");
                stage.initOwner(this.scene.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setScene(scene);
                stage.getIcons().add(new Image("images/msoe.png"));
                stage.showAndWait();
            } catch (IOException e) {
                stageUtils.errorAlert("Couldn't load add admin page");
                e.printStackTrace();
            }
            tscTable.populateTable();
        } else {
            impossibleOperation("add admin");
        }
    }

    @FXML
    public void deleteEmployee(ActionEvent event) {
        if (screen == TableScreen.EMPLOYEES) {
            int admins = database.getNumAdmins();
            if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
                ManageEmployeesTable temp = (ManageEmployeesTable) tscTable;
                Worker w = temp.getSelectedWorker();
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
            tscTable.populateTable();
        } else {
            impossibleOperation("delete employee");
        }
    }

    public StackPane getScene() {
        return scene;
    }

    public void setScreen(TableScreen tableScreen) {
        screen = tableScreen;
        reloadScreen();
    }

    private void impossibleOperation(String operation) {
        stageUtils.errorAlert("It should be impossible to access " + operation + " from " + screen);
    }
}
