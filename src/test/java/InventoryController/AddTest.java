package InventoryController;

import Database.AddPart;
import Database.Database;
import Database.Part;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AddTest {
    private Database database;

    @Before
    public void before(){
        database = new Database("terrybc", "Sharks$199714");
    }

    @Test
    public void addParts(){
        AddPart addPart = new AddPart();
        Part part = new Part("testPart", "serial", "manufacturer",0.00, "vendor", "location", "barcode", false, 100, false);
        addPart.addItem(part,"terrybc", "Sharks$199714");
        ControllerTotalTab ctt = new ControllerTotalTab();
        assertTrue(ctt.tableView.getItems().contains(part));
    }
}
