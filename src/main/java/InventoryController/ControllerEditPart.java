package InventoryController;

import Database.ObjectClasses.Part;
import javafx.fxml.Initializable;

abstract class ControllerEditPart implements Initializable {

    abstract void initPart(Part part);

    abstract void updateItem();

}