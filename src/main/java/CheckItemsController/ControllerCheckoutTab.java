package CheckItemsController;

import HelperClasses.StageWrapper;
import InventoryController.ControllerMenu;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ControllerCheckoutTab extends ControllerMenu implements Initializable {
    @FXML
    private AnchorPane main;

    @FXML
    private JFXSpinner loadIndicator;

    @FXML
    private JFXTextField studentID, barcode, quantity;

    @FXML
    private JFXCheckBox faulty, extended;

    private StageWrapper stageWrapper = new StageWrapper();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        acceptIntegerOnly();
        setRequiredFieldValidator();



    }


    private void acceptIntegerOnly(){
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        studentID.setTextFormatter(textFormatter);
    }

    public void moveToBarcodeField(){
        //System.out.println("test");
        if (studentID.getText().matches("^\\D*(?:\\d\\D*){5,}$")){
           barcode.requestFocus();
        }
    }



    public void submit(){
        loadIndicator.setVisible(true);
    }


    public void returnHome(){
        if(fieldsFilled()) {
            if (!userReturnsHome()) {
                return;
            }
        }
        stageWrapper.newStage("Menu.fxml", main);
    }

    public void setItemStatus() {

        if (checkInValidator()) {
            stageWrapper.slidingAlert("Checkin Item", "Item is being checked back in");
            setCheckinCheckBox();
        } else if (checkOutValidator()){
            stageWrapper.slidingAlert("Checkout Item", "Item is being checked out");
            setCheckoutCheckBox();
        }
    }

    public void reset(){
        studentID.clear();
        barcode.clear();
        quantity.setText("1");
        extended.setSelected(false);
        faulty.setSelected(false);
    }

    private boolean checkInValidator(){
        return barcode.getText().equals("test");
    }

    private boolean checkOutValidator(){
        return barcode.getText().equals("checkout");
    }

    private boolean fieldsFilled(){
        return !studentID.getText().isEmpty() | !barcode.getText().isEmpty();
    }

    private boolean userReturnsHome(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Information may be lost");
        alert.setHeaderText("If you leave, unsubmitted information may be lost");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        }
        return false;
    }




    private void setCheckinCheckBox(){
        extended.setVisible(false);
        faulty.setVisible(true);
    }

    private void setCheckoutCheckBox(){
        faulty.setVisible(false);
        extended.setVisible(true);
    }

    private void setRequiredFieldValidator(){
        stageWrapper.requiredInputValidator(studentID);
        stageWrapper.requiredInputValidator(barcode);
        stageWrapper.requiredInputValidator(quantity);
    }



}
