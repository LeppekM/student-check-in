package CheckItemsController;

import Database.*;
import HelperClasses.StageWrapper;
import InventoryController.ControllerMenu;
import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import javafx.util.Duration;


import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ControllerCheckoutPage extends ControllerMenu implements Initializable {
    @FXML
    private AnchorPane main;

    @FXML
    private JFXSpinner loadIndicator;

    @FXML
    private JFXTextField studentID, barcode, barcode2, barcode3, barcode4, barcode5, quantity, profName, courseName, studentNameField, studentEmail;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private JFXCheckBox faulty, extended;

    @FXML
    private JFXButton studentInfo, submitButton, home, resetButton, addNewBarcode, addNewBarcode2, addNewBarcode3, addNewBarcode4, addNewBarcode5,
            deleteBarcode, deleteBarcode2, deleteBarcode3, deleteBarcode4, deleteBarcode5;

    @FXML
    private Spinner<Integer> newQuantity, newQuantity2, newQuantity3, newQuantity4, newQuantity5;

    @FXML
    private Label itemStatus, studentNameText, profNameLabel, courseNameLabel, dueAt, checkoutHeader, quantityLabel, studentEmailLabel, scanBarcode;

    @FXML
    private TextArea faultyTextArea;

    @FXML
    private JFXToggleButton checkingOutToggle;

    @FXML
    private HBox HBoxBarcode, HBoxBarcode2, HBoxBarcode3, HBoxBarcode4, HBoxBarcode5;

    private CheckoutObject checkoutObject;

    private StageWrapper stageWrapper = new StageWrapper();
    private Database database = new Database();
    //private CheckedOutParts checkedOutParts = new CheckedOutParts();
    private CheckingOutPart checkOut = new CheckingOutPart();
    private StudentInfo student = new StudentInfo();
    private TransitionHelper transitionHelper = new TransitionHelper();
    private ExtendedCheckOut extendedCheckOut = new ExtendedCheckOut();
    private FaultyCheckIn faultyCheckIn = new FaultyCheckIn();
    private String partNameFromBarcode;
    private List<CheckedOutPartsObject> checkoutParts = new ArrayList<>();
    private List<String> studentIDVerifier = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        home.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        studentInfo.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        setFieldValidator();
        setItemStatus();
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){5}$")) {
            studentInfo.setDisable(false);
        } else {
            studentInfo.setDisable(true);
        }

        quantity.setDisable(true);
        barcode.setOnKeyReleased(event -> {
            if (containsNumber(barcode.getText())) {
                partNameFromBarcode = database.getPartNameFromBarcode(Integer.parseInt(barcode.getText()));
                if (database.hasUniqueBarcodes(partNameFromBarcode)) {
                    quantity.setDisable(true);
                    quantity.setText("1");
                } else {
                    quantity.setDisable(false);
                }
            }
        });
        studentID.setOnKeyReleased(event -> {
            if (studentID.getText().matches("^\\D*(?:\\d\\D*){5}$")) {
                studentInfo.setDisable(false);
            } else {
                studentInfo.setDisable(true);
            }
        });

        // only allows user to enter 5 digits
        studentID.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("^\\D*(?:\\d\\D*){0,5}$")) {
                    studentID.setText(oldValue);
                }
            }
        });
        setItemStatus();
        getStudentName();
        unlockFields();
        unlockExtended();
        transitionHelper.spinnerInit(newQuantity);
        submitTimer();
        //barcodeListener();
    }

    /**
     * Method to submit after new student ID is scanned
     * @param keyEvent Keyevent recording any action
     */
    public void submitAfterStudentIDScanned(KeyEvent keyEvent){
        studentIDVerifier.add(keyEvent.getCharacter());
        if(stageWrapper.getStudentID(studentIDVerifier).contains("rfid")) {
            submit();
            studentIDVerifier.clear();
        }
    }


    /**
     * If no movement is recorded on page for 15 minutes, item will submit automatically
     */
    private void submitTimer(){
        int duration =15;
        PauseTransition delay = new PauseTransition(Duration.minutes(duration));
        delay.setOnFinished(event -> submit());
        main.addEventFilter(InputEvent.ANY, evt -> delay.playFromStart());
        delay.play();
    }


    public void initCheckoutObject(CheckoutObject checkoutObject) {
        this.checkoutObject = checkoutObject;
        studentID.setText(checkoutObject.getStudentID());
        barcode.setText(checkoutObject.getBarcode());
        quantity.setText(checkoutObject.getQuantity());
        if (checkoutObject.isExtended()) {
            extended.setSelected(true);
            isExtended();
            courseName.setText(checkoutObject.getExtendedCourseName());
            profName.setText(checkoutObject.getExtendedProfessor());
            datePicker.setValue(checkoutObject.getExtendedReturnDate());
        } else if (checkoutObject.isFaulty()) {
            faulty.setSelected(true);
            faultyTextArea.setText(checkoutObject.getFaultyDescription());
        }

        // enable the switch to student info button iff the student ID field contains a student ID
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){5}$")) {
            studentInfo.setDisable(false);
        } else {
            studentInfo.setDisable(true);
        }

        // enable the quantity field iff the barcode field contains a barcode for a part with
        // unique barcodes
        if (containsNumber(barcode.getText())) {
            partNameFromBarcode = database.getPartNameFromBarcode(Integer.parseInt(barcode.getText()));
            if (database.hasUniqueBarcodes(partNameFromBarcode)) {
                quantity.setDisable(true);
                quantity.setText("1");
            } else {
                quantity.setDisable(false);
            }
        }
    }

    /**
     * Sets cursor to next field
     */
    public void checkStudentHasOverdue() {
        Student thisStudent = database.selectStudent(Integer.parseInt(studentID.getText()));
        if (thisStudent.getOverdueItems().size() != 0 && checkingOutToggle.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Student has overdue items, they cannot checkout more items");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
        //barcode.requestFocus();
    }

    /**
     * Submits the information entered to checkouts/checkoutParts table or removes if item is being checked back in.
     */
    public void submit() {
        Student thisStudent = database.selectStudent(getstudentID());
        if(!fieldsFilled()){
            stageWrapper.errorAlert("Please fill out all fields before submitting info!");
            return;
        }
        if (thisStudent.getOverdueItems().size() == 0) {
            if(multipleItemsBeingCheckedOut()){
                submitMultipleItems();
            } else if (extendedCheckoutIsSelected(getBarcode())) {
                if(newStudentIsCheckingOutItem()){
                    createNewStudent();
                }
                extendedCheckoutHelper();
            } else if (itemBeingCheckedBackInIsFaulty(getBarcode())) {
                faultyCheckinHelper();
            } else if (itemIsBeingCheckedIn(getBarcode())) {
                checkOut.setItemtoCheckedin(getBarcode());
            } else {
                if(newStudentIsCheckingOutItem()){
                    createNewStudent();
                }
                checkOut.addNewCheckoutItem(getBarcode(), getstudentID());
            }
            reset();
        } else { //todo: check to see if there are overdue items that arent saved, if there is only saved items overdue then don't show popup
            stageWrapper.errorAlert("Student has overdue items and cannot check anything" + " else out until they return or pay for these items");
        }
    }

    /**
     * Checks if multiple items being checked out
     * @return True if multiple items being checked out
     */
    private boolean multipleItemsBeingCheckedOut(){
        return (!barcode2.getText().isEmpty() | !barcode3.getText().isEmpty() | !barcode4.getText().isEmpty() | !barcode5.getText().isEmpty());
    }

    /**
     * Submits multiple items
     */
    private void submitMultipleItems(){
        List<Integer> barcodes = new ArrayList<>();
        if(!barcode.getText().isEmpty()){
            barcodes.add(getBarcode());
        }
        if(!barcode2.getText().isEmpty()){
            barcodes.add(getBarcode2());
        }
        if(!barcode3.getText().isEmpty()){
            barcodes.add(getBarcode3());
        }
        if(!barcode4.getText().isEmpty()){
            barcodes.add(getBarcode4());
        }
        if(!barcode5.getText().isEmpty()){
            barcodes.add(getBarcode5());
        }

        for (int i =0; i<barcodes.size(); i++){
            if(itemIsBeingCheckedIn(barcodes.get(i))){
                checkOut.setItemtoCheckedin(barcodes.get(i));
            }
            else {
                checkOut.addNewCheckoutItem(barcodes.get(i), getstudentID());
            }
        }
    }


    /**
     * Helper method to checkout an item
     */
    private void extendedCheckoutHelper() {
        extendedCheckOut.addExtendedCheckout(getBarcode(), getstudentID(), getProfName(), getCourseName(), getExtendedDueDate());
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
     * @return True if item is being checked in
     */
    private boolean itemIsBeingCheckedIn(int barcode) {
        checkoutParts = checkOut.returnCheckedOutObjects();
        int studentID = getstudentID();

        // getStudentID returns -1 if the field does not contain a number
        if (studentID != -1) {
            CheckedOutPartsObject currentInfo = new CheckedOutPartsObject(barcode, getstudentID());
            for (int i = 0; i < checkoutParts.size(); i++) {
                if (checkoutParts.get(i).equals(currentInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method if item is extended checkout
     * @return True if item is extended
     */
    private boolean extendedCheckoutIsSelected(int barcode) {
        return !itemIsBeingCheckedIn(barcode) && extended.isSelected();
    }

    /**
     * Helper method if item being checked back in is faulty
     * @return True if item is faulty
     */
    private boolean itemBeingCheckedBackInIsFaulty(int barcode) {
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
        stageWrapper.newStage("fxml/Menu.fxml", main);
    }

    /**
     * Gets student name
     */
    private void getStudentName() {
        studentID.focusedProperty().addListener((ov, oldV, newV) -> {
            if(studentID.getText().isEmpty()){
                return;
            }
            if (!newV) {
                extended.setDisable(false);
                resetButton.setDisable(false);
                addNewBarcode.setDisable(false);
                String studentName = student.getStudentNameFromID(studentID.getText());
                if(studentName.isEmpty()){ //If no student is found in database create new one
                    setNewStudentDropdown();
                    addNewBarcode.setVisible(false);
                }
                studentNameField.setText(student.getStudentNameFromID(studentID.getText()));
            }
        });
    }


    /**
     * Adds new student to database
     */
    private void createNewStudent(){
        student.createNewStudent(getstudentID(), getStudentEmail(), getNewStudentName());
    }

    /**
     * Checks if student is new
     * @return True if student email has text
     */
    private boolean newStudentIsCheckingOutItem(){
        return !studentEmail.getText().isEmpty();
    }

    /**
     * Drops down more fields to create a new student
     */
    private void setNewStudentDropdown(){
        transitionHelper.translateExtendedStudentItems(courseNameLabel, profNameLabel, dueAt, courseName, profName, datePicker, extended, submitButton, resetButton);
        transitionHelper.translateNewStudentItems(scanBarcode, quantityLabel, barcode, quantity, extended, submitButton, resetButton);
        transitionHelper.fadeTransitionNewStudentObjects(studentEmailLabel, studentEmail);
        setItemStatusNewStudent();
    }

    /**
     * Helper method to set button access
     */
    private void setItemStatusNewStudent(){
        studentEmail.setVisible(true);
        studentEmailLabel.setVisible(true);
        studentNameField.setDisable(false);
        studentNameField.requestFocus();
    }


    /**
     * Resets all fields
     */
    public void reset() {
        stageWrapper.newStage("fxml/CheckOutItems.fxml", main);
    }

    /**
     * Checks if fields are filled
     *
     * @return True if fields are not empty
     */
    private boolean fieldsFilled() {
        return !studentID.getText().isEmpty() | !barcode.getText().isEmpty() | quantity.getText().isEmpty();
    }

    /**
     * Only allows user to submit when all fields are filled out
     */
    private void unlockFields() {
        BooleanBinding binding;
        binding = quantity.textProperty().isEmpty()
                .or(studentID.textProperty().isEmpty())
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

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    /**
     * Sets checkin information
     */
    private void setCheckinInformation() {
        extended.setVisible(false);
        faulty.setVisible(true);
        itemStatus.setText("Checking In");
    }

    /**
     * Sets checkout information
     */
    private void setCheckoutInformation() {
        faulty.setVisible(false);
        extended.setVisible(true);
        itemStatus.setText("Checking Out");
    }

    /**
     * Changes to student info tab
     *
     * @author Bailey Terry
     */
    public void goToStudent() {
        Student s = database.selectStudent(Integer.parseInt(studentID.getText()));
        if (!s.getName().equals("")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Student.fxml"));
                Parent root = (Parent) loader.load();
                StudentPage sp = loader.getController();
                sp.setStudent(s);
                checkoutObject = new CheckoutObject(studentID.getText(), barcode.getText(), quantity.getText(), extended.isSelected(), faulty.isSelected());
                if (extended.isSelected()) {
                    checkoutObject.initExtendedInfo(courseName.getText(), profName.getText(), datePicker.getValue());
                } else if (faulty.isSelected()) {
                    checkoutObject.initFaultyInfo(faultyTextArea.getText());
                }
                sp.initCheckoutObject(checkoutObject);
                main.getScene().setRoot(root);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load student page");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no student found with associated RFID");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
    }

    /**
     * Helper method to set the validators for fields
     */
    private void setFieldValidator() {
        stageWrapper.requiredInputValidator(studentID);
        stageWrapper.requiredInputValidator(barcode);
        stageWrapper.requiredInputValidator(quantity);
        stageWrapper.acceptIntegerOnly(studentID);
        stageWrapper.acceptIntegerOnly(quantity);
        stageWrapper.acceptIntegerOnly(barcode);
    }

    /**
     * Sets item status to in or out based on database queries
     */
    private void setItemStatus() {
        barcode.focusedProperty().addListener((ov, oldv, newV) -> {
            if (!newV && !barcode.getText().isEmpty()) {
                if (itemIsBeingCheckedIn(getBarcode())) {
                    setCheckinInformation();
                } else {
                    setCheckoutInformation();
                }
            }
            if(!newV){
                main.requestFocus();
            }
        });
    }

    /**
     * Gets barcode as text, returns as int
     *
     * @return barcode as integer
     */
    private int getBarcode() { return Integer.parseInt(barcode.getText());}


    private int getBarcode2() {return Integer.parseInt(barcode2.getText());}

    private int getBarcode3() {return Integer.parseInt(barcode3.getText());}

    private int getBarcode4() {return Integer.parseInt(barcode4.getText());}

    private int getBarcode5() {return Integer.parseInt(barcode5.getText());}

    /**
     * Gets quantity as text, returns as int
     *
     * @return quantity as integer
     */
    private int getQuantity() {
        return Integer.parseInt(quantity.getText());
    }

    /**
     * Gets studentID as text, returns as int
     *
     * @return StudentID as integer
     */
    private int getstudentID() {
        if (containsNumber(studentID.getText())) {
            return Integer.parseInt(studentID.getText());
        } else {
            return -1;
        }
    }

    /**
     * When new student is being created, gets their email address
     * @return Email address of new student
     */
    private String getStudentEmail(){
        return studentEmail.getText();
    }

    /**
     * Gets the name of a new student in database
     * @return New student name
     */
    private String getNewStudentName(){
        return studentNameField.getText();
    }

    /**
     * Gets professor name
     * @return Professor name
     */
    private String getProfName() {
        return profName.getText();
    }

    /**
     * Gets course name
     * @return Course name
     */
    private String getCourseName() {
        return courseName.getText();
    }

    /**
     * Gets extended due date
     * @return Return extended date
     */
    private String getExtendedDueDate() {
        return datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Disables submitting information until all fields are filled out for extended checkbox.
     */
    private void unlockExtended() {
        BooleanBinding binding;
        if (extended.isSelected()) {
            binding = courseName.textProperty().isEmpty()
                    .or(profName.textProperty().isEmpty())
                    .or(studentID.textProperty().isEmpty())
                    .or(barcode.textProperty().isEmpty())
                    .or(quantity.textProperty().isEmpty())
                    .or(datePicker.valueProperty().isNull());
            submitButton.disableProperty().bind(binding);
        } else {
            unlockFields();
        }
    }

    /**
     * If extended is selected, more items will be displayed
     */
    public void isExtended() {
        unlockExtended();
        int translateDown = 190;
        int translateUp = -190;
        if (extended.isSelected()) {
            setExtendedTransition(translateDown, true);
            setCheckoutItemsDisable(true);
        } else {
            if (loseExtendedInformation()) {
                extended.setSelected(true);
                return;
            }
            resetExtended();
            setExtendedTransition(translateUp, false);
            setCheckoutItemsDisable(false);
        }
    }

    /**
     * Resets extended fields.
     */
    private void resetExtended() {
        courseName.setText("");
        profName.setText("");
        datePicker.setValue(null);
    }

    /**
     * Helper method to show extended fields
     *
     * @param direction Direction items will be moved in
     * @param showItems True if items should be shown
     */
    private void setExtendedTransition(int direction, boolean showItems) {
        translateButtons(direction);
        translateExtended(direction);
        extendedItemsFadeTransition();
        setExtendedItemsVisible(showItems);
    }

    /**
     * Helper method to set extended items to be visible
     *
     * @param isVisible True if items should be shown
     */
    private void setExtendedItemsVisible(boolean isVisible) {
        dueAt.setVisible(isVisible);
        courseName.setVisible(isVisible);
        profName.setVisible(isVisible);
        datePicker.setVisible(isVisible);
        courseNameLabel.setVisible(isVisible);
        profNameLabel.setVisible(isVisible);
    }

    /**
     * If faulty checkbox is shown, more items will be displayed
     */
    public void isFaulty() {
        int translateFaultyDown = 125;
        int translateFaultyUp = -125;
        if (faulty.isSelected()) {
            setFaultyTransition(translateFaultyDown, true);
            setCheckoutItemsDisable(true);
        } else {
            if (faultyItemLossInfo()) {
                faulty.setSelected(true);
                return;
            }
            setFaultyTransition(translateFaultyUp, false);
            faultyTextArea.setText("");
            setCheckoutItemsDisable(false);

        }
    }

    /**
     * Fields to check if user clicks away
     * @return Returns true if fields are not empty
     */
    private boolean extendedItemLossInfo() {
        return !(courseName.getText().isEmpty() | profName.getText().isEmpty() | datePicker.getValue() == null);
    }

    /**
     * Alerts user if they click away and information could be lost
     * @return User response to alert
     */
    private boolean loseExtendedInformation() {
        if (extendedItemLossInfo()) {
            if (!fieldsNotFilledDialog()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Alerts user if they click away and information could be lost
     * @return User response to alert
     */
    private boolean faultyItemLossInfo() {
        if (!faultyTextArea.getText().isEmpty()) {
            if (!fieldsNotFilledDialog()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method for checking out items
     * @param value True or false to disable buttons
     */
    private void setCheckoutItemsDisable(boolean value) {
        barcode.setDisable(value);
        studentID.setDisable(value);
        addNewBarcode.setDisable(value);
    }


    /**
     * Helper method to transition all faulty items
     *
     * @param direction    Direction to be moved in
     * @param showTextarea True if text area will be shown
     */
    private void setFaultyTransition(int direction, boolean showTextarea) {
        translateButtons(direction);
        faultyBoxFadeTransition();
        faultyTextArea.setVisible(showTextarea);
    }

    /**
     * Translates extended checkbox
     *
     * @param direction Direction to be translated
     */
    private void translateExtended(int direction) {
        int duration = 500;
        TranslateTransition t = new TranslateTransition(Duration.millis(duration), extended);
        t.setByY(direction);
        t.play();
    }

    /**
     * Translates buttons vertically
     *
     * @param direction Direction to move
     */
    private void translateButtons(int direction) {
        int duration = 500;
        TranslateTransition transition = new TranslateTransition(Duration.millis(duration), submitButton);
        TranslateTransition transition2 = new TranslateTransition(Duration.millis(duration), resetButton);
        transition.setByY(direction);
        transition2.setByY(direction);
        transition.play();
        transition2.play();
    }

    /**
     * Helper method to fade faulty textbox
     */
    private void faultyBoxFadeTransition() {
        int initial = 0;
        int end = 1;
        int duration = 500;
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), faultyTextArea);
        fadeTransition.setFromValue(initial);
        fadeTransition.setToValue(end);
        fadeTransition.play();
    }

    /**
     * Helper method that transitions item to be visible
     */
    private void extendedItemsFadeTransition() {
        List<FadeTransition> transitions = new ArrayList<>();
        int initial = 0;
        int end = 1;
        int numItems = 6;
        int duration = 750;

        transitions.add(new FadeTransition(Duration.millis(duration), dueAt));
        transitions.add(new FadeTransition(Duration.millis(duration), datePicker));
        transitions.add(new FadeTransition(Duration.millis(duration), courseName));
        transitions.add(new FadeTransition(Duration.millis(duration), courseNameLabel));
        transitions.add(new FadeTransition(Duration.millis(duration), profName));
        transitions.add(new FadeTransition(Duration.millis(duration), profNameLabel));
        for (int i = 0; i < numItems; i++) {
            transitions.get(i).setFromValue(initial);
            transitions.get(i).setToValue(end);
            transitions.get(i).play();
        }
    }

    /**
     * Creates new barcode field
     */
    public void newBarcode() {
        setNewBarcodeFieldsHelper();
        transitionHelper.barcodeItemsFadeTransition(newQuantity, deleteBarcode, barcode2);
        transitionHelper.fadeTransition(HBoxBarcode2);
        transitionHelper.spinnerInit(newQuantity2);
        addNewBarcode.setDisable(true);
        barcode2.requestFocus();
    }

    /**
     * Sets correct field info when new barcode field is added
     */
    private void setNewBarcodeFieldsHelper() {
        extended.setVisible(false);
        faulty.setVisible(false);
        quantity.setVisible(false);
        quantityLabel.setVisible(false);
        newQuantity.setVisible(true);
        deleteBarcode.setVisible(true);
        barcode2.setVisible(true);
        HBoxBarcode2.setVisible(true);
    }

    /**
     * Creates new barcode field
     */
    public void newBarcode2() {
        NewBarcodeFieldHelper(HBoxBarcode3, barcode3, newQuantity3, addNewBarcode2);
        barcode3.requestFocus();
    }

    /**
     * Creates new barcode field
     */
    public void newBarcode3() {
        NewBarcodeFieldHelper(HBoxBarcode4, barcode4, newQuantity4, addNewBarcode3);
        barcode4.requestFocus();
    }

    /**
     * Creates new barcode field
     */
    public void newBarcode4() {
        NewBarcodeFieldHelper(HBoxBarcode5, barcode5, newQuantity5, addNewBarcode4);
        barcode5.requestFocus();
    }

    /**
     * Removes barcode
     */
    public void deleteBarcode1(){
        deleteBarcodeHelper(barcode, HBoxBarcode);
    }

    public void deleteBarcode2(){
        deleteBarcodeHelper(barcode2, HBoxBarcode2);
    }

    public void deleteBarcode3(){
        deleteBarcodeHelper(barcode3, HBoxBarcode3);
    }

    public void deleteBarcode4(){
        deleteBarcodeHelper(barcode4, HBoxBarcode4);
    }

    public void deleteBarcode5(){
        deleteBarcodeHelper(barcode5, HBoxBarcode5);
    }


    private void deleteBarcodeHelper(JFXTextField barcode, HBox hbox){
        barcode.clear();
        barcode.setDisable(true);
        barcode.setText("Removed");
        hbox.setVisible(false);
    }

    /**
     * Helper method to generate new barcode
     * @param hBoxBarcode4 HBox parent to be used
     * @param barcode4 Barcode field to be used
     * @param newQuantity4 Quantity of parts
     */
    private void NewBarcodeFieldHelper(HBox hBoxBarcode4, JFXTextField barcode4, Spinner<Integer> newQuantity4, JFXButton add) {
        add.setDisable(true);
        transitionHelper.translateBarcodeItems(submitButton, resetButton, extended, faulty, 60);
        transitionHelper.fadeTransition(hBoxBarcode4);
        transitionHelper.fadeTransition(barcode4);
        transitionHelper.spinnerInit(newQuantity4);
        hBoxBarcode4.setVisible(true);
        barcode4.setVisible(true);
        itemStatus.setText("Items are being checked in/out");
    }

    /**
     * Checks if input contains number
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

}
