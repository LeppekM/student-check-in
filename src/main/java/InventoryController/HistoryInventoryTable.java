package InventoryController;

import Database.HistoryParts;
import Database.ObjectClasses.Checkout;
import Database.ObjectClasses.Part;
import HelperClasses.ExportToExcel;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;

import static InventoryController.ControllerInventoryPage.database;

public class HistoryInventoryTable extends TSCTable {

    private JFXTreeTableColumn<HIRow, String> studentNameCol, partNameCol, actionCol;
    private JFXTreeTableColumn<HIRow, Date> dateCol;
    private JFXTreeTableColumn<HIRow, Long> barcodeCol;

    public HistoryInventoryTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 5;
        table.setPlaceholder(getEmptyTableLabel());

        studentNameCol = createNewCol("Student");
        studentNameCol.setCellValueFactory(col -> col.getValue().getValue().getStudentName());
        partNameCol = createNewCol("Part");
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());
        barcodeCol = createNewCol("Barcode");
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());
        actionCol = createNewCol("Action");
        actionCol.setCellValueFactory(col -> col.getValue().getValue().getAction());
        dateCol = createNewCol("Date");
        dateCol.setCellValueFactory(col -> col.getValue().getValue().getDate());

        setDoubleClickBehavior();
    }

    @Override
    public void export(ExportToExcel exportToExcel) {
        ObservableList<Checkout> list = database.getAllCheckoutHistory();
        exportToExcel.exportTransactionHistory(list, false);
    }

    @Override
    public void populateTable() {
        // clear previous data
        rows.clear();
        table.getColumns().clear();
        // get and add all rows
        ObservableList<Checkout> list = database.getAllCheckoutHistory();
        rows = FXCollections.observableArrayList();

        for (Checkout c : list) {
            rows.add(new HIRow(c.getStudentName().get(), c.getStudentEmail().get(), c.getPartName().get(),
                    c.getBarcode().get(), c.getAction().get(), c.getDate().get()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> studentNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) studentNameCol;
        TreeTableColumn<TableRow, String> partNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) partNameCol;
        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;
        TreeTableColumn<TableRow, String> actionTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) actionCol;
        TreeTableColumn<TableRow, String> dateTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) dateCol;

        table.getColumns().setAll(studentNameTemp, partNameTemp, barcodeTemp, actionTemp, dateTemp);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    @Override
    protected boolean isMatch(TableRow value, String filter) {
        HIRow val = (HIRow) value;
        String input = filter.toLowerCase();
        String student = val.getStudentName().getValue();
        String partName = val.getPartName().getValue();
        String serialNumber = val.getBarcode().getValue().toString();
        String action = val.getAction().getValue();
        String date = val.getDate().getValue().toString().toLowerCase();

        return ((student != null && student.toLowerCase().contains(input))
                || (partName != null && partName.toLowerCase().contains(input))
                || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                || (action != null && action.toLowerCase().contains(input))
                || (date != null && date.toLowerCase().contains(input)));
    }

    @Override
    protected void popupRow(int index) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewHistoryPart.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("View Transaction");
            stage.initOwner(scene.getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            if (index != -1) {
                TreeItem item = table.getSelectionModel().getModelItem(index);
                // null if user clicks on empty row
                if (item != null) {
                    HIRow row = ((HIRow) item.getValue());
                    ((ControllerViewHistoryPart) loader.getController()).populate(row);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class HIRow extends TableRow {
        private StringProperty studentName;
        private StringProperty studentEmail;
        private StringProperty partName;
        private LongProperty barcode;

        private StringProperty action;
        private ObjectProperty<Date> date;

        public HIRow(String studentName, String studentEmail, String partName, long barcode,
                                  String action, Date date) {
            this.studentName = new SimpleStringProperty(studentName);
            this.studentEmail = new SimpleStringProperty(studentEmail);
            this.partName = new SimpleStringProperty(partName);
            this.barcode = new SimpleLongProperty(barcode);
            this.action = new SimpleStringProperty(action);
            this.date = new SimpleObjectProperty<Date>(date) {
            };
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

        public StringProperty getAction() {
            return action;
        }

        public ObjectProperty<Date> getDate() {
            return date;
        }
    }
}
