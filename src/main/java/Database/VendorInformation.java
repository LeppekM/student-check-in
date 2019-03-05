package Database;

import java.sql.*;
import java.util.ArrayList;

public class VendorInformation {
    private final String url = Database.host + "/student_check_in";
    private String getVendorFromIDQuery = "SELECT vendor FROM vendors WHERE vendorID = ?;";

    private String getVendorIDFromVendorQuery = "SELECT vendorID FROM vendors WHERE vendor = ?;";

    private String getVendorListQuery = "SELECT vendor FROM vendors;";

    private String getVendorIDQuery = "SELECT vendorID\n" +
            "FROM vendors\n" +
            "ORDER BY vendorID DESC\n" +
            "LIMIT 1";

    private String createNewVendorQuery = "INSERT INTO vendors(vendorID, vendor)\n" +
             " values (?, ?)";

    /**
     * This method queries the database to get the vendor corresponding to a particular vendorID.
     * @param vendorID the vendorID of the vendor to be returned
     * @return the vendor corresponding to the vendorID
     */
    public String getVendorFromID(String vendorID){
        String result = null;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(getVendorFromIDQuery);
            preparedStatement.setString(1, vendorID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return result;
    }

    /**
     * This method queries the database to get the vendorID corresponding to a particular vendor.
     * @param vendor the vendorID of the vendor to be returned
     * @return the vendorID corresponding to the vendor
     */
    public int getVendorIDFromVendor(String vendor) {
        int result = -1;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(getVendorIDFromVendorQuery);
            preparedStatement.setString(1, vendor);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return result;
    }

    /**
     * Creates a new vendor
     * @param vendorName Name of vendor
     */
    public void createNewVendor(String vendorName){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(createNewVendorQuery);
            preparedStatement.setInt(1, getNewVendorID());
            preparedStatement.setString(2, vendorName);
            preparedStatement.execute();

        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    /**
     * Gets max vendor ID, adds one to it to generate a new vendor id
     * @return Vendor ID
     */
    private int getNewVendorID(){
        int vendorID = 0;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(getVendorIDQuery);
            while(rs.next()){
                vendorID = rs.getInt("vendorID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        //Gets max id from table, then increments to create new partID
        return vendorID + 1;
    }

    /**
     * This method queries the database to get a list of all vendors
     * @return the list of vendors
     */
    public ArrayList<String> getVendorList() {
        ArrayList<String> vendors = null;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(getVendorListQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            vendors = new ArrayList<>();
            while (resultSet.next()) {
                for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                    vendors.add(resultSet.getString(i));
                }
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return vendors;
    }
}
