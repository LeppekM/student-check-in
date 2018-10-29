package Database;

import java.sql.Connection;

public class Database {

    static String host = "jdbc:mysql://192.168.56.1:3306/eqod-log";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "parts";
    static Connection connection;
}
