package Logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEntry {
    private String contents, origin;
    private Date time;
    public LogEntry(Date time, String content, String origin){

        this.time = time;
        this.contents = content;
        this.origin = origin;
    }


    public static String getCurrentTimeUsingDate() {
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    @Override
    public String toString() {
        return "[" + time.toString() + "] " + origin + ": " + contents;
    }
}
