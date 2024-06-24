package InventoryController;

import Database.ObjectClasses.DBObject;
import Database.ObjectClasses.Part;
import Database.TotalTab;
import HelperClasses.ExportToExcel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static InventoryController.ControllerInventoryPage.database;


/**
 * This class manages backend functionality for the Tab labeled Total Inventory in the inventory page
 */
public class CompleteInventoryTable extends TSCTable {

    private JFXTreeTableColumn<CIRow, String> partNameCol, locationCol, serialNumberCol;

    private JFXTreeTableColumn<CIRow, Integer> partIDCol;

    private JFXTreeTableColumn<CIRow, Long> barcodeCol;

    public CompleteInventoryTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 5;
        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setStyle("-fx-text-fill: white");
        emptyTableLabel.setFont(new Font(18));
        table.setPlaceholder(emptyTableLabel);

        partNameCol = createNewCol("Part Name");
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());

        serialNumberCol = createNewCol("Serial Number");
        serialNumberCol.setCellValueFactory(col -> col.getValue().getValue().getSerialNumber());

        locationCol = createNewCol("Location");
        locationCol.setCellValueFactory(col -> col.getValue().getValue().getLocation());

        barcodeCol = createNewCol("Barcode");
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());

        partIDCol = createNewCol("Part ID");
        partIDCol.setCellValueFactory(col -> col.getValue().getValue().getPartID().asObject());

        rows = FXCollections.observableArrayList();

        // Click to select if unselected and unselect if selected
        table.setRowFactory(param -> {
            final TreeTableRow<TableRow> row = new TreeTableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getClickCount() == 2) {
                    viewPart(row.getIndex());
                } else {
                    final int index = row.getIndex();
                    if (index >= 0 && index < table.getCurrentItemsCount() && table.getSelectionModel().isSelected(index)) {
                        table.getSelectionModel().clearSelection();
                        event.consume();
                    }
                }
            });
            return row;
        });

        populateTable();
    }

    @Override
    public ObservableList<DBObject> getParts() {
        return null;
    }

    @Override
    public void export(ExportToExcel exportToExcel) {

    }

    @Override
    public void populateTable() {
        rows.clear();
        table.getColumns().clear();
        //stuff
        TotalTab totalTab = new TotalTab();
        ObservableList<Part> list = totalTab.getTotalTabParts();
        for (Part part : list) {
            rows.add(new CIRow(
                    part.getPartID(), part.getBarcode(), part.getSerialNumber(), part.getLocation(), part.getPartName(), part.getPrice()));
        }
        root = new RecursiveTreeItem<>(
                rows, RecursiveTreeObject::getChildren
        );
        TreeTableColumn<TableRow, String> partNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) partNameCol;
        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;
        TreeTableColumn<TableRow, String> serialNumberTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) serialNumberCol;
        TreeTableColumn<TableRow, String> locationTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) locationCol;
        TreeTableColumn<TableRow, Integer> partIDTemp = (TreeTableColumn<TableRow, Integer>) (TreeTableColumn) partIDCol;

        table.getColumns().setAll(partNameTemp, barcodeTemp, serialNumberTemp, locationTemp, partIDTemp);
        table.setRoot(root);
        table.setShowRoot(true);
    }

    @Override
    protected boolean isMatch(TableRow value, String filter) {
        CIRow val = (CIRow) value;
        String input = filter.toLowerCase();
        String partName = val.getPartName().getValue();
        String serialNumber = val.getSerialNumber().getValue();
        String loc = val.getLocation().getValue();
        String barcode = val.getBarcode().getValue().toString();
        String partID = val.getPartID().getValue().toString();

        return ((partName != null && partName.toLowerCase().contains(input))
                || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                || (loc != null && loc.toLowerCase().contains(input))
                || barcode.toLowerCase().contains(input)
                || partID.toLowerCase().contains(input));
    }

    public int getRowPartID(int row) {
        return partIDCol.getCellData(row);
    }

    public class CIRow extends TableRow {
        private StringProperty studentName, studentEmail, partName,  location,
                status, dueDate, className, professorName, serialNumber, fee;

        private IntegerProperty partID;
        private DoubleProperty price;
        private LongProperty barcode;

        private String actionType;

        private String action;

        public CIRow(int partID, long barcode, String serialNumber, String location, String partName, double price){
            this.partID = new SimpleIntegerProperty(partID);
            this.barcode = new SimpleLongProperty(barcode);
            this.serialNumber = new SimpleStringProperty(serialNumber);
            this.location = new SimpleStringProperty(location);
            this.partName = new SimpleStringProperty(partName);
            this.price = new SimpleDoubleProperty(price);
        }

        public DoubleProperty getPrice() {return price;}

        public void initFee(String fee) {
            this.fee = new SimpleStringProperty(fee);
        }

        public StringProperty getStudentName() {
            return studentName;
        }

        public StringProperty getStudentEmail() {
            return studentEmail;
        }

        public StringProperty getPartName() {
            return partName;
        }

        public LongProperty getBarcode() {
            return barcode;
        }

        public StringProperty getSerialNumber() {
            return serialNumber;
        }

        public StringProperty getLocation() {
            return location;
        }

        public StringProperty getStatus() {
            return status;
        }

        public String getAction() {
            return action;
        }

        public StringProperty getDueDate() {
            return dueDate;
        }

        public StringProperty getFee() {
            return fee;
        }

        public IntegerProperty getPartID() {
            return partID;
        }

        public String getActionType() {
            return actionType;
        }

        public StringProperty getClassName() {
            return className;
        }

        public StringProperty getProfessorName() {
            return professorName;
        }
    }
}
