package Database;

import java.sql.*;
import java.time.LocalDateTime;

public class AddPart {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
//    private final String username = "langdk";
//    private final String password = "password";
    private String addQuery = "INSERT INTO parts(partID, partName, serialnumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity,  availableQuantity, createdAt, createdBy)"+
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
    private String getpartIDQuery = "SELECT partID\n" +
            "FROM parts\n" +
            "ORDER BY partID DESC\n" +
            "LIMIT 1";

    /**
     * This method adds an item to the database
     * @param part The part to be added
     */
    public void addItem(Part part, String username, String password){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
            insertQuery(part, preparedStatement).execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    /**
     * This method sets the information from a part to the item being added to the database
     * @param part The part being added to the database
     * @param preparedStatement The statement that has items being set to it
     * @return
     */
    private PreparedStatement insertQuery(Part part, PreparedStatement preparedStatement){
        try {
            preparedStatement.setInt(1, part.getPartID());
            preparedStatement.setString(2, part.getPartName());
            preparedStatement.setString(3, part.getSerialNumber());
            preparedStatement.setString(4, part.getManufacturer());
            preparedStatement.setDouble(5, part.getPrice());
            //Hardcoded vendorID for now.
            preparedStatement.setInt(6, 2);
            preparedStatement.setString(7, part.getLocation());
            preparedStatement.setString(8, part.getBarcode());
            preparedStatement.setInt(9, part.getQuantity());
            preparedStatement.setInt(10, part.getQuantity());
            preparedStatement.setString(11, getCurrentDate());
            //Hardcoded created by because we don't have workers setup yet
            preparedStatement.setString(12, "John");
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

    /**
     * This method queries the database and finds the max ID. It then increments this id to return
     * the next partID when adding a part
     * @return The new part ID to be added to the database for the corresponding part
     */
    public int getPartID(String username, String password){
        int partID = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(getpartIDQuery);
            while(rs.next()){
                partID = rs.getInt("partID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        //Gets max id from table, then increments to create new partID
        return partID + 1;
    }
}
