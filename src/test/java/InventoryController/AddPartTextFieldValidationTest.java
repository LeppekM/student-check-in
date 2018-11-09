package InventoryController;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddPartTextFieldValidationTest {

    private ControllerAddPart controllerAddPart;


    @Before
    public void before(){
        controllerAddPart = new ControllerAddPart();
    }


    /**
     * If a string is entered into the quantity textfield, -1 should be returned as a sign that the value is invalid.
     */
    @Test
    public void enteringStringIntoIntegerOnlyFieldShouldReturnNegativeOne(){
        //Arrange
        String invalidInput = "invalid";
        int correctValue = -1;

        //Act
        int testValue = controllerAddPart.quantityCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue);
    }

    /**
     * If a string is entered into price textfield, -1 should be returned as a sign that the value is invalid.
     */
    @Test
    public void enteringStringIntoDoubleOnlyFieldShouldReturnNegativeOne(){
        //Arrange
        String invalidInput = "invalid";
        double correctValue = -1;

        //Act
        double testValue = controllerAddPart.priceCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

    /**
     * If a negative value is entered into quantity textfield, -1 should be returned as a sign that the value is invalid.
     */
    @Test
    public void enteringNegativeIntoQuantityFieldShouldReturnNegativeOne(){
        //Arrange
        String invalidInput = "-1000";
        int correctValue = -1;

        //Act
        int testValue = controllerAddPart.quantityCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue);
    }

    /**
     * If a negative value is entered into price textfield, -1 should be returned as a sign that the value is invalid.
     */
    @Test
    public void enteringNegativeIntoPriceFieldShouldReturnNegativeOne(){
        //Arrange
        String invalidInput = "-1000";
        double correctValue = -1;

        //Act
        double testValue = controllerAddPart.quantityCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

}
