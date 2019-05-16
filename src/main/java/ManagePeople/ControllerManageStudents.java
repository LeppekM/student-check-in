package ManagePeople;

import Database.Database;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import HelperClasses.AdminPinRequestController;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
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
    private TreeItem<ManageStudentsTabTableRow> root;

    @FXML
    private JFXTextField searchInput;

    private JFXTreeTableColumn<ManageStudentsTabTableRow, String> nameCol, idCol, emailCol;

    private String name, id, email;

    private static ObservableList<Student> data = FXCollections.observableArrayList();

    /**
     * This method sets the data in the Manage Students page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.worker = null;

        Label emptyTableLabel = new Label("No students found.");
        emptyTableLabel.setFont(new Font(18));
        manageStudentsTable.setPlaceholder(emptyTableLabel);

        nameCol = new JFXTreeTableColumn<>("Name");
        nameCol.prefWidthProperty().bind(manageStudentsTable.widthProperty().divide(3));
        nameCol.setStyle("-fx-font-size: 18px");
        nameCol.setResizable(false);
        nameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String> param) {
                return param.getValue().getValue().getName();
            }
        });

        idCol = new JFXTreeTableColumn<>("ID");
        idCol.prefWidthProperty().bind(manageStudentsTable.widthProperty().divide(3));
        idCol.setStyle("-fx-font-size: 18px");
        idCol.setResizable(false);
        idCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String> param) {
                return param.getValue().getValue().getId();
            }
        });

        emailCol = new JFXTreeTableColumn<>("Email");
        emailCol.prefWidthProperty().bind(manageStudentsTable.widthProperty().divide(3));
        emailCol.setStyle("-fx-font-size: 18px");
        emailCol.setResizable(false);
        emailCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String> param) {
                return param.getValue().getValue().getEmail();
            }
        });

        tableRows = FXCollections.observableArrayList();
        searchInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                manageStudentsTable.setPredicate(new Predicate<TreeItem<ManageStudentsTabTableRow>>() {
                    @Override
                    public boolean test(TreeItem<ManageStudentsTabTableRow> tableRow) {
                        String input = newValue.toLowerCase();
                        name = tableRow.getValue().getName().getValue();
                        id = tableRow.getValue().getId().getValue();
                        email = tableRow.getValue().getEmail().getValue();

                        return ((name != null && name.toLowerCase().contains(input))
                            || (id != null && id.toLowerCase().contains(input))
                            || (email != null && email.toLowerCase().contains(input)));
                    }
                });
            }
        });

        populateTable();
    }

    public void populateTable() {
        tableRows.clear();
        manageStudentsTable.getColumns().clear();
        this.data.clear();
        database = new Database();
        this.data = database.getStudents();

        for (int i = 0; i < data.size(); i++) {
            tableRows.add(new ManageStudentsTabTableRow(data.get(i).getName(),
                    "" + data.get(i).getRFID(), data.get(i).getEmail()));
        }

        root = new RecursiveTreeItem<ManageStudentsTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

        manageStudentsTable.getColumns().setAll(nameCol, idCol, emailCol);
        manageStudentsTable.setRoot(root);
        manageStudentsTable.setShowRoot(false);
    }

    public void addStudent() {
        StringBuilder name = new StringBuilder();
        String id = "";
        String email = "";
        boolean notIncluded = true;
        boolean invalid = true;
        while (invalid && notIncluded){
            id = JOptionPane.showInputDialog(null, "Please enter the student RFID.");
            if (id != null) {
                Pattern p = Pattern.compile("^(rfid:)");
                Matcher m = p.matcher(id);
                if (m.find()) {
                    id = id.substring(5);
                }
                if (!id.matches("[a-zA-Z]*") && id.length() == 5) {
                    if (!database.selectStudent(Integer.parseInt(id), null).getName().equals("")) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Student is already in the database!");
                        StudentCheckIn.logger.info("Manage Students: Student is already in the database!");
                        alert.showAndWait();
                        notIncluded = false;
                        break;
                    }
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Students RFID is invalid.");
                    StudentCheckIn.logger.error("Manage Students: Student's RFID is invalid.");
                }
            }else {
                break;
            }
        }
        invalid = true;
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m;
        while (invalid && notIncluded){
            String input = JOptionPane.showInputDialog(null, "Please enter the students first name.");
            if (input != null) {
                m = p.matcher(input);
                name = new StringBuilder(input);
                if (!m.matches() && !name.toString().matches("\\s*")) {
                    String temp = name.substring(0, 1).toUpperCase() + name.substring(1);
                    name = new StringBuilder(temp);
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Students first name is invalid or blank.");
                    StudentCheckIn.logger.error("Manage Students: Student's first name is invalid or blank.");
                }
            }else {
                break;
            }
        }
        invalid = true;
        while (invalid && notIncluded){
            String input = JOptionPane.showInputDialog(null, "Please enter the students last name.");
            if (input != null) {
                m = p.matcher(input);
                name.append(" ");
                name.append(input);
                if (!m.matches() && !name.toString().matches("\\s+")) {
                    int space = name.indexOf(" ");
                    String temp = name.substring(0, space + 1) + name.substring(space + 1, space + 2).toUpperCase() + name.substring(space + 2);
                    name = new StringBuilder(temp);
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Students last name is invalid or blank.");
                    StudentCheckIn.logger.error("Manage Students: Student's last name is invalid or blank.");
                }
            }else {
                break;
            }
        }
        invalid = true;
        while (invalid && notIncluded){
            email = JOptionPane.showInputDialog(null, "Please enter the students MSOE email.");
            if (email != null) {
                ObservableList<Student> students = database.getStudents();
                if (email.matches("^\\w+[+.\\w-]*@msoe\\.edu$")) {
                    invalid = false;
                    for (Student s: students){
                        if (s.getEmail().equals(email)) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Student email already in use try another");
                            alert.showAndWait();
                            invalid = true;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Students email must be their MSOE email.");
                    StudentCheckIn.logger.error("Manage Students: Student's email must be their MSOE email.");
                }
            }else {
                break;
            }
        }
        if (notIncluded && name != null && id != null && email != null) {
            database.initWorker(worker);
            database.addStudent(new Student(name.toString(), Integer.parseInt(id), email));
        }
        populateTable();
    }

    @FXML
    private void importStudents() {
        database.initWorker(worker);
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Students");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
//        fileChooser.setSelectedExtensionFilter();

            File file = fileChooser.showOpenDialog(manageStudentsTable.getScene().getWindow());
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIt = sheet.iterator();
            // skip the first row, which just has column labels
            if (rowIt.hasNext()) {
                rowIt.next();
            }

            // parse the rest of the rows
            while (rowIt.hasNext()) {
                Row row = rowIt.next();
//                Iterator<Cell> cellIterator = row.cellIterator();
                if (row.getCell(0) != null && row.getCell(3) != null) {
                    String name = row.getCell(0).toString();
                    String lastName = name.substring(0, name.indexOf(", "));
                    String restOfName = name.substring(name.indexOf(", ") + 2);
                    String firstName = "";
                    if (restOfName.contains(" ")) {
                        firstName = restOfName.substring(0, restOfName.indexOf(" "));
                    } else {
                        firstName = restOfName;
                    }
                    if (restOfName.contains(", ")) {
                        lastName += restOfName.substring(restOfName.indexOf(", ") + 1);
                    }
                    String email = row.getCell(3).toString();
                    System.out.println(firstName + " " + lastName + ": " + email);
                    if (!email.matches("^\\w+[+.\\w-]*@msoe\\.edu$")){
                        System.out.println("bad email");
                    } else if (!database.getStudentEmails().contains(email)) {
                        database.importStudent(new Student(firstName + " " + lastName, email));
                    }
                } else {
                    System.out.println("wrong number of columns");
                }
            }
            populateTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *Clears the current scene and loads the main menu. If no menu stage was found, sends an alert to user.
     */
    @FXML
    public void goBack(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Menu.fxml"));
            Parent root = loader.load();
            IController controller = loader.<IController>getController();
            controller.initWorker(worker);
            manageStudentsScene.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
            manageStudentsScene.getChildren().clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            StudentCheckIn.logger.error("IOException: No valid stage was found to load");
            alert.showAndWait();
        }
    }

    public void deleteStudent(ActionEvent actionEvent) {
        if (manageStudentsTable.getSelectionModel().getSelectedCells().size() != 0) {
            if ((worker != null && worker.isAdmin())
                    || requestAdminPin("delete a student")) {
                int row = manageStudentsTable.getSelectionModel().getFocusedIndex();
                Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete this student?");
                alert.setTitle("Delete This Student?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    database.deleteStudent(data.get(row).getName());
                    data.remove(row);
                    populateTable();
                }
            }
        }
    }

    public boolean requestAdminPin(String action) {
        AtomicBoolean isValid = new AtomicBoolean(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminPinRequest.fxml"));
            Parent root = loader.load();
            ((AdminPinRequestController) loader.getController()).setAction(action);
            Scene scene = new Scene(root, 400, 250);
            Stage stage = new Stage();
            stage.setTitle("Admin Pin Required");
            stage.initOwner(manageStudentsScene.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.setResizable(false);
            stage.setOnCloseRequest(e -> {
                // checks to see whether the pin was submitted or the window was just closed
                if (((AdminPinRequestController) loader.getController()).isSubmitted()) {
                    // checks to see if the input pin is empty. if empty, close pop up
                    if (((AdminPinRequestController) loader.getController()).isNotEmpty()) {
                        // checks to see whether the submitted pin matches one of the admin's pins
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

    public void edit(MouseEvent event) {
        if (event.getClickCount() == 2){
            Stage stage = new Stage();
            int f = manageStudentsTable.getSelectionModel().getSelectedIndex();
            ManageStudentsTabTableRow r = manageStudentsTable.getSelectionModel().getModelItem(f).getValue();
            Student s = database.selectStudent(Integer.parseInt(r.getId().get()), null);
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
                if (sp.changed()) {
                    stage.setOnCloseRequest(event1 -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close?");
                        alert.setTitle("Confirm Close");
                        alert.setHeaderText("If you leave now, unsaved changes could be lost.");
                        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                        alert.showAndWait().ifPresent(buttonType -> {
                            if (buttonType == ButtonType.YES) {
                                stage.close();
                            } else if (buttonType == ButtonType.NO) {
                                event1.consume();
                            }
                        });
                    });
                }
                stage.show();
                populateTable();
            }catch (IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load student info page");
                alert.initStyle(StageStyle.UTILITY);
                StudentCheckIn.logger.error("IOException: Couldn't load student info page.");
                alert.showAndWait();
                e.printStackTrace();
            }
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
     * Alert that the pin entered does not match one of the admin pins.
     */
    private void invalidAdminPinAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("The pin entered is invalid.");
        StudentCheckIn.logger.error("The pin entered is invalid.");
        alert.showAndWait();
    }
}