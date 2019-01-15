package CheckItemsController;

import Database.StudentInfo;
import Database.CheckingOutPart;
import Database.Database;
import HelperClasses.StageWrapper;
import InventoryController.ControllerMenu;
import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
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
    private JFXTextField studentID, barcode, quantity, profName, courseName;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private JFXCheckBox faulty, extended;

    @FXML
    private JFXButton studentInfo, submitButton, home, resetButton;

    @FXML
    private Label itemStatus, studentNameText, profNameLabel, courseNameLabel, dueAt;

    @FXML
    private TextArea faultyTextArea;


    private StageWrapper stageWrapper = new StageWrapper();
    private CheckingOutPart checkOut = new CheckingOutPart();
    private StudentInfo student = new StudentInfo();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        home.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        studentInfo.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        setFieldValidator();
        setItemStatus();
        getStudentName();
        unlockFields();
    }

    /**
     * Sets cursor to next field
     */
    public void moveToBarcodeField() {
        studentInfo.setDisable(true);
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){5,}$")) {
            barcode.requestFocus();
            studentInfo.setDisable(false);
        }
    }

    /**
     * Submits the information entered to checkouts/checkoutParts table or removes if item is being checked back in.
     */
    public void submit() {
        if(itemIsBeingCheckedOut()) {
            checkOut.addNewCheckoutItem(getBarcode(), getstudentID());
        }
        else {
            checkOut.setItemtoCheckedin(getBarcode());
        }
        reset();
    }

    private boolean itemIsBeingCheckedOut(){
        return itemStatus.getText().equals("Checking Out");
    }

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

    /**
     * If barcode entered is in checked out database, item is being checked back in. Otherwise, item is being checked out.
     */
    private void setItemStatus() {
        barcode.focusedProperty().addListener((ov, oldV, newV)->{
            if (!newV && !barcode.getText().isEmpty()){
                if(checkInValidator()){
                    stageWrapper.slidingAlert("Checkin Item", "Item is being checked back in");
                    setCheckinInformation();
                }
                else {
                    stageWrapper.slidingAlert("Checkout Item", "Item is being checked out");
                    setCheckoutInformation();
                }
            }
        });
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
        studentID.clear();
        barcode.clear();
        quantity.setText("1");
        extended.setSelected(false);
        faulty.setSelected(false);
        itemStatus.setText("");
        studentNameText.setText("");
    }


    /**
     * Checks if item is being checked in or out
     * @return True if item is being checked in
     */
    private boolean checkInValidator() {
        return checkOut.returnBarcodes().contains(barcode.getText());
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
        BooleanBinding booleanBind = quantity.textProperty().isEmpty()
                .or(studentID.textProperty().isEmpty())
                .or(barcode.textProperty().isEmpty());
        submitButton.disableProperty().bind(booleanBind);
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
        Database database = new Database();
        if (database.selectStudent(Integer.parseInt(studentID.getText())) != null) {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Student.fxml"));
                Parent root = (Parent) loader.load();
                StudentPage sp = loader.getController();
                sp.setStudent(database.selectStudent(Integer.parseInt(studentID.getText())));
                main.getScene().setRoot(root);
            }catch (IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load student page");
                alert.showAndWait();
                e.printStackTrace();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no student found with associated RFID");
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
        return Integer.parseInt(studentID.getText());
    }

    /**
     * If extended is selected, more items will be displayed
     */
    public void isExtended(){
        int translateDown = 190;
        int translateUp = -190;
        if(extended.isSelected()){
            setExtendedTransition(translateDown, true);
        }
        else {
            setExtendedTransition(translateUp, false);
        }
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
        }
        else {
            setFaultyTransition(translateFaultyUp, false);
        }
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


}
