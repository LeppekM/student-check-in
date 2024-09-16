package Tables;

import Database.ObjectClasses.Checkout;
import Database.ObjectClasses.Student;
import HelperClasses.ExportToExcel;
import Controllers.TableScreensController;
import HelperClasses.StageUtils;
import Popups.EditStudentController;
import Popups.Popup;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static Controllers.CheckOutController.EMAIL_REGEX;

/**
 * Manages the table which contains all students in the manage students screen
 */
public class ManageStudentsTable extends TSCTable {

    private JFXTreeTableColumn<MSRow, String> firstNameCol, lastNameCol, emailCol;
    private JFXTreeTableColumn<MSRow, Long> studentIDCol;

    public ManageStudentsTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 4;
        table.setPlaceholder(getEmptyTableLabel());

        firstNameCol = createNewCol("First Name");
        firstNameCol.setCellValueFactory(col -> col.getValue().getValue().getFirstName());
        lastNameCol = createNewCol("Last Name");
        lastNameCol.setCellValueFactory(col -> col.getValue().getValue().getLastName());
        studentIDCol = createNewCol("RFID");
        studentIDCol.setCellValueFactory(col -> col.getValue().getValue().getId().asObject());
        emailCol = createNewCol("Email");
        emailCol.setCellValueFactory(col -> col.getValue().getValue().getEmail());

        rows = FXCollections.observableArrayList();

        setDoubleClickBehavior();
    }

    @Override
    public void export(ExportToExcel exportToExcel) {
        // students don't need to be exported
    }

    @Override
    public void populateTable() {
        // clear previous data
        rows.clear();
        table.getColumns().clear();
        // get and add all rows
        ObservableList<Student> list = database.getStudents();
        for (Student student : list) {
            rows.add(new MSRow(student.getFirstName(), student.getLastName(),
                    student.getRFID(), student.getEmail()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> firstNameTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) firstNameCol;
        TreeTableColumn<TableRow, String> lastNameTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) lastNameCol;
        TreeTableColumn<TableRow, Long> studentIDTemp =
                (TreeTableColumn<TableRow, Long>) (TreeTableColumn) studentIDCol;
        TreeTableColumn<TableRow, String> emailTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) emailCol;

        table.getColumns().setAll(firstNameTemp, lastNameTemp, studentIDTemp, emailTemp);
        table.setRoot(root);
        // needs to be false so that it doesn't group all elements, effectively hiding them until you drop them down
        table.setShowRoot(false);
        controller.repopulatedTableSearch();
    }

    @Override
    public boolean isMatch(TableRow value, String[] filters) {
        for (String filter : filters) {
            MSRow val = (MSRow) value;
            String input = filter.toLowerCase();
            String firstName = val.getFirstName().getValue();
            String lastName = val.getLastName().getValue();
            String id = "" + val.getId().getValue();
            String email = val.getEmail().getValue();
            if (!(firstName != null && firstName.toLowerCase().contains(input)
                    || id.toLowerCase().contains(input)
                    || email != null && email.toLowerCase().contains(input)
                    || lastName != null && lastName.toLowerCase().contains(input))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void popupRow(int index) {
        Stage stage = new Stage();
        MSRow r = (MSRow) table.getSelectionModel().getModelItem(index).getValue();
        Student s;
        if (r.getId().get() == 0) {
            s = database.selectStudentWithoutLists(r.getEmail().get());
        } else {
            s = database.selectStudent(r.getId().get(), null);
        }

        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/EditStudent.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            EditStudentController sp = loader.getController();
            sp.setStudent(s);
            sp.initWorker(worker);
            Scene scene = new Scene(root, 840, 630);
            stage.setTitle("Edit " + s.getName());
            stage.initOwner(scene.getWindow());
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
            stageUtils.errorAlert("Couldn't load student info page");
        }
    }

    public void importStudents() {
        database.initWorker(worker);
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Students");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));

            File file = fileChooser.showOpenDialog(table.getScene().getWindow());
            if (file != null) {
                FileInputStream fis = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(fis);
                XSSFSheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIt = sheet.iterator();
                // skip the first row, which just has column labels
                if (rowIt.hasNext()) {
                    rowIt.next();
                }

                List<Student> failedImports = new ArrayList<>();
                List<String> allStudentEmails = database.getStudentEmails().stream().map(String::toLowerCase)
                        .collect(Collectors.toList()) ;
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
                            if (!email.matches(EMAIL_REGEX)) {
                                failedImports.add(new Student(firstName + " " + lastName, email));
                            } else {
                                if (!allStudentEmails.contains(email.toLowerCase())) {
                                    if (!database.importStudent(new Student(firstName + " " + lastName, email))) {
                                        failedImports.add(new Student(firstName + " " + lastName, email));
                                    }
                                }
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            failedImports.add(new Student(name, email));
                        }
                    } else {
                        stageUtils.errorAlert("The name must be in the first row and the email must " +
                                "be in the fourth row of the imported excel file.");
                    }
                }
                populateTable();

                if (!failedImports.isEmpty()) {
                    List<String> lines = new ArrayList<>();
                    for (Student student : failedImports) {
                        lines.add(student.getName());
                    }
                    Path filePath = Paths.get("failed_students_import.txt");
                    Files.write(filePath, lines);
                    stageUtils.errorAlert("The program failed to import the students listed in the text file: \""
                            + filePath.getFileName() + "\"");
                }
            }  // do nothing if no file was selected / operation cancelled
        } catch (IOException e) {
            stageUtils.errorAlert("Failed to import students");
        }
    }

    public void addStudent() {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setTitle("Add a New Student");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);

        new Popup(root) {
            private JFXTextField email, first, last, rfid;

            @Override
            public void populate() {
                first = (JFXTextField) add("First Name: ", "", true).getChildren().get(1);
                last = (JFXTextField) add("Last Name: ", "", true).getChildren().get(1);
                email = (JFXTextField) add("Email: ", "", true).getChildren().get(1);
                rfid = (JFXTextField) add("RFID: ", "", true).getChildren().get(1);
            }

            @Override
            public void submit() {
                if (!first.getText().isEmpty() && !last.getText().isEmpty() &&
                        !email.getText().isEmpty() && !rfid.getText().isEmpty()) {
                    if (rfid.getText().matches("[0-9]{4,}")) {
                        if (email.getText().matches("^\\w+[+.\\w'-]*@msoe\\.edu$")) {
                            if (database.getStudentEmails().contains(email.getText())) {
                                stageUtils.errorAlert("A student with that email already exists.");
                            } else if (database.studentRFIDExists(Long.parseLong(rfid.getText()))) {
                                stageUtils.errorAlert("A student with that rfid already exists.");
                            } else {
                                database.initWorker(worker);
                                database.addStudent(new Student(first.getText() + " " + last.getText(),
                                        Long.parseLong(rfid.getText()), email.getText()));
                                stage.close();
                            }
                        } else {
                            stageUtils.errorAlert("Please enter a valid msoe email.");
                        }
                    } else {
                        stageUtils.errorAlert("The rfid must be a validly formatted student RFID." +
                                " Scan the student ID.");
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

    public void deleteStudent() {
        if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
            if (worker != null && worker.isAdmin()
                    || StageUtils.getInstance().requestAdminPin("delete a student", controller.getScene())) {

                int index = table.getSelectionModel().getFocusedIndex();
                String email = emailCol.getCellData(index);
                ArrayList<Checkout> currentlyCheckedOut = new ArrayList<>(database.selectStudent(-1 ,email)
                        .getCheckedOut());
                StringBuilder currentlyCheckedOutParts = new StringBuilder();
                for (Checkout checkout : currentlyCheckedOut) {
                    currentlyCheckedOutParts.append(checkout.getPartName().get()).append(" partID = ")
                            .append(checkout.getPartID().get()).append(", ");
                }
                if (currentlyCheckedOutParts.length() > 0) {
                    if (stageUtils.confirmationAlert("Student has Parts Checked Out", "The selected student " +
                            firstNameCol.getCellData(index) + " " + lastNameCol.getCellData(index) +
                            " has parts currently checked out. They will be marked as returned before the student is " +
                            "deleted", "Parts currently checked out: "
                            + currentlyCheckedOutParts.substring(0, currentlyCheckedOutParts.length() - 2))) {
                        for (Checkout checkout : currentlyCheckedOut) {
                            database.checkInPart(checkout.getBarcode().get(), checkout.getStudentID().get());
                        }
                    } else {
                        return;
                    }
                }
                if (stageUtils.confirmationAlert("Delete Student", "Are you ok with this?", "Delete this Student?")) {
                    database.deleteStudent(email);
                    populateTable();
                }
            }
        }
    }

    public void clearUnusedStudents() {
        if (this.worker != null && this.worker.isAdmin()) {
            if (stageUtils.confirmationAlert("Confirm Delete Unused Students",
                    "Are you sure you want to clear unused students?",
                    "Do you want to delete students with no transaction history?")) {
                database.clearUnusedStudents();
                populateTable();
            }
        }
    }

    public class MSRow extends TableRow {

        private final StringProperty firstName;
        private final StringProperty lastName;
        private final StringProperty email;
        private final LongProperty id;

        public MSRow(String firstName, String lastName, long id, String email) {
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.id = new SimpleLongProperty(id);
            this.email = new SimpleStringProperty(email);
        }

        public StringProperty getFirstName() {
            return firstName;
        }

        public StringProperty getLastName() {
            return lastName;
        }

        public LongProperty getId() {
            return id;
        }

        public StringProperty getEmail() {
            return email;
        }
    }
}
