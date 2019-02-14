package InventoryControllerTestFX;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TableViewMatchers;

import java.io.IOException;
import java.net.URL;

import static org.testfx.api.FxAssert.verifyThat;

public class CheckedOutTabTest extends ApplicationTest {


    @BeforeClass
    public static void setupHeadlessMode()throws Exception{
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
//        if(Boolean.getBoolean("headless")){
//            System.setProperty("testfx.robot", "glass");
//            System.setProperty("testfx.headless", "true");
//            System.setProperty("prism.order", "sw");
//            System.setProperty("prism.txt", "t2k");
//            System.setProperty("java.awt.headless", "true");
//        }
//        registerPrimaryStage();
    }

    @Override
    public void start (Stage stage)throws IOException {

        URL myFxmlURL = ClassLoader.getSystemResource("fxml/InventoryCheckedOutTab.fxml");
        FXMLLoader loader = new FXMLLoader(myFxmlURL);
        Parent root = loader.load(myFxmlURL);
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Checked Out Parts Test");
        stage.setScene(scene);
        stage.show();

    }


    /**
     * Not a complete test, only figuring out how to access tableview data.
     */
    @Test
    public void verifyTableData(){
        verifyThat("#checkedOutItems", TableViewMatchers.containsRow("Daniel Lang","Circuit Designers", 1, "2018-10-31", "2018-11-01"));
    }
}
