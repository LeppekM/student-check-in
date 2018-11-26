package InventoryControllerTestFX;
import InventoryController.ControllerAddPart;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.net.URL;

import static org.testfx.assertions.api.Assertions.assertThat;

public class AddPartTest extends ApplicationTest {



    @Override
    public void start (Stage stage)throws IOException{
        URL myFxmlURL = ClassLoader.getSystemResource("AddPart.fxml");
        FXMLLoader loader = new FXMLLoader(myFxmlURL);
        Parent root = loader.load(myFxmlURL);
        Scene scene = new Scene(root, 789, 620);
        stage.setResizable(false);
        stage.setTitle("Barcode Scanner");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testEnglishInput () {
        clickOn("#nameField");
        write("This is a test!");

    }



//    private final ControllerAddPart controllerAddPart = new ControllerAddPart();
//
//
////    protected Parent getRootNode() throws IOException {
////        Parent parent = null;
////        try {
////            parent = FXMLLoader.load(getClass().getResource("AddPart.fxml"));
////            return parent;
////        }catch (IOException e){
////            e.printStackTrace();
////        }
////        return parent;
////    }
//
//    @Override
//    public void start (Stage stage){
//
//    }
//
//    @Test
//    public void hasAddButton(){
//        TextField firstname = find("priceField");
//        firstname.setText("23");
//        verifyThat("#firstname", hasText("23"));
//    }
}
