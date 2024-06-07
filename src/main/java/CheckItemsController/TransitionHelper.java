package CheckItemsController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to transition items
 */
public class TransitionHelper {
    /**
     * Initializes the increment/decremnt buttons on spinners. 1-10 is the default value
     * @param spinner Spinner to be initialized
     */
    void spinnerInit(Spinner<Integer> spinner){
        final int initialValue = 1;
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, initialValue);
        spinner.setValueFactory(valueFactory);
    }


    /**
     * A fade transition for a generic object
     * @param object Object to be transitioned
     */
    void fadeTransition(Object object){
        int initial = 0;
        int end = 1;
        int duration = 500;
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), (Node)object);
        fadeTransition.setFromValue(initial);
        fadeTransition.setToValue(end);
        fadeTransition.play();
    }


    /**
     * Helper method for making new barcode
     */
    void translateBarcodeItems(JFXButton button1, JFXButton button2, JFXCheckBox box1, int direction){
        List<TranslateTransition> transitions = new ArrayList<>();
        int duration = 1;
        transitions.add(new TranslateTransition(Duration.millis(duration), button1));
        transitions.add(new TranslateTransition(Duration.millis(duration), button2));
        transitions.add(new TranslateTransition(Duration.millis(duration), box1));
        translateList(transitions, direction);
    }

    /**
     * Helper method to easily transition multiple items
     * @param items Items to be moved
     * @param direction Direction to be moved in
     */
    private void translateList(List<TranslateTransition> items, int direction){
        for (TranslateTransition transition : items) {
            transition.setByY(direction);
            transition.play();
        }
    }
}
