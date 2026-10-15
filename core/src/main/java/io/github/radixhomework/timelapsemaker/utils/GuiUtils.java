package io.github.radixhomework.timelapsemaker.utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GuiUtils {

    public static void updateTextInput(TextInputControl input, String value) {
        log.debug("Updating text input to {}", value);
        Platform.runLater(() -> input.setText(value));
    }

    public static void changeComponentsState(List<Node> components, boolean enable) {
        String action = enable ? "Enabling" : "Disabling";
        components.forEach(component -> Platform.runLater(() -> {
            log.debug("{} component", action);
            component.setDisable(!enable);
        }));
    }
}
