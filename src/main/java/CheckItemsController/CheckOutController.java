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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CheckOutController extends ControllerMenu implements IController, Initializable {

    @FXML
    private VBox main;

    @FXML

    private JFXTextField barcode, barcode2, barcode3, barcode4, barcode5, studentNameField;

    @FXML
    private AutoCompleteTextField studentID;


    @FXML
    private JFXCheckBox faulty, extended;

    @FXML
    private JFXButton studentInfo, submitButton, resetButton;

    @FXML
    private Spinner<Integer> newQuantity, newQuantity2, newQuantity3, newQuantity4, newQuantity5;

    @FXML
    private Label statusLabel,
            statusLabel2, statusLabel3, statusLabel4, statusLabel5;


    @FXML
    private TextArea faultyTextArea;


    @FXML
    private HBox HBoxBarcode, HBoxBarcode2, HBoxBarcode3, HBoxBarcode4, HBoxBarcode5;

    private PauseTransition delay;
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

    private Worker worker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
    }


    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }

    /**
     * Method to submit after new student ID is scanned
     *
     * @param keyEvent Keyevent recording any action
     */
    public void submitAfterStudentIDScanned(KeyEvent keyEvent) {
        studentIDVerifier.add(keyEvent.getCharacter());
        if (stageWrapper.getStudentID(studentIDVerifier).contains("rfid")) {
            submit();
            studentIDVerifier.clear();
        }
        StudentCheckIn.logger.info("New student ID scanned, submitting...");
    }


    /**
     * If no movement is recorded on page for 15 minutes, item will submit automatically
     */
    private void submitTimer() {
        int duration = 5;
        delay = new PauseTransition(Duration.minutes(duration));
        main.addEventFilter(InputEvent.ANY, evt -> delay.playFromStart());
        delay.setOnFinished(event -> submit());
        delay.play();
    }

    /**
     * Initializes extended object
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
     * @param checkoutObject Object to initialize
     */
    public void initCheckoutObject(CheckoutObject checkoutObject) {
        this.checkoutObject = checkoutObject;
        studentID.setText(checkoutObject.getStudentID());
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
            faultyTextArea.setText(checkoutObject.getFaultyDescription());
        }

        // enable the switch to student info button iff the student ID field contains a student ID
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){5}$") || studentID.getText().matches("^\\w+[+.\\w-]*@msoe\\.edu$")) {
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
     * Submits the information entered to checkouts/checkoutParts table or removes if item is being checked back in.
     */
    public void submit() {
        Student thisStudent = null;
        if (containsNumber(getstudentID())) {
            thisStudent = database.selectStudent(Integer.parseInt(getstudentID()), null);
        }else {
            thisStudent = database.selectStudent(-1, getstudentID());
        }        database.initWorker(worker);
        if (ensureNotOverdue(thisStudent)) {
            if (!fieldsFilled()) {
                return;
            }
            if (extendedCheckoutIsSelected(getBarcode())) {
//                if (newStudentIsCheckingOutItem()) {
//                    createNewStudent();
//                }
                extendedCheckoutHelper(thisStudent.getRFID());
            } else if (itemBeingCheckedBackInIsFaulty(getBarcode())) {
                faultyCheckinHelper();
//            } else if (newStudentIsCheckingOutItem()) {
//                createNewStudent();
//                checkOut.addNewCheckoutItem(getBarcode(), thisStudent.getRFID());
            } else {
                submitMultipleItems();
            }
            reset();
        }
    }

    /**
     * If student has overdue items, system will ask for override to checkout more items.
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
                    // checks to see whether the submitted pin matches one of the admin's pins
                    if (((AdminPinRequestController) loader.getController()).isValid()) {
                        stage.close();
                        isValid.set(true);
                    } else {
                        stage.close();
                        invalidAdminPinAlert();
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
     * @return List of barcodes and their information
     */
    private List<MultipleCheckoutObject> collectMultipleBarcodes() {
        List<MultipleCheckoutObject> barcodeInfo = new LinkedList<>();
        if (barcodeIsNotEmpty(barcode)) {
            addBarcodes(getQuantitySpinner(), barcodeInfo, statusLabel, getBarcode());
        }
        if (barcodeIsNotEmpty(barcode2)) {
            addBarcodes(getQuantitySpinner2(), barcodeInfo, statusLabel2, getBarcode2());
        }
        if (barcodeIsNotEmpty(barcode3)) {
            addBarcodes(getQuantitySpinner3(), barcodeInfo, statusLabel3, getBarcode3());
        }
        if (barcodeIsNotEmpty(barcode4)) {
            addBarcodes(getQuantitySpinner4(), barcodeInfo, statusLabel4, getBarcode4());
        }
        if (barcodeIsNotEmpty(barcode5)) {
            addBarcodes(getQuantitySpinner5(), barcodeInfo, statusLabel5, getBarcode5());
        }
        return barcodeInfo;
    }

    /**
     * Submits multiple items
     */
    private void submitMultipleItems() {
        Student thisStudent = null;
        if (containsNumber(getstudentID())) {
            thisStudent = database.selectStudent(Integer.parseInt(getstudentID()), null);
        }else {
            thisStudent = database.selectStudent(-1, getstudentID());
        }
        List<MultipleCheckoutObject> barcodes = collectMultipleBarcodes();
        for (MultipleCheckoutObject barcode : barcodes) {
            if (barcode.isCheckedOut()) {
                checkOut.addMultipleCheckouts(barcode.getBarcode(), barcode.getStudentID(), barcode.getQuantity());
            } else {
                for(int i = 0;i<barcode.getQuantity();i++) {
                    checkOut.setItemtoCheckedin(barcode.getBarcode());
                }
            }
        }
    }

    /**
     * Helper method to add barcodes
     */
    private void addBarcodes(int quantity, List<MultipleCheckoutObject> barcodes, Label status, long barcode) {
        Student thisStudent = null;
        if (containsNumber(getstudentID())) {
            thisStudent = database.selectStudent(Integer.parseInt(getstudentID()), null);
        }else {
            thisStudent = database.selectStudent(-1, getstudentID());
        }        database.initWorker(worker);
        boolean checkStatus = statusIsOut(status);
        barcodes.add(new MultipleCheckoutObject(barcode, thisStudent.getRFID(), checkStatus, quantity));
    }

    /**
     * Helper to check if item is being checked in or out
     * @param status Status of item
     * @return True if item is being checked out
     */
    private boolean statusIsOut(Label status) {
        return status.getText().equals("Out");
    }

//    /**
//     * Submits multiple items
//     */
//    private void submitMultipleItems() {
//        List<MultipleCheckoutObject> barcodes = collectMultipleBarcodes();
//        for (MultipleCheckoutObject barcode : barcodes) {
//            if (barcode.isCheckedOut()) {
//                checkOut.addMultipleCheckouts(barcode.getBarcode(), barcode.getStudentID(), barcode.getQuantity());
//            } else {
//                for(int i = 0;i<barcode.getQuantity();i++) {
//                    checkOut.setItemtoCheckedin(barcode.getBarcode());
//        List<Long> stripped = barcodes.stream().distinct().collect(Collectors.toList());
//        if(quantityIsOne()) {
//            for (Long aStripped : stripped) {
//                if (itemIsBeingCheckedIn(aStripped)) {
//                    checkOut.setItemtoCheckedin(aStripped);
//                } else {
//                    checkOut.addNewCheckoutItem(aStripped, thisStudent.getRFID());
//                }
//            }
//        }
//        else {
//            checkOut.addMultipleCheckouts(getBarcode(), thisStudent.getRFID(), getQuantitySpinner());
//
//        }
//        StudentCheckIn.logger.info("Submitting multiple items with barcodes: " + barcodes.toString());
//    }

    //    /**
//     * Helper method to check if barcode field contains any barccodes
//     * @param barcode Barcode field
//     * @return True if not empty
//     */
    private boolean barcodeIsNotEmpty(JFXTextField barcode) {
        return !(barcode.getText().isEmpty() || barcode.getText().equals("Removed"));
    }


    /**
     * Helper method to checkout an item
     */
    private void extendedCheckoutHelper(int id) {
        for(MultipleCheckoutObject barcode : collectMultipleBarcodes()){
            for(int i = 0; i<barcode.getQuantity(); i++){
                extendedCheckOut.addExtendedCheckout(barcode.getBarcode(), id, professor, course, dueDate);
            }
        }
    }


    /**
     * Helper method to checkin an item
     */
    private void faultyCheckinHelper() {
        faultyCheckIn.setPartToFaultyStatus(getBarcode());
        faultyCheckIn.addToFaultyTable(getBarcode(), faultyTextArea.getText());
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
     * Gets student name
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
                if (studentID.getText().matches("^\\w+[+.\\w-]*@msoe\\.edu$")) {
                    studentName = student.getStudentNameFromEmail(studentID.getText());
                } else if (studentID.getText().matches("^\\D*(?:\\d\\D*){5}$")) {
                    studentName = student.getStudentNameFromID(studentID.getText());
                }
                if (studentName.isEmpty()) { //If no student is found in database create new one
                    //setNewStudentDropdown();
                }
                studentNameField.setText(studentName);
            }
        });
    }


    /**
     * Adds new student to database
     */
    private void createNewStudent() {
        if (containsNumber(getstudentID())) {
            //student.createNewStudent(Integer.parseInt(getstudentID()), getStudentEmail(), getNewStudentName());
        }else {
            student.createNewStudent(database.selectStudent(-1, getstudentID()));
        }
    }

    /**
     * Checks if student is new
     *
     * @return True if student email has text
     */
//    private boolean newStudentIsCheckingOutItem() {
//        return !studentEmail.getText().isEmpty();
//    }

//    /**
//     * Drops down more fields to create a new student
//     */
//    private void setNewStudentDropdown() {
//        transitionHelper.translateNewStudentItems(scanBarcode, quantityLabel, barcode, quantity, extended, submitButton, resetButton);
//        transitionHelper.fadeTransitionNewStudentObjects(studentEmailLabel, studentEmail);
//        setItemStatusNewStudent();
//    }

    /**
     * Helper method to set button access
     */
    private void setItemStatusNewStudent() {
        HBoxBarcode.setVisible(false);

        studentNameField.setDisable(false);
        studentNameField.requestFocus();
    }


    /**
     * Resets all fields
     */
    public void reset() {
        stageWrapper.newStage("/fxml/test.fxml", main, worker);
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
                .or(barcode.textProperty().isEmpty());
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

    private boolean trus(){
        return true;
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
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){5}$")) {
            s = database.selectStudent(Integer.parseInt(studentID.getText()), null);
        }else if (studentID.getText().matches("^\\w+[+.\\w-]*@msoe\\.edu$")){
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
                    checkoutObject.initFaultyInfo(faultyTextArea.getText());
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
        //stageWrapper.requiredInputValidator(quantity);
//        stageWrapper.acceptIntegerOnly(studentID);
        //stageWrapper.acceptIntegerOnly(quantity);
        stageWrapper.acceptIntegerOnly(barcode);
        stageWrapper.acceptIntegerOnly(barcode2);
        stageWrapper.acceptIntegerOnly(barcode3);
        stageWrapper.acceptIntegerOnly(barcode4);
        stageWrapper.acceptIntegerOnly(barcode5);
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
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){5}$")) {
            id = studentID.getText();
        }else if (studentID.getText().matches("^\\w+[+.\\w-]*@msoe\\.edu$")){
            id = studentID.getText();
        }
        return id;
    }

//    /**
//     * When new student is being created, gets their email address
//     *
//     * @return Email address of new student
//     */
//    private String getStudentEmail() {
//        return studentEmail.getText();
//    }

    /**
     * Gets the name of a new student in database
     *
     * @return New student name
     */
    private String getNewStudentName() {
        return studentNameField.getText();
    }

    /**
     * If extended is selected, more items will be displayed
     */
    public void isExtended() {
        if (extended.isSelected()) {
            stageWrapper.popupPage("fxml/ExtendedCheckout.fxml", main);
        }
    }

    /**
     * If faulty checkbox is shown, more items will be displayed
     */
    public void isFaulty() {
        int translateFaultyDown = 75;
        int translateFaultyUp = -75;
        if (faulty.isSelected()) {
            setFaultyTransition(translateFaultyDown, true);
            setCheckoutItemsDisable(true);
            faultyTransitionItems(true);
        } else {
            if (faultyItemLossInfo()) {
                faulty.setSelected(true);
                return;
            }
            setFaultyTransition(translateFaultyUp, false);
            faultyTextArea.setText("");
            setCheckoutItemsDisable(false);
            faultyTransitionItems(false);

        }
    }

    /**
     * Alerts user if they click away and information could be lost
     *
     * @return User response to alert
     */
    private boolean faultyItemLossInfo() {
        if (!faultyTextArea.getText().isEmpty()) {
            return !fieldsNotFilledDialog();
        }
        return false;
    }

    /**
     * Helper method for checking out items
     *
     * @param value True or false to disable buttons
     */
    private void setCheckoutItemsDisable(boolean value) {
        barcode.setDisable(value);
        studentID.setDisable(value);
        HBoxBarcode.setVisible(!value);
    }

    private void faultyTransitionItems(boolean value) {
        barcode2.setVisible(!value);
        HBoxBarcode2.setVisible(!value);
        //quantity.setVisible(value);
        //quantityLabel.setVisible(value);
    }


    /**
     * Helper method to transition all faulty items
     *
     * @param direction    Direction to be moved in
     * @param showTextarea True if text area will be shown
     */
    private void setFaultyTransition(int direction, boolean showTextarea) {
        transitionHelper.translateButtons(submitButton, resetButton, direction);
        transitionHelper.faultyBoxFadeTransition(faulty);
        faultyTextArea.setVisible(showTextarea);
    }


    /**
     * Makes new barcode field
     */
    public void newBarcode1() {
        if(barcode2.isVisible()){
            return;
        }
        setNewBarcodeFieldsHelper();
        NewBarcodeFieldHelper(HBoxBarcode2, barcode2, newQuantity2);
//        transitionHelper.barcodeItemsFadeTransition(newQuantity, barcode2);
//        transitionHelper.fadeTransition(HBoxBarcode2);
//        transitionHelper.spinnerInit(newQuantity2);
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
       // faulty.setVisible(false);
    }


    /**
     * Sets correct field info when new barcode field is added
     */
    private void setNewBarcodeFieldsHelper() {
        //quantity.setVisible(false);
        //quantityLabel.setVisible(false);
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
            if (itemIsBeingCheckedIn(getBarcode())) {
                setCheckinInformation();
                statusLabel.setText("In");
            } else {
                setCheckoutInformation();
                statusLabel.setText("Out");
            }
            if (containsNumber(barcode.getText())) {
                partNameFromBarcode = database.getPartNameFromBarcode(Integer.parseInt(barcode.getText()));
//                if (database.hasUniqueBarcodes(partNameFromBarcode)) {
//
//                } else {
//
//                }
            }
            if (barcodesSame(getBarcode())) {
                newQuantity.setDisable(false);
            } else {
                newQuantity.setDisable(true);
                transitionHelper.spinnerInit(newQuantity);
            }
        });
    }

    private boolean barcodesSame(long barcode) {
        return checkOut.getAllBarcodes(barcode).get(0).equals(checkOut.getAllBarcodes(barcode).get(1));
    }

    /**
     * Helper method to initialize student id field properties.
     */
    private void initialStudentFieldFunctions(){
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){5}$") || studentID.getText().matches("^\\w+[+.\\w-]*@msoe\\.edu$")) {
            studentInfo.setDisable(false);
        } else {
            studentInfo.setDisable(true);
        }

        studentID.setOnKeyReleased(event -> {
            if (studentID.getText().matches("^\\D*(?:\\d\\D*){5}$") || studentID.getText().matches("^\\w+[+.\\w-]*@msoe\\.edu$")) {
                studentInfo.setDisable(false);
            } else {
                studentInfo.setDisable(true);
            }
        });

        // only allows user to enter 5 digits
        studentID.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("^\\D*(?:\\d\\D*){0,5}$") && !newValue.matches("^\\w+[+.\\w-]*@msoe\\.edu$")) {
                    studentID.setText(oldValue);
                }
            }
        });
    }

    private void setStudentEmailSuggestionListener() {
        ObservableList<Student> students = database.getStudents();

    }


    /**
     * Helper method to set items in or out
     */
    private void setLabelStatuses(){
        barcode2.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.TAB) {
                return;
            }
            if (itemIsBeingCheckedIn(getBarcode2())) {
                statusLabel2.setText("In");
                extended.setDisable(true);
            } else {
                statusLabel2.setText("Out");
            }
            if (barcodesSame(getBarcode2())) {
                newQuantity2.setDisable(false);
            } else {
                newQuantity2.setDisable(true);
                transitionHelper.spinnerInit(newQuantity2);
            }
        });
        barcode3.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.TAB) {
                return;
            }
            if (itemIsBeingCheckedIn(getBarcode3())) {
                statusLabel3.setText("In");
                extended.setDisable(true);
            } else {
                statusLabel3.setText("Out");
            }
            if (barcodesSame(getBarcode3())) {
                newQuantity3.setDisable(false);
            } else {
                newQuantity3.setDisable(true);
                transitionHelper.spinnerInit(newQuantity3);
            }
        });
        barcode4.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.TAB) {
                return;
            }
            if (itemIsBeingCheckedIn(getBarcode4())) {
                statusLabel4.setText("In");
                extended.setDisable(true);
            } else {
                statusLabel4.setText("Out");
            }
            if (barcodesSame(getBarcode4())) {
                newQuantity4.setDisable(false);
            } else {
                newQuantity4.setDisable(true);
                transitionHelper.spinnerInit(newQuantity4);
            }
        });
        barcode5.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.TAB) {
                return;
            }
            if (itemIsBeingCheckedIn(getBarcode5())) {
                statusLabel5.setText("In");
                extended.setDisable(true);
            } else {
                statusLabel5.setText("Out");
            }
            if (barcodesSame(getBarcode5())) {
                newQuantity5.setDisable(false);
            } else {
                newQuantity5.setDisable(true);
                transitionHelper.spinnerInit(newQuantity5);
            }
        });
    }
}
