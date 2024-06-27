package Tables;

import Database.ObjectClasses.Student;
import HelperClasses.ExportToExcel;
import App.StudentCheckIn;
import Controllers.TableScreensController;
import Popups.EditStudent;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class ManageStudentsTable extends TSCTable {

    private JFXTreeTableColumn<MSRow, String> firstNameCol, lastNameCol, emailCol;
    private JFXTreeTableColumn<MSRow, Integer> studentIDCol;

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
        TreeTableColumn<TableRow, String> firstNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) firstNameCol;
        TreeTableColumn<TableRow, String> lastNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) lastNameCol;
        TreeTableColumn<TableRow, Integer> studentIDTemp = (TreeTableColumn<TableRow, Integer>) (TreeTableColumn) studentIDCol;
        TreeTableColumn<TableRow, String> emailTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) emailCol;

        table.getColumns().setAll(firstNameTemp, lastNameTemp, studentIDTemp, emailTemp);
        table.setRoot(root);
        // needs to be false so that it doesn't group all elements, effectively hiding them until you drop them down
        table.setShowRoot(false);
    }

    @Override
    public boolean isMatch(TableRow value, String filter) {
        MSRow val = (MSRow) value;
        String input = filter.toLowerCase();
        String firstName = val.getFirstName().getValue();
        String lastName = val.getLastName().getValue();
        String id = "" + val.getId().getValue();
        String email = val.getEmail().getValue();

        return ((firstName != null && firstName.toLowerCase().contains(input))
                || (id != null && id.toLowerCase().contains(input))
                || (email != null && email.toLowerCase().contains(input)))
                || (lastName != null && lastName.toLowerCase().contains(input));
    }

    @Override
    protected void popupRow(int index) {
        Stage stage = new Stage();
        MSRow r = (MSRow) table.getSelectionModel().getModelItem(index).getValue();
        Student s = null;
        if (r.getId().get() == 0) {
            s = database.selectStudentWithoutLists(r.getEmail().get());
        } else {
            s = database.selectStudent(r.getId().get(), null);
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load student info page");
            alert.initStyle(StageStyle.UTILITY);
            StudentCheckIn.logger.error("IOException: Couldn't load student info page.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public String getEmail(int row) {
        return emailCol.getCellData(row);
    }

    public class MSRow extends TableRow {

        private final StringProperty firstName;
        private final StringProperty lastName;
        private final StringProperty email;
        private final IntegerProperty id;

        public MSRow(String firstName, String lastName, int id, String email) {
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.id = new SimpleIntegerProperty(id);
            this.email = new SimpleStringProperty(email);
        }

        public StringProperty getFirstName() {
            return firstName;
        }

        public StringProperty getLastName() {
            return lastName;
        }

        public IntegerProperty getId() {
            return id;
        }

        public StringProperty getEmail() {
            return email;
        }
    }
}
