package CheckItemsController;

import HelperClasses.StageWrapper;
import InventoryController.ControllerInventoryPage;
import InventoryController.ControllerMenu;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class ControllerCheckoutTab extends ControllerMenu implements Initializable {
    @FXML
    private AnchorPane main;

    @FXML
    private JFXSpinner loadIndicator;

    @FXML
    private JFXTextField studentID, barcode, quantity;

    private StageWrapper stageWrapper = new StageWrapper();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        acceptIntegerOnly();


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

    public void checkIn(){
        stageWrapper.newStage("CheckInItems.fxml", main);
    }

    public void returnHome(){
        stageWrapper.newStage("Menu.fxml", main);
    }
}
