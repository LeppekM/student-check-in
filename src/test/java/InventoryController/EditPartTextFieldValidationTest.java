package InventoryController;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EditPartTextFieldValidationTest {

    private ControllerEditOnePart controllerEditOnePart;

    @Before
    public void before(){
        controllerEditOnePart = new ControllerEditOnePart();
//        Part testPart = new Part("Test Part Name", "Test Serial Number", "Test Manufacturer", 12.345,
//                "Test Vendor", "Over Here", "Scan me, please", true, 123456, false);
//        controllerEditOnePart.initPart(testPart);
    }


    /**
     * Tests that entering something that does not contain just a number returns false.
     */
    @Test
    public void validateNumberInputsContainNumbersShouldReturnFalseWhenItContainsNonNumericSymbols(){
        //Arrange
        String invalidInput = "31inv2alid4";
        boolean correctValue = false;

        //Act
        boolean testValue = controllerEditOnePart.validateNumberInputsContainNumber(invalidInput);

        //Assert
        assertEquals(correctValue, testValue);
    }

    /**
     * Tests that entering something that does not contain just a number returns false.
     */
    @Test
    public void validateAllFieldsFilledInShouldReturnFalseWhenFieldsAreEmpty(){
        //Arrange
        boolean correctValue = false;

        //Act
        boolean testPartNameEmpty = controllerEditOnePart.validateAllFieldsFilledIn("", "test", "test",
                "test", "test", "test", "test");

        boolean testSerialNumberEmpty = controllerEditOnePart.validateAllFieldsFilledIn("test", "", "test",
                "test", "test", "test", "test");

        boolean testManufacturerEmpty = controllerEditOnePart.validateAllFieldsFilledIn("test", "", "",
                "test", "test", "test", "test");

        boolean testPriceEmpty = controllerEditOnePart.validateAllFieldsFilledIn("test", "test", "test",
                "", "test", "test", "test");

        boolean testLocationEmpty = controllerEditOnePart.validateAllFieldsFilledIn("test", "test", "test",
                "test", "", "test", "test");

        boolean testBarcodeEmpty = controllerEditOnePart.validateAllFieldsFilledIn("test", "test", "test",
                "test", "test", "", "test");

        boolean testQuantityEmpty = controllerEditOnePart.validateAllFieldsFilledIn("test", "test", "test",
                "test", "test", "test", "");

        boolean testAllEmpty = controllerEditOnePart.validateAllFieldsFilledIn("", "", "", "", "", "",
                "");

        //Assert
        assertEquals(correctValue, testPartNameEmpty);
        assertEquals(correctValue, testSerialNumberEmpty);
        assertEquals(correctValue, testManufacturerEmpty);
        assertEquals(correctValue, testPriceEmpty);
        assertEquals(correctValue, testLocationEmpty);
        assertEquals(correctValue, testBarcodeEmpty);
        assertEquals(correctValue, testQuantityEmpty);
        assertEquals(correctValue, testAllEmpty);
    }

    /**
     * Tests that entering something that does not contain just a number returns false.
     */
    @Test
    public void validateNumberInputsWithinRangeReturnFalseWhenEitherNumberIsTooSmallOrTooBig(){
        //Arrange
        String nominalValidPriceTestInput = "10";
        String validPriceTestInput = "0";
        String invalidPriceTestInput1 = "-10";
        String invalidPriceTestInput2 = "-2";
        String invalidPriceTestInput3 = "-1";

        String nominalValidQuantityTestInput = "10";
        String validQuantityTestInput = "0";
        String invalidQuantityTestInput1 = "-10";
        String invalidQuantityTestInput2 = "-2";
        String invalidQuantityTestInput3 = "-1";

        //Act
        boolean invalidPrice1Test = controllerEditOnePart.validateNumberInputsWithinRange(invalidPriceTestInput1,
                nominalValidQuantityTestInput);
        boolean invalidPrice2Test = controllerEditOnePart.validateNumberInputsWithinRange(invalidPriceTestInput2,
                nominalValidQuantityTestInput);
        boolean invalidPrice3Test = controllerEditOnePart.validateNumberInputsWithinRange(invalidPriceTestInput2,
                nominalValidQuantityTestInput);

        boolean nominalValidInputTest = controllerEditOnePart.validateNumberInputsWithinRange(nominalValidPriceTestInput,
                nominalValidQuantityTestInput);


        //Assert
        assertEquals(false, invalidPrice1Test);
        assertEquals(false, invalidPrice2Test);
        assertEquals(false, invalidPrice3Test);
        assertEquals(true, nominalValidInputTest);
    }


}
