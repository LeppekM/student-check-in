package InventoryController;

import Database.AddPart;
import Database.EditPart;
import Database.Database;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class EditPartsTest {

    private Database database;

    @Before
    public void before(){
        database = new Database();
    }

    @Test
    public void editParts(){
        EditPart editPart = new EditPart();
        AddPart addPart = new AddPart();
        Part part = new Part("testPart", "serial", "manufacturer",0.00, "2", "location", "barcode", false, 1000, 0);
        addPart.addItem(part);

        Part editedPart = new Part("testPart", "xyz", "MSOE", 17.20, "3", "there", "scan me", false, 1000, 0);
        editPart.editItem(editedPart);
        Part test = database.selectPart(1000);
        assertEquals(editedPart.getPartName(), test.getPartName());
        assertEquals(editedPart.getSerialNumber(), test.getSerialNumber());
        assertEquals(editedPart.getManufacturer(), test.getManufacturer());
        assertEquals("0", test.getVendor());    // 0, because vendors are not in the inventory, so no drop down has been implemented
        assertEquals(editedPart.getLocation(), test.getLocation());
        assertEquals(editedPart.getBarcode(), test.getBarcode());
        assertEquals(editedPart.getFault(), test.getFault());
        assertEquals(editedPart.getPartID(), part.getPartID());
        assertEquals(editedPart.getIsDeleted(), test.getIsDeleted());

        // Undoes change to part
        editPart.editItem(part);
    }

}
