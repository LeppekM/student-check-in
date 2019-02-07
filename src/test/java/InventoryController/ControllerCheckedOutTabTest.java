package InventoryController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Test;

import static org.junit.Assert.*;

public class ControllerCheckedOutTabTest {
    final ObservableList<CheckedOutItems> data = FXCollections.observableArrayList(new CheckedOutItems("Daniel", "test", 1, "2018-10-10","2018-10-11"));

    /**
     * Not a good test, but will remake later
     */
    @Test
    public void populateTable() {
        assertEquals(data.get(0).getStudentName(), "Daniel");
        assertEquals(data.get(0).getPartName(),"test");
        assertEquals(data.get(0).getCheckedOutAt(), "2018-10-10");
        assertEquals(data.get(0).getDueDate(), "2018-10-11");
    }
}