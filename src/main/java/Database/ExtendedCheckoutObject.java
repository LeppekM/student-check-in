package Database;

/**
 * Stores info about an extended checkout object
 */
public class ExtendedCheckoutObject {

    private static String barcode, course, prof, extendedDate;

    public ExtendedCheckoutObject(String course, String prof, String extendedDate) {
        ExtendedCheckoutObject.course = course;
        ExtendedCheckoutObject.prof = prof;
        ExtendedCheckoutObject.extendedDate = extendedDate;
    }
    /*************************
     *
     * Below are methods to get the stored info about the checked out part
     *
     ************************/

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        ExtendedCheckoutObject.barcode = barcode;
    }

    public String getCourse() {
        return course;
    }

    public String getProf() {
        return prof;
    }

    public String getExtendedDate() {
        return extendedDate;
    }
}
