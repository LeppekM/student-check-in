package Database;



import InventoryController.FaultyPartTabTableRow;
import InventoryController.StudentCheckIn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FaultyPartLookup {
    private final String url = Database.host + "/student_check_in";
    private Statement statement;
    private Database database = new Database();
    private final String selectFaulty = "\n" +
            "select parts.partName, parts.location, parts.barcode, fault.partID, fault.description, parts.partID\n" +
            "from fault\n" +
            "inner join parts on fault.partID = parts.partID";

    public ObservableList<FaultyPartTabTableRow> populateFaulty() {
        ObservableList<FaultyPartTabTableRow> data = FXCollections.observableArrayList();
        StudentCheckIn.logger.info(selectFaulty);
        try {
            Connection connection = database.getConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectFaulty);
            while (rs.next()) {
                String partName = rs.getString("partName");
                String location = rs.getString("location");
                long barcode = rs.getLong("barcode");
                String description = rs.getString("description");
                int partID = rs.getInt("partID");
                FaultyPartTabTableRow part = new FaultyPartTabTableRow(partID, partName, location, "" +barcode, description);
                data.add(part);
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("Could not retrieve the list of students");
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            statement = null;
        }
        return data;
    }
}
