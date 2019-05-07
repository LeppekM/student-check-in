package CheckItemsController;

import java.time.LocalDate;

public class ExtendedCheckoutObject {

    private static String barcode, barcode2, barcode3, barcode4, barcode5, course, prof, extendedDate;
    private static boolean fieldsFilled;


    public ExtendedCheckoutObject(String course, String prof, String extendedDate) {
        this.course = course;
        this.prof = prof;
        this.extendedDate = extendedDate;
    }

    public static boolean isFieldsFilled() {
        return fieldsFilled;
    }


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        ExtendedCheckoutObject.barcode = barcode;
    }

    public String getBarcode2() {
        return barcode2;
    }

    public void setBarcode2(String barcode2) {
        ExtendedCheckoutObject.barcode2 = barcode2;
    }

    public String getBarcode3() {
        return barcode3;
    }

    public void setBarcode3(String barcode3) {
        ExtendedCheckoutObject.barcode3 = barcode3;
    }

    public String getBarcode4() {
        return barcode4;
    }

    public void setBarcode4(String barcode4) {
        ExtendedCheckoutObject.barcode4 = barcode4;
    }

    public String getBarcode5() {
        return barcode5;
    }

    public void setBarcode5(String barcode5) {
        ExtendedCheckoutObject.barcode5 = barcode5;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        ExtendedCheckoutObject.course = course;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        ExtendedCheckoutObject.prof = prof;
    }

    public String getExtendedDate() {
        return extendedDate;
    }

    public void setExtendedDate(String extendedDate) {
        ExtendedCheckoutObject.extendedDate = extendedDate;
    }
}
