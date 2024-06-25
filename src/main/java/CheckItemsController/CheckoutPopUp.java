package CheckItemsController;

import Database.ObjectClasses.Checkout;
import Database.ObjectClasses.Worker;
import InventoryController.IController;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;

public class CheckoutPopUp implements IController {

    @FXML
    private JFXTextField name, part, barcode, coDate, dueDate;

    @FXML
    private Label cID;
    private Worker worker;

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker){
        if (this.worker == null){
            this.worker = worker;
        }
    }

    public void populate(Checkout checked){
        name.setText(checked.getStudentName().get());
        part.setText(checked.getPartName().get());
        barcode.setText(checked.getBarcode().get() + "");
        coDate.setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(checked.getCheckedOutDate().get()));
        dueDate.setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(checked.getDueDate().get()));
        cID.setText("Checkout ID: " + checked.getCheckoutID().get());
    }
}
