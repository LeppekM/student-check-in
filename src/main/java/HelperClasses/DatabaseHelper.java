package HelperClasses;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper {
    /**
     * This method gets the current date
     * @return Current date
     */
    public String getCurrentDate(){
        return new SimpleDateFormat("dd MMM yyyy hh:mm a").format(Calendar.getInstance().getTime());
    }

    public String setDueDate(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        calendar.setTime(date);
//        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
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
