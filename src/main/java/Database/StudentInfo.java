package Database;

import Database.ObjectClasses.Student;
import HelperClasses.DatabaseHelper;
import InventoryController.StudentCheckIn;

import java.sql.*;

public class StudentInfo {
    private final String url = Database.host + "/student_check_in";
    private final String getStudentNameFromIDQuery = "\n" +
            "select studentName from students\n" +
            "where studentID = ?";
    private final String createNewStudent ="insert into students(studentID, email, studentName, createdAt, createdBy)\n" +
            "values(?,?,?,?,?);";
    private final String getStudentNameFromEmailQuery = "select studentName from students where email = ?";

    private DatabaseHelper helper = new DatabaseHelper();

    Database database = new Database();

    public Student selectStudentClean(String email){
        String query = "Select studentName, email, studentID from students where email = ?";
        String studentEmail = "";
        String name ="";
        int id = 0;
        try( Connection connection = DriverManager.getConnection(url, Database.username, Database.password)){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                studentEmail = rs.getString("email");
                name = rs.getString("studentName");
                id = rs.getInt("studentID");
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return new Student(name,id,studentEmail);
    }

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
            StudentCheckIn.logger.error("IllegalStateException: Can't connect to the database when looking for student.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return sName;
    }

    public boolean getStudentIDFromEmail(String email){
        String sName = "";
        String query = "select studentID from students where email = ?";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                sName= rs.getString("studentID");
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("IllegalStateException: Can't connect to the database when looking for student.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        if (sName==null){
            return true;
        }
        return false;

    }

    public void updateStudent(String studentEmail, int studentID){
        String query = " update students\n" +
                "  set studentID = ?\n" +
                " where email = ?";

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,studentID);
            preparedStatement.setString(2, studentEmail);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when setting part status.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    private PreparedStatement createNewStudentHelper(int studentID, String email, String studentName, PreparedStatement preparedStatement){
        try {
            preparedStatement.setInt(1, studentID);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, studentName);
            preparedStatement.setString(4, helper.getCurrentDate());
            preparedStatement.setString(5, "Jim");//Hardcoded for now
        }catch (SQLException e){
            StudentCheckIn.logger.error("IllegalStateException: Can't connect to the database when looking for student.");
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
            StudentCheckIn.logger.error("IllegalStateException: Can't connect to the database when looking for student.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public void createNewStudent(Student s){
        try(Connection connection = DriverManager.getConnection(url, Database.username, Database.password)){
            PreparedStatement statement = connection.prepareStatement(createNewStudent);
            createNewStudentHelper(s.getRFID(), s.getEmail(), s.getName(), statement);
            statement.close();
        }catch (SQLException e){
            StudentCheckIn.logger.error("IllegalStateException: Can't connect to the database when looking for student.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    public String getStudentNameFromEmail(String email){
        String sName = "";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(getStudentNameFromEmailQuery);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                sName = rs.getString("studentName");
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("IllegalStateException: Can't connect to the database when looking for student.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return sName;
    }

}
