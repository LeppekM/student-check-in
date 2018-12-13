package CheckItemsController;

import Database.CheckedOutParts;
import Database.Database;
import HelperClasses.StageWrapper;
import InventoryController.ControllerMenu;
import InventoryController.StudentPage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ControllerCheckoutTab extends ControllerMenu implements Initializable {
    @FXML
    private AnchorPane main;

    @FXML
    private JFXSpinner loadIndicator;

    @FXML
    private JFXTextField studentID, barcode, quantity;

    @FXML
    private JFXCheckBox faulty, extended;

    @FXML
    private JFXButton studentInfo;

    @FXML
    private Label itemStatus;

    private StageWrapper stageWrapper = new StageWrapper();
    private CheckedOutParts checkedOutParts = new CheckedOutParts();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFieldValidator();
        setItemStatus();
    }


    public void moveToBarcodeField() {
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){5,}$")) {
            barcode.requestFocus();
        }
    }


    public void submit() {
        if(itemStatus.getText().equals("Checking Out")){
            checkedOutParts.insertIntoCheckoutParts(getBarcode(), getQuantity());
            checkedOutParts.insertIntoCheckouts(getstudentID());
        }
        else if (itemStatus.getText().equals("Checking In")){
            System.out.println("Check in");
            //Remove from checkout tables
        }
        else {
            //Print error
            System.out.println("Error");
        }
//        //System.out.println(checkedOutParts.returnBarcodes());
//        loadIndicator.setVisible(true);
    }


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

    public void reset() {
        studentID.clear();
        barcode.clear();
        quantity.setText("1");
        extended.setSelected(false);
        faulty.setSelected(false);
    }

    private boolean checkInValidator() {
        return checkedOutParts.returnBarcodes().contains(barcode.getText());
    }

    private boolean fieldsFilled() {
        return !studentID.getText().isEmpty() | !barcode.getText().isEmpty();
    }

    private boolean userReturnsHome() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Information may be lost");
        alert.setHeaderText("If you leave, unsubmitted information may be lost");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        }
        return false;
    }


    private void setCheckinInformation() {
        extended.setVisible(false);
        faulty.setVisible(true);
        itemStatus.setText("Checking In");
    }

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
//        if (database.selectStudent(Integer.parseInt(studentID.getText())) != null) {
            try {
                URL myFxmlURL = ClassLoader.getSystemResource("Student.fxml");
                FXMLLoader loader = new FXMLLoader(myFxmlURL);
                main.getScene().setRoot(loader.load(myFxmlURL));
                StudentPage studentPage = new StudentPage();
                studentPage.setStudent(database.selectStudent(Integer.parseInt(studentID.getText())));
            } catch (IOException invoke) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
                alert.showAndWait();
                invoke.printStackTrace();
            }
//        }else {
//            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no student found with associated RFID");
//            alert.showAndWait();
//        }
    }

    private void setFieldValidator() {
        stageWrapper.requiredInputValidator(studentID);
        stageWrapper.requiredInputValidator(barcode);
        stageWrapper.requiredInputValidator(quantity);
        stageWrapper.acceptIntegerOnly(studentID);
        stageWrapper.acceptIntegerOnly(quantity);
    }

    private int getBarcode(){
        return Integer.parseInt(barcode.getText());
    }
    private int getQuantity(){
        return Integer.parseInt(quantity.getText());
    }
    private int getstudentID(){
        return Integer.parseInt(studentID.getText());
    }



}
