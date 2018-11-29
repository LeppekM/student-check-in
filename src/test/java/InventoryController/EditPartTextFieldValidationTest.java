package InventoryController;

import Database.Part;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EditPartTextFieldValidationTest {

    private ControllerEditPart controllerEditPart;

    @Before
    public void before(){
        controllerEditPart = new ControllerEditPart();
//        Part testPart = new Part("Test Part Name", "Test Serial Number", "Test Manufacturer", 12.345,
//                "Test Vendor", "Over Here", "Scan me, please", true, 123456, false);
//        controllerEditPart.initPart(testPart);
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
        boolean testValue = controllerEditPart.validateNumberInputsContainNumber(invalidInput);

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
        boolean testPartNameEmpty = controllerEditPart.validateAllFieldsFilledIn("", "test", "test",
                "test", "test", "test", "test");

        boolean testSerialNumberEmpty = controllerEditPart.validateAllFieldsFilledIn("test", "", "test",
                "test", "test", "test", "test");

        boolean testManufacturerEmpty = controllerEditPart.validateAllFieldsFilledIn("test", "", "",
                "test", "test", "test", "test");

        boolean testPriceEmpty = controllerEditPart.validateAllFieldsFilledIn("test", "test", "test",
                "", "test", "test", "test");

        boolean testLocationEmpty = controllerEditPart.validateAllFieldsFilledIn("test", "test", "test",
                "test", "", "test", "test");

        boolean testBarcodeEmpty = controllerEditPart.validateAllFieldsFilledIn("test", "test", "test",
                "test", "test", "", "test");

        boolean testQuantityEmpty = controllerEditPart.validateAllFieldsFilledIn("test", "test", "test",
                "test", "test", "test", "");

        boolean testAllEmpty = controllerEditPart.validateAllFieldsFilledIn("", "", "", "", "", "",
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
        boolean invalidPrice1Test = controllerEditPart.validateNumberInputsWithinRange(invalidPriceTestInput1,
                nominalValidQuantityTestInput);
        boolean invalidPrice2Test = controllerEditPart.validateNumberInputsWithinRange(invalidPriceTestInput2,
                nominalValidQuantityTestInput);
        boolean invalidPrice3Test = controllerEditPart.validateNumberInputsWithinRange(invalidPriceTestInput2,
                nominalValidQuantityTestInput);

        boolean nominalValidInputTest = controllerEditPart.validateNumberInputsWithinRange(nominalValidPriceTestInput,
                nominalValidQuantityTestInput);


        //Assert
        assertEquals(false, invalidPrice1Test);
        assertEquals(false, invalidPrice2Test);
        assertEquals(false, invalidPrice3Test);
        assertEquals(true, nominalValidInputTest);
    }


}
