package ManagePeople;

import Database.Database;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import HelperClasses.StageUtils;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerManageStudents implements IController, Initializable {

    private ObservableList<ManageStudentsTabTableRow> tableRows;

    Database database;

    Worker worker;

    @FXML
    private VBox manageStudentsScene;

    @FXML
    private JFXTreeTableView<ManageStudentsTabTableRow> manageStudentsTable;

    @FXML
    private JFXTextField searchInput;

    private JFXTreeTableColumn<ManageStudentsTabTableRow, String> firstNameCol, lastNameCol, idCol, emailCol;

    private String id, email, firstName, lastName;

    private static ObservableList<Student> data = FXCollections.observableArrayList();

    private final StageUtils stageUtils = StageUtils.getInstance();

    /**
     * This method sets the data in the Manage Students page.
     *
     * @param location  used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.worker = null;

        Label emptyTableLabel = new Label("No students found.");
        emptyTableLabel.setFont(new Font(18));
        manageStudentsTable.setPlaceholder(emptyTableLabel);

        firstNameCol = new JFXTreeTableColumn<>("First Name");
        firstNameCol.prefWidthProperty().bind(manageStudentsTable.widthProperty().divide(4));
        firstNameCol.setStyle("-fx-font-size: 18px");
        firstNameCol.setResizable(false);
        firstNameCol.setCellValueFactory(param -> param.getValue().getValue().getFirstName());

        lastNameCol = new JFXTreeTableColumn<>("Last Name");
        lastNameCol.prefWidthProperty().bind(manageStudentsTable.widthProperty().divide(4));
        lastNameCol.setStyle("-fx-font-size: 18px");
        lastNameCol.setResizable(false);
        lastNameCol.setCellValueFactory(param -> param.getValue().getValue().getLastName());

        idCol = new JFXTreeTableColumn<>("ID");
        idCol.prefWidthProperty().bind(manageStudentsTable.widthProperty().divide(4));
        idCol.setStyle("-fx-font-size: 18px");
        idCol.setResizable(false);
        idCol.setCellValueFactory(param -> param.getValue().getValue().getId());

        emailCol = new JFXTreeTableColumn<>("Email");
        emailCol.prefWidthProperty().bind(manageStudentsTable.widthProperty().divide(4));
        emailCol.setStyle("-fx-font-size: 18px");
        emailCol.setResizable(false);
        emailCol.setCellValueFactory(param -> param.getValue().getValue().getEmail());

        tableRows = FXCollections.observableArrayList();
        searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            Pattern p = Pattern.compile("^(rfid:)");
            Matcher m = p.matcher(searchInput.getText());
            if (m.find()) {
                Platform.runLater(() -> {
                    searchInput.setText(searchInput.getText().substring(5));
                });
            }

            manageStudentsTable.setPredicate(tableRow -> {
                String input = newValue.toLowerCase();
                firstName = tableRow.getValue().getFirstName().getValue();
                lastName = tableRow.getValue().getLastName().getValue();
                id = tableRow.getValue().getId().getValue();
                email = tableRow.getValue().getEmail().getValue();

                return ((firstName != null && firstName.toLowerCase().contains(input))
                        || (id != null && id.toLowerCase().contains(input))
                        || (email != null && email.toLowerCase().contains(input)))
                        || (lastName != null && lastName.toLowerCase().contains(input));
            });
        });


        manageStudentsTable.setRowFactory(param -> {
            final TreeTableRow<ManageStudentsTabTableRow> row = new TreeTableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getClickCount() == 2) {
                    edit(row.getIndex());
                } else {
                    final int index = row.getIndex();
                    if (index >= 0 && index < manageStudentsTable.getCurrentItemsCount() && manageStudentsTable.getSelectionModel().isSelected(index)) {
                        manageStudentsTable.getSelectionModel().clearSelection();
                        event.consume();
                    }
                }
            });
            return row;
        });

        populateTable();
    }

    /**
     * This method fills the table with the data, if there is any
     */
    public void populateTable() {
        tableRows.clear();
        manageStudentsTable.getColumns().clear();
        data.clear();
        database = new Database();
        data = database.getStudents();

        for (Student datum : data) {
            tableRows.add(new ManageStudentsTabTableRow(datum.getFirstName(), datum.getLastName(),
                    "" + datum.getRFID(), datum.getEmail()));
        }

        TreeItem<ManageStudentsTabTableRow> root = new RecursiveTreeItem<>(
                tableRows, RecursiveTreeObject::getChildren
        );

        manageStudentsTable.getColumns().setAll(firstNameCol, lastNameCol, idCol, emailCol);
        manageStudentsTable.setRoot(root);
        manageStudentsTable.setShowRoot(false);
    }

    /**
     * This method creates a new student
     */
    public void addStudent() {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/addStudent.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            IController controller = loader.getController();
            controller.initWorker(worker);
            Scene scene = new Scene(root);
            stage.setTitle("Add a New Student");
            stage.initOwner(manageStudentsScene.getScene().getWindow());
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
        populateTable();
    }

    /**
     * This method takes in an Excel file to import a list of students
     */
    @FXML
    private void importStudents() {
        database.initWorker(worker);
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Students");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));

            File file = fileChooser.showOpenDialog(manageStudentsTable.getScene().getWindow());
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
                                email = email.replace("'", "\\'");
                                if (!database.importStudent(new Student((firstName + " " + lastName).replace("'", "\\'"), email))) {
                                    failedImports.add(new Student(firstName + " " + lastName, email));
                                }
                            }
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        failedImports.add(new Student(name, email));
                    }
                } else {
                    wrongNumRowsAlert();
                }

            }
            populateTable();

            if (!failedImports.isEmpty()) {
                failedImportAlert(failedImports);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the current scene and loads the main menu. If no menu stage was found, sends an alert to user.
     */
    @FXML
    public void goBack() {
        stageUtils.goBack(manageStudentsScene, worker);
    }

    /**
     * Deletes a student
     */
    @FXML
    public void deleteStudent() {
        if (!manageStudentsTable.getSelectionModel().getSelectedCells().isEmpty()) {
            if ((worker != null && worker.isAdmin())
                    || StageUtils.getInstance().requestAdminPin("delete a student", manageStudentsScene)) {

                int row = manageStudentsTable.getSelectionModel().getFocusedIndex();
                String email = emailCol.getCellData(row);
                Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete this student?");
                alert.setTitle("Delete This Student?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    database.deleteStudent(email);
                    populateTable();
                }
            }
        }
    }

    /**
     * This brings up the edit student window
     *
     * @param row row in the table that the student is selected
     */
    public void edit(int row) {
        Stage stage = new Stage();
        ManageStudentsTabTableRow r = manageStudentsTable.getSelectionModel().getModelItem(row).getValue();
        Student s = null;
        if (Integer.parseInt(r.getId().get()) == 0) {
            s = database.selectStudentWithoutLists(r.getEmail().get());
        } else {
            s = database.selectStudent(Integer.parseInt(r.getId().get()), null);
        }

        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/EditStudent.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            EditStudent sp = loader.getController();
            sp.setStudent(s);
            sp.initWorker(worker);
            Scene scene = new Scene(root, 840, 630);
            stage.setTitle("Edit " + s.getName());
            stage.initOwner(manageStudentsScene.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.setOnHiding(event1 -> populateTable());
            stage.setResizable(false);
            if (sp.changed()) {
                stageUtils.unsavedChangesAlert(stage);
            }
            stage.show();
            populateTable();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load student info page");
            alert.initStyle(StageStyle.UTILITY);
            StudentCheckIn.logger.error("IOException: Couldn't load student info page.");
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

    /**
     * This is an error that shows up if there is a format error in the excel file
     */
    private void wrongNumRowsAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("The name must be in the first row and the email must be in the fourth row of the imported excel file.");
        StudentCheckIn.logger.error("The name must be in the first row and the email must be in the fourth row of the imported excel file.");
        alert.showAndWait();
    }

    /**
     * This is an error that shows up if a student(s) can't be imported
     *
     * @param failedImports list of failed students
     */
    private void failedImportAlert(List<Student> failedImports) {
        List<String> lines = new ArrayList<>();
        for (Student student : failedImports) {
            lines.add(student.getName());
        }
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            Path file = Paths.get("failed_students_import.txt");
            alert.setContentText("The program failed to import the students listed in the text file: \"" + file.getFileName() + "\"");
            StudentCheckIn.logger.error("The program failed to import the students listed in the text file: \"" + file.getFileName() + "\"");
            Files.write(file, lines);
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}