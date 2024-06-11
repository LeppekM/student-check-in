package HelperClasses;

import InventoryController.StudentCheckIn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper {

    public Date convertStringtoDate(String stringDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("d MMM yyyy hh:mm:ss a").parse(stringDate);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + stringDate);
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
}
