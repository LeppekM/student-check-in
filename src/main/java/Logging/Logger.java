package Logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private final String logLocation = System.getProperty("user.dir");
    private String logName;
    public Logger(){
        logName = "log-" + getCurrentTimeUsingDate() + ".txt";
    }

    public static String getCurrentTimeUsingDate() {
        Date date = new Date();
        String strDateFormat = "yyyy,MM,dd";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    private void logToFile(LogEntry entry) throws IOException {
        String fileContent = "\n"+entry.toString();
        BufferedWriter writer = new BufferedWriter(new FileWriter(logLocation + "/" + logName));
        writer.write(fileContent);
        writer.close();
    }
}
