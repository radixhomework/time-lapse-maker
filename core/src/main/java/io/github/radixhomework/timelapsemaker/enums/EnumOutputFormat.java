package io.github.radixhomework.timelapsemaker.enums;

import io.github.radixhomework.timelapsemaker.exception.EnumValueNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum EnumOutputFormat {
    MP4("MPEG4", ".mp4"),
    AVI("AVI", ".avi");

    public static final String ERROR_PREFIX = "Image type with value ";
    public static final String ERROR_SUFFIX = " not found";

    @Getter(value = AccessLevel.NONE)
    public static final String DEFAULT_VALUE = MP4.getLabel();

    private final String label;
    private final String extension;

    public static EnumOutputFormat findByLabel(String label) {
        for (EnumOutputFormat value : EnumOutputFormat.values()) {
            if (value.matches(label)) {
                return value;
            }
        }
        throw new EnumValueNotFoundException(ERROR_PREFIX + label + ERROR_SUFFIX);
    }

    public static List<String> getValues() {
        return Arrays.stream(EnumOutputFormat.values()).map(EnumOutputFormat::getLabel).toList();
    }

    public boolean matches(String label) {
        return label.equals(this.getLabel());
    }
}
