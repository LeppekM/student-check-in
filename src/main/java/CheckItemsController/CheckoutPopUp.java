package CheckItemsController;

import InventoryController.CheckedOutItems;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class CheckoutPopUp extends StudentPage {

    @FXML
    private JFXTextField student, part, quantity, date, dueDate;

    public void populate(CheckedOutItems checked){
        student.setText(checked.getStudentName().get());
        part.setText(checked.getPartName().get());
        quantity.setText(checked.getQuantity().get() + "");
        date.setText(checked.getCheckedOutAt().get());
        dueDate.setText(checked.getDueDate().get());
    }

}
