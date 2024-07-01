package InventoryController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EditPartTextFieldValidationTest {
//
//    private EditOnePartController editOnePartController;
//
//    @Before
//    public void before(){
//        editOnePartController = new EditOnePartController();
////        Part testPart = new Part("Test Part Name", "Test Serial Number", "Test Manufacturer", 12.345,
////                "Test Vendor", "Over Here", "Scan me, please", true, 123456, false);
////        controllerEditOnePart.initPart(testPart);
//    }
//
//
//    /**
//     * Tests that entering something that does not contain just a number returns false.
//     */
//    @Test
//    public void validateNumberInputsContainNumbersShouldReturnFalseWhenItContainsNonNumericSymbols(){
//        //Arrange
//        String invalidInput = "31inv2alid4";
//        boolean correctValue = false;
//
//        //Act
//        boolean testValue = editOnePartController.validateNumberInputsContainNumber(invalidInput);
//
//        //Assert
//        assertEquals(correctValue, testValue);
//    }
//
//    /**
//     * Tests that entering something that does not contain just a number returns false.
//     */
//    @Test
//    public void validateAllFieldsFilledInShouldReturnFalseWhenFieldsAreEmpty(){
//        //Arrange
//        boolean correctValue = false;
//
//        //Act
//        boolean testPartNameEmpty = editOnePartController.validateAllFieldsFilledIn("", "test", "test");
//
//        boolean testSerialNumberEmpty = editOnePartController.validateAllFieldsFilledIn("test", "", "test");
//
//        boolean testManufacturerEmpty = editOnePartController.validateAllFieldsFilledIn("test", "", "");
//
//        boolean testPriceEmpty = editOnePartController.validateAllFieldsFilledIn("test", "test", "test");
//
//        boolean testLocationEmpty = editOnePartController.validateAllFieldsFilledIn("test", "test", "test");
//
//        boolean testBarcodeEmpty = editOnePartController.validateAllFieldsFilledIn("test", "test", "test");
//
//        boolean testQuantityEmpty = editOnePartController.validateAllFieldsFilledIn("test", "test", "test");
//
//        boolean testAllEmpty = editOnePartController.validateAllFieldsFilledIn("", "", "");
//
//        //Assert
//        assertEquals(correctValue, testPartNameEmpty);
//        assertEquals(correctValue, testSerialNumberEmpty);
//        assertEquals(correctValue, testManufacturerEmpty);
//        assertEquals(correctValue, testPriceEmpty);
//        assertEquals(correctValue, testLocationEmpty);
//        assertEquals(correctValue, testBarcodeEmpty);
//        assertEquals(correctValue, testQuantityEmpty);
//        assertEquals(correctValue, testAllEmpty);
//    }
//
//    /**
//     * Tests that entering something that does not contain just a number returns false.
//     */
//    @Test
//    public void validateNumberInputsWithinRangeReturnFalseWhenEitherNumberIsTooSmallOrTooBig(){
//        //Arrange
//        String nominalValidPriceTestInput = "10";
//        String validPriceTestInput = "0";
//        String invalidPriceTestInput1 = "-10";
//        String invalidPriceTestInput2 = "-2";
//        String invalidPriceTestInput3 = "-1";
//
//        String nominalValidQuantityTestInput = "10";
//        String validQuantityTestInput = "0";
//        String invalidQuantityTestInput1 = "-10";
//        String invalidQuantityTestInput2 = "-2";
//        String invalidQuantityTestInput3 = "-1";
//
//        //Act
//        boolean invalidPrice1Test = editOnePartController.validateNumberInputsWithinRange(invalidPriceTestInput1);
//        boolean invalidPrice2Test = editOnePartController.validateNumberInputsWithinRange(invalidPriceTestInput2);
//        boolean invalidPrice3Test = editOnePartController.validateNumberInputsWithinRange(invalidPriceTestInput2);
//
//        boolean nominalValidInputTest = editOnePartController.validateNumberInputsWithinRange(nominalValidPriceTestInput);
//
//
//        //Assert
//        assertFalse(invalidPrice1Test);
//        assertFalse(invalidPrice2Test);
//        assertFalse(invalidPrice3Test);
//        assertTrue(nominalValidInputTest);
//    }


}
