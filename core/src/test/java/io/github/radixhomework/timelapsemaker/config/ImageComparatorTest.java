package io.github.radixhomework.timelapsemaker.config;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageComparatorTest {

    private final ImageComparator imageComparator = new ImageComparator();

    @Test
    void sortsFilesByName() {
        List<String> sortedNames = Stream.of(new File("IMG_0002.png"), new File("IMG_0003.png"), new File("IMG_0001.png"))
                .sorted(imageComparator.byFileName())
                .map(File::getName)
                .collect(Collectors.toList());
        assertEquals(List.of("IMG_0001.png", "IMG_0002.png", "IMG_0003.png"), sortedNames);
    }

    @Test
    void usesNaturalCaseSensitiveStringOrder() {
        // Documents that the ordering is the natural String order, where uppercase letters sort before lowercase
        List<String> sortedNames = Stream.of(new File("a.png"), new File("B.png"))
                .sorted(imageComparator.byFileName())
                .map(File::getName)
                .collect(Collectors.toList());
        assertEquals(List.of("B.png", "a.png"), sortedNames);
    }
}
