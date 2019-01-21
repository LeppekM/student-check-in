package Database;

public class FaultyCheckIn {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    private final String setPartsTableFaulty = "update parts\n" +
            "set isFaulty = 1\n" +
            "where partID = ? ";
}
