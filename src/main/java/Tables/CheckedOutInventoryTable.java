package Tables;

import Database.ObjectClasses.Checkout;
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
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckedOutInventoryTable extends TSCTable {

    private JFXTreeTableColumn<CORow, String> studentNameCol, partNameCol;
    private JFXTreeTableColumn<CORow, Long> barcodeCol;
    private JFXTreeTableColumn<CORow, Date> checkOutDateCol, dueDateCol;

    public CheckedOutInventoryTable(TableScreensController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        NUM_COLS = 5;
        table.setPlaceholder(getEmptyTableLabel());

        studentNameCol = createNewCol("Student");
        studentNameCol.setCellValueFactory(col -> col.getValue().getValue().getStudentName());
        partNameCol = createNewCol("Part Name");
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());
        barcodeCol = createNewCol("Barcode", 0.1);
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());
        checkOutDateCol = createNewCol("Check Out Date", 0.25);
        checkOutDateCol.setCellValueFactory(col -> col.getValue().getValue().getCheckedOutAt());
        checkOutDateCol.setCellFactory(dateColFormat());
        dueDateCol = createNewCol("Due Date", 0.25);
        dueDateCol.setCellValueFactory(col -> col.getValue().getValue().getDueDate());
        dueDateCol.setCellFactory(dateColFormat());

        rows = FXCollections.observableArrayList();

        setDoubleClickBehavior();

    }

    @Override
    public void export(ExportToExcel exportToExcel) {
        ObservableList<Checkout> list = database.getAllCurrentlyCheckedOut();
        exportToExcel.exportCheckedOut(list);
    }

    @Override
    public void populateTable() {
        // clear previous data
        rows.clear();
        table.getColumns().clear();
        // get and add all rows
        ObservableList<Checkout> list = database.getAllCurrentlyCheckedOut();
        for (Checkout checkedOutItems : list) {
            rows.add(new CORow(checkedOutItems.getStudentName().getValue(),
                    checkedOutItems.getStudentEmail().get(), checkedOutItems.getPartName().getValue(),
                    checkedOutItems.getBarcode().getValue(), checkedOutItems.getSerialNumber().get(),
                    checkedOutItems.getPartID().get(), checkedOutItems.getCheckedOutDate().get(),
                    checkedOutItems.getDueDate().get(), checkedOutItems.getFee().getValue()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> studentNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) studentNameCol;
        TreeTableColumn<TableRow, String> partNameTemp = (TreeTableColumn<TableRow, String>) (TreeTableColumn) partNameCol;
        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;
        TreeTableColumn<TableRow, Date> checkedOutDateTemp = (TreeTableColumn<TableRow, Date>) (TreeTableColumn) checkOutDateCol;
        TreeTableColumn<TableRow, Date> dueDateTemp = (TreeTableColumn<TableRow, Date>) (TreeTableColumn) dueDateCol;

        table.getColumns().setAll(studentNameTemp, partNameTemp, barcodeTemp, checkedOutDateTemp, dueDateTemp);
        table.setRoot(root);
        // needs to be false so that it doesn't group all elements, effectively hiding them until you drop them down
        table.setShowRoot(false);
    }

    @Override
    protected boolean isMatch(TableRow value, String filter) {
        CORow val = (CORow) value;
        String input = filter.toLowerCase();
        String studentName = val.getStudentName().getValue();
        String partName = val.getPartName().getValue();
        String barcode = val.getBarcode().getValue().toString();
        String checkedOutAt = val.getCheckedOutAt().getValue().toString();
        String dueDate = val.getDueDate().getValue().toString();

        return ((studentName != null && studentName.toLowerCase().contains(input))
                || (partName != null && partName.toLowerCase().contains(input))
                || (barcode != null && barcode.toLowerCase().contains(input))
                || (checkedOutAt != null && checkedOutAt.toLowerCase().contains(input))
                || (dueDate != null && dueDate.toLowerCase().contains(input)));
    }

    @Override
    protected void popupRow(int index) {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setTitle("View Checked Out Part");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        if (index != -1) {
            TreeItem item = table.getSelectionModel().getModelItem(index);
            // null if user clicks on empty row
            if (item != null) {
                CORow row = ((CORow) item.getValue());

                Popup checkedOutPopup = new Popup(root) {
                    @Override
                    public void populate() {
                        add("Student Name: ", row.getStudentName().get(), false);
                        add("Student Email: ", row.getStudentEmail().getValue(), false);
                        add("Part Name: ", row.getPartName().getValue(), false);
                        add("Barcode: ", "" + row.getBarcode().get(), false);
                        add("Serial Number: ", row.getSerialNumber().get(), false);
                        add("Part ID: ", "" + row.getPartID().get(), false);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                        add("Checked Out Date: ", dateFormat.format(row.getCheckedOutAt().getValue()), false);
                        Label dueDateLabel = (Label) add("Due Date: ", dateFormat.format(row.getDueDate().getValue()), false).getChildren().get(0);
                        if (database.isOverdue(String.valueOf(row.getDueDate().get()))) {
                            dueDateLabel.setStyle(LABEL_STYLE + " -fx-text-fill: FIREBRICK");
                        }
                        add("Fee: ", "$" + new DecimalFormat("#,###,##0.00").format(Double.parseDouble(row.getFee().get()) / 100), false);

                        submitButton.setText("Close");
                    }

                    @Override
                    public void submit() {
                        stage.close();
                    }
                };

                stage.getIcons().add(new Image("images/msoe.png"));
                stage.show();
            }
        }
    }

    public class CORow extends TableRow {
        private final StringProperty studentName;
        private final StringProperty studentEmail;
        private final StringProperty partName;
        private final StringProperty serialNumber;
        private final StringProperty fee;
        private final LongProperty barcode;
        private final IntegerProperty partID;
        private final ObjectProperty<Date> dueDate;
        private final ObjectProperty<Date> checkedOutAt;

        public CORow(String studentName, String studentEmail, String partName, long barcode, String serialNumber,
                     int partID, Date checkedOutAt, Date dueDate, String fee) {
            this.studentName = new SimpleStringProperty(studentName);
            this.studentEmail = new SimpleStringProperty(studentEmail);
            this.partName = new SimpleStringProperty(partName);
            this.barcode = new SimpleLongProperty(barcode);
            this.serialNumber = new SimpleStringProperty(serialNumber);
            this.partID = new SimpleIntegerProperty(partID);
            this.checkedOutAt = new SimpleObjectProperty<>(checkedOutAt);
            this.dueDate = new SimpleObjectProperty<>(dueDate);
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

        public IntegerProperty getPartID() {
            return partID;
        }

        public ObjectProperty<Date> getCheckedOutAt() {
            return checkedOutAt;
        }

        public ObjectProperty<Date> getDueDate() {
            return dueDate;
        }

        public StringProperty getFee() {
            return fee;
        }
    }
}
