package CheckItemsController;

import Database.SavedPart;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class SavedPopUp extends StudentPage {

    @FXML
    private JFXTextField student, part, quantity, date, dueDate, saved,  prof, course;

    @FXML
    private JFXTextArea reason;

    public void populate(SavedPart part){
        student.setText(part.getStudentName().get());
        this.part.setText(part.getPartName().get());
        quantity.setText(part.getQuantity().get() + "");
        date.setText(part.getCheckedOutAt().get());
        dueDate.setText(part.getDueAt().get());
        saved.setText(part.getSavedAt().get());
        prof.setText("Mr. Julio Coolio");
        course.setText("SE8362 - 001");
        reason.setText("He needs to be cooler with this grade 'A' " + part.getPartName().get());
    }
}
