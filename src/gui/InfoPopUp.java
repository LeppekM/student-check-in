package gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;

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
                cc.savedTable.getItems().add(name.getText());
                cc.savedTable.refresh();
            }
        }
    }
}

//    public void saveItem(){
//        if (save.isSelected()){
//            CheckItemsController cc = new CheckItemsController();
//            ControllerInventory ic = new ControllerInventory();
//            ArrayList<Part> listOfParts = ic.getInventory();
//            int partName = -1;
//            if (listOfParts.contains(name.getText())) {
//                partName = listOfParts.indexOf(name.getText());
//            }
//            if(partName != -1){
//            Part part = new Part(listOfParts.get(partName).getName(), listOfParts.get(partName).getLocation(), listOfParts.get(partName).getPrice(),
//                    listOfParts.get(partName).getSerialNumber(), listOfParts.get(partName).getBarcode(), listOfParts.get(partName).getVendor(),
//                    listOfParts.get(partName).getManufacturer(), listOfParts.get(partName).isFault(),
//                    listOfParts.get(partName).getStudentID()); //need inventory/database to have a list of parts so that I can comapre name.getText() to the name of a part and use that info to create a new item in the save items table
////            if(cc.checkOutTable.getItems().contains(name.getText())){
//                cc.savedTable.getItems().add(part/*new Part(name.getText(), "shelf", .99, Integer.parseInt(serialNumber.getText()),
//                12345, "me", "you", false, 460753)*/);
//                cc.savedTable.refresh();
//            }
//        }
//    }
