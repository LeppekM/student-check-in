package Database;

import InventoryController.CheckedOutList;
import InventoryController.ControllerCheckedOutTab;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class CheckedOutParts {


    private final String url = "jdbc:mysql://localhost:3306/sdl";
    private final String username = "langdk";
    private final String password = "password";
    private final String SELECTQUERY = "SELECT checkout_parts.partID, checkout_parts.checkoutQuantity, checkout_parts.dueAt, parts.partName\n" +
            "FROM checkout_parts\n" +
            "INNER JOIN parts ON checkout_parts.partID = parts.partID";
    private Statement statement;
    private int checkoutID;
    private int partID;
    private int checkoutQuantity;
    private String dueDate;
    private String partName;
    //ArrayList<CheckedOutList> checkedOutLists = new ArrayList<>();
    public ObservableList<CheckedOutList> data = FXCollections.observableArrayList();


    public void getCheckedOutItems(){

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECTQUERY);
            while(resultSet.next()){
                setVariables(resultSet);
                CheckedOutList checkedOutList = new CheckedOutList(partID, partName, checkoutQuantity, dueDate);
                data.add(checkedOutList);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database", e);
        }
    }

    private void setVariables(ResultSet resultSet){
        try {
            partID = resultSet.getInt("partId");
            checkoutQuantity = resultSet.getInt("checkoutQuantity");
            dueDate = resultSet.getString("dueAt");
            partName = resultSet.getString("partName");
        } catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database");
        }
    }





}
