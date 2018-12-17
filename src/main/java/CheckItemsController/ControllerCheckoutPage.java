package CheckItemsController;

import Database.CheckedOutParts;
import Database.Database;
import HelperClasses.StageWrapper;
import InventoryController.ControllerMenu;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ControllerCheckoutPage extends ControllerMenu implements Initializable {
    @FXML
    private AnchorPane main;

    @FXML
    private JFXSpinner loadIndicator;

    @FXML
    private JFXTextField studentID, barcode, quantity;

    @FXML
    private JFXCheckBox faulty, extended;

    @FXML
    private JFXButton studentInfo, submitButton;

    @FXML
    private Label itemStatus;

    private StageWrapper stageWrapper = new StageWrapper();
    private CheckedOutParts checkedOutParts = new CheckedOutParts();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFieldValidator();
        setItemStatus();
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
        if(itemStatus.getText().equals("Checking Out")){//Item is being checked out
            if(checkedOutParts.insertIntoCheckoutParts(getBarcode(), getQuantity())) {//Order is important or checkoutID will get incremented twice
                //There is a check in the above method that checks if barcode is found in system. If it isn't, both methods will not execute.
                checkedOutParts.insertIntoCheckouts(getstudentID());
            }
            else {
                return;  //If an error occurred then an alert will show, and then this method will exit.
            }
        }
        else if (itemStatus.getText().equals("Checking In")){ //Item is being checked in
            System.out.println("Check in");
            //Remove from checkout tables
        }
        else {//This case should never be reached, but error will be thrown just in case.
            stageWrapper.errorAlert("Please fill out all fields");
        }
        reset();
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
        stageWrapper.newStage("Menu.fxml", main);
    }

    /**
     * If barcode entered is in checked out database, item is being checked back in. Otherwise, item is being checked out.
     */
    public void setItemStatus() {
        barcode.focusedProperty().addListener((ov, oldV, newV)->{
            if (!newV){
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
    }



    /**
     * Checks if item is being checked in or out
     * @return True if item is being checked in
     */
    private boolean checkInValidator() {
        return checkedOutParts.returnBarcodes().contains(barcode.getText());
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
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Student.fxml"));
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

}
