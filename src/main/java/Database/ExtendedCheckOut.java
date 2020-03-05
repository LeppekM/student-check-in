package Database;

import HelperClasses.DatabaseHelper;
import HelperClasses.StageWrapper;
import InventoryController.StudentCheckIn;

import java.sql.*;

public class ExtendedCheckOut {

    private final String url = Database.host + "/student_check_in";
    private CheckingOutPart checkHelper = new CheckingOutPart();
    private DatabaseHelper helper = new DatabaseHelper();
    private StageWrapper stageWrapper = new StageWrapper();

    /**
     * Adds a new checkout item to the database
     * @param barcode
     * @param studentID
     */
    public void addExtendedCheckout(long barcode, int studentID,  String profName, String courseName, String dueDate){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            String extendedCheckout = "INSERT INTO checkout (partID, studentID, barcode, checkoutAt, prof, course, dueAt)\n" +
                    "VALUE(?,?,?,?,?,?,?);";
            PreparedStatement statement = connection.prepareStatement(extendedCheckout);
            addExtendedCheckoutHelper(barcode, studentID, profName, courseName, dueDate, statement).execute();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        catch (NullPointerException e){
            stageWrapper.errorAlert("Barcode " + barcode +" is already checked out by another student");
        }

    }


    private int getPartIDViaBarcode(long barcode){
        String query = "select partID from parts\n" +
                "where barcode = ?\n" +
                "and isCheckedOut = 0\n"+
                "LIMIT 1";
        int partID = 0;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, barcode);
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                partID = rs.getInt("partID");
            }
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when getting part ID from barcode.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }

        return partID;
    }

    /**
     * Helper for adding checkout item to DB, also sets isCheckedout in parts table to 1
     * @param barcode Barcode of part
     * @param studentID Student ID entered
     * @param preparedStatement Statement to be executed
     * @return
     */
    private PreparedStatement addExtendedCheckoutHelper(long barcode, int studentID, String profName, String courseName, String dueDate, PreparedStatement preparedStatement){
        int partID = getPartIDViaBarcode(barcode);
        if (partID == 0){
            return null;
        }

        try {
            preparedStatement.setInt(1, partID);
            preparedStatement.setInt(2, studentID);
            preparedStatement.setLong(3, barcode);
            preparedStatement.setString(4, helper.getCurrentDateTimeStamp());
            preparedStatement.setString(5, profName);
            preparedStatement.setString(6, courseName);
            preparedStatement.setString(7, dueDate);
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        String setPartStatusCheckedOut = "UPDATE parts SET isCheckedOut = 1 WHERE partID = ?";
        checkHelper.setPartStatus(partID, setPartStatusCheckedOut); //This will set the partID found above to a checked out status
        return preparedStatement;
    }
}
