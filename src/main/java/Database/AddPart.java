package Database;

import java.sql.*;
import java.time.LocalDateTime;

public class AddPart {
    private final String url = "jdbc:mysql://localhost:3306/sdl";
    private final String username = "langdk";
    private final String password = "password";
    private String addQuery = "INSERT INTO parts(partID, partName, serialnumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity,  availableQuantity, createdAt, createdBy)"+
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";


    public void addItem(Part part){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(addQuery);
            insertQuery(part, preparedStatement).execute();
//            ResultSet resultSet = statement.executeQuery(SELECTQUERY);
//            while(resultSet.next()){
//                setVariables(resultSet);
//                CheckedOutItems checkedOutItems = new CheckedOutItems(studentName, partName, checkoutQuantity, checkedOutAt, dueDate);
//                data.add(checkedOutItems);
//            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    private PreparedStatement insertQuery(Part part, PreparedStatement preparedStatement){
        try {
            preparedStatement.setInt(1, 5);
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
            e.printStackTrace();
        }
        return preparedStatement;
    }

    private String getCurrentDate(){
        return LocalDateTime.now().toString();
    }
}
