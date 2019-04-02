package CheckItemsController;

import java.time.LocalDate;

public class ExtendedCheckoutObject {

    private static String barcode, barcode2, barcode3, barcode4, barcode5, course, prof;
    private static LocalDate extendedDate;

    public ExtendedCheckoutObject(String barcode, String barcode2, String barcode3, String barcode4, String barcode5, String course, String prof, LocalDate extendedDate) {
        this.barcode = barcode;
        this.barcode2 = barcode2;
        this.barcode3 = barcode3;
        this.barcode4 = barcode4;
        this.barcode5 = barcode5;
        this.course = course;
        this.prof = prof;
        this.extendedDate = extendedDate;
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

    public LocalDate getExtendedDate() {
        return extendedDate;
    }

    public void setExtendedDate(LocalDate extendedDate) {
        ExtendedCheckoutObject.extendedDate = extendedDate;
    }
}
