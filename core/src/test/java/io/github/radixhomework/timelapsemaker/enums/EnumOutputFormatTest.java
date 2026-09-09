package io.github.radixhomework.timelapsemaker.enums;

import io.github.radixhomework.timelapsemaker.exception.EnumValueNotFoundException;
import org.apache.pivot.collections.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumOutputFormatTest {

    @Test
    void findByLabelReturnsMatchingFormat() {
        assertSame(EnumOutputFormat.MP4, EnumOutputFormat.findByLabel("MPEG4"));
        assertSame(EnumOutputFormat.AVI, EnumOutputFormat.findByLabel("AVI"));
    }

    @Test
    void findByLabelThrowsForUnknownLabel() {
        EnumValueNotFoundException exception = assertThrows(EnumValueNotFoundException.class,
                () -> EnumOutputFormat.findByLabel("MP4"));
        assertEquals("Image type with value MP4 not found", exception.getMessage());
    }

    @Test
    void matchesOnlyExactLabel() {
        assertTrue(EnumOutputFormat.MP4.matches("MPEG4"));
        assertFalse(EnumOutputFormat.MP4.matches("MPEG"));
        assertFalse(EnumOutputFormat.AVI.matches("MPEG4"));
    }

    @Test
    void getValuesReturnsAllLabelsInDeclarationOrder() {
        List<String> values = EnumOutputFormat.getValues();
        assertEquals(2, values.getLength());
        assertEquals(EnumOutputFormat.MP4.getLabel(), values.get(0));
        assertEquals(EnumOutputFormat.AVI.getLabel(), values.get(1));
    }

    @Test
    void exposesDefaultAndExtensions() {
        assertEquals("MPEG4", EnumOutputFormat.DEFAULT_VALUE);
        assertEquals(".mp4", EnumOutputFormat.MP4.getExtension());
        assertEquals(".avi", EnumOutputFormat.AVI.getExtension());
    }
}
