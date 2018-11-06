package InventoryController;

import Database.AddPart;
import Database.Database;
import Database.Part;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
        Part part = new Part("testPart", "serial", "manufacturer",0.00, "2", "location", "barcode", false, 100, false);
        addPart.addItem(part,"terrybc", "Sharks$199714");
        Part test = database.selectPart(100);
        assertEquals(part, test);
    }
}
