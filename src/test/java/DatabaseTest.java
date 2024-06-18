import Database.Database;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

public class DatabaseTest {
    Database db;
    Connection con;

    @Before
    public void setUp() {
        db = new Database();
        con = db.getConnection();
    }

    @Test
    public void connectionTest() {

    }

    @Test
    public void getAllOverdueTest() {

    }

    @Test
    public void getOverdueTest() {

    }

    @Test
    public void getTodayTest() {

    }

    @Test
    public void getTwoYearsAgoTest() {

    }

    @Test
    public void deleteItemTest() {

    }

    @Test
    public void deletePartsTest() {

    }

    @Test
    public void selectPartTest() {

    }

    @Test
    public void barcodeExistsTest() {

    }

    @Test
    public void setPartStatusTest() {

    }

    @Test
    public void getPartIDFromBarcodeTest() {

    }

    @Test
    public void test() {

    }

}
