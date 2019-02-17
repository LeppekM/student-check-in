package ManagePeople;

import Database.*;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerManageWorkers implements Initializable {
    private ObservableList<ManageWorkersTabTableRow> tableRows;

    private Database database;

    @FXML
    private AnchorPane manageWorkersScene;

    @FXML
    private JFXTreeTableView<ManageWorkersTabTableRow> manageWorkersTable;
    private TreeItem<ManageWorkersTabTableRow> root;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private Button addWorker;

    @FXML
    private JFXTreeTableColumn<ManageWorkersTabTableRow, String> nameCol, emailCol;

    @FXML
    private JFXTreeTableColumn<ManageWorkersTabTableRow, Boolean> adminCol;

    private String name, email, admin;

    private static ObservableList<Worker> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No workers found.");
        emptyTableLabel.setFont(new Font(18));
        manageWorkersTable.setPlaceholder(emptyTableLabel);

        nameCol = new JFXTreeTableColumn<>("Name");
        nameCol.setPrefWidth(800/3);
        nameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, String> param) {
                return param.getValue().getValue().getName();
            }
        });

        emailCol = new JFXTreeTableColumn<>("Email");
        emailCol.setPrefWidth(800/3);
        emailCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, String> param) {
                return param.getValue().getValue().getEmail();
            }
        });

        adminCol = new JFXTreeTableColumn<>("Admin");
        adminCol.setPrefWidth(800/3);
        adminCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, Boolean>,
                ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<ManageWorkersTabTableRow, Boolean> param) {
                TreeItem<ManageWorkersTabTableRow> treeItem = param.getValue();
                ManageWorkersTabTableRow tRow = treeItem.getValue();
                return new SimpleBooleanProperty(tRow.getIsAdmin().get());
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

        manageWorkersTable.setRowFactory(new Callback<TreeTableView<ManageWorkersTabTableRow>, TreeTableRow<ManageWorkersTabTableRow>>() {
            @Override
            public TreeTableRow<ManageWorkersTabTableRow> call(TreeTableView<ManageWorkersTabTableRow> param) {
                final TreeTableRow<ManageWorkersTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = row.getIndex();
                        if (index >= 0 && index < manageWorkersTable.getCurrentItemsCount() && manageWorkersTable.getSelectionModel().isSelected(index)) {
                            manageWorkersTable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                return row;
            }
        });

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
        StringBuilder name = new StringBuilder();
        String pin = "";
        String email = "";
        boolean notIncluded = true;
        boolean invalid = true;
        while (invalid && notIncluded){
            email = JOptionPane.showInputDialog(null, "Please enter the workers MSOE email.");
            if (email != null) {
                ObservableList<Worker> workers = database.getWorkers();
                for (Worker w : workers) {
                    boolean match = w.getEmail().equals(email);
                    if (match) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Worker is already in the database!");
                        alert.showAndWait();
                        notIncluded = false;
                        break;
                    }
                }
                if (email.matches("^\\w+[+.\\w-]*@msoe\\.edu$")) {
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Workers email must be their MSOE email.");
                }
            }else {
                break;
            }
        }
        invalid = true;
        Pattern p = Pattern.compile("[0-9]*");
        String n = name.toString();
        StringBuilder n1 = name;
        Matcher m = null;
        while (invalid && notIncluded){
            String input = JOptionPane.showInputDialog(null, "Please enter the workers first name.");
            if (input != null) {
                m = p.matcher(input);
                name = new StringBuilder(input);
                if (!m.matches() && !name.toString().matches("\\s*")) {
                    String temp = name.substring(0, 1).toUpperCase() + name.substring(1);
                    name = new StringBuilder(temp);
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Workers first name is invalid or blank.");
                }
            }else {
                break;
            }
        }
        invalid = true;
        while (invalid && notIncluded){
            String input = JOptionPane.showInputDialog(null, "Please enter the workers last name.");
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
                    JOptionPane.showMessageDialog(null, "Workers last name is invalid or blank.");
                }
            }else {
                break;
            }
        }
        invalid = true;
        while (invalid && notIncluded){
            pin = JOptionPane.showInputDialog(null, "Please enter a four digit pin for the admin");
            if (pin != null) {
                if (pin.matches("[0-9]{4}")) {
                    invalid = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Must be a four digit pin");
                }
            }else {
                break;
            }
        }
        if (notIncluded && name != null && email != null && pin != null) {
            database.addWorker(new Worker(name.toString(), email, pin, true));
        }
        populateTable();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/Menu.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            manageWorkersScene.getChildren().clear();
            manageWorkersScene.getScene().setRoot(loader.load(myFxmlURL));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }
}
