package CheckItemsController;

import HelperClasses.StageWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCheckinTab  implements Initializable {

    @FXML
    private AnchorPane main;

    private StageWrapper stageWrapper = new StageWrapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void submit(){

    }
    public void returnHome(){
        stageWrapper.newStage("Menu.fxml", main);
    }

    public void goToCheckout(){
        stageWrapper.newStage("CheckOutItems.fxml", main);

    }
}
