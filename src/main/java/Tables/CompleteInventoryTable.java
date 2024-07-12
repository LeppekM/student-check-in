package Tables;

import Database.ObjectClasses.Checkout;
import Database.ObjectClasses.Part;
import Database.ObjectClasses.Student;
import HelperClasses.ExportToExcel;
import Controllers.TableScreensController;
import HelperClasses.StageUtils;
import Popups.Popup;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static Controllers.CheckOutController.BARCODE_STRING_LENGTH;


/**
 * This class manages backend functionality for the Tab labeled Total Inventory in the inventory page
 * This page is in charge of adding/editing/deleting parts
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
        table.setPlaceholder(getEmptyTableLabel());

        partNameCol = createNewCol("Part Name", 0.3);
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());
        serialNumberCol = createNewCol("Serial Number", 0.15);
        serialNumberCol.setCellValueFactory(col -> col.getValue().getValue().getSerialNumber());
        locationCol = createNewCol("Location", 0.25);
        locationCol.setCellValueFactory(col -> col.getValue().getValue().getLocation());
        barcodeCol = createNewCol("Barcode", 0.15);
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());
        barcodeCol.setCellFactory(barcodeColFormat());
        partIDCol = createNewCol("Part ID", 0.15);
        partIDCol.setCellValueFactory(col -> col.getValue().getValue().getPartID().asObject());

        rows = FXCollections.observableArrayList();

        setDoubleClickBehavior();
    }

    @Override
    public void export(ExportToExcel exportToExcel) {
        ObservableList<Part> list = database.getAllParts();
        exportToExcel.exportPartList(list);
    }

    @Override
    public void populateTable() {
        // clear previous data
        rows.clear();
        table.getColumns().clear();
        // get and add all rows
        // todo add cache
        ObservableList<Part> list = database.getAllParts();
        for (Part part : list) {
            rows.add(new CIRow(part.getPartID(), part.getBarcode(), part.getSerialNumber(), part.getLocation(),
                    part.getPartName(), part.getPrice()));
        }
        root = new RecursiveTreeItem<>(rows, RecursiveTreeObject::getChildren);

        // unfortunately, this cast needs to be here to add the cols to the table
        TreeTableColumn<TableRow, String> partNameTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) partNameCol;
        TreeTableColumn<TableRow, Long> barcodeTemp = (TreeTableColumn<TableRow, Long>) (TreeTableColumn) barcodeCol;
        TreeTableColumn<TableRow, String> serialNumberTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) serialNumberCol;
        TreeTableColumn<TableRow, String> locationTemp =
                (TreeTableColumn<TableRow, String>) (TreeTableColumn) locationCol;
        TreeTableColumn<TableRow, Integer> partIDTemp =
                (TreeTableColumn<TableRow, Integer>) (TreeTableColumn) partIDCol;

        table.getColumns().setAll(partNameTemp, barcodeTemp, serialNumberTemp, locationTemp, partIDTemp);
        table.setRoot(root);
        // needs to be false so that it doesn't group all elements, effectively hiding them until you drop them down
        table.setShowRoot(false);
        controller.repopulatedTableSearch();
    }

    @Override
    protected boolean isMatch(TableRow value, String[] filters) {
        for (String filter : filters) {
            CIRow val = (CIRow) value;
            String input = filter.toLowerCase();
            String partName = val.getPartName().getValue();
            String serialNumber = val.getSerialNumber().getValue();
            String loc = val.getLocation().getValue();
            String barcode = String.format("%06d", val.getBarcode().getValue());
            String partID = val.getPartID().getValue().toString();
            if (!(partName != null && partName.toLowerCase().contains(input)
                    || serialNumber != null && serialNumber.toLowerCase().contains(input)
                    || loc != null && loc.toLowerCase().contains(input)
                    || barcode.toLowerCase().contains(input) || partID.toLowerCase().contains(input))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void popupRow(int index) {
        String titleStyle = "-fx-font-weight: bolder; -fx-font-size: 20px;";
        int height = 40;
        Stage stage = new Stage();
        HBox root = new HBox();

        VBox vBox1 = new VBox();
        vBox1.setAlignment(Pos.TOP_CENTER);
        Label vLabel1 = new Label("Part Info");
        vLabel1.setStyle(titleStyle);
        vLabel1.setMinHeight(height);
        vBox1.getChildren().add(vLabel1);
        root.getChildren().add(vBox1);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        root.getChildren().add(separator);

        VBox vBox2 = new VBox();
        vBox2.setAlignment(Pos.TOP_CENTER);
        Label vLabel2 = new Label("Last Transaction Info");
        vLabel2.setStyle(titleStyle);
        vLabel2.setMinHeight(height);
        vBox2.getChildren().add(vLabel2);
        root.getChildren().add(vBox2);

        Scene scene = new Scene(root);
        stage.setTitle("View Part");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        if (index != -1) {
            TreeItem<TableRow> item = table.getSelectionModel().getModelItem(index);
            // null if user clicks on empty row
            if (item != null) {
                CompleteInventoryTable.CIRow row = ((CompleteInventoryTable.CIRow) item.getValue());
                new Popup(vBox1) {
                    @Override
                    public void populate() {
                        add("Part Name: ", row.getPartName().get(), false);
                        add("Barcode: ", "" + row.getBarcode().get(), false);
                        add("Serial Number: ", row.getSerialNumber().get(), false);
                        add("Part ID: ", "" + row.getPartID().get(), false);

                        submitButton.setVisible(false);
                    }

                    @Override
                    public void submit() {
                        // no button in this half of box
                    }
                };

                new Popup(vBox2) {
                    @Override
                    public void populate() {
                        Student student = database.getStudentToLastCheckout(row.getPartID().get());

                        if (student != null) {
                            Checkout checkoutObject = database.getLastCheckoutOf(row.getPartID().get());
                            String type = checkoutObject.getCheckedInDate() == null ? "Checked Out" : "Check In";

                            add("Student Name: ", student.getName(), false);
                            add("Student Email: ", student.getEmail(), false);

                            boolean isOverdue = false;
                            if (checkoutObject.getCheckedOutDate().get() != null) {
                                String className = checkoutObject.getCourse() == null ? null : checkoutObject.getCourse().get();
                                if (className != null && !className.isEmpty()) {
                                    add("Class Name: ", className, false);
                                    add("Professor Name: ", checkoutObject.getProfessor().get(), false);
                                }
                                Date current = new Date();
                                isOverdue = !checkoutObject.getDueDate().get().after(current) &&
                                        checkoutObject.getCheckedInDate() == null;
                                if (isOverdue) {
                                    DecimalFormat df = new DecimalFormat("#,###,##0.00");
                                    add("Fee: ", "$" + df.format(row.getPrice().get() / 100), false);
                                }
                            }
                            Label label;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                            if (type.equals("Check In")){
                                add(type + ": ", dateFormat.format(checkoutObject.getCheckedInDate().getValue()),
                                        false);
                            } else {
                                add(type + ": ", dateFormat.format(checkoutObject.getCheckedOutDate().getValue()),
                                        false);
                            }
                            label = (Label) add("Due Date: ", dateFormat.format(checkoutObject.getDueDate()
                                    .getValue()), false).getChildren().get(0);
                            if (isOverdue) {
                                label.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                            }
                        } else {
                            Label label = new Label("No Previous Checkout History Associated with this Part");
                            label.setStyle("-fx-font-size: 16px;");
                            HBox hBox = new HBox(label);
                            addHBox(hBox);
                        }
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

    public int getRowPartID(int row) {
        return partIDCol.getCellData(row);
    }

    public void addPart() {
        VBox root = new VBox();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setTitle("Add Part(s)");
        stage.initOwner(scene.getWindow());

        new Popup(root) {
            private JFXTextField nameField, serialField, manufacturerField, quantityField, barcodeField, priceField,
                    locationField, suffixField;
            private JFXCheckBox differentBarcodes;
            private JFXComboBox<String> vendorField;
            private HBox barcodeCheckBoxBox;
            private final ArrayList<String> vendors = database.getVendorList();


            @Override
            public void populate() {
                nameField = (JFXTextField) add("Part Name: ", "", true).getChildren().get(1);
                quantityField = (JFXTextField) add("Quantity: ", "", true).getChildren().get(1);
                quantityField.setText("1");
                barcodeField = (JFXTextField) add("Barcode: ", "", true).getChildren().get(1);

                barcodeCheckBoxBox = new HBox();
                barcodeCheckBoxBox.setAlignment(Pos.CENTER);
                Label barcodeLabel = new Label();
                barcodeCheckBoxBox.getChildren().add(barcodeLabel);
                differentBarcodes = new JFXCheckBox("This part type uses different barcodes");
                barcodeCheckBoxBox.getChildren().add(differentBarcodes);
                addHBox(barcodeCheckBoxBox);

                HBox serialBox = addSerialBox("", "");
                serialField = (JFXTextField) serialBox.getChildren().get(1);
                suffixField = (JFXTextField) serialBox.getChildren().get(3);

                locationField = (JFXTextField) add("Location: ", "", true).getChildren().get(1);
                priceField = (JFXTextField) add("Price: ", "", true).getChildren().get(1);
                manufacturerField = (JFXTextField) add("Manufacturer: ", "", true).getChildren().get(1);
                manufacturerField.setText("MSOE");
                vendorField = addVendorField();

                HBox spacer = new HBox();
                spacer.setMinHeight(HEIGHT);
                addHBox(spacer);

                stageUtils.acceptIntegerOnly(barcodeField);
                stageUtils.setMaxTextLength(barcodeField, BARCODE_STRING_LENGTH);
                stageUtils.acceptIntegerOnly(quantityField);
                stageUtils.acceptIntegerOnly(priceField);
                stageUtils.acceptIntegerOnly(serialField);

                differentBarcodes.selectedProperty().addListener((observable, oldValue, newValue) ->
                        barcodeField.setDisable(newValue));

                quantityField.textProperty().addListener((observable, oldValue, newValue) ->
                        showBarcodesCheckbox(!newValue.isEmpty() && Integer.parseInt(newValue) > 1));
                showBarcodesCheckbox(false);


            }

            private void showBarcodesCheckbox(boolean bool) {
                if (bool) {
                    barcodeCheckBoxBox.setVisible(true);
                    barcodeCheckBoxBox.setMinHeight(HEIGHT);
                    barcodeCheckBoxBox.setMaxHeight(HEIGHT);
                } else {
                    barcodeCheckBoxBox.setVisible(false);
                    barcodeCheckBoxBox.setMinHeight(0);
                    barcodeCheckBoxBox.setMaxHeight(0);
                    differentBarcodes.setSelected(false);
                    barcodeField.setDisable(false);
                }
            }

            @Override
            public void submit() {
                database.initWorker(worker);
                if (validateFieldsNotEmpty() && validateQuantityField() && validatePriceField()) {
                    if(!vendorExists(getVendorName())){
                        database.createNewVendor(getVendorName(), vendorInformation());
                    }
                    long barcode = barcodeField.getText().isEmpty() ? database.getMaxPartID() :
                            Long.parseLong(barcodeField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    String partName = nameField.getText();

                    if (quantity > 1) {
                        if (differentBarcodes.isSelected()) {
                            if (database.partNameExists(partName)) {
                                // check that entered/starting serial number isn't the same as an existing for part name
                                if (checkExistingSNAgainstGenerated(quantity, partName)) {
                                    addManyPartsWithDifferentBarcodes(quantity, partName);
                                    partAddedSuccess(quantity);
                                    stage.close();
                                    populateTable();
                                }
                            } else {
                                addManyPartsWithDifferentBarcodes(quantity, partName);
                                partAddedSuccess(quantity);
                                stage.close();
                                populateTable();
                            }
                        } else {
                            List<String> barcodesWithSamePartName = database.getAllBarcodesForPartName(partName)
                                    .stream().distinct().collect(Collectors.toList());
                            if (barcodesWithSamePartName.size() == 1 && Long.parseLong(
                                    barcodesWithSamePartName.get(0)) == barcode
                                    || barcodesWithSamePartName.isEmpty()) {
                                // adds many with same barcodes, and serial num & other fields don't matter
                                for (int i = 0; i < quantity; i++) {
                                    database.addPart(new Part(partName, serialField.getText(),
                                            manufacturerField.getText(), Double.parseDouble(priceField.getText()),
                                            getVendorName(), locationField.getText(), barcode));
                                }
                                partAddedSuccess(quantity);
                                stage.close();
                                populateTable();
                            } else {
                                stageUtils.errorAlert("This part name already has different barcodes for each part");
                            }
                        }
                    } else if (quantity == 1) {
                        if (database.partNameExists(partName)) {
                            ArrayList<Long> barcodesSamePartName = new ArrayList<>();
                            for (String str : database.getAllBarcodesForPartName(partName)) {
                                barcodesSamePartName.add(Long.parseLong(str));
                            }
                            if (barcodesSamePartName.stream().distinct().count() == 1
                                    && barcodesSamePartName.get(0).equals(barcode)) {
                                // Same names, same barcodes, serial does not need checked
                                database.addPart(new Part(partName, serialField.getText(),
                                        manufacturerField.getText(), Double.parseDouble(priceField.getText()),
                                        getVendorName(), locationField.getText(), barcode));
                                partAddedSuccess(1);
                                stage.close();
                                populateTable();
                            } else {
                                // Same names, different barcodes, serial number should be different
                                if (database.getAllSerialNumbersForPartName(partName).contains(serialField.getText())) {
                                    stageUtils.errorAlert("This serial number is already used for a different " +
                                            "part of this name");
                                } else if (barcodesSamePartName.contains(barcode)) {
                                    stageUtils.errorAlert("This barcode is already used for a different part " +
                                            "of this name");
                                } else {
                                    database.addPart(new Part(partName, serialField.getText(),
                                            manufacturerField.getText(), Double.parseDouble(priceField.getText()),
                                            getVendorName(), locationField.getText(), barcode));
                                    partAddedSuccess(1);
                                    stage.close();
                                    populateTable();
                                }
                            }
                        } else {
                            if (database.barcodeExists(barcode)) {
                                stageUtils.errorAlert("Barcode is already being used for a different part name");
                            } else {
                                database.addPart(new Part(partName, serialField.getText(),
                                        manufacturerField.getText(), Double.parseDouble(priceField.getText()),
                                        getVendorName(), locationField.getText(), barcode));
                                partAddedSuccess(1);
                                stage.close();
                                populateTable();
                            }
                        }
                    }

                } else {
                    if(!validateFieldsNotEmpty()){
                        stageUtils.errorAlert("Please fill out all fields before submitting info.");
                    } else if (validateQuantityField()) {
                        stageUtils.errorAlert("Please make sure you are entering non-negative numbers " +
                                "into price and quantity fields");
                    }
                }
            }

            /**
             * Determines if the price textField input is valid or not
             * @return True if the price is valid
             */
            private boolean validatePriceField(){
                return Integer.parseInt(priceField.getText()) > 0;
            }

            /**
             * Determines if the quantity textField input is valid or not
             * @return True if the quantity is valid
             */
            private boolean validateQuantityField(){
                return Integer.parseInt(quantityField.getText()) > 0;
            }

            /**
             * This checks to see if the textFields are empty
             * @return False if any field is empty
             */
            private boolean validateFieldsNotEmpty(){
                return !(nameField.getText().isEmpty() || serialField.getText().isEmpty() ||
                        manufacturerField.getText().isEmpty() || priceField.getText().isEmpty() ||
                        locationField.getText().isEmpty() || serialField.getText().isEmpty() ||
                        (barcodeField.getText().isEmpty() && !differentBarcodes.isSelected()) ||
                        quantityField.getText().isEmpty() || getVendorName().contains("-1"));
            }

            /**
             * This method returns true if no overlapping serial numbers exist indexing from the starting
             * number or if the user allows indexing from next available serial number
             * @return false if overlapping serial numbers are found and the user doesn't allow indexing
             *         from next available serial number
             */
            private boolean checkExistingSNAgainstGenerated(int quantity, String partName) {
                int startingSN = serialField.getText().isEmpty() ? 1: Integer.parseInt(serialField.getText());
                int currentSN = startingSN;
                String suffix = suffixField.getText();
                ArrayList<String> serialNums = database.getAllSerialNumbersForPartName(partName);
                while (currentSN < quantity + startingSN) {
                    if (serialNums.contains(currentSN + suffix)) {
                        return stageUtils.confirmationAlert("Not enough serial numbers in sequence",
                                "Are you ok with this?",
                                "When adding " + quantity + " parts of " + partName + " with the starting " +
                                        "serial number of " + serialField.getText() + suffixField.getText() +
                                        " the serial number overlaps with existing parts starting with " +
                                        currentSN + suffix + "\nIf the operation continues, more serial numbers " +
                                        "will be generated from the next available lowest number.");
                    }
                    currentSN++;
                }
                return true;
            }

            /**
             * Adds parts with different serial numbers and barcodes
             * @param quantity number of parts being added
             */
            private void addManyPartsWithDifferentBarcodes(int quantity, String partName) {
                int currentSN = serialField.getText().isEmpty() ? 1: Integer.parseInt(serialField.getText());
                long currentBarcode = database.getMaxPartID() + 1;  // gets max part
                String suffix = suffixField.getText();
                ArrayList<String> serialNums = database.getAllSerialNumbersForPartName(partName);
                for (int i = 0; i < quantity; i++) {
                    while (serialNums.contains(currentSN + suffix)) {
                        currentSN++;
                    }
                    database.addPart(new Part(partName, currentSN + suffix, manufacturerField.getText(),
                            Double.parseDouble(priceField.getText()), getVendorName(),
                            locationField.getText(), currentBarcode));
                    currentBarcode++;
                    currentSN++;
                }
            }

            /**
             * Creates an alert informing user that part was added successfully
             */
            private void partAddedSuccess(int num){
                stageUtils.successAlert(num == 1 ? "Part added successfully." : num + " parts added successfully.");
            }

            /**
             * Helper method to get vendor selection
             * @return Vendor name
             */
            private String getVendorName(){
                String failedCheck = "-1";
                if(vendorField.getValue() != null) {
                    return vendorField.getValue();
                }
                return failedCheck;
            }

            /**
             * Checks to see if vendor is new
             * @param vendorName Name to be checked against list of vendors
             * @return True if vendor name is new
             */
            private boolean vendorExists(String vendorName){
                return vendors.contains(vendorName);
            }

        };

        stage.setScene(scene);
        stage.setOnCloseRequest(event -> stage.close());
        stage.getIcons().add(new Image("images/msoe.png"));
        stage.show();
    }

    public void editPart() {
        if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
            if (worker != null && (worker.canEditParts() || worker.isAdmin())
                    || StageUtils.getInstance().requestAdminPin("edit a part", controller.getScene())) {

                Part part = database.selectPart(getRowPartID(table.getSelectionModel().getFocusedIndex()));
                VBox root = new VBox();
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.initOwner(scene.getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setScene(scene);

                setupEditPopup(root, stage, part, false);

                stage.setTitle("Edit a " + part.getPartName());
                stage.getIcons().add(new Image("images/msoe.png"));
                stage.setOnCloseRequest(ev -> stage.close());
                stage.show();
            }
        }
    }

    public void editPartType() {
        if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
            Part part = database.selectPart(getRowPartID(table.getSelectionModel().getFocusedIndex()));

            if (worker != null && (worker.canEditParts() || worker.isAdmin())
                    || StageUtils.getInstance().requestAdminPin("edit all parts named "
                    + part.getPartName(), controller.getScene())) {

                VBox root = new VBox();
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Edit all " + part.getPartName());
                stage.initOwner(scene.getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setScene(scene);

                setupEditPopup(root, stage, part, true);

                stage.getIcons().add(new Image("images/msoe.png"));
                stage.setOnCloseRequest(ev -> stage.close());
                stage.show();
            }
        }
    }

    public void deletePart() {
        if (!table.getSelectionModel().getSelectedCells().isEmpty()) {
            int row = table.getSelectionModel().getFocusedIndex();
            int partID = getRowPartID(row);
            Part part = database.selectPart(partID);

            if ((worker != null && (worker.canRemoveParts() || worker.isAdmin())) ||
                    stageUtils.requestAdminPin("Delete a Part", controller.getScene())) {
                if (!part.getCheckedOut()) {
                    database.initWorker(worker);
                    try {
                        if (database.selectPart(partID) != null) {
                            if (stageUtils.confirmationAlert("Are you sure?", "Delete this part?",
                                    "Are you sure you wish to delete the part with ID = " + partID + "?")) {
                                database.deletePart(partID);
                                populateTable();
                            }
                        }
                    } catch (Exception e) {
                        stageUtils.errorAlert("Error deleting part");
                    }
                } else {
                    stageUtils.errorAlert("This part is currently checked out and cannot be deleted.");
                }
            }
        }
    }

    public void deletePartType() {
        int row = table.getSelectionModel().getFocusedIndex();
        int partID = getRowPartID(row);
        Part part = database.selectPart(partID);

        if ((worker != null && (worker.canRemoveParts() || worker.isAdmin())) ||
                stageUtils.requestAdminPin("delete parts", controller.getScene())) {
            boolean typeHasOneCheckedOut = false;
            ArrayList<String> partIDs = database.getAllPartIDsForPartName("" + part.getPartID());
            for (String id : partIDs) {
                if (database.getIsCheckedOut(id)) {
                    typeHasOneCheckedOut = true;
                }
            }
            String partName = part.getPartName();
            if (!typeHasOneCheckedOut) {
                database.initWorker(worker);
                try {
                    if (database.hasPartName(partName)) {
                        if (stageUtils.confirmationAlert("Are you sure?",
                                "Delete all " + partName + "s?",
                                "Are you sure you wish to delete all parts named: " + partName + "?")) {
                            database.deleteParts(partName);
                            populateTable();
                        }
                    }
                } catch (Exception e) {
                    stageUtils.errorAlert("Error deleting parts with name: " + partName);
                }
            } else {
                stageUtils.errorAlert("At least one " + partName + " is currently checked out, so "
                        + partName + " parts cannot be deleted.");
            }
        }
    }

    /**
     * If new vendor is created, this popup asks for vendor information
     * Currently nothing is done with the result of the text
     */
    private String vendorInformation(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Vendor Information");
        dialog.setHeaderText("New vendor created, please enter vendor information");
        dialog.setContentText("Please enter vendor information");
        dialog.showAndWait();
        return dialog.getResult();
    }

    private boolean validSerialNumChange(Part part, String serialNum) {
        return part.getSerialNumber().equals(serialNum) || !(database.getOtherSerialNumbersForPartName(
                part.getPartName(), "" + part.getPartID()).contains(serialNum)
                && database.hasUniqueBarcodes(part.getPartName()));
    }

    private boolean validBarcodeChange(Part part, long barcode) {
        return part.getBarcode() == barcode || !database.barcodeExists(barcode) || !(database.hasUniqueBarcodes(
                part.getPartName()) && database.getAllBarcodesForPartName(
                        part.getPartName()).contains("" + part.getBarcode()));
    }

    private void setupEditPopup(Pane root, Stage stage, Part part, boolean isPartType) {
        new Popup(root) {
            private JFXTextField nameField, serialField, manufacturerField, priceField, locationField,
                    barcodeField, suffixField;
            private JFXComboBox<String> vendorField;

            @Override
            public void populate() {
                nameField = (JFXTextField) add("Part Name: ", part.getPartName(), true).getChildren().get(1);
                HBox serialBox = addSerialBox("", "");
                serialField = (JFXTextField) serialBox.getChildren().get(1);
                suffixField = (JFXTextField) serialBox.getChildren().get(3);
                Pattern pattern = Pattern.compile("^(\\d+)(.*)$");
                Matcher matcher = pattern.matcher(part.getSerialNumber());
                if (matcher.matches()) {
                    serialField.setText(matcher.group(1));
                    suffixField.setText(matcher.group(2));
                } else {
                    stageUtils.errorAlert("Error parsing serial number");
                }

                barcodeField = (JFXTextField) add("Barcode: ", part.getBarcode().toString(), true).getChildren().get(1);

                if (isPartType) {
                    serialField.setDisable(true);
                    barcodeField.setDisable(true);
                }

                manufacturerField = (JFXTextField) add("Manufacturer: ", part.getManufacturer(), true)
                        .getChildren().get(1);
                priceField = (JFXTextField) add("Price: ", "" + part.getPrice(), true).getChildren().get(1);
                vendorField = addVendorField();
                vendorField.getSelectionModel().select(database.getVendorFromID(part.getVendor()));
                locationField = (JFXTextField) add("Location: ", part.getLocation(), true).getChildren().get(1);

                stageUtils.acceptIntegerOnly(priceField);
                stageUtils.acceptIntegerOnly(serialField);
                stageUtils.acceptIntegerOnly(barcodeField);
            }

            @Override
            public void submit() {
                if (validateInput()) {
                    if (isPartType){
                        String originalPartName = part.getPartName();
                        part.update(nameField.getText().trim(), serialField.getText() +
                                        suffixField.getText().trim(), manufacturerField.getText().trim(),
                                        Double.parseDouble(priceField.getText()), vendorField.getValue(),
                                        locationField.getText().trim(), Long.parseLong(barcodeField.getText()));
                        // checks if other parts with same name but different partIDs, then updates all their names
                        if (database.hasUniqueBarcodes(originalPartName)) {
                            database.editAllOfPartName(originalPartName, part);
                        } else {
                            database.editAllOfPartNameCommonBarcode(originalPartName, part);
                        }
                        stageUtils.successAlert("All " + part.getPartName() + " parts edited successfully.");
                    } else {
                        part.update(nameField.getText().trim(), serialField.getText() +
                                        suffixField.getText().trim(), manufacturerField.getText().trim(),
                                        Double.parseDouble(priceField.getText()), vendorField.getValue(),
                                        locationField.getText().trim(), Long.parseLong(barcodeField.getText()));
                        database.editPart(part);
                        stageUtils.successAlert("Part edited successfully.");
                    }
                    stage.close();
                    populateTable();
                }
            }

            /**
             * This method uses helper methods to ensure that he inputs for the part are valid.
             * @return true if the inputs are valid, false otherwise
             */
            private boolean validateInput() {
                boolean isValid = true;
                String vendor = vendorField.getSelectionModel().getSelectedItem();

                if (nameField.getText().trim().isEmpty() || serialField.getText().trim().isEmpty() ||
                        barcodeField.getText().trim().isEmpty() || manufacturerField.getText().trim().isEmpty()
                        || priceField.getText().trim().isEmpty()|| vendorField.getValue().trim().isEmpty() ||
                        locationField.getText().trim().isEmpty()) {
                    isValid = false;
                    stageUtils.errorAlert("Fill in all necessary fields");
                } else if (Double.parseDouble(priceField.getText()) < 0 || Long.parseLong(barcodeField.getText()) < 1) {
                    isValid = false;
                    stageUtils.errorAlert("Price and barcode must be positive integers");
                } else if (!validSerialNumChange(part, serialField.getText() + suffixField.getText().trim())) {
                    isValid = false;
                    stageUtils.errorAlert("Invalid serial number for part type");
                } else if (!validBarcodeChange(part, Long.parseLong(barcodeField.getText()))) {
                    isValid = false;
                    stageUtils.errorAlert("Invalid barcode for part type");
                }
                boolean newVendor = true;
                for (String vendors : database.getVendorList()) {
                    if (vendors.equals(vendor)) {
                        newVendor = false;
                        break;
                    }
                }
                if (newVendor){
                    database.createNewVendor(vendor, vendorInformation());
                }
                return isValid;
            }
        };
    }

    public class CIRow extends TableRow {
        private final StringProperty partName;
        private final StringProperty location;
        private final StringProperty serialNumber;
        private final IntegerProperty partID;
        private final DoubleProperty price;
        private final LongProperty barcode;

        public CIRow(int partID, long barcode, String serialNumber, String location, String partName, double price){
            this.partID = new SimpleIntegerProperty(partID);
            this.barcode = new SimpleLongProperty(barcode);
            this.serialNumber = new SimpleStringProperty(serialNumber);
            this.location = new SimpleStringProperty(location);
            this.partName = new SimpleStringProperty(partName);
            this.price = new SimpleDoubleProperty(price);
        }

        public DoubleProperty getPrice() {
            return price;
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

        public IntegerProperty getPartID() {
            return partID;
        }
    }
}
