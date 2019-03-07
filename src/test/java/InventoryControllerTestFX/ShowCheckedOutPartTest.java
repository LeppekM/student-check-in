package InventoryControllerTestFX;
import InventoryController.ControllerShowPart;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.net.URL;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

public class ShowCheckedOutPartTest extends ApplicationTest {



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
    public void start (Stage stage)throws IOException{
        Part part = new Part("HDMI", "H86234", "SONY", 3.99, "MSOE", "Closet", "123456", false, 1, 0);

        URL myFxmlURL = ClassLoader.getSystemResource("ShowPart.fxml");
        FXMLLoader loader = new FXMLLoader(myFxmlURL);
        Parent root = loader.load(myFxmlURL);
        ((ControllerShowPart) loader.getController()).initPart(part, "checkedOut");
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Show Part Test");
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void verifyFieldInformation(){
        //Assert
        verifyFields();
    }

    private void verifyFields(){
        verifyThat("#nameField", hasText("HDMI"));
        verifyThat("#serialField", hasText("H86234"));
        verifyThat("#manufacturerField", hasText("SONY"));
        verifyThat("#quantityField", hasText("0"));
        verifyThat("#priceField", hasText("$3.99"));
        verifyThat("#vendorList", hasText("MSOE"));
        verifyThat("#locationField", hasText("Closet"));
        verifyThat("#barcodeField", hasText("123456"));
    }

}
