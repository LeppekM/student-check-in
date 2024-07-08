package HelperClasses;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Helper class which assists in managing time
 */
public class TimeUtils {

    public Date convertStringtoDate(String stringDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("d MMM yyyy hh:mm:ss a").parse(stringDate);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + stringDate);
        } catch (NullPointerException e){
            return null;
        }
        return date;
    }

    /**
     * @param timestamp the SQL timestamp that is being parsed
     * @return a Java Date object with the correct date AND time
     */
    public static Date parseTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : new Date(timestamp.getTime());
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
     * @return current dateTime in SQL friendly insertable
     */
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * Helper method to get the current date
     * @return today's date
     */
    public static Date getToday() {
        long date = System.currentTimeMillis();
        return new java.sql.Date(date);
    }

    /**
     * Sets custom due date
     *
     * @return Custom due date
     */
    public String setDueDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return setDueDate(calendar);
    }

    /**
     * Sets extended due date
     *
     * @param localDate Date to be set
     * @return Extended due date
     */
    public String setExtendedDuedate(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        return setDueDate(calendar);
    }

    /**
     * Helper method to set the due date as the end of the calendar date for the selected date on the passed calendar
     * @param calendar that is formatted to the correct date for the part to be due
     * @return the DateFormat String of the end of the day
     */
    private String setDueDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Calculates the date that was 2 years ago from today
     * @return the date that was 2 years ago from today
     */
    public static Timestamp getTwoYearsAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -2);
        return new Timestamp(cal.getTimeInMillis());
    }
}
