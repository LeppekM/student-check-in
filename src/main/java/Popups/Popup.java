package Popups;

import Database.Database;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public abstract class Popup {

    private Pane root;
    protected VBox vbox;

    protected JFXButton submitButton;

    protected static final int HEIGHT = 35;
    protected static final int WIDTH = 200;
    protected static final String LABEL_STYLE = "-fx-font-size: 16px;";
    protected static final String TEXTFIELD_STYLE = "-fx-font-size: 16px; -jfx-focus-color: FIREBRICK;";


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

    protected Label createLabel(String text) {
        Label l = new Label(text);
        l.setMaxSize(WIDTH-75, HEIGHT);
        l.setMinSize(WIDTH-75, HEIGHT);
        l.setStyle(LABEL_STYLE);
        l.alignmentProperty().set(Pos.CENTER_RIGHT);
        return l;
    }

    protected JFXTextField createTextField(String text, boolean editable) {
        JFXTextField textField = new JFXTextField();
        textField.setText(text);
        textField.setEditable(editable);
        textField.setStyle(TEXTFIELD_STYLE);
        textField.setMinSize(WIDTH, HEIGHT);
        textField.setMaxSize(WIDTH, HEIGHT);
        return textField;
    }

    public HBox add(String label, String field, boolean editable) {
        HBox hbox = new HBox();
        hbox.getChildren().add(createLabel(label));
        hbox.getChildren().add(createTextField(field, editable));
        vbox.getChildren().add(hbox);
        return hbox;
    }

    public void addHBox(HBox hbox) {
        vbox.getChildren().add(hbox);
    }

    public JFXPasswordField addPasswordField(String label){
        HBox hbox = new HBox();
        hbox.getChildren().add(createLabel(label));
        JFXPasswordField field = new JFXPasswordField();
        field.setStyle(TEXTFIELD_STYLE);
        field.setMinSize(WIDTH, HEIGHT);
        field.setMaxSize(WIDTH, HEIGHT);
        hbox.getChildren().add(field);
        vbox.getChildren().add(hbox);
        return field;
    }

    public JFXComboBox<String> addVendorField() {
        JFXComboBox<String> vendorField;
        HBox vendorBox = new HBox();
        Label label = createLabel("Vendor: ");
        vendorBox.getChildren().add(label);
        ArrayList<String> vendors = Database.getInstance().getVendorList();
        vendorField = new JFXComboBox<>(FXCollections.observableArrayList(vendors));
        vendorField.setValue("MSOE");
        vendorField.setMinSize(WIDTH, HEIGHT);
        vendorField.setMaxSize(WIDTH, HEIGHT);
        vendorField.setStyle(TEXTFIELD_STYLE);
        vendorBox.getChildren().add(vendorField);
        vbox.getChildren().add(vendorBox);
        return vendorField;
    }

    public HBox addSerialBox(String serialNum, String suffix) {
        JFXTextField serialField, suffixField;
        HBox serialBox = new HBox();
        serialBox.getChildren().add(createLabel("Serial Number: "));
        serialField = createTextField(serialNum, true);
        serialField.setMinWidth(WIDTH * 0.3);
        serialField.setMaxWidth(WIDTH * 0.3);
        serialField.setText("1");
        serialBox.getChildren().add(serialField);
        Region region = new Region();
        region.setMaxWidth(WIDTH * 0.1);
        region.setMinWidth(WIDTH * 0.1);
        serialBox.getChildren().add(region);
        suffixField = createTextField(suffix, true);
        suffixField.setMinWidth(WIDTH * 0.6);
        suffixField.setMaxWidth(WIDTH * 0.6);
        suffixField.setPromptText("Suffix ie:V2");
        serialBox.getChildren().add(suffixField);
        vbox.getChildren().add(serialBox);
        return serialBox;
    }

    public abstract void populate();

    public abstract void submit();

}
