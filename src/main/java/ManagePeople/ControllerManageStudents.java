package ManagePeople;

import Database.Database;
import Database.Student;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerManageStudents implements Initializable {

    private ObservableList<ManageStudentsTabTableRow> tableRows;

    Database database;

    @FXML
    private AnchorPane manageStudentsScene;

    @FXML
    private JFXTreeTableView<ManageStudentsTabTableRow> manageStudentsTable;
    private TreeItem<ManageStudentsTabTableRow> root;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private Button addStudent;

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
        Label emptyTableLabel = new Label("No students found.");
        emptyTableLabel.setFont(new Font(18));
        manageStudentsTable.setPlaceholder(emptyTableLabel);

        nameCol = new JFXTreeTableColumn<>("Name");
        nameCol.setPrefWidth(800/3);
        nameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String> param) {
                return param.getValue().getValue().getName();
            }
        });

        idCol = new JFXTreeTableColumn<>("ID");
        idCol.setPrefWidth(800/3);
        idCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ManageStudentsTabTableRow, String> param) {
                return param.getValue().getValue().getId();
            }
        });

        emailCol = new JFXTreeTableColumn<>("Email");
        emailCol.setPrefWidth(800/3);
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



        manageStudentsTable.setRowFactory(new Callback<TreeTableView<ManageStudentsTabTableRow>, TreeTableRow<ManageStudentsTabTableRow>>() {
            @Override
            public TreeTableRow<ManageStudentsTabTableRow> call(TreeTableView<ManageStudentsTabTableRow> param) {
                final TreeTableRow<ManageStudentsTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = row.getIndex();
                        if (index >= 0 && index < manageStudentsTable.getCurrentItemsCount() && manageStudentsTable.getSelectionModel().isSelected(index)) {
                            manageStudentsTable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                return row;
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
                    "" + data.get(i).getID(), data.get(i).getEmail()));
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
                    if (!database.selectStudent(Integer.parseInt(id)).getName().equals("")) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Student is already in the database!");
                        alert.showAndWait();
                        notIncluded = false;
                        break;
                    }
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Students RFID is invalid.");
                }
            }else {
                break;
            }
        }
        invalid = true;
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(name);
        while (invalid && notIncluded){
            String input = JOptionPane.showInputDialog(null, "Please enter the students first name.");
            if (input != null) {
                name = new StringBuilder(input);
                if (!m.find() && !name.toString().equals("")) {
                    String temp = name.substring(0, 1).toUpperCase() + name.substring(1);
                    name = new StringBuilder(temp);
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Students first name is invalid or blank.");
                }
            }else {
                break;
            }
        }
        invalid = true;
        while (invalid && notIncluded){
            String input = JOptionPane.showInputDialog(null, "Please enter the students last name.");
            if (input != null) {
                name.append(" ");
                name.append(input);
                if (!m.find() && !name.toString().equals(" ")) {
                    int space = name.indexOf(" ");
                    String temp = name.substring(0, space + 1) + name.substring(space + 1, space + 2).toUpperCase() + name.substring(space + 2);
                    name = new StringBuilder(temp);
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Students last name is invalid or blank.");
                }
            }else {
                break;
            }
        }
        invalid = true;
        while (invalid && notIncluded){
            email = JOptionPane.showInputDialog(null, "Please enter the students MSOE email.");
            if (email != null) {
                if (email.matches("^\\w+[+.\\w-]*@msoe\\.edu$")) {
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Students email must be their MSOE email.");
                }
            }else {
                break;
            }
        }
        if (notIncluded && name != null && id != null && email != null) {
            database.addStudent(new Student(name.toString(), Integer.parseInt(id), email));
        }
        populateTable();
    }

    /**
     *Clears the current scene and loads the main menu. If no menu stage was found, sends an alert to user.
     * @author Matthew Karcz
     */
    @FXML
    public void goBack(){
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/Menu.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            manageStudentsScene.getChildren().clear();
            manageStudentsScene.getScene().setRoot(loader.load(myFxmlURL));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }

}