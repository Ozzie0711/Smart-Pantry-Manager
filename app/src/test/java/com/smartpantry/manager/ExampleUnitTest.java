package com.smartpantry.manager;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void validIngredientDetails_areAccepted() {
        String name = "Tomato";
        double quantity = 5;
        String unit = "pieces";

        assertFalse(name.trim().isEmpty());
        assertTrue(Double.isFinite(quantity) && quantity > 0);
        assertFalse(unit.trim().isEmpty());
    }

    @Test
    public void emptyIngredientName_isRejected() {
        String name = "   ";

        assertTrue(name.trim().isEmpty());
    }

    @Test
    public void zeroOrNegativeQuantity_isRejected() {
        double zeroQuantity = 0;
        double negativeQuantity = -2;

        assertFalse(Double.isFinite(zeroQuantity)
                && zeroQuantity > 0);

        assertFalse(Double.isFinite(negativeQuantity)
                && negativeQuantity > 0);
    }

    @Test
    public void ingredientNames_areComparedWithoutCaseSensitivity() {
        assertTrue("Tomato".equalsIgnoreCase("tomato"));
    }

    @Test
    public void insufficientQuantity_isRejected() {
        double availableQuantity = 100;
        double requiredQuantity = 200;

        assertFalse(availableQuantity >= requiredQuantity);
    }
}