package Popups;

import Controllers.CheckOutController;
import Database.ExtendedCheckoutObject;
import HelperClasses.TimeUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Creates the extended checkout popup in the checkout/in screen which marks the checkout date as non-standard
 * and attaches additional information on professor/course the extended checkout is allowed under
 */
public class ExtendedCheckoutController implements Initializable {
    @FXML
    Pane main;

    @FXML
    JFXTextField courseName, profName;

    @FXML
    JFXDatePicker returnDate;

    @FXML
    JFXButton submitButton;

    private final TimeUtils dbHelp = new TimeUtils();

    /**
     * Gets extended due date
     * @return Return extended date
     */
    private String getExtendedDueDate() {
        LocalDate ld = returnDate.getValue();
        return dbHelp.setExtendedDuedate(ld);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unlockFields();
        setDatePickerValues();
    }

    /**
     * Submits fields to check out page
     */
    public void submit(){
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/CheckOutPage.fxml"));
        CheckOutController controller = loader.getController();
        ExtendedCheckoutObject checkout = new ExtendedCheckoutObject(courseName.getText(), profName.getText(),
                getExtendedDueDate());
        controller.initExtendedObject(checkout);
        main.getScene().getWindow().hide();
    }

    /**
     * Resets fields
     */
    public void reset(){
        courseName.clear();
        profName.clear();
        returnDate.setValue(null);
    }

    /**
     * Disables previous day values to be picked
     */
    private void setDatePickerValues(){
        returnDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.isBefore(today));
            }
        });
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
