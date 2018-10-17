package database;

import java.sql.*;
import java.util.ArrayList;

public class Database {

    private ArrayList<Double> partInfo = new ArrayList<>();
    final String SELECT_QUERY = "SELECT barcode FROM part";



    final String url = "jdbc:mysql://localhost:3306/sdl";
    final String username = "langdk";
    final String password = "3cisweve";
    private Statement statement;
    private PreparedStatement preparedStatement;    

    public void addStudentID(String studentID, int barcode){

        System.out.println("Connecting database...");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            statement = connection.createStatement();
            preparedStatement = connection.prepareStatement(selectQuery(barcode));
            ResultSet resultSet;
            resultSet = preparedStatement.executeQuery();
            statement.executeUpdate(updateHelper(studentID, barcode));
            System.out.println(updateHelper(studentID, barcode));
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    public String updateHelper(String studentID, int barcode){
        return "UPDATE part set studentID = "+studentID+" WHERE barcode = " + barcode;
    }

    public String selectQuery(int barcode){
        return "SELECT barcode from part where barcode = " + barcode;
    }

}
