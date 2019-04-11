package InventoryController;

import Database.Database;
import Database.OverdueItem;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ControllerOverdueTab extends ControllerInventoryPage implements Initializable {

    @FXML
    private VBox overduePage;

    @FXML
    JFXTreeTableView<OverdueTabTableRow> overdueTable;

    @FXML
    ObservableList<OverdueTabTableRow> tableRows;

    private TreeItem<OverdueTabTableRow> root;

    @FXML
    private JFXTextField searchInput;

    @FXML
    JFXTreeTableColumn<OverdueTabTableRow, String> studentIDCol, partNameCol, serialNumberCol,
            dueDateCol, feeCol;

    @FXML
    private JFXButton searchButton;

    private String studentID, partName, serialNumber, dueDate, fee;

    private Database database;
    private ObservableList<OverdueItem> list = FXCollections.observableArrayList();

    /**
     * This method puts all overdue items into the list for populating the gui table
     *
     * @param location
     * @param resources
     * @author Bailey Terry
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setFont(new Font(18));
//        searchButton.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        overdueTable.setPlaceholder(emptyTableLabel);

        studentIDCol = new JFXTreeTableColumn<>("Student ID");
        studentIDCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(5));
        studentIDCol.setStyle("-fx-font-size: 18px");
        studentIDCol.setResizable(false);
        studentIDCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getStudentID();
            }
        });

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(5));
        partNameCol.setStyle("-fx-font-size: 18px");
        partNameCol.setResizable(false);
        partNameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
        serialNumberCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(5));
        serialNumberCol.setStyle("-fx-font-size: 18px");
        serialNumberCol.setResizable(false);
        serialNumberCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getSerialNumber();
            }
        });

        dueDateCol = new JFXTreeTableColumn<>("Due Date");
        dueDateCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(5));
        dueDateCol.setStyle("-fx-font-size: 18px");
        dueDateCol.setResizable(false);
        dueDateCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getDueDate();
            }
        });

        feeCol = new JFXTreeTableColumn<>("Fee");
        feeCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(5));
        feeCol.setStyle("-fx-font-size: 18px");
        feeCol.setResizable(false);
        feeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueTabTableRow, String> param) {
                return param.getValue().getValue().getFee();
            }
        });

        tableRows = FXCollections.observableArrayList();
    }

    /**
     * Creates an informational pop up on double click
     *
     * @author Bailey Terry
     */
    public void popUp() {
            Stage stage = new Stage();
            try {
                URL myFxmlURL = ClassLoader.getSystemResource("fxml/OverduePopup.fxml");
                FXMLLoader loader = new FXMLLoader(myFxmlURL);
                Parent root = loader.load();
                Scene scene = new Scene(root, 400, 400);
                stage.setTitle("Part Information");
                stage.initOwner(overduePage.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setScene(scene);
                int i = overdueTable.getSelectionModel().getSelectedIndex();
                OverdueTabTableRow item = new OverdueTabTableRow(overdueTable.getTreeItem(i).getValue().getStudentID().get(),
                        overdueTable.getTreeItem(i).getValue().getPartName().get(), overdueTable.getTreeItem(i).getValue().getSerialNumber().get(),
                        overdueTable.getTreeItem(i).getValue().getDueDate().get(), overdueTable.getTreeItem(i).getValue().getFee().get());
                ((OverduePopUpController) loader.getController()).populate(null, item);
                stage.getIcons().add(new Image("images/msoe.png"));
                stage.showAndWait();
            } catch (IOException e) {
                StudentCheckIn.logger.error("IOException while opening Overdue popup");
                e.printStackTrace();
            }

        populateTable();
    }

    /**
     * This method populates the gui based off of the data in the Observable list
     */
    public void populateTable() {
        tableRows.clear();
        overdueTable.getColumns().clear();
        list.clear();
        database = new Database();
        list = database.getOverdue();
        DecimalFormat df = new DecimalFormat("#,###,##0.00");

        for (int i = 0; i < list.size(); i++) {
            tableRows.add(new OverdueTabTableRow("" + list.get(i).getID().getValue(),
                    list.get(i).getPart().getValue(), list.get(i).getSerial().getValue(),
                    list.get(i).getDate().getValue(), "$" +
                    df.format(Double.parseDouble(list.get(i).getPrice().getValue()))));
        }

        root = new RecursiveTreeItem<OverdueTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

        searchInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search();
            }
        });

        overdueTable.getColumns().setAll(studentIDCol, partNameCol, serialNumberCol, dueDateCol, feeCol);
        overdueTable.setRoot(root);
        overdueTable.setShowRoot(false);
    }

    @FXML
    private void search() {
        overdueTable.setPredicate(new Predicate<TreeItem<OverdueTabTableRow>>() {
            @Override
            public boolean test(TreeItem<OverdueTabTableRow> tableRow) {
                String input = searchInput.getText().toLowerCase();
                studentID = tableRow.getValue().getStudentID().getValue();
                partName = tableRow.getValue().getPartName().getValue();
                serialNumber = tableRow.getValue().getSerialNumber().getValue();
                dueDate = tableRow.getValue().getDueDate().getValue();
                fee = tableRow.getValue().getFee().getValue();

                return ((studentID != null && studentID.toLowerCase().contains(input))
                        || (partName != null && partName.toLowerCase().contains(input))
                        || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                        || (dueDate != null && dueDate.toLowerCase().contains(input))
                        || (fee != null && fee.toLowerCase().contains(input)));
            }
        });
    }
}
