package gui;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageStudentsController implements Initializable {

    @FXML
    private VBox scene;

    @FXML
    private TableView studentsTableManageStudentsPage;

    @FXML
    private Button addStudentButtonManageStudentsPage,
            viewStudentButtonManageStudentsPage,
            deleteStudentButtonManageStudentsPage,
            backToHomeButtonManageStudentsPage;

    @FXML
    private TableColumn studentNameColumn, studentRFIDColumn, studentEmailColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
    }

    public void backToHome() {
        try {
            Pane pane = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            scene.getScene().setRoot(pane);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }

    public void addStudent() {
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("AddStudent.fxml"));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Add Student");
            diffStage.showAndWait();
            populateTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void viewStudent() {
        if (studentsTableManageStudentsPage.getSelectionModel().getSelectedItem() != null){
            viewStudentButtonManageStudentsPage.setDisable(false);
        }
        EditStudentController esc = new EditStudentController();
        Student student = (Student) studentsTableManageStudentsPage.getSelectionModel().getSelectedItem();
        try {
            Stage diffStage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("EditStudent.fxml"));
            Scene scene = new Scene(pane);
            diffStage.setScene(scene);
            diffStage.initModality(Modality.APPLICATION_MODAL);
            diffStage.setTitle("Edit Student");
            if (student != null) {
                esc.studentName.setText(student.getName());
                esc.studentID.setText(student.getRfid());
                esc.studentEmail.setText(student.getEmail());
                esc.dateOfRental.setText(student.getDateOfLastCheckout().toString());
                esc.checkedOut.setItems((ObservableList) student.getCheckedOut());
                esc.overdueItems.setItems((ObservableList) student.getOverdue());
                esc.savedItems.setItems((ObservableList) student.getSaved());
            }
            diffStage.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }

    public void deleteStudent() {
        if (studentsTableManageStudentsPage.getSelectionModel().getSelectedItems() != null) {
            String col;
            for (int i = 0; i < studentsTableManageStudentsPage.getSelectionModel().getSelectedItems().size(); i++) {
                // get the value of the third column
                col = studentsTableManageStudentsPage.getSelectionModel().getSelectedItems().get(i).toString().split(", ")[2];
                col = col.substring(col.indexOf(": ") + 2, col.indexOf("]]"));
                removeFromTextFile(col);
            }
            studentsTableManageStudentsPage.getItems().removeAll(studentsTableManageStudentsPage.getSelectionModel().getSelectedItems());
        }
    }

    public void removeFromTextFile(String email) {
        try {
            File inputFile = new File("src/students.txt");
            BufferedReader r = new BufferedReader(new FileReader(inputFile));
            String line;
            String lines = "";
            while ((line = r.readLine()) != null) {
                if (!line.contains(email)) {
                    lines += line + "\r\n";
                }
            }
            BufferedWriter w = new BufferedWriter(new FileWriter(inputFile, false));
            w.write(lines);
            w.close();
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void populateTable() {
        try {
            FileReader fr = new FileReader("src/students.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            int i = 0;
            studentsTableManageStudentsPage.getItems().clear();
            studentsTableManageStudentsPage.getColumns().clear();
            studentsTableManageStudentsPage.getColumns().add(createColumn(0, "Student Name"));
            studentsTableManageStudentsPage.getColumns().add(createColumn(1, "Student RFID"));
            studentsTableManageStudentsPage.getColumns().add(createColumn(2, "Student Email"));
            while ((line = br.readLine()) != null) {
                final String[] items = line.split(",");
                for (int columnIndex = studentsTableManageStudentsPage.getColumns().size(); columnIndex < items.length; columnIndex++) {
                    studentsTableManageStudentsPage.getColumns().add(createColumn(columnIndex, ""));
                }
                ObservableList<StringProperty> data = FXCollections.observableArrayList();
                for (String value : items) {
                    data.add(new SimpleStringProperty(value));
                }
                studentsTableManageStudentsPage.getItems().add(data);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private TableColumn<ObservableList<StringProperty>, String> createColumn(
            final int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        column.setPrefWidth(150);
        String title;
        if (columnTitle == null || columnTitle.trim().length() == 0) {
            title = "Column " + (columnIndex + 1);  // DELETE??
        } else {
            title = columnTitle;
        }
        column.setText(title);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(
                    TableColumn.CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures) {
                ObservableList<StringProperty> values = cellDataFeatures.getValue();
                if (columnIndex >= values.size()) {
                    return new SimpleStringProperty("");
                } else {
                    return cellDataFeatures.getValue().get(columnIndex);
                }
            }
        });
        return column;
    }

}