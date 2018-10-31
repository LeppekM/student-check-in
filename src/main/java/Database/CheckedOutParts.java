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
    private final String SELECTQUERY = "SELECT students.studentName, parts.partName, checkout_parts.checkoutQuantity, checkouts.checkoutAt, checkout_parts.dueAt\n" +
            "FROM checkout_parts\n" +
            "INNER JOIN parts \n" +
            "ON checkout_parts.partID = parts.partID\n" +
            "INNER JOIN checkouts\n" +
            "ON checkout_parts.checkoutID = checkouts.checkoutID\n" +
            "INNER JOIN students\n" +
            "ON checkouts.studentID = students.studentID";
    private Statement statement;

    private int checkoutQuantity;
    private String checkedOutAt;
    private String dueDate;
    private String partName;
    private String studentName;

    public ObservableList<CheckedOutList> data = FXCollections.observableArrayList();


    public void getCheckedOutItems(){

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECTQUERY);
            while(resultSet.next()){
                setVariables(resultSet);
                CheckedOutList checkedOutList = new CheckedOutList(studentName, partName, checkoutQuantity, checkedOutAt, dueDate);
                data.add(checkedOutList);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database", e);
        }
    }

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





}
