package InventoryController;

import Database.AddPart;
import Database.Database;
import Database.Part;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddAndDeleteTest {
    private Database database;

    @Before
    public void before(){
        database = new Database("root", "Rootpass123");
    }

    @Test
    public void addParts(){
        AddPart addPart = new AddPart();
        Part part = new Part("testPart", "serial", "manufacturer",0.00, "2", "location", "barcode", false, 100, false);
        addPart.addItem(part,"root", "Rootpass123");
        Part test = database.selectPart(100);
        assertEquals(part.getPartName(), test.getPartName());
        assertEquals(part.getSerialNumber(), test.getSerialNumber());
        assertEquals(part.getManufacturer(), test.getManufacturer());
        assertEquals(part.getVendor(), test.getVendor());
        assertEquals(part.getLocation(), test.getLocation());
        assertEquals(part.getBarcode(), test.getBarcode());
        assertEquals(part.getFault(), test.getFault());
        assertEquals(part.getPartID(), part.getPartID());
        assertEquals(part.getIsDeleted(), test.getIsDeleted());
    }

    @Test
    public void deleteTest(){
        database.deleteItem(100);
        Part test = database.selectPart(100);
        assertTrue(test.getIsDeleted());
    }
}
