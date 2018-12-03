package InventoryController;

import Database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ControllerTotalTabTest {
    private Database database;
    ObservableList<Part> data = FXCollections.observableArrayList(new Part("HDMI Cable", "H12312", "Sony", 5.99, "MSOE", "Cabinet", "", false, 0, 0));
    @Before
    public void before(){
        database =new Database();
    }


    @Test
    public void populateTable() {
        this.data = ControllerInventoryPage.selectParts("SELECT * from parts WHERE isDeleted = 0 ORDER BY partID", this.data);
        assertEquals(data.get(0).getPartName(), "HDMI Cable");
        assertEquals(data.get(0).getSerialNumber(),"H12312");
        assertEquals(data.get(0).getManufacturer(), "Sony");
//        assertEquals(data.get(0).getPrice(), 5.99, 0);
        assertEquals(data.get(0).getVendor(), "MSOE");
        assertEquals(data.get(0).getLocation(), "Cabinet");
        assertEquals(data.get(0).getBarcode(), "");
        assertEquals(data.get(0).getFault(), false);
        assertEquals(data.get(0).getPartID(), 0);
        assertEquals(data.get(0).getIsDeleted(), false);

    }
}