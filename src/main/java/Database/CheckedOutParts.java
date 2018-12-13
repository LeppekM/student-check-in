package Database;

import InventoryController.CheckedOutItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.sql.*;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class queries the database for checked out items, and returns a student name, part name, quantity checked out, date checked out, and due date
 */
public class CheckedOutParts {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    private final String SELECTQUERY = "SELECT students.studentName, parts.partName, checkout_parts.checkoutQuantity, checkouts.checkoutAt, checkout_parts.dueAt\n" +
            "FROM checkout_parts\n" +
            "INNER JOIN parts \n" +
            "ON checkout_parts.partID = parts.partID\n" +
            "INNER JOIN checkouts\n" +
            "ON checkout_parts.checkoutID = checkouts.checkoutID\n" +
            "INNER JOIN students\n" +
            "ON checkouts.studentID = students.studentID";

    private final String SELECT_BARCODES = "select parts.barcode\n" +
            "from checkout_parts\n" +
            "inner join parts\n" +
            "on checkout_parts.partID=parts.partID";

    private final String getCheckoutIDQuery = "SELECT checkoutID\n" +
            "FROM checkouts\n" +
            "ORDER BY checkoutID DESC\n" +
            "LIMIT 1";

    private final String getPartIDFromBarcode = "SELECT partID FROM parts\n" +
            "WHERE barcode = ?";

    private final String insertIntoCheckouts = "INSERT INTO checkouts(checkoutID, studentID, checkoutAt, createdAt, createdBy)\n" +
            "VALUE(?, ?, ?, ?, ?)";

    private final String insertIntoCheckoutParts = "INSERT INTO checkout_parts(checkoutId, partID, checkoutQuantity, dueAt, createdAt, createdBy)\n" +
            "VALUE(?, ?, ?, ?, ?, ?);";
    private Statement statement;
    private int checkoutQuantity;
    private String checkedOutAt;
    private String dueDate;
    private String partName;
    private String studentName;

    public ObservableList<CheckedOutItems> data = FXCollections.observableArrayList();
    private List<String> barcodes = new ArrayList<>();


    /**
     * Queries the database for items that are checked out.
     */
    public ObservableList<CheckedOutItems> getCheckedOutItems(){

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECTQUERY);
            while(resultSet.next()){
                setVariables(resultSet);
                CheckedOutItems checkedOutItems = new CheckedOutItems(studentName, partName, checkoutQuantity, checkedOutAt, dueDate);
                data.add(checkedOutItems);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return data;
    }

    /**
     * Returns barcodes of items that are checked out
     * @return A list of barcodes in the checked out tab
     */
    public List<String> returnBarcodes(){
        if(barcodes.size()!=0){
            barcodes.clear();
        }
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_BARCODES);
            while(resultSet.next()){
                barcodes.add(resultSet.getString("barcode"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return barcodes;
    }

    /**
     * Sets variables to the results of the query
     * @param resultSet The results of the query
     */
    private void setVariables(ResultSet resultSet){
        try {
            studentName = resultSet.getString("studentName");
            partName = resultSet.getString("partName");
            checkoutQuantity = resultSet.getInt("checkoutQuantity");
            checkedOutAt = resultSet.getString("checkoutAt");
            dueDate = resultSet.getString("dueAt");

        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database");
        }
    }

    /**
     * This method queries the database and finds the max ID. It then increments this id to return
     * the next checkoutID when adding a part
     * @return The new part ID to be added to the database for the corresponding part
     */
    public int getCheckoutID(){
        int checkOutID = 0;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(getCheckoutIDQuery);
            while(rs.next()){
                checkOutID = rs.getInt("checkoutID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        //Gets max id from table, then increments to create new partID
        return checkOutID + 1;
    }

    /**
     * This method takes a barcode as parameter and returns the corresponding part id
     * @param barcode Barcode of part
     * @return Part ID
     */
    public int getPartIDFromBarcode(String barcode){
        String partID = "";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(getPartIDFromBarcode);
            statement.setString(1, barcode);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                partID = rs.getString("partID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        if(partID!=null) {
            return Integer.parseInt(partID);
        }
        //This means an error occurred
        return -1;
    }

    public void insertIntoCheckouts(int studentID){

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(insertIntoCheckouts);
            insertCheckoutsQuery(studentID, statement).execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public void insertIntoCheckoutParts(int barcode, int quantity){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(insertIntoCheckoutParts);
            insertCheckoutPartsQuery(barcode, quantity, statement).execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }


    private PreparedStatement insertCheckoutsQuery( int studentID, PreparedStatement preparedStatement){
        try {
            preparedStatement.setInt(1, getCheckoutID());
            preparedStatement.setInt(2, studentID);
            preparedStatement.setString(3, getCurrentDate());
            preparedStatement.setString(4, getCurrentDate());
            //Hardcoded Jim for now because workers not implemented yet
            preparedStatement.setString(5, "Jim");
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    private PreparedStatement insertCheckoutPartsQuery(int barcode, int quantity, PreparedStatement preparedStatement){
        try {
            preparedStatement.setInt(1, getCheckoutID());
            preparedStatement.setInt(2, getPartIDFromBarcode(String.valueOf(barcode)));
            preparedStatement.setInt(3, quantity);
            preparedStatement.setString(4, getTomorrowDate());
            preparedStatement.setString(5, getCurrentDate());
            //Hardcoded Jim for now because workers not implemented yet
            preparedStatement.setString(6, "Jim");
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    /**
     * This method gets the current date
     * @return Current date
     */
    private String getCurrentDate(){
        return LocalDateTime.now().toString();
    }

    private String getTomorrowDate(){
        Date dt = new Date();
        return LocalDateTime.from(dt.toInstant().atZone(ZoneId.of("UTC"))).plusDays(1).toString();
    }
}