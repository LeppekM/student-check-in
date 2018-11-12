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
        double testValue = controllerAddPart.priceCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

    /**
     * If zero into price textfield, -1 should be returned as a sign that the value is invalid.
     */
    @Test
    public void enteringZeroIntoPriceFieldShouldReturnNegativeOne(){
        //Arrange
        String invalidInput = "0";
        double correctValue = -1;

        //Act
        double testValue = controllerAddPart.priceCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

    /**
     * If zero into quantity textfield, -1 should be returned as a sign that the value is invalid.
     */
    @Test
    public void enteringZeroIntoQuantityFieldShouldReturnNegativeOne(){
        //Arrange
        String invalidInput = "0";
        double correctValue = -1;

        //Act
        double testValue = controllerAddPart.quantityCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

    /**
     * If nominal value is entered into price textfield, the value should be returned as a sign that the value is valid.
     */
    @Test
    public void enteringNominalValueIntoPriceFieldShouldReturnNominalValue(){
        //Arrange
        String invalidInput = "25";
        double correctValue = 25;

        //Act
        double testValue = controllerAddPart.priceCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

    /**
     * If nominal value is entered into quantity textfield, the value should be returned as a sign that the value is valid.
     */
    @Test
    public void enteringNominalValueIntoQuantityFieldShouldReturnNominalValue(){
        //Arrange
        String invalidInput = "25";
        double correctValue = 25;

        //Act
        double testValue = controllerAddPart.quantityCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

    /**
     * If -1 is entered into price textfield, the value returned should be -1, indicating it's not valid
     */
    @Test
    public void enteringNegativeOnceIntoPriceFieldShouldReturnNegativeOne(){
        //Arrange
        String invalidInput = "-1";
        double correctValue = -1;

        //Act
        double testValue = controllerAddPart.priceCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

    /**
     * If nominal value is entered into quantity textfield, the value returned should be -1, indicating it's not valid
     */
    @Test
    public void enteringNegativeOneIntoQuantityFieldShouldReturnNegativeOne(){
        //Arrange
        String invalidInput = "-1";
        double correctValue = -1;

        //Act
        double testValue = controllerAddPart.quantityCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

    /**
     * If one is entered into quantity textfield, the value returned should be 1
     */
    @Test
    public void enteringOneIntoQuantityFieldShouldReturnOne(){
        //Arrange
        String invalidInput = "1";
        double correctValue = 1;

        //Act
        double testValue = controllerAddPart.quantityCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

    /**
     * If one is entered into price textfield, the value returned should be 1
     */
    @Test
    public void enteringOneIntoPriceFieldShouldReturnOne(){
        //Arrange
        String invalidInput = "1";
        double correctValue = 1;

        //Act
        double testValue = controllerAddPart.priceCheck(invalidInput);

        //Assert
        assertEquals(correctValue, testValue, 0.01);
    }

}
