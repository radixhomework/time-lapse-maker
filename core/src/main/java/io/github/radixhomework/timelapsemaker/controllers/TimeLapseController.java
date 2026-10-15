package io.github.radixhomework.timelapsemaker.controllers;

import io.github.radixhomework.timelapsemaker.enums.EnumFrameRate;
import io.github.radixhomework.timelapsemaker.enums.EnumOutputFormat;
import io.github.radixhomework.timelapsemaker.services.TimeLapseTask;
import io.github.radixhomework.timelapsemaker.utils.GuiUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TimeLapseController {

    @FXML
    private TextField sourceDirectory;
    @FXML
    private Button chooseSource;
    @FXML
    private TextField outputFile;
    @FXML
    private Button chooseTarget;
    @FXML
    private ChoiceBox<String> outputFormats;
    @FXML
    private ChoiceBox<String> frameRates;
    @FXML
    private ListView<String> photoList;
    @FXML
    private Label status;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button make;
    @FXML
    private Button exit;

    private List<Node> inputs;

    @FXML
    private void initialize() {
        // Built here rather than in the field initializer: @FXML fields are injected after construction
        inputs = List.of(sourceDirectory, chooseSource, outputFile, chooseTarget,
                outputFormats, frameRates, photoList, make, exit);

        outputFormats.setItems(FXCollections.observableArrayList(EnumOutputFormat.getValues()));
        outputFormats.setValue(EnumOutputFormat.DEFAULT_VALUE);

        frameRates.setItems(FXCollections.observableArrayList(EnumFrameRate.getValues()));
        frameRates.setValue(EnumFrameRate.DEFAULT_VALUE);

        chooseSource.setOnAction(event -> onChooseSource());
        chooseTarget.setOnAction(event -> onChooseTarget());

        outputFormats.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onOutputFormatChanged(oldValue, newValue));

        make.setOnAction(event -> onMake());
        exit.setOnAction(event -> Platform.exit());
    }

    private void onChooseSource() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose source directory");
        File currentDirectory = new File(sourceDirectory.getText());
        if (currentDirectory.isDirectory()) {
            directoryChooser.setInitialDirectory(currentDirectory);
        }

        File selected = directoryChooser.showDialog(currentWindow());
        if (selected == null) {
            return;
        }

        log.info("Loading images from directory {}", selected.getAbsolutePath());
        sourceDirectory.setText(selected.getAbsolutePath());
        File[] files = selected.listFiles();
        List<String> paths = files == null ? List.of() : Arrays.stream(files)
                .sorted()
                .map(File::getAbsolutePath)
                .toList();
        photoList.setItems(FXCollections.observableArrayList(paths));
    }

    private void onChooseTarget() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose output file");
        EnumOutputFormat outputFormat = EnumOutputFormat.findByLabel(outputFormats.getValue());

        File currentFile = new File(outputFile.getText());
        File parentDirectory = currentFile.getParentFile();
        if (parentDirectory != null && parentDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(parentDirectory);
        }
        if (!currentFile.getName().isEmpty()) {
            fileChooser.setInitialFileName(currentFile.getName());
        }

        File selected = fileChooser.showSaveDialog(currentWindow());
        if (selected == null) {
            return;
        }

        // Append the extension when missing from the chosen file name
        if (!selected.getName().endsWith(outputFormat.getExtension())) {
            selected = new File(selected.getAbsolutePath().concat(outputFormat.getExtension()));
        }

        log.info("Setting output file to {}", selected.getAbsolutePath());
        GuiUtils.updateTextInput(outputFile, selected.getAbsolutePath());
    }

    private void onOutputFormatChanged(String oldValue, String newValue) {
        if (outputFile.getText().isEmpty() || oldValue == null || newValue == null) {
            return;
        }
        EnumOutputFormat oldFormat = EnumOutputFormat.findByLabel(oldValue);
        EnumOutputFormat newFormat = EnumOutputFormat.findByLabel(newValue);
        GuiUtils.updateTextInput(outputFile, outputFile.getText().replace(
                oldFormat.getExtension(), newFormat.getExtension()));
    }

    private void onMake() {
        if (sourceDirectory.getText().isEmpty()) {
            alert("Source directory cannot be empty");
        } else if (outputFile.getText().isEmpty()) {
            alert("Destination file cannot be empty");
        } else {
            GuiUtils.changeComponentsState(inputs, false);

            TimeLapseTask task = new TimeLapseTask(sourceDirectory.getText(), outputFile.getText(),
                    EnumFrameRate.getByLabel(frameRates.getValue()));
            progressBar.progressProperty().bind(task.progressProperty());
            status.textProperty().bind(task.messageProperty());
            task.setOnSucceeded(event -> {
                unbindTask();
                status.setText("Done");
                GuiUtils.changeComponentsState(inputs, true);
            });
            task.setOnFailed(event -> {
                unbindTask();
                status.setText("Error");
                GuiUtils.changeComponentsState(inputs, true);
                log.error("Time lapse building failed", task.getException());
            });

            Thread thread = new Thread(task, "time-lapse-task");
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void unbindTask() {
        progressBar.progressProperty().unbind();
        status.textProperty().unbind();
        progressBar.setProgress(0);
    }

    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private Window currentWindow() {
        return chooseSource.getScene() != null ? chooseSource.getScene().getWindow() : null;
    }
}
