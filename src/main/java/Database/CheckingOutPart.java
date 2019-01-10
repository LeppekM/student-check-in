package Database;

import HelperClasses.DatabaseHelper;
import HelperClasses.StageWrapper;
import InventoryController.CheckedOutItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.sql.*;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckingOutPart {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    private final String addToCheckouts = "INSERT INTO checkout (partID, studentID, checkoutAt, dueAt)\n" +
            "VALUE(?,?,?,?);";
    private final String getPartIDtoAdd = "SELECT partID \n" +
            "FROM parts \n" +
            "WHERE barcode = ? \n" +
            "    AND isCheckedout = 0\n" +
            "    LIMIT 1";
    private final String setPartStatusCheckedOut = "UPDATE parts SET isCheckedOut = 1 WHERE partID = ?";

    DatabaseHelper helper = new DatabaseHelper();


    public void addNewCheckoutItem(int barcode, int studentID){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(addToCheckouts);
            addNewCheckoutHelper(barcode, studentID, statement).execute();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    private PreparedStatement addNewCheckoutHelper(int barcode, int studentID, PreparedStatement preparedStatement){
        int partID = getPartIDFromBarcode(barcode);
        try {
            preparedStatement.setInt(1, partID);
            preparedStatement.setInt(2, studentID);
            preparedStatement.setString(3, helper.getCurrentDate());
            preparedStatement.setString(4, helper.getTomorrowDate());
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        updatePartStatus(partID); //This will set the partID found above to a checked out status
        return preparedStatement;
    }

    /**
     * This method takes a barcode as parameter and returns the corresponding partID to be added to checkout table.
     * @param barcode barcode of part
     * @return Part ID to return
     */
    private int getPartIDFromBarcode(int barcode){
        int partID = 0;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(getPartIDtoAdd);
            statement.setInt(1, barcode);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                partID = rs.getInt("partID");
            }
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return partID;
    }

    private void updatePartStatus(int partID){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(setPartStatusCheckedOut);
            preparedStatement.setInt(1,partID);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }
}
