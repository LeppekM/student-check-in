package CheckItemsController;

import Database.*;
import HelperClasses.StageWrapper;
import InventoryController.ControllerMenu;
import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import javafx.util.Duration;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
    private JFXTextField studentID, barcode,barcode2, barcode3, barcode4, barcode5, quantity, profName, courseName;

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
    private Label itemStatus, studentNameText, profNameLabel, courseNameLabel, dueAt, checkoutHeader, quantityLabel;

    @FXML
    private TextArea faultyTextArea;

    @FXML
    private JFXToggleButton checkingOutToggle;

    @FXML
    private HBox HBoxBarcode2, HBoxBarcode3, HBoxBarcode4, HBoxBarcode5;

    private CheckoutObject checkoutObject;

    private StageWrapper stageWrapper = new StageWrapper();
    private Database database = new Database();
    //private CheckedOutParts checkedOutParts = new CheckedOutParts();
    private CheckingOutPart checkOut = new CheckingOutPart();
    private StudentInfo student = new StudentInfo();
    private NewBarcodeHelper barcodeHelper = new NewBarcodeHelper();
    private ExtendedCheckOut extendedCheckOut = new ExtendedCheckOut();
    private FaultyCheckIn faultyCheckIn = new FaultyCheckIn();
    private String partNameFromBarcode;
    private List<CheckedOutPartsObject> checkoutParts = new ArrayList<>();

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
        barcodeHelper.spinnerInit(newQuantity);
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
        if (thisStudent.getOverdueItems().size() != 0 && checkingOutToggle.isSelected()){
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
        Student thisStudent = database.selectStudent(Integer.parseInt(studentID.getText()));
        if (thisStudent.getOverdueItems().size() == 0 ) {
            if (extendedCheckoutIsSelected()) {
                extendedCheckoutHelper();
            } else if(itemBeingCheckedBackInIsFaulty()){
                faultyCheckinHelper();
            } else if(itemIsBeingCheckedIn()){
                checkOut.setItemtoCheckedin(getBarcode());
            }
            else {
                checkOut.addNewCheckoutItem(getBarcode(), getstudentID());
            }
            reset();
        }else { //todo: check to see if there are overdue items that arent saved, if there is only saved items overdue then don't show popup
            Alert alert = new Alert(Alert.AlertType.ERROR, "Student has overdue items and cannot check anything" +
                    " else out until they return or pay for these items");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
    }



    private void extendedCheckoutHelper(){
        extendedCheckOut.addExtendedCheckout(getBarcode(), getstudentID(), getProfName(), getCourseName(), getExtendedDueDate());
    }

    private void faultyCheckinHelper(){
        faultyCheckIn.setPartToFaultyStatus(getBarcode());
        faultyCheckIn.addToFaultyTable(getBarcode(), faultyTextArea.getText());
        checkOut.setItemtoCheckedin(getBarcode());

    }


    private boolean itemIsBeingCheckedIn(){
        checkoutParts = checkOut.returnCheckedOutObjects();
        int studentID = getstudentID();

        // getStudentID returns -1 if the field does not contain a number
        if (studentID != -1) {
            CheckedOutPartsObject currentInfo = new CheckedOutPartsObject(getBarcode(), getstudentID());
            for (int i = 0; i < checkoutParts.size(); i++) {
                if (checkoutParts.get(i).equals(currentInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean extendedCheckoutIsSelected(){return !itemIsBeingCheckedIn() && extended.isSelected();}
    private boolean itemBeingCheckedBackInIsFaulty(){return itemIsBeingCheckedIn() && faulty.isSelected();}



    /**
     * Returns to home, contains check if fields are filled out
     */
    public void returnHome() {
        if (fieldsFilled()) {
            if (!userReturnsHome()) {
                return;
            }
        }
        stageWrapper.newStage("fxml/Menu.fxml", main);
    }

    private void getStudentName(){
        studentID.focusedProperty().addListener((ov, oldV, newV)->{
            if(!newV){
                studentNameText.setText(student.getStudentNameFromID(studentID.getText()));
            }

        });
    }

    /**
     * Resets all fields
     */
    public void reset() {
        stageWrapper.newStage("fxml/CheckOutItems.fxml", main);
    }

    /**
     * Checks if fields are filled
     * @return True if fields are not empty
     */
    private boolean fieldsFilled() {
        return !studentID.getText().isEmpty() | !barcode.getText().isEmpty() | quantity.getText().isEmpty();
    }

    /**
     * Only allows user to submit when all fields are filled out
     */
    private void unlockFields(){
        BooleanBinding binding;
        binding= quantity.textProperty().isEmpty()
                .or(studentID.textProperty().isEmpty())
                .or(barcode.textProperty().isEmpty());
        submitButton.disableProperty().bind(binding);
    }



    /**
     * Alert if user tries to return home and fields are filled
     * @return True if user pressed ok, false otherwise
     */
    private boolean userReturnsHome() {
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
            try{
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
            }catch (IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load student page");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                e.printStackTrace();
            }
        }else {
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

    private void setItemStatus(){
        barcode.focusedProperty().addListener((ov, oldv, newV)->{
            if(!newV && !barcode.getText().isEmpty()){
                if(itemIsBeingCheckedIn()){
                    setCheckinInformation();
                }
                else {
                    setCheckoutInformation();
                }
            }
        });
    }

    /**
     * Gets barcode as text, returns as int
     * @return barcode as integer
     */
    private int getBarcode(){
        return Integer.parseInt(barcode.getText());
    }

    /**
     * Gets quantity as text, returns as int
     * @return quantity as integer
     */
    private int getQuantity(){
        return Integer.parseInt(quantity.getText());
    }

    /**
     * Gets studentID as text, returns as int
     * @return StudentID as integer
     */
    private int getstudentID(){
        if (containsNumber(studentID.getText())) {
            return Integer.parseInt(studentID.getText());
        } else {
            return -1;
        }
    }

    private String getProfName(){
        return profName.getText();
    }

    private String getCourseName(){
        return courseName.getText();
    }

    private String getExtendedDueDate(){
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
        }
        else {
            unlockFields();
        }
    }

    /**
     * If extended is selected, more items will be displayed
     */
    public void isExtended(){
        unlockExtended();
        int translateDown = 190;
        int translateUp = -190;
        if(extended.isSelected()){
            setExtendedTransition(translateDown, true);
            setCheckoutItemsDisable(true);
        }
        else {
            resetExtended();
            setExtendedTransition(translateUp, false);
            setCheckoutItemsDisable(false);
        }
    }

    /**
     * Resets extended fields.
     */
    private void resetExtended(){
        courseName.setText("");
        profName.setText("");
        datePicker.setValue(null);
    }

    /**
     * Helper method to show extended fields
     * @param direction Direction items will be moved in
     * @param showItems True if items should be shown
     */
    private void setExtendedTransition(int direction, boolean showItems){
        translateButtons(direction);
        translateExtended(direction);
        extendedItemsFadeTransition();
        setExtendedItemsVisible(showItems);
    }

    /**
     * Helper method to set extended items to be visible
     * @param isVisible True if items should be shown
     */
    private void setExtendedItemsVisible(boolean isVisible){
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
    public void isFaulty(){
        int translateFaultyDown = 125;
        int translateFaultyUp = -125;
        if(faulty.isSelected()) {
            setFaultyTransition(translateFaultyDown, true);
            setCheckoutItemsDisable(true);
        }
        else {
            setFaultyTransition(translateFaultyUp, false);
            faultyTextArea.setText("");
            setCheckoutItemsDisable(false);
        }
    }

    private void setCheckoutItemsDisable(boolean value){
        barcode.setDisable(value);
        studentID.setDisable(value);
    }


    /**
     * Helper method to transition all faulty items
     * @param direction Direction to be moved in
     * @param showTextarea True if text area will be shown
     */
    private void setFaultyTransition(int direction, boolean showTextarea){
        translateButtons(direction);
        faultyBoxFadeTransition();
        faultyTextArea.setVisible(showTextarea);
    }

    /**
     * Translates extended checkbox
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
     * @param direction Direction to move
     */
    private void translateButtons(int direction){
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
    private void faultyBoxFadeTransition(){
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
    private void extendedItemsFadeTransition(){
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
        for (int i =0; i<numItems; i++){
            transitions.get(i).setFromValue(initial);
            transitions.get(i).setToValue(end);
            transitions.get(i).play();
        }
    }


    public void newBarcode(){
        setNewBarcodeFieldsHelper();
        barcodeHelper.barcodeItemsFadeTransition(newQuantity, deleteBarcode, barcode2);
        barcodeHelper.FadeTransition(HBoxBarcode2);
        barcodeHelper.spinnerInit(newQuantity2);
    }

    private void setNewBarcodeFieldsHelper(){
        extended.setDisable(true);
        faulty.setDisable(true);
        quantity.setVisible(false);
        quantityLabel.setVisible(false);
        newQuantity.setVisible(true);
        deleteBarcode.setVisible(true);
        barcode2.setVisible(true);
        HBoxBarcode2.setVisible(true);
    }


    public void newBarcode2(){
        NewBarcodeFieldHelper(HBoxBarcode3, barcode3, newQuantity3);
    }
    public void newBarcode3(){
        NewBarcodeFieldHelper(HBoxBarcode4, barcode4, newQuantity4);
    }
    public void newBarcode4(){
        NewBarcodeFieldHelper(HBoxBarcode5, barcode5, newQuantity5);
    }

    private void NewBarcodeFieldHelper(HBox hBoxBarcode4, JFXTextField barcode4, Spinner<Integer> newQuantity4) {
        barcodeHelper.translateItems(submitButton, resetButton, extended, faulty, 60);
        barcodeHelper.FadeTransition(hBoxBarcode4);
        barcodeHelper.FadeTransition(barcode4);
        barcodeHelper.spinnerInit(newQuantity4);
        hBoxBarcode4.setVisible(true);
        barcode4.setVisible(true);
    }

    private static boolean containsNumber(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        } catch (Exception e) {
            parsable = false;
        }
        return parsable;
    }

}
