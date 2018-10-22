package database;

import java.sql.*;
import java.util.ArrayList;

public class Database {


    final String url = "jdbc:mysql://localhost:3306/sdl";
    final String username = "langdk";
    final String password = "password";
    private Statement statement;

    public void addStudentID(String studentID, int barcode){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            statement = connection.createStatement();
            statement.executeUpdate(addStudentIDHelper(studentID, barcode));
            System.out.println(addStudentIDHelper(studentID, barcode));
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    public void removeStudentID(String studentID){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            statement = connection.createStatement();
            statement.executeUpdate(removeStudentIDHelper(studentID));
            System.out.println(removeStudentIDHelper(studentID));
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

    }

    public String addStudentIDHelper(String studentID, int barcode){
        return "UPDATE part set studentID = "+studentID+" WHERE barcode = " + barcode;
    }

    public String removeStudentIDHelper(String studentID){
        return "UPDATE part set studentID = null WHERE studentID = " + studentID;
    }

}
