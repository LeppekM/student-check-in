package Controllers;

import Database.ExtendedCheckoutObject;
import Database.*;
import Database.ObjectClasses.Checkout;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import HelperClasses.AutoCompleteTextField;
import HelperClasses.StageUtils;
import Popups.Popup;
import Tables.CheckedOutInventoryTable;
import Tables.TSCTable;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

import static javafx.scene.paint.Color.FIREBRICK;

/**
 * Controls and manages functionality of the Checkout/in screen, responsible for:
 * - creating checkout entities in the database
 * - checking in and out valid parts/kits to certain students
 * - creating new students if they do not exist in the database
 * - displaying parts a student has checked out currently during the checkout process
 */
public class CheckOutController extends MenuController implements IController, Initializable {

    @FXML
    private VBox main;

    @FXML
    private JFXTextField studentNameField;

    @FXML
    private AutoCompleteTextField studentIDField;

    @FXML
    private JFXCheckBox extended;

    @FXML
    private JFXButton submitButton;

    @FXML
    private JFXTreeTableView coTable;

    @FXML
    private VBox barcodeVBox;

    private final StageUtils stageUtils = StageUtils.getInstance();
    private final Database database = Database.getInstance();

    private static String professor, course, dueDate;

    private static final int PAUSE_DELAY = 5;
    public static final int BARCODE_STRING_LENGTH = 6;
    public static final String EMAIL_REGEX = "^\\w+[+.\\w'-]*@msoe\\.edu$";
    public static final String RFID_REGEX = "^.*(\\d{4,})$";
    private static final String CHECK_IN_STR = "In";
    private static final String CHECK_OUT_STR = "Out";
    private static final String DEFAULT_STR = "--";
    private static final String ERROR_STR = "Error";
    private static final String EXTENDED_STR = "Extended?";
    private static final PauseTransition delay = new PauseTransition(Duration.minutes(PAUSE_DELAY));
    private Worker worker;
    private Student currentStudent;
    private final List<HBox> barcodes = new LinkedList<>(); // separately kept list because .getChildren() returns nodes
    private JFXTextField firstBarcodeField;
    private JFXTreeTableColumn<Checkout, String> coTableCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> studentIDField.requestFocus());
        worker = null;  // default no worker & student
        currentStudent = null;
        setFieldValidator();  // sets required fields and filters barcode fields
        firstBarcodeField = createBarcode();  // creates new barcode with listeners
        setupCOTable(); // sets up the initial values for the sidebar table
        studentIDField.initEntrySet(new TreeSet<>(database.getStudentEmails()));  // sets up email autofill for field
        getStudentName();  // sets listeners for
        submitTimer();  // starts the countdown timer for auto-clicking the submit button
    }

    /**
     * Resets all fields by recreating a new page
     */
    public void reset() {
        stageUtils.newStage("/fxml/CheckOutPage.fxml", main, worker, null);
    }

    /**
     * If no movement is recorded on page for 5 minutes, item will submit automatically
     * NOTE: this will run on any screen
     */
    private void submitTimer() {
        main.addEventFilter(InputEvent.ANY, evt -> delay.playFromStart());
        delay.setOnFinished(event -> submit());
    }

    /**
     * Initializes extended object
     * @param checkout Object to be instantiated
     */
    public void initExtendedObject(ExtendedCheckoutObject checkout) {
        course = checkout.getCourse();
        professor = checkout.getProf();
        dueDate = checkout.getExtendedDate();
    }

    /**
     * Helper method to check if extended fields are filled out
     * @return True if any of the fields are left empty
     */
    private boolean extendedFieldsNotFilled() {
        return professor == null || course == null || dueDate == null;
    }

    /**
     * Checks if student is new
     * @return True if student name has text
     */
    private boolean isStudentNameEmpty() {
        return studentNameField.getText().isEmpty();
    }

    /**
     * Submits the information entered to checkouts/checkoutParts table or removes if item is being checked back in.
     */
    public void submit() {
        if (hasError()) {
            stageUtils.errorAlert("Parts were not checked out because there are errors with at least one of them " +
                    "\n\nPart is either checked out under another students' name or no more available checkout " +
                    "slots in system");
            return;
        }
        database.initWorker(worker); // kept for tracking purposes
        if (!fieldsFilled()) {
            stageUtils.checkoutAlert("Unfilled Fields", "No parts checked in/out");
            return;
        }
        if (isStudentNameEmpty()) {
            stageUtils.errorAlert("No student found in the system with associated ID");
            return;
        } else {
            submitParts();
        }
        reset();
    }

    /**
     * @return true if any part being checked in/out have an error, otherwise false
     */
    private boolean hasError() {
        boolean hasError = false;
        for (HBox hbox : barcodes) {
            Label label = (Label) hbox.getChildren().get(2);  // should always be the label
            if (label.getText().equals(ERROR_STR)) {
                hasError = true;
            }
        }
        return hasError;
    }

    /**
     * Submits (all) part(s)
     */
    private void submitParts() {
        boolean success = false;
        ArrayList<Long> barcodesAlreadyChecked = new ArrayList<>();
        for (HBox hbox : barcodes) {
            JFXTextField barcodeField = (JFXTextField) hbox.getChildren().get(0); // get the barcodeField
            Spinner<Integer> quantitySpinner = (Spinner<Integer>) hbox.getChildren().get(1);
            Label statusLabel = (Label) hbox.getChildren().get(2);
            JFXCheckBox extendedCheckout = (JFXCheckBox) hbox.getChildren().get(3);
            long barcode = 0;
            if (barcodeField.getText().length() == BARCODE_STRING_LENGTH) {
                barcode = Long.parseLong(barcodeField.getText());
            }
            // stops incomplete barcodes from being submitted
            if (barcodeField.getText().length() == BARCODE_STRING_LENGTH && !barcodesAlreadyChecked.contains(barcode)) {
                if (statusLabel.getText().equals(CHECK_IN_STR)){
                    // check in
                    for (int i = 0; i < quantitySpinner.getValue(); i++){
                        success = database.checkInPart(barcode, currentStudent.getRFID());
                    }
                } else if (statusLabel.getText().equals(CHECK_OUT_STR)){
                    // check out a part with partID matching barcode, including a barcodes with many associated
                    // don't forget to check if extended checkout is selected
                    for (int i = 0; i < quantitySpinner.getValue(); i++){
                        if(database.barcodeExists(barcode)) {
                            if (extendedCheckout.isSelected()){
                                success = database.checkOutPart(barcode, currentStudent.getRFID(), course, professor, dueDate);
                            } else {
                                success = database.checkOutPart(barcode, currentStudent.getRFID(), null, null, null);
                            }
                        } else {
                            stageUtils.errorAlert("Barcode " + barcode + " was not found in database, " +
                                    "part was not checked out");
                        }
                    }
                    barcodesAlreadyChecked.add(barcode);
                } else {
                    // error: checked out by a different student or tried to check out more parts than available
                    // also should not be able to reach here
                    stageUtils.errorAlert("Attempted to submit a part with error");
                }
            }
        }

        if (success) {
            stageUtils.checkoutAlert("Success", "Part(s) Checked in/out successfully");
        }
    }

    /**
     * Creates a single correctly formatted barcode HBox and adds it to barcodeVBox
     * @return the JFXTextField of the newly created barcode HBox so that it can be put in focus
     */
    private JFXTextField createBarcode() {
        HBox barcodeBox = new HBox();
        barcodeBox.setPrefHeight(60);
        barcodeBox.setMinHeight(60);
        barcodeBox.setAlignment(Pos.BOTTOM_LEFT);

        JFXTextField barcodeField = new JFXTextField();
        barcodeField.setFocusColor(FIREBRICK);
        barcodeField.setPrefHeight(60);
        barcodeField.setMinWidth(400);
        barcodeField.setMaxWidth(400);
        barcodeField.setAlignment(Pos.BOTTOM_LEFT);
        barcodeBox.getChildren().add(barcodeField);

        Spinner<Integer> spinner = new Spinner<>();
        spinner.setMinHeight(20);
        spinner.setMinWidth(60);
        spinnerInit(spinner, 1);
        spinner.setEditable(false);
        barcodeBox.getChildren().add(spinner);

        Label statusLabel = new Label(DEFAULT_STR);
        statusLabel.setVisible(true);
        statusLabel.setMinHeight(30);
        statusLabel.setMinWidth(40);
        statusLabel.setFont(Font.font(14));
        statusLabel.setAlignment(Pos.CENTER);
        barcodeBox.getChildren().add(statusLabel);

        JFXCheckBox extendedCheckBox = new JFXCheckBox();
        extendedCheckBox.setCheckedColor(FIREBRICK);
        extendedCheckBox.setText(EXTENDED_STR);
        extendedCheckBox.setVisible(false);
        extendedCheckBox.setPrefSize(120, 40);
        barcodeBox.getChildren().add(extendedCheckBox);

        barcodes.add(barcodeBox);
        barcodeVBox.getChildren().add(barcodeBox);

        barcodeField.setOnKeyReleased(event -> statusLabel.setVisible(true));
        barcodeField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.length() == BARCODE_STRING_LENGTH) {

                        int numOutByCurrentStudent = database.amountOutByStudent(Long.parseLong(newValue),
                                currentStudent);
                        if (numOutByCurrentStudent > 0) {
                            spinnerInit(spinner, numOutByCurrentStudent);
                            statusLabel.setText(CHECK_IN_STR);
                        } else if (setSpinnerWithAvailableParts(Long.parseLong(newValue), spinner)){
                            statusLabel.setText(CHECK_OUT_STR);
                            extendedCheckBox.setVisible(extended.isSelected());
                            extendedCheckBox.setDisable(false);
                        } else {
                            statusLabel.setText(ERROR_STR);
                            extendedCheckBox.setVisible(false);
                            spinner.setEditable(false);
                        }

                        if (barcodeBox == barcodes.get(barcodes.size() - 1)) {
                            createBarcode().requestFocus();
                        } else {
                            barcodes.get(barcodes.size() - 1).getChildren().get(0).requestFocus();
                        }


                    } else {
                        statusLabel.setText(DEFAULT_STR);
                        extendedCheckBox.setVisible(false);
                    }
                });

        return barcodeField;
    }

    /**
     * Initializes the increment/decrement buttons on spinners. Spinner is always initially set to total
     * @param spinner Spinner to be initialized
     */
    void spinnerInit(Spinner<Integer> spinner, int total){
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, total, total);
        spinner.setValueFactory(valueFactory);
    }

    /**
     * This method sets the spinner's dropdown menu and returns the boolean if the parts are available
     * @param barcode the barcode that is being checked in the database for parts
     * @param spinner the spinner that quantifies the number of parts which can be checked out
     * @return true if the barcode has parts available to check out, false otherwise
     */
    public boolean setSpinnerWithAvailableParts(long barcode, Spinner<Integer> spinner) {
        int partsAvailable = database.getNumPartsAvailableByBarcode(barcode);
        if (partsAvailable > 0){
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, partsAvailable, 1);
            spinner.setValueFactory(valueFactory);
            return true;
        }
        return false;
    }

    /**
     * Returns to home, contains check if fields are filled out
     */
    public void returnHome() {
        if (fieldsFilled()) {
            if (!stageUtils.missingFieldsAlert()) {
                return;
            }
        }
        stageUtils.goBack(main, worker);
    }

    /**
     * Gets student name. If no student name is found in database it will create a new student, or update student.
     */
    private void getStudentName() {
        studentIDField.textProperty().addListener((ov, oldV, newV) -> {
            studentNameField.setText("");
            currentStudent = null;
            clearCOTable();
            extended.setDisable(true);
        });

        studentIDField.setOnKeyPressed(event -> {
            if (!studentIDField.getFilteredEntries().isEmpty() && (event.getCode().equals(KeyCode.ENTER)
                    || event.getCode().equals(KeyCode.TAB))) {
                studentIDField.setText(studentIDField.getFilteredEntries().get(0));
            }
            if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB)){
                firstBarcodeField.requestFocus();
            }
        });

        studentIDField.focusedProperty().addListener((ov, oldV, newV) -> {
            String input = studentIDField.getText();

            // get name
            String studentName = "";
            if (input.matches(RFID_REGEX)) {
                studentName = database.getStudentNameFromID(input);
            } else if (input.matches(EMAIL_REGEX)) {
                // want to allow them to check out via email, excluding if they do not have their ID first checkout
                studentName = database.getStudentNameFromEmail(input);
            }
            studentNameField.setText(studentName);
            if (!studentName.isEmpty()) {
                extended.setDisable(false);
                if (input.matches(RFID_REGEX)) {
                    currentStudent = database.selectStudent(getStudentID(), null);
                } else {
                    currentStudent = database.selectStudent(-1, input);
                }
            }
            studentName = studentNameField.getText();

            if (!newV) {  // if no longer in focus
                if (input.matches(EMAIL_REGEX)) {
                    int sID = database.getStudentIDFromEmail(input);
                    if (sID == 0) {
                        stageUtils.errorAlert("Student is checking out equipment for first time/has no associated ID" +
                                "\n They must use their student ID to check out an item");
                        reset();
                        return;
                    }
                } else if (!input.matches(RFID_REGEX) && !input.isEmpty()) {
                    stageUtils.errorAlert("Invalid student RFID or email address");
                    return;
                }
                if (!input.isEmpty() && studentNameField.getText().isEmpty()) { //If RFID isn't in DB, asks for email
                    String studentEmail = newStudentEmail(false);
                    // re-prompt for email if not a validly formatted MSOE email, breaks loop if cancel is selected
                    while (studentEmail != null && !studentEmail.matches(EMAIL_REGEX)) {
                        studentEmail = newStudentEmail(true);
                    }
                    if (studentEmail != null) {
                        studentName = database.getStudentNameFromEmail(studentEmail);

                        if (studentName.isEmpty()) {  //Means student isn't in database, so new one will be created
                            studentName = newStudentName(false);
                            // re-prompt for name if it doesn't contain a space
                            while (studentName != null && !studentName.contains(" ")) {
                                studentName = newStudentName(true);
                            }
                            if (studentName != null) {
                                database.initWorker(worker);
                                database.addStudent(new Student(studentName, Integer.parseInt(input), studentEmail));
                                stageUtils.successAlert("New student created");
                            }
                        } else {
                            Student s = database.selectStudent(-1, studentEmail);
                            s.setRFID(getStudentID());
                            database.updateStudent(s, s.getRFID());
                            stageUtils.successAlert("Student updated");
                        }
                    }
                    studentNameField.setText(studentName);
                }

                if (!studentNameField.getText().isEmpty()){
                    extended.setDisable(false);
                    if (studentIDField.getText().matches(RFID_REGEX)) {
                        currentStudent = database.selectStudent(getStudentID(), null);
                    } else {
                        currentStudent = database.selectStudent(-1, studentIDField.getText());
                    }
                    populateCOTable();
                }
            }
        });
    }

    /**
     * Asks user for a student name
     * @return Student name
     */
    private String newStudentName(boolean wasInvalid) {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setTitle("New Student Creation: Name");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);

        final String[] studentName = {null};
        new Popup(root) {
            Label contentLabel;
            AutoCompleteTextField nameField;
            @Override
            public void populate() {
                if (wasInvalid) {
                    contentLabel = createLabel("Student Name was not formatted correctly." +
                            "\nPlease Enter FirstName LastName seperated by space to Continue ");
                } else {
                    contentLabel = createLabel("Student Name is not in System.\nPlease Enter Name to Continue ");
                }
                contentLabel.setMinSize(WIDTH * 2, HEIGHT * 2);
                contentLabel.setAlignment(Pos.CENTER_LEFT);
                addHBox(new HBox(contentLabel));
                nameField = createTextField("", true);
                nameField.setMinWidth(WIDTH * 2);
                nameField.setPromptText("FirstName LastName");
                addHBox(new HBox(nameField));
            }

            @Override
            public void submit() {
                studentName[0] = nameField.getText();
                stage.close();
            }
        };

        stage.getIcons().add(new Image("images/msoe.png"));
        stage.showAndWait();

        return studentName[0];
    }

    /**
     * Asks user for student email, returns null if cancelled
     * @return Student email
     */
    private String newStudentEmail(boolean wasInvalid) {
        Stage stage = new Stage();
        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setTitle("New Student Creation: Email");
        stage.initOwner(scene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);

        final String[] email = {null};
        new Popup(root) {
            Label contentLabel;
            AutoCompleteTextField emailField;
            @Override
            public void populate() {
                if (wasInvalid) {
                    contentLabel = createLabel("Invalid email entered.\nPlease enter a valid MSOE email to continue ");
                } else {
                    contentLabel = createLabel("Student ID is not in system.\nPlease enter MSOE email to continue ");
                }
                contentLabel.setMinSize(WIDTH * 2, HEIGHT * 2);
                contentLabel.setAlignment(Pos.CENTER_LEFT);
                addHBox(new HBox(contentLabel));
                emailField = createTextField("", true);
                emailField.setMinWidth(WIDTH * 2);
                emailField.initEntrySet(new TreeSet<>(database.getStudentEmails()));
                emailField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.TAB)
                            || event.getCode().equals(KeyCode.ENTER)) {
                        emailField.setText(emailField.getFilteredEntries().get(0));
                    }
                });
                emailField.setPromptText("email@msoe.edu");
                addHBox(new HBox(emailField));
            }

            @Override
            public void submit() {
                email[0] = emailField.getText();
                stage.close();
            }
        };

        stage.getIcons().add(new Image("images/msoe.png"));
        stage.showAndWait();

        return email[0];
    }

    /**
     * Checks if an acceptable student identifier is input and if at least one barcode field has a part number
     * @return True if fields are not empty
     */
    private boolean fieldsFilled() {
        return !studentIDField.getText().isEmpty() && atLeastOneBarcodeFilled();
    }

    /**
     * @return true if at least one barcode is filled in, false otherwise
     */
    private boolean atLeastOneBarcodeFilled(){
        boolean isFilled = false;
        for (HBox hbox : barcodes) {
            JFXTextField barcodeField = (JFXTextField) hbox.getChildren().get(0);
            if (barcodeField.getText().length() < BARCODE_STRING_LENGTH) {
                isFilled = true;
            }
        }
        return isFilled;
    }

    /**
     * Helper method to set the validators & filters for fields and disable/enable for submit button
     */
    private void setFieldValidator() {
        // Attaches the "This field is required" text to TextField
        stageUtils.requiredInputValidator(studentIDField);
        // barcodes are not required to press submit
        for (HBox hbox : barcodes){
            JFXTextField textField = (JFXTextField) hbox.getChildren().get(0);
            //Prevents non-digits from being added to the barcode TextFields, and limits maximum characters
            stageUtils.acceptIntegerOnly(textField);
            stageUtils.setMaxTextLength(textField, BARCODE_STRING_LENGTH);
        }
        // enables submit button when a valid student id/email is entered
        submitButton.disableProperty().bind(studentIDField.textProperty().isEmpty()
                .or(studentNameField.textProperty().isEmpty()));
    }

    /**
     * Gets studentID as text, returns as int
     * @return StudentID as integer
     */
    private int getStudentID() {
        int id = 0;
        if (studentIDField.getText().matches(RFID_REGEX)) {
            // technically incorrect, but shouldn't be a problem unless some nonsense is entered IE: 297839RFID:8920
            id = Integer.parseInt(studentIDField.getText().replaceAll("\\D", ""));
        } else if (studentIDField.getText().matches(EMAIL_REGEX)) {
            id = database.getStudentIDFromEmail(studentIDField.getText());
        }
        return id;
    }

    /**
     * If extended is selected, more items will be displayed
     */
    public void isExtended() {
        if (extended.isSelected()) {
            Stage stage = stageUtils.createPopupStage("fxml/ExtendedCheckout.fxml", main, "Part Information");
            stage.showAndWait();
            if (extendedFieldsNotFilled()) {
                stageUtils.checkoutAlert("Extended Checkout",
                        "Process cancelled or fields were not filled out for extended checkout");
                extended.setSelected(false);
                setExtendedCheckboxesVisible(false);
                return;
            }
            setExtendedCheckboxesVisible(true);
        }
    }

    /**
     * Helper method that shows/hides and resets the extended checkout boxes associated with each barcode
     * @param value which determines whether to hide extended check boxes
     */
    private void setExtendedCheckboxesVisible(boolean value) {
        for (HBox hBox : barcodes) {
            JFXCheckBox checkBox = (JFXCheckBox) hBox.getChildren().get(3);
            Label statusLabel = (Label) hBox.getChildren().get(2);
            if (statusLabel.getText().equals(CHECK_OUT_STR)) {
                checkBox.setVisible(value);
                checkBox.setSelected(value);  // auto selected for every part that is being checked out currently
            } else {
                checkBox.setVisible(false);
                checkBox.setSelected(false);
            }
        }
    }

    /**
     * This sets up the formatting and CellFactories for the checkout side table
     */
    private void setupCOTable() {
        Label emptyTableLabel = TSCTable.getEmptyTableLabel();
        coTable.setPlaceholder(emptyTableLabel);
        coTable.setStyle("-fx-font-size: 16px");
        coTableCol = new JFXTreeTableColumn<>("Part Name");
        coTableCol.prefWidthProperty().bind(coTable.widthProperty().subtract(2));
        coTableCol.setResizable(false);
        coTableCol.setCellValueFactory(param -> param.getValue().getValue().getPartName());

        // this is why jfx is a crime against my sanity in particular
        Callback<TreeTableColumn<Checkout, String>, TreeTableCell<Checkout, String>> cellFactory =
                new Callback<TreeTableColumn<Checkout, String>, TreeTableCell<Checkout, String>>() {
            @Override
            public TreeTableCell<Checkout, String> call(TreeTableColumn<Checkout, String> param) {
                return new TreeTableCell<Checkout, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);

                            // if the part is overdue
                            Date currentDate = new Date();
                            Checkout model = getTreeTableRow().getItem();
                            if (model != null && model.getDueDate().get().before(currentDate)) {
                                setStyle("-fx-text-fill: #920202");
                            } else {
                                setStyle(""); // Reset to default style if condition is not met
                            }
                        }
                    }
                };
            }
        };
        coTableCol.setCellFactory(cellFactory);
    }

    /**
     * This connects the student's checkedOut items list to the side table
     */
    private void populateCOTable() {
        final TreeItem<Checkout> coItems =
                new RecursiveTreeItem<>(currentStudent.getCheckedOut(), RecursiveTreeObject::getChildren);
        coTable.getColumns().setAll(coTableCol);
        coTable.setRoot(coItems);
        coTable.setShowRoot(false);
    }

    /**
     * This clears all columns the checkout Table has
     */
    private void clearCOTable() {
        coTable.getColumns().setAll();
    }

    /**
     * Opens further part info associated with the part
     * @param event MouseEvent that listens
     */
    @FXML
    public void coPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            int index = coTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                Checkout item = (Checkout) coTable.getSelectionModel().getModelItem(index).getValue();
                CheckedOutInventoryTable.createCheckoutPopup(item);
            }
        }
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }
}
