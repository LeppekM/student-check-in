package CheckItemsController;

import Database.*;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import HelperClasses.AutoCompleteTextField;
import HelperClasses.StageUtils;
import InventoryController.ControllerMenu;
import InventoryController.IController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

import static javafx.scene.paint.Color.FIREBRICK;

public class CheckOutController extends ControllerMenu implements IController, Initializable {

    @FXML
    private VBox main;

    @FXML
    private JFXTextField studentNameField;

    @FXML
    private AutoCompleteTextField studentIDField;

    @FXML
    private JFXCheckBox extended;

    @FXML
    private JFXButton studentInfo, submitButton, resetButton;

    @FXML
    private VBox barcodeVBox;

    private final StageUtils stageUtils = StageUtils.getInstance();
    private final Database database = new Database();
    private final StudentInfo student = new StudentInfo();

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
    private boolean extendedVisible = false;
    private Student currentStudent;
    private List<HBox> barcodes = new LinkedList<>(); // separately kept list because .getChildren() returns nodes
    private JFXTextField firstBarcodeField;

    //TODO: change logic so this has a student that it keeps track of and resets iff screen gets reset

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> studentIDField.requestFocus());
        worker = null;  // default no worker & student
        currentStudent = null;
        setFieldValidator();  // sets required fields and filters barcode fields
        firstBarcodeField = createBarcode();  // creates new barcode with listeners
        studentIDField.initEntrySet(new TreeSet<>(database.getStudentEmails()));  // sets up autofill for student ID field to take Emails
        getStudentName();  // sets listeners for
        submitTimer();  // starts the countdown timer for auto-clicking the submit button
    }

    /**
     * Resets all fields by recreating a new page
     */
    public void reset() {
        stageUtils.newStage("/fxml/CheckOutPage.fxml", main, worker);
    }

    /**
     * If no movement is recorded on page for 5 minutes, item will submit automatically
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
     * Initialize checkout object when returning from student page
     * @param checkoutObject Object to initialize
     */
    public void initCheckoutObject(CheckoutObject checkoutObject) {
        studentIDField.setText(checkoutObject.getStudentID());
        //barcode.setText(checkoutObject.getBarcode());

        if (checkoutObject.isExtended()) {
            extended.setSelected(true);
            isExtended();
        }

        // enable the switch to student info button iff the student ID field contains a student ID
        studentInfo.setDisable(!studentIDField.getText().matches(RFID_REGEX) && !studentIDField.getText().matches(EMAIL_REGEX));
    }

    /**
     * Helper method to check if extended fields are filled out
     *
     * @return True if any of the fields are left empty
     */
    private boolean extendedFieldsNotFilled() {
        return (professor == null || course == null || dueDate == null);
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
        // todo make sure the error logic has a popup for what type of error (checked out by another student, unavailable quantity of parts)
        if (hasError()) {
            stageUtils.errorAlert("Parts were not checked out because there are errors with at least one of them");
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
        // todo this whole method
        for (HBox hbox : barcodes) {
            JFXTextField barcodeField = (JFXTextField) hbox.getChildren().get(0); // get the barcodeField
            Spinner<Integer> quantitySpinner = (Spinner<Integer>) hbox.getChildren().get(1);
            Label statusLabel = (Label) hbox.getChildren().get(2);
            JFXCheckBox extendedCheckout = (JFXCheckBox) hbox.getChildren().get(3);
            // want to d/c the logic from MultipleCheckoutObject and CheckingOutPart objects

            // stops incomplete barcodes from being submitted
            if (barcodeField.getText().length() == BARCODE_STRING_LENGTH) {
                int barcode = Integer.parseInt(barcodeField.getText());
                if (statusLabel.getText().equals(CHECK_IN_STR)){
                    // check in

                } else if (statusLabel.getText().equals(CHECK_OUT_STR)){
                    // check out a part with partID matching barcode, including a barcodes with many associated
                    // don't forget to check if extended checkout is selected

                } else {
                    // error: checked out by a different student or tried to check out more parts than available
                    // also should not be able to reach here
                    stageUtils.errorAlert("Attempted to submit a part with error");
                }
            }
        }

        // want to add a number of parts that went through to this message
        stageUtils.checkoutAlert("Success", "Part(s) Checked in/out successfully");
    }

    /**
     * Creates a single correctly formatted barcode HBox and adds it to barcodeVBox
     * @return the JFXTextField of the newly created barcode HBox so that it can be put in focus
     */
    private JFXTextField createBarcode() {
        HBox barcodeBox = new HBox();
        barcodeBox.setPrefHeight(60);
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
        extendedCheckBox.setVisible(extendedVisible);
        barcodeBox.getChildren().add(extendedCheckBox);

        barcodes.add(barcodeBox);
        barcodeVBox.getChildren().add(barcodeBox);

        barcodeField.setOnKeyReleased(event -> {
            statusLabel.setVisible(true);
        });
        barcodeField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.length() == BARCODE_STRING_LENGTH) {

                        int numOutByCurrentStudent = database.amountOutByStudent(Long.parseLong(newValue), currentStudent);
                        if (numOutByCurrentStudent > 0) {
                            spinnerInit(spinner, numOutByCurrentStudent);
                            statusLabel.setText(CHECK_IN_STR);
                        } else if (setSpinnerWithAvailableParts(Long.parseLong(newValue), spinner)){
                            statusLabel.setText(CHECK_OUT_STR);
                            extendedCheckBox.setVisible(extended.isSelected());
                            extendedCheckBox.setDisable(false);
                            extendedCheckBox.setText(EXTENDED_STR);
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
        stageUtils.newStage("/fxml/Menu.fxml", main, worker);
    }

    /**
     * Gets student name. If no student name is found in database it will create a new student, or update student.
     */
    private void getStudentName() {
        studentIDField.textProperty().addListener((ov, oldV, newV) -> {
            String studentName = "";

            studentNameField.setText("");
            currentStudent = null;
            extended.setDisable(true);

            if (newV.matches(RFID_REGEX)) {
                studentName = student.getStudentNameFromID(newV);
            } else if (newV.matches(EMAIL_REGEX)) {
                // want to allow them to check out via email, excluding if they do not have their ID first checkout
                studentName = student.getStudentNameFromEmail(newV);
            }
            studentNameField.setText(studentName);

            if (!studentName.isEmpty()) {
                extended.setDisable(false);
                if (newV.matches(RFID_REGEX)) {
                    currentStudent = database.selectStudent(getStudentID(), null);
                } else {
                    currentStudent = database.selectStudent(-1, newV);
                }
                //  //want to do this, un sure if possible
            }
        });

        studentIDField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)){
                firstBarcodeField.requestFocus();
            }
        });

        studentIDField.focusedProperty().addListener((ov, oldV, newV) -> {
            String input = studentIDField.getText();
            String studentName = studentNameField.getText();
            if (!newV) {  // if no longer in focus
                if (input.matches(EMAIL_REGEX)) {
                    int sID = student.getStudentIDFromEmail(input);
                    if (sID == 0) {
                        stageUtils.errorAlert("Student is checking out equipment for first time/has no associated ID\n They must use their student ID to check out an item");
                        reset();
                        return;
                    }
                } else if (!input.matches(RFID_REGEX) && !input.isEmpty()) {
                    stageUtils.errorAlert("Invalid student RFID or email address");
                    return;
                }
                if (!input.isEmpty() && studentNameField.getText().isEmpty()) { //If student ID isn't in DB, asks for email to attach the id to.
                    String studentEmail = newStudentEmail(false);
                    // re-prompt for email if not a validly formatted MSOE email, breaks loop if cancel is selected
                    while (studentEmail != null && !studentEmail.matches(EMAIL_REGEX)) {
                        studentEmail = newStudentEmail(true);
                    }
                    if (studentEmail != null) {
                        studentName = student.getStudentNameFromEmail(studentEmail);

                        if (studentName.isEmpty()) {  //Means student doesn't exist in database, so completely new one will be created
                            studentName = newStudentName(false);
                            // re-prompt for name if it doesn't contain a space
                            while (studentName != null && !studentName.contains(" ")) {
                                studentName = newStudentName(true);
                            }
                            if (studentName != null) {
                                student.createNewStudent(getStudentID(), studentEmail, studentName);
                                stageUtils.successAlert("New student created");
                            }
                        } else {
                            student.updateStudent(studentEmail, getStudentID());
                            stageUtils.successAlert("Student updated");
                        }
                    }
                    studentNameField.setText(studentName);
                }

                if (!studentNameField.getText().isEmpty()){
                    extended.setDisable(false);
                }
            }

        });

    }

    /**
     * Asks user for a student name
     * @return Student name
     */
    private String newStudentName(boolean wasInvalid) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Student Creation");
        if (wasInvalid) {
            dialog.setHeaderText("Student Name was not formatted correctly.\n Please Enter Name with space to Continue ");
        } else {
            dialog.setHeaderText("Student Name is not in System.\n Please Enter Name to Continue ");
        }
        dialog.setContentText("First and last name\n Separate by space");
        dialog.showAndWait();
        return dialog.getResult();
    }

    /**
     * Asks user for student email, returns null if cancelled
     * @return Student email
     */
    private String newStudentEmail(boolean wasInvalid) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Student Creation");
        if (wasInvalid) {
            dialog.setHeaderText("Invalid email entered.\n Please enter a valid MSOE email to continue ");
        } else {
            dialog.setHeaderText("Student ID is not in system.\n Please enter MSOE email to continue ");
        }
        dialog.setContentText("Please enter Student Email");
        dialog.showAndWait();
        return dialog.getResult();
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
     * Changes to student info tab
     *
     * @author Bailey Terry
     */
    public void goToStudent() {
//        Student s = null;
//        if (studentIDField.getText().matches(RFID_REGEX)) {
//            s = database.selectStudent(Integer.parseInt(studentIDField.getText()), null);
//        } else if (studentIDField.getText().matches(EMAIL_REGEX)) {
//            s = database.selectStudent(-1, studentIDField.getText());
//        }
//        if (s != null && !s.getName().isEmpty()) {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Student.fxml"));
//                Parent root = loader.load();
//                StudentPage sp = loader.getController();
//                sp.setStudent(s);
//                checkoutObject = new CheckoutObject(studentIDField.getText(), barcode.getText(), "1", extended.isSelected());
//                sp.initCheckoutObject(checkoutObject);
//                main.getScene().setRoot(root);
//            } catch (IOException e) {
//                stageUtils.errorAlert("Couldn't load student page");
//            }
//        } else {
//            stageUtils.errorAlert("No student found with associated RFID");
//        }
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
        // sets Student Info button enable/disable listener
        studentInfo.disableProperty().bind(studentNameField.textProperty().isEmpty());
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
            id = student.getStudentIDFromEmail(studentIDField.getText());
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
                stageUtils.errorAlert("Process cancelled or fields were not filled out for extended checkout");
                extended.setSelected(false);
                setExtendedCheckboxesVisible(false);
                return;
            }
            setExtendedCheckboxesVisible(true);
        }
    }

    private void setExtendedCheckboxesVisible(boolean value) {
        extendedVisible = value;
        for (HBox hBox : barcodes) {
            JFXCheckBox checkBox = (JFXCheckBox) hBox.getChildren().get(3);
            Label statusLabel = (Label) hBox.getChildren().get(2);
            if (statusLabel.getText().equals(CHECK_IN_STR)) {
                checkBox.setVisible(value);
                checkBox.setSelected(value);  // auto selected for every part that is being checked out currently
            } else {
                checkBox.setVisible(false);
                checkBox.setSelected(false);
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
