package Database;

import Database.ObjectClasses.Part;
import InventoryController.StudentCheckIn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class uses a query to edit a part in the database
 */
public class EditPart {
    private String editQuery = "UPDATE parts SET serialNumber = ?, barcode = ?, price = ?, location = ?, " +
            "updatedAt = ? WHERE partID = ?;";

    private String editAllCommonBarcodeQuery = "UPDATE parts SET partName = ?, price = ?, location = ?, " +
            "barcode = ?, manufacturer = ?, vendorID = ?, updatedAt = ? WHERE partID = ?;";

    private String editAllQuery = "UPDATE parts SET partName = ?, price = ?, location = ?, manufacturer = ?, vendorID = ?, " +
            "updatedAt = ? WHERE partName = ?;";

    private VendorInformation vendorInformation = new VendorInformation();
    private Database database = new Database();

    public boolean barcodeUsed(long barcode){
        String query = "SELECT barcode from parts";
        List<Long> barcodes = new ArrayList<>();
        try {
            PreparedStatement statement = database.getConnection().prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                barcodes.add(rs.getLong("barcode"));
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return barcodes.contains(barcode);
    }

    /**
     * This method edits an item in the database
     * @param part The part to be edited
     */
    public void editItem(Part part){
        try {
            PreparedStatement preparedStatement = database.getConnection().prepareStatement(editQuery);
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
    private PreparedStatement editAllQuery(String originalPartName, Part part,PreparedStatement preparedStatement){
        try {
            preparedStatement.setString(1, part.getPartName());
            preparedStatement.setDouble(2, part.getPrice());
            preparedStatement.setString(3, part.getLocation());
            preparedStatement.setString(4, part.getManufacturer());
            preparedStatement.setInt(5, new VendorInformation().getVendorIDFromVendor(part.getVendor()));
            preparedStatement.setString(6, getCurrentDate());
            preparedStatement.setString(7, originalPartName);
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
    private PreparedStatement editAllCommonBarcodeQuery(String originalPartName, Part part, String partID, PreparedStatement preparedStatement){
        try {
//            ArrayList<String> partIDsForPart = new Database().getAllPartIDsForPartName(originalPartName);
            preparedStatement.setString(1, part.getPartName());
            preparedStatement.setDouble(2, part.getPrice());
            preparedStatement.setString(3, part.getLocation());
            preparedStatement.setLong(4, part.getBarcode());
            preparedStatement.setString(5, part.getManufacturer());
            preparedStatement.setInt(6, new VendorInformation().getVendorIDFromVendor(part.getVendor()));
            preparedStatement.setString(7, getCurrentDate());
            preparedStatement.setString(8, partID);
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    public void editAllOfType(String originalPartName, Part updatedPart) {
        try {
            PreparedStatement preparedStatement = database.getConnection().prepareStatement(editAllQuery);
            ArrayList<String> partIDsForPart = database.getAllPartIDsForPartName(originalPartName);
            for (String id: partIDsForPart) {
                preparedStatement = editAllQuery(originalPartName, updatedPart, preparedStatement);
                preparedStatement.execute();
            }
            preparedStatement.close();
            vendorInformation.getVendorList(); //NEEDED?
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public void editAllOfTypeCommonBarcode(String originalPartName, Part updatedPart) {
        try {
            PreparedStatement preparedStatement = database.getConnection().prepareStatement(editAllCommonBarcodeQuery);
            ArrayList<String> partIDsForPart = database.getAllPartIDsForPartName(originalPartName);
            for (String id: partIDsForPart) {
                preparedStatement = editAllCommonBarcodeQuery(originalPartName, updatedPart, id, preparedStatement);
                preparedStatement.execute();
            }
            preparedStatement.close();
            vendorInformation.getVendorList(); //NEEDED?
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

}
