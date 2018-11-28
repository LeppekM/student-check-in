package InventoryControllerTestFX;

import InventoryController.CheckedOutItems;
import InventoryController.ControllerCheckedOutTab;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.net.URL;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

public class CheckedOutTabTest extends ApplicationTest {



    @Override
    public void start (Stage stage)throws IOException {
        final ObservableList<CheckedOutItems> data = FXCollections.observableArrayList(new CheckedOutItems("Daniel", "test", 1, "2018-10-10","2018-10-11"));

        URL myFxmlURL = ClassLoader.getSystemResource("InventoryCheckedOutTab.fxml");
        FXMLLoader loader = new FXMLLoader(myFxmlURL);
        Parent root = loader.load(myFxmlURL);
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Checked Out Parts Test");
        stage.setScene(scene);
        stage.show();

    }



    @Test
    public void verifyTableData(){
        clickOn("#sNameCol");
        clickOn("#partNameCol");
        clickOn("#quantityCol");
        clickOn("#checkOutAtCol");
        clickOn("#dueDateCol");
    }
}
