package InventoryController;

import Database.Objects.Part;
import javafx.fxml.Initializable;

abstract class ControllerEditPart extends ControllerInventoryPage implements Initializable {

    abstract void initPart(Part part);

    abstract void updateItem();

}