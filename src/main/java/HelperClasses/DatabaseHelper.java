package HelperClasses;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DatabaseHelper {
    /**
     * This method gets the current date
     * @return Current date
     */
    public String getCurrentDate(){
        return LocalDateTime.now().toString();
    }

    /**
     * Helper method gets tomorrow's date.
     * @return Tomorrow's date
     */
    public String getTomorrowDate(){
        Date dt = new Date();
        return LocalDateTime.from(dt.toInstant().atZone(ZoneId.of("UTC"))).plusDays(1).toString();
    }
}
