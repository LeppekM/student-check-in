package Database;

import HelperClasses.DatabaseHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExtendedCheckOut {

    private final String url = Database.host + "/student_check_in";
    private final String extendedCheckout = "INSERT INTO checkout (partID, studentID, barcode, checkoutAt, prof, course, dueAt)\n" +
            "VALUE(?,?,?,?,?,?,?);";
    private final String getPartIDtoAdd = "SELECT partID \n" +
            "FROM parts \n" +
            "WHERE barcode = ? \n" +
            "    AND isCheckedout = 0\n" +
            "    LIMIT 1";
    private final String setPartStatusCheckedOut = "UPDATE parts SET isCheckedOut = 1 WHERE partID = ?";


    private CheckingOutPart checkHelper = new CheckingOutPart();
    private DatabaseHelper helper = new DatabaseHelper();

    /**
     * Adds a new checkout item to the database
     * @param barcode
     * @param studentID
     */
    public void addExtendedCheckout(int barcode, int studentID,  String profName, String courseName, String dueDate){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(extendedCheckout);
            addExtendedCheckoutHelper(barcode, studentID, profName, courseName, dueDate, statement).execute();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    /**
     * Helper for adding checkout item to DB, also sets isCheckedout in parts table to 1
     * @param barcode Barcode of part
     * @param studentID Student ID entered
     * @param preparedStatement Statement to be executed
     * @return
     */
    private PreparedStatement addExtendedCheckoutHelper(int barcode, int studentID, String profName, String courseName, String dueDate,PreparedStatement preparedStatement){
        int partID = checkHelper.getPartIDFromBarcode(barcode, getPartIDtoAdd);
        try {
            preparedStatement.setInt(1, partID);
            preparedStatement.setInt(2, studentID);
            preparedStatement.setInt(3, barcode);
            preparedStatement.setString(4, helper.getCurrentDate());
            preparedStatement.setString(5, profName);
            preparedStatement.setString(6, courseName);
            preparedStatement.setString(7, dueDate);
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        checkHelper.setPartStatus(partID, setPartStatusCheckedOut); //This will set the partID found above to a checked out status
        return preparedStatement;
    }
}
