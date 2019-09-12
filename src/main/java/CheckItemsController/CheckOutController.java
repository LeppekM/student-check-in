package CheckItemsController;

import Database.*;
import Database.ObjectClasses.CheckedOutPartsObject;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import HelperClasses.AdminPinRequestController;
import HelperClasses.AutoCompleteTextField;
import HelperClasses.DatabaseHelper;
import HelperClasses.StageWrapper;
import InventoryController.ControllerMenu;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

public class CheckOutController extends ControllerMenu implements IController, Initializable {

    @FXML
    private VBox main;

    @FXML

    private JFXTextField barcode, barcode2, barcode3, barcode4, barcode5, studentNameField;

    @FXML
    private AutoCompleteTextField studentID;

    @FXML
    private TextArea faultyArea;


    @FXML
    private JFXCheckBox faulty, extended, extended1, extended2, extended3, extended4, extended5;

    @FXML
    private JFXButton studentInfo, submitButton, resetButton;

    @FXML
    private Spinner<Integer> newQuantity, newQuantity2, newQuantity3, newQuantity4, newQuantity5;

    @FXML
    private Label statusLabel,
            statusLabel2, statusLabel3, statusLabel4, statusLabel5;

    @FXML
    private StackPane faultPane;


//    @FXML
//    private TextArea faultyTextArea;


    @FXML
    private HBox HBoxBarcode, HBoxBarcode2, HBoxBarcode3, HBoxBarcode4, HBoxBarcode5;


    private CheckoutObject checkoutObject;
    private ExtendedCheckoutObject extendedCheckOutObject;
    private StageWrapper stageWrapper = new StageWrapper();
    private Database database = new Database();
    private CheckingOutPart checkOut = new CheckingOutPart();
    private StudentInfo student = new StudentInfo();
    private TransitionHelper transitionHelper = new TransitionHelper();
    private ExtendedCheckOut extendedCheckOut = new ExtendedCheckOut();
    private FaultyCheckIn faultyCheckIn = new FaultyCheckIn();
    private String partNameFromBarcode;
    private List<CheckedOutPartsObject> checkoutParts = new ArrayList<>();
    private List<String> studentIDVerifier = new ArrayList<>();
    private DatabaseHelper dbHelp = new DatabaseHelper();
    private static String professor, course, dueDate;
    private static boolean fieldsFilled;
    private String faultyText;
    private List<String> id = new ArrayList<>();
    private static final int PAUSE_DELAY = 5;
    private static PauseTransition delay = new PauseTransition(Duration.minutes(PAUSE_DELAY));
    private Worker worker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> studentID.requestFocus());
        this.worker = null;
        setFieldValidator();
        setItemStatus();
        initialBarodeFieldFunctions();
        initialStudentFieldFunctions();
        studentID.initEntrySet(new TreeSet(database.getStudentEmails()));
        setLabelStatuses();
        getStudentName();
        unlockFields();
        transitionHelper.spinnerInit(newQuantity);
        submitTimer();
        dropBarcode();
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     *
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
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
     *
     * @param checkout Object to be instantiated
     */
    public void initExtendedObject(ExtendedCheckoutObject checkout) {
        this.extendedCheckOutObject = checkout;
        course = checkout.getCourse();
        professor = checkout.getProf();
        dueDate = checkout.getExtendedDate();
    }

    /**
     * Initialize checkout object when returning from student page
     *
     * @param checkoutObject Object to initialize
     */
    public void initCheckoutObject(CheckoutObject checkoutObject) {
        this.checkoutObject = checkoutObject;
        studentID.setText(checkoutObject.getStudentID());

        // Yes... these two lines are actually required and work for some reason.
        // Without them, the suggested student ID drop down appears top left of screen.
        studentID.setVisible(false);
        studentID.setVisible(true);

        barcode.setText(checkoutObject.getBarcode());
        //quantity.setText(checkoutObject.getQuantity());
        if (checkoutObject.isExtended()) {
            extended.setSelected(true);
            isExtended();
//            courseName.setText(checkoutObject.getExtendedCourseName());
//            profName.setText(checkoutObject.getExtendedProfessor());
//            datePicker.setValue(checkoutObject.getExtendedReturnDate());
        } else if (checkoutObject.isFaulty()) {
            faulty.setSelected(true);
            //faultyTextArea.setText(checkoutObject.getFaultyDescription());
        }

        // enable the switch to student info button iff the student ID field contains a student ID
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){4,}$") || studentID.getText().matches("^\\w+[+.\\w'-]*@msoe\\.edu$")) {
            studentInfo.setDisable(false);
        } else {
            studentInfo.setDisable(true);
        }

        // enable the quantity field iff the barcode field contains a barcode for a part with
        // unique barcodes
        if (containsNumber(barcode.getText())) {
            partNameFromBarcode = database.getPartNameFromBarcode(Integer.parseInt(barcode.getText()));
            if (database.hasUniqueBarcodes(partNameFromBarcode)) {
//                quantity.setDisable(true);
//                quantity.setText("1");
            } else {
                //quantity.setDisable(false);
            }
        }
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
     *
     * @return True if student email has text
     */
    private boolean newStudentIsCheckingOutItem() {
        return studentNameField.getText().isEmpty();
    }


    /**
     * If student has overdue items, system will ask for override to checkout more items.
     *
     * @param student Student checking out items
     * @return Response if override is authorized
     */
    private boolean ensureNotOverdue(Student student) {
        if (!barcode.getText().equals("") && !itemIsBeingCheckedIn(Long.parseLong(barcode.getText()))
                || (!barcode2.getText().equals("") && !itemIsBeingCheckedIn(Long.parseLong(barcode2.getText())))
                || (!barcode3.getText().equals("") && !itemIsBeingCheckedIn(Long.parseLong(barcode3.getText())))
                || (!barcode4.getText().equals("") && !itemIsBeingCheckedIn(Long.parseLong(barcode4.getText())))
                || (!barcode5.getText().equals("") && !itemIsBeingCheckedIn(Long.parseLong(barcode5.getText())))) {
            if (student.getOverdueItems().size() > 0) {
                if ((worker != null && worker.isAdmin() || worker.isOver())) {
                    return ensureOverride();
                } else {
                    return requestAdminPin("override overdue");
                }
            }
        }
        return true;
    }

    /**
     * Request for admin pin.
     *
     * @param action Action required; pin to be entered
     * @return True if pin is correct
     */
    public boolean requestAdminPin(String action) {
        AtomicBoolean isValid = new AtomicBoolean(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminPinRequest.fxml"));
            Parent root = loader.load();
            ((AdminPinRequestController) loader.getController()).setAction(action);
            Scene scene = new Scene(root, 400, 250);
            Stage stage = new Stage();
            stage.setTitle("Admin Pin Required");
            stage.initOwner(main.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.setResizable(false);
            stage.setOnCloseRequest(e -> {
                // checks to see whether the pin was submitted or the window was just closed
                if (((AdminPinRequestController) loader.getController()).isSubmitted()) {
                    // checks to see if the input pin is empty. if empty, close pop up
                    if (((AdminPinRequestController) loader.getController()).isNotEmpty()) {
                        // checks to see whether the submitted pin matches one of the admin's pins
                        if (((AdminPinRequestController) loader.getController()).isValid()) {
                            stage.close();
                            isValid.set(true);
                        } else {
                            stage.close();
                            invalidAdminPinAlert();
                            isValid.set(false);
                        }
                    } else {
                        stage.close();
                        isValid.set(false);
                    }
                }
            });
            stage.showAndWait();
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Loading Admin Pin Request.");
            e.printStackTrace();
        }
        return isValid.get();
    }

    /**
     * Collects barcodes and pertinent information about them
     *
     * @return List of barcodes and their information
     */
    private List<MultipleCheckoutObject> collectMultipleBarcodes() {
        List<MultipleCheckoutObject> barcodeInfo = new LinkedList<>();
        if (barcodeIsNotEmpty(barcode)) {
            addBarcodes(getQuantitySpinner(), barcodeInfo, statusLabel, getBarcode(), checkboxSelected(extended1));
        }
        if (barcodeIsNotEmpty(barcode2)) {
            addBarcodes(getQuantitySpinner2(), barcodeInfo, statusLabel2, getBarcode2(), checkboxSelected(extended2));
        }
        if (barcodeIsNotEmpty(barcode3)) {
            addBarcodes(getQuantitySpinner3(), barcodeInfo, statusLabel3, getBarcode3(), checkboxSelected(extended3));
        }
        if (barcodeIsNotEmpty(barcode4)) {
            addBarcodes(getQuantitySpinner4(), barcodeInfo, statusLabel4, getBarcode4(), checkboxSelected(extended4));
        }
        if (barcodeIsNotEmpty(barcode5)) {
            addBarcodes(getQuantitySpinner5(), barcodeInfo, statusLabel5, getBarcode5(), checkboxSelected(extended5));
        }
        return barcodeInfo;
    }

    /**
     * Submits the information entered to checkouts/checkoutParts table or removes if item is being checked back in.
     */
    public void submit() {
        Student thisStudent = null;
        if (containsNumber(getstudentID())) {
            thisStudent = database.selectStudent(Integer.parseInt(getstudentID()), null);
        } else {
            thisStudent = database.selectStudent(-1, getstudentID());
        }
        database.initWorker(worker);
        if (ensureNotOverdue(thisStudent)) {
            if (!fieldsFilled()) {
                return;
            }
            if (extendedCheckoutIsSelected(getBarcode())) {
                if (extendedFieldsNotFilled()) {
                    stageWrapper.errorAlert("Some fields were not filled out for extended checkout");
                    return;
                }
                if (newStudentIsCheckingOutItem()) {
                    noStudentError();
                    return;
                }
                extendedCheckoutHelper(thisStudent.getRFID());
            } else if (itemBeingCheckedBackInIsFaulty(getBarcode())) {
                faultyCheckinHelper();
            } else if (newStudentIsCheckingOutItem()) {
                noStudentError();
                return;
            } else {
                submitMultipleItems();
            }
            stageWrapper.checkoutAlert("Success", "Part(s) Checked in/out successfully");
            reset();

        }
    }

    /**
     * Submits multiple items
     */
    private void submitMultipleItems() {
        Student thisStudent = null;
        if (containsNumber(getstudentID())) {
            thisStudent = database.selectStudent(Integer.parseInt(getstudentID()), null);
        } else {
            thisStudent = database.selectStudent(-1, getstudentID());
        }
        List<MultipleCheckoutObject> barcodes = collectMultipleBarcodes();
        for (MultipleCheckoutObject barcode : barcodes) {
            if (barcode.isCheckedOut()) {
                checkOut.addMultipleCheckouts(barcode.getBarcode(), barcode.getStudentID(), barcode.getQuantity());
            } else {
                for (int i = 0; i < barcode.getQuantity(); i++) {
                    checkOut.setItemtoCheckedin(barcode.getBarcode());
                }
            }
        }
    }

    /**
     * Helper method to add barcodes
     */
    private void addBarcodes(int quantity, List<MultipleCheckoutObject> barcodes, Label status, long barcode, boolean extendedStatus) {
        Student thisStudent = null;
        if (containsNumber(getstudentID())) {
            thisStudent = database.selectStudent(Integer.parseInt(getstudentID()), null);
        } else {
            thisStudent = database.selectStudent(-1, getstudentID());
        }
        database.initWorker(worker);
        boolean checkStatus = statusIsOut(status);
        barcodes.add(new MultipleCheckoutObject(barcode, thisStudent.getRFID(), checkStatus, quantity, extendedStatus));
    }

    /**
     * Helper to check if item is being checked in or out
     *
     * @param status Status of item
     * @return True if item is being checked out
     */
    private boolean statusIsOut(Label status) {
        return status.getText().equals("Out");
    }

    /**
     * Helper method to check if barcode is empty
     *
     * @param barcode Barcode field to be checked
     * @return True if barcode is not empty
     */
    private boolean barcodeIsNotEmpty(JFXTextField barcode) {
        return !(barcode.getText().isEmpty());
    }


    /**
     * Helper method to checkout an item
     */
    private void extendedCheckoutHelper(int id) {
        for (MultipleCheckoutObject barcode : collectMultipleBarcodes()) {
            for (int i = 0; i < barcode.getQuantity(); i++) {
                if (barcode.isExtended()) {
                    extendedCheckOut.addExtendedCheckout(barcode.getBarcode(), id, professor, course, dueDate);
                } else {
                    checkOut.addNewCheckoutItem(barcode.getBarcode(), id);
                }
            }
        }
    }

    private boolean checkboxSelected(JFXCheckBox box) {
        return box.isSelected();
    }


    /**
     * Helper method to checkin an item
     */
    private void faultyCheckinHelper() {
        faultyCheckIn.setPartToFaultyStatus(getBarcode());
        faultyCheckIn.addToFaultyTable(getBarcode(), faultyArea.getText());
        checkOut.setItemtoCheckedin(getBarcode());
    }

    /**
     * Checks if item is being checked in
     *
     * @return True if item is being checked in
     */
    private boolean itemIsBeingCheckedIn(long barcode) {
        checkoutParts = checkOut.returnCheckedOutObjects();
        int studentID = -1;
        if (containsNumber(getstudentID())) {
            studentID = Integer.parseInt(getstudentID());
        }

        // getStudentID returns -1 if the field does not contain a number
        if (containsNumber(getstudentID())) {
            CheckedOutPartsObject currentInfo = containsNumber(getstudentID()) ? new CheckedOutPartsObject(barcode,
                    database.selectStudent(studentID, null).getRFID()) : new CheckedOutPartsObject(barcode,
                    database.selectStudent(studentID, getstudentID()).getRFID());
            for (CheckedOutPartsObject checkoutPart : checkoutParts) {
                if (checkoutPart.equals(currentInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method if item is extended checkout
     *
     * @return True if item is extended
     */
    private boolean extendedCheckoutIsSelected(long barcode) {
        return !itemIsBeingCheckedIn(barcode) && extended.isSelected();
    }

    /**
     * Helper method if item being checked back in is faulty
     *
     * @return True if item is faulty
     */
    private boolean itemBeingCheckedBackInIsFaulty(long barcode) {
        return itemIsBeingCheckedIn(barcode) && faulty.isSelected();
    }


    /**
     * Returns to home, contains check if fields are filled out
     */
    public void returnHome() {
        if (fieldsFilled()) {
            if (!fieldsNotFilledDialog()) {
                return;
            }
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Menu.fxml"));
            Parent root = loader.load();
            IController controller = loader.<IController>getController();
            controller.initWorker(worker);
            main.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
        } catch (IOException invoke) {
            StudentCheckIn.logger.error("No valid stage was found to load. This could likely be because of a database disconnect.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
    }

    /**
     * Gets student name. If no student name is found in database it will create a new student, or update student.
     */
    private void getStudentName() {
        studentID.focusedProperty().addListener((ov, oldV, newV) -> {
            if (studentID.getText().isEmpty()) {
                return;
            }
            if (!newV) {
                extended.setDisable(false);
                resetButton.setDisable(false);
                String studentName = "";
                if (studentID.getText().matches("^\\w+[+.\\w'-]*@msoe\\.edu$")) {
                    studentName = student.getStudentNameFromEmail(studentID.getText());
                    if (student.getStudentIDFromEmail(studentID.getText().replace("'", "\\'"))) {
                        stageWrapper.errorAlert("Student is checking out equipment for first time\n They must use their student ID to check out an item");
                        reset();
                        return;
                    }

                } else if (studentID.getText().matches("^\\D*(?:\\d\\D*){4,}$")) {
                    studentName = student.getStudentNameFromID(studentID.getText());
                }
                if (studentName.isEmpty()) { //If student ID isn't in DB, asks for email to attach the id to.
                    String studentEmail = newStudentEmail();
                    studentName = student.getStudentNameFromEmail(studentEmail);

                    if (studentName.isEmpty()) {//Means student doesn't exist in database, so completely new one will be created
                        studentName = newStudentName();
                        if (studentName != null) {
                            if (studentName.contains(" ")) {
                                student.createNewStudent(Integer.parseInt(getstudentID()), studentEmail.replace("'", "\\'"), studentName.replace("'", "\\'"));
                            } else {
                                stageWrapper.errorAlert("Error, student name must contain first and last name separated by a space");
                            }
                        }

                    }
                    if (studentEmail != null) {
                        updateStudent(studentEmail);
                    }
                }
                studentNameField.setText(studentName);
            }
        });
    }

    /**
     * Updates student info given an email, will add the student ID
     *
     * @param studentEmail Email to be checked for
     */
    private void updateStudent(String studentEmail) {
        student.updateStudent(studentEmail.replace("'", "\\'"), Integer.parseInt(getstudentID()));
    }

    /**
     * Asks user for a student name
     *
     * @return Student name
     */
    private String newStudentName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Student Creation");
        dialog.setHeaderText("Student Name is not in System.\n Please Enter Name to Continue ");
        dialog.setContentText("First and last name\n Separate by space");
        dialog.showAndWait();
        return dialog.getResult();

    }

    /**
     * Asks user for student email
     *
     * @return Student email
     */
    private String newStudentEmail() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Student Creation");
        dialog.setHeaderText("Student ID is not in system.\n Please enter email to continue ");
        dialog.setContentText("Please enter Student Email");
        dialog.showAndWait();
        return dialog.getResult();
    }


    /**
     * Adds new student to database
     */
    private void noStudentError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("No student found in the system with associated ID");

        alert.showAndWait();
    }


    /**
     * Resets all fields
     */
    public void reset() {
        newStage("/fxml/CheckOutPage.fxml");
    }


    /**
     * Checks if fields are filled
     *
     * @return True if fields are not empty
     */
    private boolean fieldsFilled() {
        return !studentID.getText().isEmpty() | !barcode.getText().isEmpty();
    }

    /**
     * Only allows user to submit when all fields are filled out
     */
    private void unlockFields() {
        BooleanBinding binding;
        binding = studentID.textProperty().isEmpty()
                .or(barcode.textProperty().isEmpty())
                .or(studentNameField.textProperty().isEmpty());
        submitButton.disableProperty().bind(binding);
    }


    /**
     * Alert if user tries to return home and fields are filled
     *
     * @return True if user pressed ok, false otherwise
     */
    private boolean fieldsNotFilledDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Information may be lost");
        alert.setHeaderText("If you leave, unsubmitted information may be lost");
        alert.setContentText("Are you ok with this?");
        StudentCheckIn.logger.info("Some fields are not filled. Asking user if losing unsubmitted information is okay...");

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    private boolean ensureOverride() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Overdue Override");
        alert.setHeaderText("This student has overdue equipment.");
        alert.setContentText("Do you want to override and checkout anyway?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }


    /**
     * Sets checkin information
     */
    private void setCheckinInformation() {
        extended.setVisible(false);
        faulty.setVisible(true);
    }

    /**
     * Sets checkout information
     */
    private void setCheckoutInformation() {
        faulty.setVisible(false);
        extended.setVisible(true);
    }

    /**
     * Changes to student info tab
     *
     * @author Bailey Terry
     */
    public void goToStudent() {
        Student s = null;
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){4,}$")) {
            s = database.selectStudent(Integer.parseInt(studentID.getText()), null);
        } else if (studentID.getText().matches("^\\w+[+.\\w'-]*@msoe\\.edu$")) {
            s = database.selectStudent(-1, studentID.getText());
        }
        if (s != null && !s.getName().equals("")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Student.fxml"));
                Parent root = (Parent) loader.load();
                StudentPage sp = loader.getController();
                sp.setStudent(s);
                sp.initWorker(worker);
                checkoutObject = new CheckoutObject(studentID.getText(), barcode.getText(), "1", extended.isSelected(), faulty.isSelected());
                if (extended.isSelected()) {
                    //checkoutObject.initExtendedInfo(courseName.getText(), profName.getText(), datePicker.getValue());
                } else if (faulty.isSelected()) {
                    //checkoutObject.initFaultyInfo(faultyTextArea.getText());
                }
                sp.initCheckoutObject(checkoutObject);
                main.getScene().setRoot(root);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load student page");
                alert.initStyle(StageStyle.UTILITY);
                StudentCheckIn.logger.error("IOException: Couldn't load student page.");
                alert.showAndWait();
                e.printStackTrace();
            }
        } else {
            stageWrapper.errorAlert("No student found with asscoiated RFID");
        }
    }

    /**
     * Helper method to set the validators for fields
     */
    private void setFieldValidator() {
        stageWrapper.requiredInputValidator(studentID);
        stageWrapper.requiredInputValidator(barcode);
        acceptIntegerOnlyCheckout(barcode);
        acceptIntegerOnlyCheckout(barcode2);
        acceptIntegerOnlyCheckout(barcode3);
        acceptIntegerOnlyCheckout(barcode4);
        acceptIntegerOnlyCheckout(barcode5);
    }

    /**
     * Sets item status to in or out based on database queries
     */
    private void setItemStatus() {
        barcode.focusedProperty().addListener((ov, oldv, newV) -> {
            if (!newV) {
                if (itemIsBeingCheckedIn(getBarcode())) {
                    setCheckinInformation();
                } else {
                    setCheckoutInformation();
                }
            }
        });
    }


    /**
     * Gets barcode as text, returns as int
     *
     * @return barcode as integer
     */
    private long getBarcode() {
        if (!barcode.getText().isEmpty()) {
            return Long.parseLong(barcode.getText());
        }
        return 0;
    }

    /**
     * Gets barcode as text, returns as int
     *
     * @return barcode as integer
     */
    private long getBarcode2() {
        if (!barcode2.getText().isEmpty()) {
            return Long.parseLong(barcode2.getText());
        }
        return 0;
    }

    /**
     * Gets barcode as text, returns as int
     *
     * @return barcode as integer
     */
    private long getBarcode3() {
        if (!barcode3.getText().isEmpty()) {
            return Long.parseLong(barcode3.getText());
        }
        return 0;
    }

    /**
     * Gets barcode as text, returns as int
     *
     * @return barcode as integer
     */
    private long getBarcode4() {
        if (!barcode4.getText().isEmpty()) {
            return Long.parseLong(barcode4.getText());
        }
        return 0;
    }

    /**
     * Gets barcode as text, returns as int
     *
     * @return barcode as integer
     */
    private long getBarcode5() {
        if (!barcode5.getText().isEmpty()) {
            return Long.parseLong(barcode5.getText());
        }
        return 0;
    }


    private int getQuantitySpinner() {
        return Integer.parseInt(newQuantity.getValue().toString());
    }

    private int getQuantitySpinner2() {
        return Integer.parseInt(newQuantity2.getValue().toString());
    }

    private int getQuantitySpinner3() {
        return Integer.parseInt(newQuantity3.getValue().toString());
    }

    private int getQuantitySpinner4() {
        return Integer.parseInt(newQuantity4.getValue().toString());
    }

    private int getQuantitySpinner5() {
        return Integer.parseInt(newQuantity5.getValue().toString());
    }

    /**
     * Gets studentID as text, returns as int
     *
     * @return StudentID as integer
     */
    private String getstudentID() {
        String id = null;
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){4,}$")) {
            id = studentID.getText();
        } else if (studentID.getText().matches("^\\w+[+.\\w'-]*@msoe\\.edu$")) {
            id = studentID.getText();
        }
        return id;
    }


    /**
     * If extended is selected, more items will be displayed
     */
    public void isExtended() {
        if (extended.isSelected()) {
            stageWrapper.popupPage("fxml/ExtendedCheckout.fxml", main);
            initExtendedCheckoutBoxes(true);
        } else {
            initExtendedCheckoutBoxes(false);
        }
    }

    private void initExtendedCheckoutBoxes(boolean value) {
        setSelected(extended1, value);
        setSelected(extended2, value);
        setSelected(extended3, value);
        setSelected(extended4, value);
        setSelected(extended5, value);
    }

    private void setSelected(JFXCheckBox box, boolean value) {
        box.setVisible(value);
        box.setSelected(value);
    }

    /**
     * If faulty checkbox is shown, more items will be displayed
     */
    public void isFaulty() {
        if (faulty.isSelected()) {
            faultyText = faultyArea.getText();
            transitionHelper.faultyTransition(faulty, resetButton, submitButton, 50);
            transitionHelper.faultyBoxFadeTransition(faultyArea, -40);
            faultPane.toFront();
            faultyArea.setPrefColumnCount(400);
            faultyArea.setPrefRowCount(400);
            setCheckoutItemsDisable(true);
        } else {
            faultPane.toBack();
            barcode.requestFocus();
            transitionHelper.faultyTransition(faulty, resetButton, submitButton, -50);
            transitionHelper.faultyBoxFadeTransition(faultyArea, 40);
            setCheckoutItemsDisable(false);
        }
    }

    /**
     * Helper method for checking out items
     *
     * @param value True or false to disable buttons
     */
    private void setCheckoutItemsDisable(boolean value) {
        HBoxBarcode2.setVisible(!value);
        barcode2.setVisible(!value);
        studentID.setDisable(value);
        faultyArea.setVisible(value);

    }

    /**
     * Makes new barcode field
     */
    public void newBarcode1() {
        if (barcode2.isVisible()) {
            return;
        }
        setNewBarcodeFieldsHelper();
        NewBarcodeFieldHelper(HBoxBarcode2, barcode2, newQuantity2);
    }

    /**
     * New barcode field maker
     */
    public void dropBarcode2() {
        if (barcode3.isVisible()) {
            return;
        }
        barcodeDropHelper();
        NewBarcodeFieldHelper(HBoxBarcode3, barcode3, newQuantity3);
    }

    /**
     * New barcode field maker
     */
    public void dropBarcode3() {
        if (barcode4.isVisible()) {
            return;
        }
        barcodeDropHelper();
        NewBarcodeFieldHelper(HBoxBarcode4, barcode4, newQuantity4);
    }

    /**
     * New barcode field maker
     */
    public void dropBarcode4() {
        if (barcode5.isVisible()) {
            return;
        }
        barcodeDropHelper();
        NewBarcodeFieldHelper(HBoxBarcode5, barcode5, newQuantity5);
    }


    /**
     * Helper method for dropping barcode down
     */
    private void barcodeDropHelper() {
        //extended.setVisible(false);
        faulty.setVisible(false);
    }


    /**
     * Sets correct field info when new barcode field is added
     */
    private void setNewBarcodeFieldsHelper() {
        HBoxBarcode.setVisible(true);
        barcode2.setVisible(true);
        HBoxBarcode2.setVisible(true);
    }

    /**
     * Helper method to generate new barcode
     *
     * @param hBoxBarcode4 HBox parent to be used
     * @param barcode4     Barcode field to be used
     * @param newQuantity4 Quantity of parts
     */
    private void NewBarcodeFieldHelper(HBox hBoxBarcode4, JFXTextField barcode4, Spinner<Integer> newQuantity4) {
        transitionHelper.translateBarcodeItems(submitButton, resetButton, extended, faulty, 60);
        transitionHelper.fadeTransition(hBoxBarcode4);
        transitionHelper.fadeTransition(barcode4);
        transitionHelper.spinnerInit(newQuantity4);
        hBoxBarcode4.setVisible(true);
        barcode4.setVisible(true);
    }

    /**
     * Checks if input contains number
     *
     * @param input Input string entered
     * @return True if it input contains number
     */
    private static boolean containsNumber(String input) {
        boolean parsable = true;
        try {
            Integer.parseInt(input);
        } catch (Exception e) {
            parsable = false;
        }
        return parsable;
    }

    /**
     * Alert that the pin entered does not match one of the admin pins.
     */
    private void invalidAdminPinAlert() {
        stageWrapper.errorAlert("The entered pin is invalid");
    }

    /**
     * Helper method to initialize barcode field properties.
     */
    private void initialBarodeFieldFunctions() {
        barcode.setOnKeyReleased(event -> {
            statusLabel.setVisible(true);
            if (event.getCode() == KeyCode.TAB) {
                return;
            }

        });
        barcode.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (newValue.length() == 6) {
                            if (itemIsBeingCheckedIn(getBarcode())) {
                                setCheckinInformation();
                                statusLabel.setText("In");
                            } else {
                                setCheckoutInformation();
                                statusLabel.setText("Out");
                            }
                            if (containsNumber(barcode.getText())) {
                                partNameFromBarcode = database.getPartNameFromBarcode(Integer.parseInt(barcode.getText()));
                            }
                            if (barcodesSame(getBarcode())) {
                                newQuantity.setDisable(false);
                            } else {
                                newQuantity.setDisable(true);
                                transitionHelper.spinnerInit(newQuantity);
                            }
                            barcode2.requestFocus();
                        }

                    }
                });
    }

    /**
     * Checks if barcodes are same
     *
     * @param barcode to be checked
     * @return true if barcodes are same
     */
    private boolean barcodesSame(long barcode) {
        try {
            return checkOut.getAllBarcodes(barcode).get(0).equals(checkOut.getAllBarcodes(barcode).get(1));
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * Helper method to initialize student id field properties.
     */
    private void initialStudentFieldFunctions() {
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){4,}$") || studentID.getText().matches("^\\w+[+.\\w'-]*@msoe\\.edu$")) {
            studentInfo.setDisable(false);
        } else {
            studentInfo.setDisable(true);
        }

        studentID.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (studentID.getText().matches("^\\D*(?:\\d\\D*){4,}$")) {
                            studentInfo.setDisable(false);
                        } else if (studentID.getText().matches("^\\w+[+.\\w'-]*@msoe\\.edu$")) {
                            studentInfo.setDisable(false);
                            studentNameField.setText(student.getStudentNameFromEmail(studentID.getText().replace("'", "\\'")));
                        } else {
                            studentInfo.setDisable(true);
                        }
                    }
                }
        );

        rfidFilter(studentID);

    }

    /**
     * Helper method to tab down after a barcode is scanned
     */
    private void dropBarcode() {

        barcode2.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (newValue.length() == 6) {
                            if (itemIsBeingCheckedIn(getBarcode2())) {
                                statusLabel2.setText("In");
                                extended.setDisable(true);
                            } else {
                                statusLabel2.setText("Out");
                                extended.setDisable(false);
                            }
                            if (barcodesSame(getBarcode2())) {
                                newQuantity2.setDisable(false);
                            } else {
                                newQuantity2.setDisable(true);
                                transitionHelper.spinnerInit(newQuantity2);
                            }
                            barcode3.requestFocus();
                        }
                    }
                });
        barcode3.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (newValue.length() == 6) {
                            if (itemIsBeingCheckedIn(getBarcode3())) {
                                statusLabel3.setText("In");
                                extended.setDisable(true);
                            } else {
                                statusLabel3.setText("Out");
                                extended.setDisable(false);
                            }
                            if (barcodesSame(getBarcode3())) {
                                newQuantity3.setDisable(false);
                            } else {
                                newQuantity3.setDisable(true);
                                transitionHelper.spinnerInit(newQuantity3);
                            }
                            barcode4.requestFocus();
                        }
                    }
                });
        barcode4.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (newValue.length() == 6) {
                            if (itemIsBeingCheckedIn(getBarcode4())) {
                                statusLabel4.setText("In");
                                extended.setDisable(true);
                            } else {
                                statusLabel4.setText("Out");
                                extended.setDisable(false);
                            }
                            if (barcodesSame(getBarcode4())) {
                                newQuantity4.setDisable(false);
                            } else {
                                newQuantity4.setDisable(true);
                                transitionHelper.spinnerInit(newQuantity4);
                            }
                            barcode5.requestFocus();
                        }
                    }
                });

    }


    /**
     * Helper method to set items in or out
     */
    private void setLabelStatuses() {
//        barcode2.setOnKeyReleased(event -> {
//            if (event.getCode() == KeyCode.TAB) {
//                return;
//            }
//
//
//        });
//        barcode3.setOnKeyReleased(event -> {
//            if (event.getCode() == KeyCode.TAB) {
//                return;
//            }
//
//        });
//        barcode4.setOnKeyReleased(event -> {
//            if (event.getCode() == KeyCode.TAB) {
//                return;
//            }
//
//        });
        barcode5.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.TAB) {
                return;
            }
            if (itemIsBeingCheckedIn(getBarcode5())) {
                statusLabel5.setText("In");
                extended.setDisable(true);
            } else {
                statusLabel5.setText("Out");
                extended.setDisable(false);
            }
            if (barcodesSame(getBarcode5())) {
                newQuantity5.setDisable(false);
            } else {
                newQuantity5.setDisable(true);
                transitionHelper.spinnerInit(newQuantity5);
            }
        });
    }

    /**
     * If new rfid is scanned, submits the checkout
     *
     * @param textField Textfield for change to be applied to
     */
    private void acceptIntegerOnlyCheckout(JFXTextField textField) {

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            id.add(text);
            if (stageWrapper.getStudentID(id).contains("rfid:")) {
                submit();
                id.clear();
            }
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    /**
     * Filters for rfid
     *
     * @param textField Textfield to be filtered
     */
    private void rfidFilter(JFXTextField textField) {
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    //in focus
                } else {
                    String id = textField.getText();
                    if (textField.getText().contains("rfid:")) {
                        textField.setText(id.substring(5));
                    }
                }
            }
        });

    }

    public void newStage(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            IController controller = loader.<IController>getController();
            controller.initWorker(worker);
            main.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
            // NEEDED?
            //mainMenuScene.getChildren().clear();
        } catch (IOException invoke) {
            StudentCheckIn.logger.error("No valid stage was found to load. This could likely be because of a database disconnect.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
    }
}
