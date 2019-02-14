package InventoryControllerTestFX;
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

public class AddPartTest extends ApplicationTest {



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
        URL myFxmlURL = ClassLoader.getSystemResource("fxml/AddPart.fxml");
        FXMLLoader loader = new FXMLLoader(myFxmlURL);
        Parent root = loader.load(myFxmlURL);
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("AddPart Test");
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void verifyFieldInformation(){
        //Arrange/Act
        setupFields();

        //Assert
        verifyFields();
    }

    private void setupFields(){
        clickOn("#nameField");
        write("Circuit Designers");
        clickOn("#serialField");
        write("100");
        clickOn("#manufacturerField");
        write("MSOE");
        clickOn("#quantityField");
        write("100");
        clickOn("#priceField");
        write("10");
        clickOn("#vendorField");
        write("MSOE");
        clickOn("#locationField");
        write("S351");
    }

    private void verifyFields(){
        verifyThat("#nameField", hasText("Circuit Designers"));
        verifyThat("#serialField", hasText("100"));
        verifyThat("#manufacturerField", hasText("MSOE"));
        verifyThat("#quantityField", hasText("100"));
        verifyThat("#priceField", hasText("10"));
        verifyThat("#vendorField", hasText("MSOE"));
        verifyThat("#locationField", hasText("S351"));
    }

}
