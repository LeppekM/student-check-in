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
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
//        System.out.println(getStudentID());

        //studentID.setText(getStudentID());
//        IntegerValidator intValidate = new IntegerValidator();
//        studentID.getValidators().add(intValidate);
//        intValidate.setMessage("Only numbers allowed!");
//        studentID.focusedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if(!newValue){
//                    studentID.validate();
//                }
//            }
//        });

    }

    private String getStudentID(){
        String studentID = null;
        for (int i =0; i<studentIDArray.size(); i++){
            studentID +=Integer.parseInt(studentIDArray.get(i));
        }
        return studentID;
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
