package InventoryControllerTestFX;

import Database.Part;
import InventoryController.ControllerEditPart;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import java.io.IOException;
import java.text.DecimalFormat;

public class EditPartTestUtilsTest extends GuiTest {

    Scene scene;

    private ControllerEditPart controller;

    Part part;

    @Before
    public final void setUp() {
        part = new Part("test part", "serial", "manufacturer",23.52, "0", "location", "barcode", false, 1000, false);
        part.setQuantity(4);
        scene = stage.getScene();
    }


    @Test
    public void verifyFieldInformation(){
        if (controller != null) {
            //Arrange/Act
            controller.initPart(part);

            //Assert
            verifyStartingFields();
        }
    }

    private void verifyStartingFields(){
        DecimalFormat df = new DecimalFormat("#,###,##0.00");

        TextField nameField = (TextField) scene.lookup("#nameField");
        assertEquals(nameField.getText(), part.getPartName());

        TextField serialField = (TextField) scene.lookup("#serialField");
        assertEquals(serialField.getText(), part.getSerialNumber());

        TextField barcodeField = (TextField) scene.lookup("#barcodeField");
        assertEquals(barcodeField.getText(), part.getBarcode());

        TextField manufacturerField = (TextField) scene.lookup("#manufacturerField");
        assertEquals(manufacturerField.getText(), part.getManufacturer());

        TextField quantityField = (TextField) scene.lookup("#quantityField");
        assertEquals(quantityField.getText(), "" + part.getQuantity());

        TextField priceField = (TextField) scene.lookup("#priceField");
        assertEquals(priceField.getText(), df.format(part.getPrice() / 100));

        // NOTE: "Hot Dog" is a test vendor for Joe's local database.
        ComboBox vendorList = (ComboBox) scene.lookup("#vendorList");
        assertEquals(vendorList.getItems().get(5).toString(), "Hot Dog");

        TextField locationField = (TextField) scene.lookup("#locationField");
        assertEquals(locationField.getText(), part.getLocation());
    }

    @Override
    protected Parent getRootNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditPart.fxml"));
            loader.load();
            controller = loader.getController();
            return loader.getRoot();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
