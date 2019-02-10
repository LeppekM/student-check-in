package CheckItemsController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class TransitionHelper {

    void spinnerInit(Spinner<Integer> spinner){
        final int initialValue = 1;
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, initialValue);
        spinner.setValueFactory(valueFactory);
    }

    void barcodeItemsFadeTransition(Spinner spinner, JFXButton button, JFXTextField textField){
        int initial = 0;
        int end = 1;
        int duration = 500;
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), spinner);
        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(duration), button);
        FadeTransition fadeTransition2 = new FadeTransition(Duration.millis(duration), textField);
        fadeTransition1.setFromValue(initial);
        fadeTransition2.setFromValue(initial);
        fadeTransition.setFromValue(initial);
        fadeTransition1.setToValue(end);
        fadeTransition.setToValue(end);
        fadeTransition2.setToValue(end);
        fadeTransition.play();
        fadeTransition1.play();
        fadeTransition2.play();
    }

    void deleteBarcodeTranslate(HBox hbox, JFXTextField field){
        int direction = -60;
        int duration = 500;
        TranslateTransition t = new TranslateTransition(Duration.millis(duration), hbox);
        TranslateTransition t2 = new TranslateTransition(Duration.millis(duration), field);
        t.setByY(direction);
        t2.setByY(direction);
        t.play();
        t2.play();
    }

    void fadeTransition(Object object){
        int initial = 0;
        int end = 1;
        int duration = 500;
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), (Node)object);
        fadeTransition.setFromValue(initial);
        fadeTransition.setToValue(end);
        fadeTransition.play();
    }

    void fadeTransitionNewStudentObjects(Label email, JFXTextField emailField){
        fadeTransition(email);
        fadeTransition(emailField);
    }

    void translateExtendedStudentItems(Label course, Label prof, Label due, JFXTextField courseT, JFXTextField profT, JFXDatePicker dueT, JFXCheckBox box, JFXButton butt, JFXButton butt2){
        int direction = 35;
        int direction2 = 10;
        course.setTranslateY(direction);
        prof.setTranslateY(direction);
        due.setTranslateY(direction);
        courseT.setTranslateY(direction);
        profT.setTranslateY(direction);
        dueT.setTranslateY(direction);
    }

    void translateNewStudentItems(Label barcode, Label quantity, JFXTextField barcodeField, JFXTextField quantityField, JFXCheckBox box, JFXButton submit, JFXButton reset){
        List<TranslateTransition> transitions = new ArrayList<>();
        int duration = 500;
        int direction = 50;
        transitions.add(new TranslateTransition(Duration.millis(duration), barcode));
        transitions.add(new TranslateTransition(Duration.millis(duration), quantity));
        transitions.add(new TranslateTransition(Duration.millis(duration), barcodeField));
        transitions.add(new TranslateTransition(Duration.millis(duration), quantityField));
        transitions.add(new TranslateTransition(Duration.millis(duration), box));
        transitions.add(new TranslateTransition(Duration.millis(duration), submit));
        transitions.add(new TranslateTransition(Duration.millis(duration), reset));
        translateList(transitions, direction);
    }

    void translateBarcodeItems(JFXButton button1, JFXButton button2, JFXCheckBox box1, JFXCheckBox box2, int direction){
        List<TranslateTransition> transitions = new ArrayList<>();
        int duration = 500;
        transitions.add(new TranslateTransition(Duration.millis(duration), button1));
        transitions.add(new TranslateTransition(Duration.millis(duration), button2));
        transitions.add(new TranslateTransition(Duration.millis(duration), box1));
        transitions.add(new TranslateTransition(Duration.millis(duration), box2));
        translateList(transitions, direction);
    }

    private void translateList(List<TranslateTransition> items, int direction){
        for (TranslateTransition transition : items) {
            transition.setByY(direction);
            transition.play();
        }
    }
}
