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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ControllerManageWorkers implements Initializable {
    private ObservableList<ManageWorkersTabTableRow> tableRows;

    private Database database;

    @FXML
    private AnchorPane manageWorkersScene;

    @FXML
    private JFXTreeTableView<ManageStudentsTabTableRow> manageWorkersTable;
    private TreeItem<ManageWorkersTabTableRow> root;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private Button addWorker;

    private JFXTreeTableColumn<ManageWorkersTabTableRow, String> nameCol, emailCol, adminCol;

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
                manageWorkersTable.setPredicate(new Predicate<TreeItem<ManageStudentsTabTableRow>>() {
                    @Override
                    public boolean test(TreeItem<ManageStudentsTabTableRow> tableRow) {
                        String input = newValue.toLowerCase();
                        name = tableRow.getValue().getName().getValue();
                        email = tableRow.getValue().getEmail().getValue();

                        return ((name != null && name.toLowerCase().contains(input))
                                || (email != null && email.toLowerCase().contains(input)));
                    }
                });
            }
        });

        manageWorkersTable.setRowFactory(new Callback<TreeTableView<ManageStudentsTabTableRow>, TreeTableRow<ManageStudentsTabTableRow>>() {
            @Override
            public TreeTableRow<ManageStudentsTabTableRow> call(TreeTableView<ManageStudentsTabTableRow> param) {
                final TreeTableRow<ManageStudentsTabTableRow> row = new TreeTableRow<>();
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

        root = new RecursiveTreeItem<ManageWorkersTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

//        manageWorkersTable.getColumns().setAll(nameCol, emailCol, adminCol);
//        manageWorkersTable.setRoot(root);
//        manageWorkersTable.setShowRoot(false);

    }

    public void addWorker(ActionEvent actionEvent) {
        //todo
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
