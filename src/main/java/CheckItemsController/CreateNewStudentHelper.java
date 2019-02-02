package CheckItemsController;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;

import java.util.List;

public class CreateNewStudentHelper {

    TransitionHelper transition = new TransitionHelper();



    void addFadeTransitionItems(){

    }

    void setFieldInfo(JFXTextField studentNameField, JFXTextField emailField, Label email, JFXCheckBox extended){
        studentNameField.setDisable(false);
        emailField.setVisible(true);
        email.setVisible(true);
        extended.setDisable(true);

    }

}
