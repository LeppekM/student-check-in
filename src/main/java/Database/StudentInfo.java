package Database;

import HelperClasses.DatabaseHelper;

import java.sql.*;

public class StudentInfo {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    private final String getStudentNameFromIDQuery = "\n" +
            "select studentName from students\n" +
            "where studentID = ?";
    private final String createNewStudent ="insert into students(studentID, email, studentName, createdAt, createdBy)\n" +
            "values(?,?,?,?,?);";

    private DatabaseHelper helper = new DatabaseHelper();




    public String getStudentNameFromID(String studentID){
        String sName = "";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(getStudentNameFromIDQuery);
            statement.setString(1, studentID);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                sName= rs.getString("studentName");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return sName;
    }

    private PreparedStatement createNewStudentHelper(int studentID, String email, String studentName, PreparedStatement preparedStatement){
        try {
            preparedStatement.setInt(1, studentID);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, studentName);
            preparedStatement.setString(4, helper.getCurrentDate());
            preparedStatement.setString(5, "Jim");//Hardcoded for now
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    public void createNewStudent(int studentID, String email, String studentName){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(createNewStudent);
            createNewStudentHelper(studentID, email, studentName, statement).execute();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }


}
