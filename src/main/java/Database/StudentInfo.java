package Database;

import java.sql.*;

public class StudentInfo {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    private final String getStudentNameFromIDQuery = "\n" +
            "select studentName from students\n" +
            "where studentID = ?";




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


}
