package io.github.radixhomework.timelapsemaker;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class TimeLapseApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        Parent root = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        Scene scene = new Scene(root, 600, 230);

        stage.setTitle("Time Lapse Maker");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/radixhome.png")));
        stage.setScene(scene);
        stage.show();
    }
}
