package gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class InfoPopUp {
    @FXML
    CheckBox save;

    @FXML
    TextField name, serialNumber;

    public void saveItem(){
        if (save.isSelected()){
            CheckItemsController cc = new CheckItemsController();
            Part part; //need inventory/database to have a list of parts so that I can comapre name.getText() to the name of a part and use that info to create a new item in the save items table
            if(cc.checkOutTable.getItems().contains(name.getText())){
                cc.savedTable.getItems().add(new Part(name.getText(), "shelf", .99, Integer.parseInt(serialNumber.getText()),
                12345, "me", "you", false, 460753));
                cc.savedTable.refresh();
            }
        }
    }
}
