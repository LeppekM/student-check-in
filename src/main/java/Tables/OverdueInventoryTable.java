package Tables;

import Database.ObjectClasses.Student;
import Database.OverdueItem;
import HelperClasses.ExportToExcel;
import Controllers.TableScreensController;
import Popups.Popup;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * In charge of displaying parts that are currently Overdue in the Overdue tab of the Inventory screen
 */
public class OverdueInventoryTable extends TSCTable {

    private JFXTreeTableColumn<OIRow, Integer> studentIDCol;
    private JFXTreeTableColumn<OIRow, String> studentNameCol, partNameCol, serialNumberCol;
    private JFXTreeTableColumn<OIRow, Long> barcodeCol;
    private JFXTreeTableColumn<OIRow, Date> dueDateCol;

    public OverdueInventoryTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 6;
        table.setPlaceholder(getEmptyTableLabel());

        studentIDCol = createNewCol("Student ID", 0.1);
        studentIDCol.setCellValueFactory(col -> col.getValue().getValue().getStudentID().asObject());
        studentNameCol = createNewCol("Student Name", 0.2);
        studentNameCol.setCellValueFactory(col -> col.getValue().getValue().getStudentName());
        partNameCol = createNewCol("Part Name", 0.2);
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());
        serialNumberCol = createNewCol("Serial Number", 0.15);
        serialNumberCol.setCellValueFactory(col -> col.getValue().getValue().getSerialNumber());
        barcodeCol = createNewCol("Barcode", 0.1);
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());
        barcodeCol.setCellFactory(barcodeColFormat());
        dueDateCol = createNewCol("Due Date", 0.25);
        dueDateCol.setCellValueFactory(col -> col.getValue().getValue().getDueDate());
        dueDateCol.setCellFactory(dateColFormat());

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
        ObservableList<OverdueItem> list = database.getOverdue();
        for (OverdueItem overdueItem : list) {
            rows.add(new OIRow(overdueItem.getStudentName().get(), overdueItem.getID().get(),
                            overdueItem.getPartName().get(), overdueItem.getSerialNumber().get(),
                            overdueItem.getBarcode().get(), overdueItem.getDueDate().get(),
                            overdueItem.getCheckID().get()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> studentNameTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) studentNameCol;
        TreeTableColumn<TableRow, String> partNameTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) partNameCol;
        TreeTableColumn<TableRow, String> serialNumberTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) serialNumberCol;
        TreeTableColumn<TableRow, Date> dueDateTemp = (TreeTableColumn<TableRow, Date>) (TreeTableColumn) dueDateCol;
        TreeTableColumn<TableRow, Integer> studentIDTemp =
                (TreeTableColumn<TableRow, Integer>) (TreeTableColumn) studentIDCol;
        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;

        table.getColumns().setAll(studentIDTemp, studentNameTemp, partNameTemp, serialNumberTemp,
                barcodeTemp, dueDateTemp);
        table.setRoot(root);
        // needs to be false so that it doesn't group all elements, effectively hiding them until you drop them down
        table.setShowRoot(false);
    }

    @Override
    protected boolean isMatch(TableRow value, String[] filters) {
        for (String filter : filters) {
            OIRow val = (OIRow) value;
            String input = filter.toLowerCase();
            String studentID = val.getStudentID().getValue().toString();
            String studentName = val.getStudentName().getValue();
            String partName = val.getPartName().getValue();
            String serialNumber = val.getSerialNumber().getValue();
            String dueDate = val.getDueDate().getValue().toString();
            String barcode = val.getBarcode().getValue().toString();
            if (!(studentID.toLowerCase().contains(input) || partName != null && partName.toLowerCase().contains(input)
                    || barcode.toLowerCase().contains(input) || dueDate != null
                    && dueDate.toLowerCase().contains(input) || studentName != null
                    && studentName.toLowerCase().contains(input) || serialNumber.toLowerCase().contains(input))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void popupRow(int index) {
        if (index != -1) {
            TreeItem<TableRow> item = table.getSelectionModel().getModelItem(index);
            if (item != null) {
                OIRow row = (OIRow) item.getValue();
                OverdueItem overdueItem = database.selectStudent(row.getStudentID().get(), null)
                        .getOverdueItem(row.getCheckInID().get());
                createOverduePartPopup(overdueItem);
            }
        }
        populateTable();
    }

    public static void createOverduePartPopup(OverdueItem row) {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setTitle("Part Information");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);

        new Popup(root) {
            @Override
            public void populate() {
                add("Student Name: ", row.getStudentName().getValue(), false);
                add("Student ID: ", "" + row.getID().get(), false);
                Student student = database.selectStudent(row.getID().get(), null);
                add("Student Email: ", student.getEmail(), false);
                add("Part Name: ", row.getPartName().getValue(), false);
                add("Barcode: ", "" + row.getBarcode().get(), false);
                add("Due Date: ", new SimpleDateFormat("dd MMM yyyy hh:mm:ss a")
                        .format(row.getDueDate().getValue()), false);

                submitButton.setText("Close");
            }

            @Override
            public void submit() {
                stage.close();
            }
        };

        stage.getIcons().add(new Image("images/msoe.png"));
        stage.showAndWait();
    }

    public class OIRow extends TableRow {

        private final StringProperty studentName, partName, serialNumber, checkInID;
        private final LongProperty barcode;
        private final IntegerProperty studentID;
        private final ObjectProperty<Date> dueDate;

        public OIRow(String studentName, int studentID, String partName, String serialNumber, long barcode,
                                  Date dueDate, String checkInID) {
            this.studentID = new SimpleIntegerProperty(studentID);
            this.partName = new SimpleStringProperty(partName);
            this.studentName = new SimpleStringProperty(studentName);
            this.barcode = new SimpleLongProperty(barcode);
            this.dueDate = new SimpleObjectProperty<>(dueDate);
            this.serialNumber = new SimpleStringProperty(serialNumber);
            this.checkInID = new SimpleStringProperty(checkInID);
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

        public StringProperty getCheckInID(){
            return checkInID;
        }
    }
}
