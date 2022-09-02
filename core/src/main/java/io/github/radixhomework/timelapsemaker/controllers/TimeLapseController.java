package io.github.radixhomework.timelapsemaker.controllers;

import io.github.radixhomework.timelapsemaker.enums.EnumFrameRate;
import io.github.radixhomework.timelapsemaker.enums.EnumOutputFormat;
import io.github.radixhomework.timelapsemaker.events.ChooseSourceEvent;
import io.github.radixhomework.timelapsemaker.events.ChooseTargetEvent;
import io.github.radixhomework.timelapsemaker.events.SelectOutputFormatEvent;
import io.github.radixhomework.timelapsemaker.services.TimeLapseTask;
import io.github.radixhomework.timelapsemaker.services.TimeLapseTaskListener;
import io.github.radixhomework.timelapsemaker.utils.GuiUtils;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TimeLapseController extends Window implements Bindable {

    private static final List<Component> buttons = new ArrayList<>();
    @BXML
    private TextInput sourceDirectory;
    @BXML
    private PushButton chooseSource;
    @BXML
    private TextInput outputFile;
    @BXML
    private PushButton chooseTarget;
    @BXML
    private ListButton outputFormats;
    @BXML
    private ListButton frameRates;
    @BXML
    private TableView tableView;
    @BXML
    private Label status;
    @BXML
    private Meter progressBar;
    @BXML
    private PushButton make;
    @BXML
    private PushButton exit;

    @Override
    public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
        // Filling components list to disable during time lapse assembling
        buttons.add(sourceDirectory);
        buttons.add(chooseSource);
        buttons.add(outputFile);
        buttons.add(chooseTarget);
        buttons.add(outputFormats);
        buttons.add(frameRates);
        buttons.add(tableView);
        buttons.add(make);
        buttons.add(exit);

        // Filling output formats ant frame rates drop-downs
        outputFormats.setListData(EnumOutputFormat.getValues());
        outputFormats.setSelectedItem(EnumOutputFormat.DEFAULT_VALUE);

        frameRates.setListData(EnumFrameRate.getValues());
        frameRates.setSelectedItem(EnumFrameRate.DEFAULT_VALUE);

        // Adding events on buttons and drop-downs
        chooseSource.getButtonPressListeners()
                .add(new ChooseSourceEvent(TimeLapseController.this, sourceDirectory, tableView));

        ChooseTargetEvent chooseTargetEvent = new ChooseTargetEvent(TimeLapseController.this, outputFile,
                (String) outputFormats.getSelectedItem());
        chooseTarget.getButtonPressListeners().add(chooseTargetEvent);

        outputFormats.getListButtonSelectionListeners().add(new SelectOutputFormatEvent(TimeLapseController.this,
                outputFile, chooseTargetEvent));

        exit.getButtonPressListeners().add(button -> System.exit(0));

        make.getButtonPressListeners().add(button -> {
            if (sourceDirectory.getText().isEmpty()) {
                Alert.alert(MessageType.ERROR, "Source directory cannot be empty", TimeLapseController.this);
            } else if (outputFile.getText().isEmpty()) {
                Alert.alert(MessageType.ERROR, "Destination file cannot be empty", TimeLapseController.this);
            } else {
                GuiUtils.changeComponentsState(buttons, false);
                TimeLapseTask task = new TimeLapseTask(sourceDirectory.getText(), outputFile.getText(), progressBar,
                        EnumFrameRate.getByLabel((String) frameRates.getSelectedItem()), status);
                task.execute(new TimeLapseTaskListener(progressBar, status, buttons));
            }
        });
    }
}
