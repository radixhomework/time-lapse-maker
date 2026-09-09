package io.github.radixhomework.timelapsemaker.enums;

import io.github.radixhomework.timelapsemaker.exception.EnumValueNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumImageTypeTest {

    @TempDir
    Path tempDir;

    @Test
    void findByTypeStringReturnsMatchingType() {
        assertSame(EnumImageType.JPG, EnumImageType.findByType("image/jpeg"));
        assertSame(EnumImageType.PNG, EnumImageType.findByType("image/png"));
    }

    @Test
    void findByTypeStringThrowsForUnknownType() {
        EnumValueNotFoundException exception = assertThrows(EnumValueNotFoundException.class,
                () -> EnumImageType.findByType("image/gif"));
        assertEquals("Image type with value image/gif not found", exception.getMessage());
    }

    @Test
    void findByTypePathReturnsMatchingType() throws IOException {
        Path jpg = Files.createTempFile(tempDir, "photo", ".jpg");
        Path png = Files.createTempFile(tempDir, "photo", ".png");
        assertSame(EnumImageType.JPG, EnumImageType.findByType(jpg));
        assertSame(EnumImageType.PNG, EnumImageType.findByType(png));
    }

    @Test
    void findByTypePathThrowsForNonImage() throws IOException {
        Path txt = Files.createTempFile(tempDir, "note", ".txt");
        assertThrows(EnumValueNotFoundException.class, () -> EnumImageType.findByType(txt));
    }

    @Test
    void matchesStringOnlyExactType() {
        assertTrue(EnumImageType.JPG.matches("image/jpeg"));
        assertFalse(EnumImageType.JPG.matches("image/jpg"));
        assertFalse(EnumImageType.JPG.matches("image/png"));
    }

    @Test
    void matchesPathDetectsImagesByProbeContentType() throws IOException {
        assertTrue(EnumImageType.JPG.matches(Files.createTempFile(tempDir, "photo", ".jpg")));
        assertTrue(EnumImageType.PNG.matches(Files.createTempFile(tempDir, "photo", ".png")));
        assertFalse(EnumImageType.JPG.matches(Files.createTempFile(tempDir, "note", ".txt")));
    }

    @Test
    void isAllowedAcceptsKnownImageTypes() {
        assertTrue(EnumImageType.isAllowed("image/jpeg"));
        assertTrue(EnumImageType.isAllowed("image/png"));
    }

    @Test
    void isAllowedRejectsUnknownTypesByThrowing() {
        // Current behavior: rejection is reported by throwing instead of returning false
        assertThrows(EnumValueNotFoundException.class, () -> EnumImageType.isAllowed("image/bmp"));
    }

    @Test
    void isAllowedPathAcceptsImagesAndThrowsForOthers() throws IOException {
        Path jpg = Files.createTempFile(tempDir, "photo", ".jpg");
        Path txt = Files.createTempFile(tempDir, "note", ".txt");
        assertTrue(EnumImageType.isAllowed(jpg));
        assertThrows(EnumValueNotFoundException.class, () -> EnumImageType.isAllowed(txt));
    }
}
