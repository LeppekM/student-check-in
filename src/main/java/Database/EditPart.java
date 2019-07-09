package Database;

import Database.ObjectClasses.Part;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * This class uses a query to edit a part in the database
 */
public class EditPart {
    private final String url = Database.host + "/student_check_in";
    private String editQuery = "UPDATE parts SET serialNumber = ?, barcode = ?, price = ?, location = ?, " +
            "updatedAt = ? WHERE partID = ?;";

    private String editAllCommonBarcodeQuery = "UPDATE parts SET partName = ?, price = ?, location = ?, " +
            "barcode = ?, updatedAt = ? WHERE partName = ?;";

    private String editAllQuery = "UPDATE parts SET partName = ?, price = ?, location = ?, " +
            "updatedAt = ? WHERE partName = ?;";

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
            preparedStatement.setLong(2, part.getBarcode());
            preparedStatement.setDouble(3, part.getPrice());
            preparedStatement.setString(4, part.getLocation());
            preparedStatement.setString(5, getCurrentDate());
            preparedStatement.setString(6, "" + part.getPartID());
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    /**
     * This method sets the information from a part to the item being edited in the database
     * @param part The part being edited in the database
     * @param preparedStatement The statement that has items being set to it
     * @return the statement that has items being set to it
     */
    private PreparedStatement editAllQuery(String originalPartName, Part part, Part OGPart, PreparedStatement preparedStatement){
        try {
            preparedStatement.setString(1, part.getPartName());
            preparedStatement.setDouble(2, part.getPrice());
            preparedStatement.setString(3, part.getLocation());
            preparedStatement.setString(4, getCurrentDate());
            preparedStatement.setString(5, originalPartName);
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    /**
     * This method sets the information from a part to the item being edited in the database
     * @param part The part being edited in the database
     * @param preparedStatement The statement that has items being set to it
     * @return the statement that has items being set to it
     */
    private PreparedStatement editAllCommonBarcodeQuery(String originalPartName, Part part, Part OGPart, PreparedStatement preparedStatement){
        try {

            preparedStatement.setString(1, part.getPartName());
            preparedStatement.setDouble(2, part.getPrice());
            preparedStatement.setString(3, part.getLocation());
            preparedStatement.setLong(4, part.getBarcode());
            preparedStatement.setString(5, getCurrentDate());
            preparedStatement.setString(6, originalPartName);
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    public void editAllOfType(Part OGPart, Part updatedPart) {

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {

            PreparedStatement preparedStatement = connection.prepareStatement(editAllQuery);
            preparedStatement = editAllQuery(OGPart.getPartName(), updatedPart, OGPart, preparedStatement);
            preparedStatement.execute();
            preparedStatement.close();
            vendorInformation.getVendorList(); //NEEDED?
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public void editAllOfTypeCommonBarcode(Part OGPart, Part updatedPart) {
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {

            PreparedStatement preparedStatement = connection.prepareStatement(editAllCommonBarcodeQuery);
            preparedStatement = editAllCommonBarcodeQuery(OGPart.getPartName(), updatedPart, OGPart, preparedStatement);
            preparedStatement.execute();
            preparedStatement.close();
            vendorInformation.getVendorList(); //NEEDED?
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

}
