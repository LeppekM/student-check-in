package Database;

import java.sql.*;
import java.util.ArrayList;

public class VendorInformation {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    private String getVendorFromIDQuery = "SELECT vendor FROM vendors WHERE vendorID = ?;";

    private String getVendorIDFromVendorQuery = "SELECT vendorID FROM vendors WHERE vendor = ?;";

    private String getVendorListQuery = "SELECT vendor FROM vendors;";

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
