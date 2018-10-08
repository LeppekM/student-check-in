package gui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class InfoPopUp {
    @FXML
    CheckBox save;

    @FXML
    TextField name;

    public void saveItem(){
        if (save.isSelected()){
            CheckItemsController cc = new CheckItemsController();
            if(cc.checkOutTable.getItems().contains(name.getAccessibleText())){

            }
        }
    }
}
