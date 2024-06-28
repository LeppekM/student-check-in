package Popups;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public abstract class Popup {

    private Pane root;
    protected VBox vbox;

    protected JFXButton submitButton;


    public Popup(Pane root) {
        this.root = root;

        VBox outerVBox = new VBox();
        outerVBox.setAlignment(Pos.CENTER);
        vbox = new VBox();
        root.getChildren().add(outerVBox);
        outerVBox.getChildren().add(vbox);
        vbox.setPadding(new Insets(0, 5, 10, 5));
        root.setPadding(new Insets(5, 20, 10, 20));

        submitButton = new JFXButton("Submit");
        submitButton.setOnAction(e -> submit());
        submitButton.getStylesheets().add("/css/ButtonStyle.css");
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(submitButton);
        outerVBox.getChildren().add(hbox);
        populate();
    }

    public Label add(String label, String field, boolean editable) {
        int height = 35;
        int width = 150;

        HBox hbox = new HBox();
        Label l = new Label(label);
        l.setMaxSize(width, height);
        l.setMinSize(width, height);
        l.setStyle("-fx-font-size: 16px;");
        l.alignmentProperty().set(Pos.CENTER_RIGHT);
        hbox.getChildren().add(l);

        JFXTextField textField = new JFXTextField();
        textField.setText(field);
        textField.setEditable(editable);
        textField.setStyle("-fx-font-size: 16px; -jfx-focus-color: FIREBRICK;");
        textField.setMinSize(width, height);
        textField.setMaxSize(width, height);
        hbox.getChildren().add(textField);
        vbox.getChildren().add(hbox);
        return l;
    }

    public void addHBox(HBox hbox) {
        vbox.getChildren().add(hbox);
    }

    public void addAll(ArrayList<String> inputs, boolean editable) {
        for (String s : inputs) {
            add(s, "", editable);
        }
    }

    public abstract void populate();

    public abstract void submit();

}
