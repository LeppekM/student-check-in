package CheckItemsController;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ExtendedCheckoutController implements Initializable {

    @FXML
    JFXTextField courseName, profName;

    @FXML
    JFXDatePicker returnDate;

    private ExtendedCheckoutObject checkout;

    public void initExtendedInfo(ExtendedCheckoutObject extendedCheckoutObject){
        this.checkout = extendedCheckoutObject;
    }

    private void setInfo(){

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void submit(){

    }


}
