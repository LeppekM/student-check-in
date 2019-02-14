package Logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class LogEntry {
    private String contents, origin;
    private LocalDateTime time;
    public LogEntry(LocalDateTime time, String content){

        this.time = time;
        this.contents = content;
    }

    @Override
    public String toString() {
        return "[" + time.toString() + "] " + origin + ": " + contents;
    }
}
