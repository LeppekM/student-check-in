package InventoryController;

import Database.ObjectClasses.Part;
import javafx.fxml.Initializable;

abstract class ControllerEditPart extends ControllerInventoryPage implements Initializable {

    abstract void initPart(Part part);

    abstract void updateItem();

}