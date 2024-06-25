package InventoryController;

import Database.Database;
import Database.OverdueItem;
import HelperClasses.ExportToExcel;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private JFXTreeTableColumn<OverdueTabTableRow, String> studentNameCol,  partNameCol, serialNumberCol;

    @FXML
    private JFXTreeTableColumn<OverdueTabTableRow, Date> dueDateCol;

    @FXML
    private JFXTreeTableColumn<OverdueTabTableRow, Integer> studentIDCol;

    @FXML
    private JFXTreeTableColumn<OverdueTabTableRow, Long> barcodeCol;


    @FXML
    private JFXButton searchButton;

    private String studentID, partName, serialNumber, dueDate, fee, barcode, studentName;


    private Database database;
    private ObservableList<OverdueItem> list = FXCollections.observableArrayList();
    private ExportToExcel export = new ExportToExcel();
    private final double NUM_OF_COLS = 6;

    /**
     * This method puts all overdue items into the list for populating the gui table
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setStyle("-fx-text-fill: white");
        emptyTableLabel.setFont(new Font(18));
        overdueTable.setPlaceholder(emptyTableLabel);

        studentNameCol = new JFXTreeTableColumn<>("Student Name");
        studentNameCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(NUM_OF_COLS));
        studentNameCol.setStyle("-fx-font-size: 18px");
        studentNameCol.setResizable(false);
        studentNameCol.setCellValueFactory(col-> col.getValue().getValue().getStudentName());

        studentIDCol = new JFXTreeTableColumn<>("Student ID");
        studentIDCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(NUM_OF_COLS));
        studentIDCol.setStyle("-fx-font-size: 18px");
        studentIDCol.setResizable(false);
        studentIDCol.setCellValueFactory(col -> col.getValue().getValue().getStudentID().asObject());

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(NUM_OF_COLS));
        partNameCol.setStyle("-fx-font-size: 18px");
        partNameCol.setResizable(false);
        partNameCol.setCellValueFactory(col-> col.getValue().getValue().getPartName());

        serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
        serialNumberCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(NUM_OF_COLS));
        serialNumberCol.setStyle("-fx-font-size: 18px");
        serialNumberCol.setResizable(false);
        serialNumberCol.setCellValueFactory(col -> col.getValue().getValue().getSerialNumber());

        barcodeCol = new JFXTreeTableColumn<>("Barcode");
        barcodeCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(NUM_OF_COLS));
        barcodeCol.setStyle("-fx-font-size: 18px");
        barcodeCol.setResizable(false);
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());


        dueDateCol = new JFXTreeTableColumn<>("Date");
        dueDateCol.prefWidthProperty().bind(overdueTable.widthProperty().divide(NUM_OF_COLS));
        dueDateCol.setStyle("-fx-font-size: 18px");
        dueDateCol.setResizable(false);
        dueDateCol.setCellValueFactory(col -> col.getValue().getValue().getDueDate());
        dueDateCol.setCellFactory(col -> new TreeTableCell<OverdueTabTableRow, Date>(){
            @Override
            protected void updateItem(Date date, boolean empty){
                if (empty) {
                    setText("");
                } else {
                    setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(date));
                }
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
                URL myFxmlURL = ClassLoader.getSystemResource("fxml/ViewOverduePart.fxml");
                FXMLLoader loader = new FXMLLoader(myFxmlURL);
                Parent root = loader.load();
                Scene scene = new Scene(root, 400, 400);
                stage.setTitle("Part Information");
                stage.initOwner(overduePage.getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setScene(scene);
                int i = overdueTable.getSelectionModel().getSelectedIndex();
                OverdueTabTableRow item = new OverdueTabTableRow(
                        overdueTable.getTreeItem(i).getValue().getStudentName().get(),
                        overdueTable.getTreeItem(i).getValue().getStudentID().get(),
                        overdueTable.getTreeItem(i).getValue().getPartName().get(),
                        overdueTable.getTreeItem(i).getValue().getSerialNumber().get(),
                        overdueTable.getTreeItem(i).getValue().getBarcode().get(),
                        overdueTable.getTreeItem(i).getValue().getDueDate().get());
                ((OverduePopUpController) loader.getController()).populate(null, item);
                stage.getIcons().add(new Image("images/msoe.png"));
                stage.showAndWait();
            } catch (IOException e) {
                StudentCheckIn.logger.error("IOException while opening Overdue popup");
                e.printStackTrace();
            }

        populateTable();
    }

    public void importToExcel(){
        export.exportOverdue(list);

    }

    /**
     * This method populates the gui based off of the data in the Observable list
     */
    public void populateTable() {
        tableRows.clear();
        overdueTable.getColumns().clear();
        list.clear();
        database = Database.getInstance();
        list = database.getOverdue();

        for (OverdueItem overdueItem : list) {
            tableRows.add(new OverdueTabTableRow
                    (overdueItem.getName().get(),
                            overdueItem.getID().get(),
                            overdueItem.getPart().get(),
                            overdueItem.getSerialNumber().get(),
                            overdueItem.getBarcode().get(),
                            overdueItem.getDate().get()));
        }

        root = new RecursiveTreeItem<OverdueTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );

        searchInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search();
            }
        });

        overdueTable.getColumns().setAll(studentIDCol, studentNameCol, partNameCol, serialNumberCol, barcodeCol, dueDateCol);
        overdueTable.setRoot(root);
        overdueTable.setShowRoot(false);
    }

    @FXML
    private void search() {
        overdueTable.setPredicate(new Predicate<TreeItem<OverdueTabTableRow>>() {
            @Override
            public boolean test(TreeItem<OverdueTabTableRow> tableRow) {
                String input = searchInput.getText().toLowerCase();
                studentID = tableRow.getValue().getStudentID().getValue().toString();
                studentName = tableRow.getValue().getStudentName().getValue();
                partName = tableRow.getValue().getPartName().getValue();
                serialNumber = tableRow.getValue().getBarcode().getValue().toString();
                dueDate = tableRow.getValue().getDueDate().getValue().toString();


                return ((studentID != null && studentID.toLowerCase().contains(input))
                        || (partName != null && partName.toLowerCase().contains(input))
                        || (barcode!= null && barcode.toLowerCase().contains(input))
                        || (dueDate != null && dueDate.toLowerCase().contains(input))
                        || (studentName != null && studentName.toLowerCase().contains(input))
                        || (serialNumber != null && serialNumber.toLowerCase().contains(input)));


            }
        });
    }
}
