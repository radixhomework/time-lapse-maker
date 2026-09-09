package io.github.radixhomework.timelapsemaker.enums;

import org.apache.pivot.collections.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class EnumFrameRateTest {

    @Test
    void getByLabelReturnsMatchingRate() {
        assertSame(EnumFrameRate.FR_12, EnumFrameRate.getByLabel("12"));
        assertSame(EnumFrameRate.FR_24, EnumFrameRate.getByLabel("24"));
        assertSame(EnumFrameRate.FR_96, EnumFrameRate.getByLabel("96"));
    }

    @Test
    void getByLabelReturnsNullForUnknownLabel() {
        assertNull(EnumFrameRate.getByLabel("25"));
        assertNull(EnumFrameRate.getByLabel(null));
    }

    @Test
    void getValuesReturnsAllRatesInDeclarationOrder() {
        EnumFrameRate[] rates = EnumFrameRate.values();
        List<String> values = EnumFrameRate.getValues();
        assertEquals(rates.length, values.getLength());
        for (int i = 0; i < rates.length; i++) {
            assertEquals(rates[i].getLabel(), values.get(i));
        }
    }

    @Test
    void defaultFrameRateIs24() {
        assertEquals("24", EnumFrameRate.DEFAULT_VALUE);
    }

    @Test
    void denominatorMatchesLabelledRate() {
        for (EnumFrameRate rate : EnumFrameRate.values()) {
            assertEquals(1, rate.getNumerator());
            assertEquals(Integer.parseInt(rate.getLabel()), rate.getDenominator());
        }
    }
}
