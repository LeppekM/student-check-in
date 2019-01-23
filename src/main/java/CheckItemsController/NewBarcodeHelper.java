package CheckItemsController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class NewBarcodeHelper {

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

    void FadeTransition(Object object){
        int initial = 0;
        int end = 1;
        int duration = 500;
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), (Node)object);
        fadeTransition.setFromValue(initial);
        fadeTransition.setToValue(end);
        fadeTransition.play();
    }

    void translateItems(JFXButton button1, JFXButton button2, JFXCheckBox box1, JFXCheckBox box2, int direction){
        List<TranslateTransition> transitions = new ArrayList<>();
        int duration = 500;
        transitions.add(new TranslateTransition(Duration.millis(duration), button1));
        transitions.add(new TranslateTransition(Duration.millis(duration), button2));
        transitions.add(new TranslateTransition(Duration.millis(duration), box1));
        transitions.add(new TranslateTransition(Duration.millis(duration), box2));

        for (TranslateTransition transition : transitions) {
            transition.setByY(direction);
            transition.play();
        }


    }


}
