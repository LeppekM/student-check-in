package InventoryController;

import Database.Database;
import Database.OverdueItem;
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
import java.net.URL;
import java.util.Date;

public class OverdueInventoryTable extends TSCTable {

    private JFXTreeTableColumn<OIRow, Integer> studentIDCol;
    private JFXTreeTableColumn<OIRow, String> studentNameCol, partNameCol, serialNumberCol;
    private JFXTreeTableColumn<OIRow, Long> barcodeCol;
    private JFXTreeTableColumn<OIRow, Date> dueDateCol;
    private final Database database = Database.getInstance();

    public OverdueInventoryTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 6;
        table.setPlaceholder(getEmptyTableLabel());

        studentIDCol = createNewCol("Student ID");
        studentIDCol.setCellValueFactory(col -> col.getValue().getValue().getStudentID().asObject());
        studentNameCol = createNewCol("Student Name");
        studentNameCol.setCellValueFactory(col -> col.getValue().getValue().getStudentName());
        partNameCol = createNewCol("Part Name");
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());
        serialNumberCol = createNewCol("Serial Number");
        serialNumberCol.setCellValueFactory(col -> col.getValue().getValue().getSerialNumber());
        barcodeCol = createNewCol("Barcode");
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());
        dueDateCol = createNewCol("Due Date");
        dueDateCol.setCellValueFactory(col -> col.getValue().getValue().getDueDate());

        rows = FXCollections.observableArrayList();

        setDoubleClickBehavior();
    }

    @Override
    public void export(ExportToExcel exportToExcel) {
        exportToExcel.exportOverdue(database.getOverdue());
    }

    @Override
    public void populateTable() {
        // clear previous data
        rows.clear();
        table.getColumns().clear();
        // get and add all rows
        // todo add cache
        ObservableList<OverdueItem> list = database.getOverdue();
        for (OverdueItem overdueItem : list) {
            rows.add(new OIRow(overdueItem.getName().get(),
                            overdueItem.getID().get(),
                            overdueItem.getPart().get(),
                            overdueItem.getSerialNumber().get(),
                            overdueItem.getBarcode().get(),
                            overdueItem.getDate().get()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> studentNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) studentNameCol;
        TreeTableColumn<TableRow, String> partNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) partNameCol;
        TreeTableColumn<TableRow, String> serialNumberTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) serialNumberCol;
        TreeTableColumn<TableRow, Date> dueDateTemp = (TreeTableColumn<TableRow, Date>) (TreeTableColumn) dueDateCol;
        TreeTableColumn<TableRow, Integer> studentIDTemp = (TreeTableColumn<TableRow, Integer>) (TreeTableColumn) studentIDCol;
        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;

        table.getColumns().setAll(studentIDTemp, studentNameTemp, partNameTemp, serialNumberTemp, barcodeTemp, dueDateTemp);
        table.setRoot(root);
        // needs to be false so that it doesn't group all elements, effectively hiding them until you drop them down
        table.setShowRoot(false);
    }

    @Override
    protected boolean isMatch(TableRow value, String filter) {
        OIRow val = (OIRow) value;
        String input = filter.toLowerCase();
        String studentID = val.getStudentID().getValue().toString();
        String studentName = val.getStudentName().getValue();
        String partName = val.getPartName().getValue();
        String serialNumber = val.getBarcode().getValue().toString();
        String dueDate = val.getDueDate().getValue().toString();
        String barcode = val.getBarcode().getValue().toString();


        return ((studentID != null && studentID.toLowerCase().contains(input))
                || (partName != null && partName.toLowerCase().contains(input))
                || (barcode!= null && barcode.toLowerCase().contains(input))
                || (dueDate != null && dueDate.toLowerCase().contains(input))
                || (studentName != null && studentName.toLowerCase().contains(input))
                || (serialNumber != null && serialNumber.toLowerCase().contains(input)));
    }

    @Override
    protected void popupRow(int index) {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/ViewOverduePart.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Part Information");
            stage.initOwner(scene.getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            if (index != -1) {
                TreeItem item = table.getSelectionModel().getModelItem(index);
                if (item != null) {
                    OverdueInventoryTable.OIRow row = (OverdueInventoryTable.OIRow) item.getValue();
                    ((OverduePopUpController) loader.getController()).populate(null,
                            new OIRow(row.getStudentName().get(), row.getStudentID().get(),
                                    row.getPartName().get(), row.getSerialNumber().get(), row.getBarcode().get(),
                                    row.getDueDate().get()));
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.showAndWait();
                }
            }
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException while opening Overdue popup");
            e.printStackTrace();
        }

        populateTable();
    }

    public class OIRow extends TableRow {

        private StringProperty studentName, partName, fee, serialNumber;
        private LongProperty barcode;
        private IntegerProperty studentID;
        private ObjectProperty<Date> dueDate;

        public OIRow(String studentName, int studentID, String partName, String serialNumber, long barcode,
                                  Date dueDate) {
            this.studentID = new SimpleIntegerProperty(studentID);
            this.partName = new SimpleStringProperty(partName);
            this.studentName = new SimpleStringProperty(studentName);
            this.barcode = new SimpleLongProperty(barcode);
            this.dueDate = new SimpleObjectProperty<Date>(dueDate);
            this.serialNumber = new SimpleStringProperty(serialNumber);
        }

        public IntegerProperty getStudentID() {
            return studentID;
        }

        public StringProperty getPartName() {
            return partName;
        }

        public LongProperty getBarcode(){
            return barcode;
        }

        public StringProperty getStudentName(){
            return studentName;
        }

        public ObjectProperty<Date> getDueDate() {
            return dueDate;
        }

        public StringProperty getSerialNumber(){
            return serialNumber;
        }
    }
}
