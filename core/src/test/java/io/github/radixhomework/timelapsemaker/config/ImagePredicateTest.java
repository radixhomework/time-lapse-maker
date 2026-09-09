package io.github.radixhomework.timelapsemaker.config;

import io.github.radixhomework.timelapsemaker.exception.EnumValueNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImagePredicateTest {

    @TempDir
    Path tempDir;

    private final ImagePredicate imagePredicate = new ImagePredicate();

    @Test
    void acceptsJpgAndPngFiles() throws IOException {
        assertTrue(imagePredicate.test(Files.createTempFile(tempDir, "photo", ".jpg").toFile()));
        assertTrue(imagePredicate.test(Files.createTempFile(tempDir, "photo", ".png").toFile()));
    }

    @Test
    void rejectsNonImageFilesByThrowing() throws IOException {
        // Documented current behavior: the predicate throws for unknown file types instead of
        // returning false, so a single non-image file aborts the whole stream instead of being filtered out.
        File txt = Files.createTempFile(tempDir, "note", ".txt").toFile();
        assertThrows(EnumValueNotFoundException.class, () -> imagePredicate.test(txt));
    }
}
