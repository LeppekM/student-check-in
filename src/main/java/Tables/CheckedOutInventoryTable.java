package Tables;

import Database.ObjectClasses.Checkout;
import HelperClasses.ExportToExcel;
import Controllers.TableScreensController;
import HelperClasses.TimeUtils;
import Popups.Popup;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Manages the table in Inventory screen that shows all parts currently checked out
 */
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
        barcodeCol.setCellFactory(barcodeColFormat());
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
        for (Checkout checkedOutPart : list) {
            rows.add(new CORow(checkedOutPart.getStudentName().getValue(),
                    checkedOutPart.getStudentEmail().get(), checkedOutPart.getPartName().getValue(),
                    checkedOutPart.getBarcode().getValue(), checkedOutPart.getSerialNumber().get(),
                    checkedOutPart.getPartID().get(), checkedOutPart.getCheckedOutDate().get(),
                    checkedOutPart.getDueDate().get(), checkedOutPart.getFee().getValue(),
                    checkedOutPart.getStudentID().get(), checkedOutPart.getCheckoutID().get()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> studentNameTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) studentNameCol;
        TreeTableColumn<TableRow, String> partNameTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) partNameCol;
        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;
        TreeTableColumn<TableRow, Date> checkedOutDateTemp =
                (TreeTableColumn<TableRow, Date>) (TreeTableColumn) checkOutDateCol;
        TreeTableColumn<TableRow, Date> dueDateTemp = (TreeTableColumn<TableRow, Date>) (TreeTableColumn) dueDateCol;

        table.getColumns().setAll(studentNameTemp, partNameTemp, barcodeTemp, checkedOutDateTemp, dueDateTemp);
        table.setRoot(root);
        // needs to be false so that it doesn't group all elements, effectively hiding them until you drop them down
        table.setShowRoot(false);
    }

    @Override
    protected boolean isMatch(TableRow value, String[] filters) {
        for (String filter : filters) {
            CORow val = (CORow) value;
            String input = filter.toLowerCase();
            String studentName = val.getStudentName().getValue();
            String partName = val.getPartName().getValue();
            String barcode = String.format("%06d", val.getBarcode().getValue());
            String checkedOutAt = val.getCheckedOutAt().getValue().toString();
            String dueDate = val.getDueDate().getValue().toString();
            if (!(studentName != null && studentName.toLowerCase().contains(input) ||
                    partName != null && partName.toLowerCase().contains(input) || barcode.toLowerCase().contains(input)
                    || checkedOutAt != null && checkedOutAt.toLowerCase().contains(input)
                    || dueDate != null && dueDate.toLowerCase().contains(input))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void popupRow(int index) {
        if (index != -1) {
            TreeItem<TableRow> item = table.getSelectionModel().getModelItem(index);
            // null if user clicks on empty row
            if (item != null) {
                CORow row = (CORow) item.getValue();

                Checkout checkout = new Checkout(row.getCheckoutID().get(), row.getStudentName().get(),
                        row.getStudentEmail().get(), row.getStudentRFID().get(), row.getPartName().get(),
                        row.getBarcode().getValue(), row.getSerialNumber().get(), row.getPartID().get(),
                        row.getCheckedOutAt().get(), row.getDueDate().get(), row.getFee().getValue());

                createCheckoutPopup(checkout);
            }
        }
    }

    public static void createCheckoutPopup(Checkout checkout) {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setTitle("View Checked Out Part");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        new Popup(root) {
            @Override
            public void populate() {
                add("Student Name: ", checkout.getStudentName().get(), false);
                add("Student Email: ", checkout.getStudentEmail().getValue(), false);
                add("Part Name: ", checkout.getPartName().getValue(), false);
                add("Barcode: ", "" + checkout.getBarcode().get(), false);
                add("Serial Number: ", checkout.getSerialNumber().get(), false);
                add("Part ID: ", "" + checkout.getPartID().get(), false);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                add("Out Date: ", dateFormat.format(checkout.getCheckedOutDate().getValue()), false);
                Label dueDateLabel = (Label) add("Due Date: ", dateFormat.format(checkout.getDueDate().getValue()), false).getChildren().get(0);
                TimeUtils timeUtils = new TimeUtils();
                try{
                    if (dateFormat.parse(timeUtils.getCurrentDateTimeStamp()).after(checkout.getDueDate().get())) {
                        dueDateLabel.setStyle(LABEL_STYLE + " -fx-text-fill: FIREBRICK;");
                    }
                } catch (ParseException ignored) { }

                add("Fee: ", "$" + new DecimalFormat("#,###,##0.00").format(Double.parseDouble(checkout.getFee().get()) / 100), false);

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

    public class CORow extends TableRow {
        private final StringProperty studentName;
        private final StringProperty studentEmail;
        private final StringProperty partName;
        private final StringProperty serialNumber;
        private final StringProperty fee;
        private final LongProperty barcode;
        private final IntegerProperty partID;
        private final IntegerProperty studentRFID;
        private final IntegerProperty checkoutID;
        private final ObjectProperty<Date> dueDate;
        private final ObjectProperty<Date> checkedOutAt;

        public CORow(String studentName, String studentEmail, String partName, long barcode, String serialNumber,
                     int partID, Date checkedOutAt, Date dueDate, String fee, int studentRFID, int checkoutID) {
            this.studentName = new SimpleStringProperty(studentName);
            this.studentEmail = new SimpleStringProperty(studentEmail);
            this.partName = new SimpleStringProperty(partName);
            this.barcode = new SimpleLongProperty(barcode);
            this.serialNumber = new SimpleStringProperty(serialNumber);
            this.partID = new SimpleIntegerProperty(partID);
            this.checkedOutAt = new SimpleObjectProperty<>(checkedOutAt);
            this.dueDate = new SimpleObjectProperty<>(dueDate);
            this.fee = new SimpleStringProperty(fee);
            this.studentRFID = new SimpleIntegerProperty(studentRFID);
            this.checkoutID = new SimpleIntegerProperty(checkoutID);
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

        public IntegerProperty getStudentRFID() {
            return studentRFID;
        }

        public IntegerProperty getCheckoutID() {
            return checkoutID;
        }
    }
}
