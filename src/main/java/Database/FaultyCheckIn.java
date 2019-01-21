package Database;

import HelperClasses.DatabaseHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FaultyCheckIn {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    private final String setPartsTableFaulty = "update parts\n" +
            "set isFaulty = 1\n" +
            "where partID = ? ";
    private final String getPartIDtoCheckin = "SELECT partID \n" +
            "FROM parts \n" +
            "WHERE barcode = ? \n" +
            "    AND isCheckedout = 1\n" +
            "    LIMIT 1";

    private final String addToFaulty = "INSERT INTO fault(checkoutID, partID, description, createdAt)\n" +
            "VALUE(?,?,?,?)";

    private CheckingOutPart checkingOutPart = new CheckingOutPart();
    private DatabaseHelper helper = new DatabaseHelper();


    public void setPartToFaultyStatus(int barcode){
        int partID = checkingOutPart.getPartIDFromBarcode(barcode, getPartIDtoCheckin);
        checkingOutPart.setPartStatus(partID, setPartsTableFaulty);
    }

    public void addToFaultyTable(int barcode, String description){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(addToFaulty);
            addToFaultyHelper(barcode, description, statement).execute();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }

    }

    private PreparedStatement addToFaultyHelper(int barcode, String description, PreparedStatement preparedStatement){
        int partID = checkingOutPart.getPartIDFromBarcode(barcode, getPartIDtoCheckin);
        int checkoutID = checkingOutPart.getCheckoutIDfromPartID(partID);
        try {
            preparedStatement.setInt(1, checkoutID);
            preparedStatement.setInt(2, partID);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, helper.getCurrentDate());
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

}
