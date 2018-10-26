package InventoryController;

import org.junit.Test;

import static org.junit.Assert.*;

public class ShapeTest {

    @Test
    public void getWidth() {
        Shape shape = new Shape(2,2);
        assertEquals(shape.getWidth(), 2);
    }

    @Test
    public void getHeight() {
        Shape shape = new Shape(2,2);
        assertEquals(shape.getHeight(), 2);
    }

    @Test
    public void failedTest(){
        Shape shape = new Shape(2,2);
        assertEquals(shape.getHeight(), 2);
    }
}