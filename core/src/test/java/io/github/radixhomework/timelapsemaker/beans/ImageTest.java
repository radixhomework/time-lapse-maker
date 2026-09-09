package io.github.radixhomework.timelapsemaker.beans;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageTest {

    @Test
    void exposesPathAndMatchingFile() {
        Image image = new Image("C:/photos/IMG_0001.jpg");
        assertEquals("C:/photos/IMG_0001.jpg", image.getPath());
        assertEquals(new File("C:/photos/IMG_0001.jpg"), image.getFile());
    }
}
