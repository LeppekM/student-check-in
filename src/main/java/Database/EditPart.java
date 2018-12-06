package Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This class uses a query to edit a part in the database
 */
public class EditPart {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    //    private final String username = "langdk";
//    private final String password = "password";
    private String editQuery = "UPDATE parts SET serialNumber = ?, manufacturer = ?, " +
            "price = ?, vendorID = ?, location = ?, barcode = ?, totalQuantity = ?," +
            "updatedAt = ? WHERE partID = ?;";

    private String getVendorFromIDQuery = "SELECT vendor FROM vendors WHERE vendorID = ?;";

    private String getVendorIDFromVendorQuery = "SELECT vendorID FROM vendors WHERE vendor = ?;";

    private String getVendorListQuery = "SELECT vendor FROM vendors;";

    VendorInformation vendorInformation = new VendorInformation();

    /**
     * This method edits an item in the database
     * @param part The part to be edited
     */
    public void editItem(Part part){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(editQuery);
            preparedStatement = editQuery(part, preparedStatement);
            preparedStatement.execute();
            preparedStatement.close();
            vendorInformation.getVendorList();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

//    /**
//     * This method queries the database to get the vendor corresponding to a particular vendorID.
//     * @param vendorID the vendorID of the vendor to be returned
//     * @return the vendor corresponding to the vendorID
//     */
//    public String getVendorFromID(String vendorID){
//        String result = null;
//        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
//            PreparedStatement preparedStatement = connection.prepareStatement(getVendorFromIDQuery);
//            preparedStatement.setString(1, vendorID);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                result = resultSet.getString(1);
//            }
//            preparedStatement.close();
//        } catch (SQLException e) {
//            throw new IllegalStateException("Cannot connect to the database", e);
//        }
//        return result;
//    }
//
//    /**
//     * This method queries the database to get the vendorID corresponding to a particular vendor.
//     * @param vendor the vendorID of the vendor to be returned
//     * @return the vendorID corresponding to the vendor
//     */
//    private int getVendorIDFromVendor(String vendor) {
//        int result = -1;
//        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
//            PreparedStatement preparedStatement = connection.prepareStatement(getVendorIDFromVendorQuery);
//            preparedStatement.setString(1, vendor);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                result = resultSet.getInt(1);
//            }
//            preparedStatement.close();
//        } catch (SQLException e) {
//            throw new IllegalStateException("Cannot connect to the database", e);
//        }
//        return result;
//    }
//
//    /**
//     * This method queries the database to get a list of all vendors
//     * @return the list of vendors
//     */
//    public ArrayList<String> getVendorList() {
//        ArrayList<String> vendors = null;
//        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
//            PreparedStatement preparedStatement = connection.prepareStatement(getVendorListQuery);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            ResultSetMetaData rsmd = resultSet.getMetaData();
//            vendors = new ArrayList<>();
//            while (resultSet.next()) {
//                for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
//                    vendors.add(resultSet.getString(i));
//                }
//            }
//            preparedStatement.close();
//        } catch (SQLException e) {
//            throw new IllegalStateException("Cannot connect to the database", e);
//        }
//        return vendors;
//    }

    /**
     * This method gets the current date
     * @return Current date
     */
    private String getCurrentDate(){
        return LocalDateTime.now().toString();
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
            preparedStatement.setInt(4, vendorInformation.getVendorIDFromVendor(part.getVendor()));
            preparedStatement.setString(5, part.getLocation());
            preparedStatement.setString(6, part.getBarcode());
            preparedStatement.setInt(7, part.getQuantity());
            preparedStatement.setString(8, getCurrentDate());
            preparedStatement.setString(9, "" + part.getPartID());
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

}
