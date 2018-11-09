package Database;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * This class uses a query to edit a part in the database
 */
public class EditPart {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    //    private final String username = "langdk";
//    private final String password = "password";
    private String editQuery = "UPDATE parts SET serialNumber = ?, manufacturer = ?, " +
            "price = ?, vendorID = ?, location = ?, barcode = ?, totalQuantity = ? " +
            "WHERE partID = ?;";

    private String getVendorIDQuery = "SELECT vendorID FROM vendors WHERE vendor = ?;";
    /**
     * This method edits an item in the database
     * @param part The part to be edited
     */
    public void editItem(Part part, String username, String password){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(editQuery);
            preparedStatement = editQuery(part, preparedStatement);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public int getVendorID(Part part, String username, String password){
        int result = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(getVendorIDQuery);
            preparedStatement.setString(1, part.getVendor());
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            String s = rsmd.getColumnLabel(1);
            System.out.println(s + " HERE");
            preparedStatement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return result;
    }

    /**
     * This method sets the information from a part to the item being edited in the database
     * @param part The part being edited in the database
     * @param preparedStatement The statement that has items being set to it
     * @return the statement that has items being set to it
     */
    private PreparedStatement editQuery(Part part, PreparedStatement preparedStatement){
        try {
            preparedStatement.setString(1, part.getSerialNumber());
            preparedStatement.setString(2, part.getManufacturer());
            preparedStatement.setDouble(3, part.getPrice());
            //Hardcoded vendorID for now.
            preparedStatement.setInt(4, 0);
            preparedStatement.setString(5, part.getLocation());
            preparedStatement.setString(6, part.getBarcode());
            preparedStatement.setInt(7, part.getQuantity());

            //Hardcoded created by because we don't have workers setup yet
            preparedStatement.setString(8, "" + part.getPartID());
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

}
