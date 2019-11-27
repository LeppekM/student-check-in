package Database;

import HelperClasses.DatabaseHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FaultyCheckIn {
    private final String url = Database.host + "/student_check_in";
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

    /**
     * sets part to faulty status
     * @param barcode Barcode of part
     */
    public void setPartToFaultyStatus(long barcode){
        int partID = checkingOutPart.getPartIDFromBarcode(barcode, getPartIDtoCheckin);
        checkingOutPart.setPartStatus(partID, setPartsTableFaulty);
    }

    /**
     * Adds part to faulty table
     * @param barcode Barcode of part
     * @param description Description of fault
     */
    public void addToFaultyTable(int studentID, long barcode, String description){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(addToFaulty);
            addToFaultyHelper(studentID, barcode, description, statement).execute();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }

    }

    /**
     * Helper method to add part to faulty table
     * @param barcode Barcode of part
     * @param description Description of fault
     * @param preparedStatement Statement to be executed
     * @return Statement to execute
     */
    private PreparedStatement addToFaultyHelper(int studentID,long barcode, String description, PreparedStatement preparedStatement){
        int partID = checkingOutPart.getPartIDFromBarcodeAndStudentID(studentID, barcode);
        int checkoutID = checkingOutPart.getCheckoutIDFromBarcodeAndStudentID(studentID, barcode);
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
