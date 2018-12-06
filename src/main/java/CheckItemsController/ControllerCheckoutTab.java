package CheckItemsController;

import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCheckoutTab implements Initializable {

    @FXML
    private JFXSpinner loadIndicator;

    @FXML
    private JFXTextField studentID, barcode, quantity;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        IntegerValidator intValidate = new IntegerValidator();
        studentID.getValidators().add(intValidate);
        intValidate.setMessage("Only numbers allowed!");
        studentID.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    studentID.validate();
                }
            }
        });

    }

    public void submit(){
        loadIndicator.setVisible(true);

    }
}
