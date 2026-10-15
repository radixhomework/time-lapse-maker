package io.github.radixhomework.timelapsemaker;

import javafx.application.Application;

/**
 * Launcher-pattern entry point: a plain main class is required so JavaFX can start
 * from an ordinary classpath (fat jar) without missing runtime components errors.
 */
public class TimeLapseMakerApplication {

    public static void main(String[] args) {
        Application.launch(TimeLapseApplication.class, args);
    }
}
