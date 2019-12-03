package HelperClasses;

import InventoryController.StudentCheckIn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper {

    public Date convertStringtoDate(String stringDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("d MMM yyyy hh:mm:ss a").parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            StudentCheckIn.logger.error("Item is being checked out for first time; no checkout date");
            return null;
        }
        return date;
    }

    /**
     * This method gets the current date and time
     *
     * @return Current date
     */
    public String getCurrentDateTimeStamp() {
        return new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(Calendar.getInstance().getTime());
    }

    /**
     * Gets current date
     *
     * @return Current date
     */
    public String getCurrentDate() {
        return LocalDateTime.now().toString();
    }

    /**
     * Sets custom due date
     *
     * @return Custom due date
     */
    public String setDueDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Sets extended due date
     *
     * @param localDate Date to be set
     * @return Extended due date
     */
    public String setExtendedDuedate(LocalDate localDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Helper method gets tomorrow's date.
     *
     * @return Tomorrow's date
     */
    public String getTomorrowDate() {
        Date dt = new Date();
        return LocalDateTime.from(dt.toInstant().atZone(ZoneId.of("UTC"))).plusDays(1).toString();
    }
}
