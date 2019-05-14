package CheckItemsController;

import HelperClasses.DatabaseHelper;
import HelperClasses.StageWrapper;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ExtendedCheckoutController implements Initializable {
    @FXML
    Pane main;

    @FXML
    JFXTextField courseName, profName;

    @FXML
    JFXDatePicker returnDate;

    @FXML
    JFXButton submitButton;

    private ExtendedCheckoutObject checkout;
    private DatabaseHelper dbHelp = new DatabaseHelper();
    private StageWrapper helper = new StageWrapper();

    /**
     * Gets extended due date
     *
     * @return Return extended date
     */
    private String getExtendedDueDate() {
        LocalDate ld = returnDate.getValue();
        return dbHelp.setExtendedDuedate(ld);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unlockFields();
    }

    public void submit(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/CheckOutPage.fxml"));
            Parent root = (Parent) loader.load();
            CheckOutController controller = loader.getController();
            checkout = new ExtendedCheckoutObject(courseName.getText(), profName.getText(), getExtendedDueDate());
            controller.initExtendedObject(checkout);
            main.getScene().getWindow().hide();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load student page");
            alert.initStyle(StageStyle.UTILITY);
            StudentCheckIn.logger.error("IOException: Couldn't load student page.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }



    public void reset(){
        courseName.clear();
        profName.clear();
        returnDate.setValue(null);
    }

    /**
     * Only allows user to submit when all fields are filled out
     */
    private void unlockFields() {
        BooleanBinding binding;
        binding = courseName.textProperty().isEmpty()
                .or(profName.textProperty().isEmpty())
                .or(returnDate.valueProperty().isNull());
        submitButton.disableProperty().bind(binding);
    }
}
