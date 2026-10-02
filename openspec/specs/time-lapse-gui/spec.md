## Purpose

Defines the observable behavior of the Time Lapse Maker desktop window: how the user selects photos and output, configures the time lapse, and what feedback the application gives while assembling the video. The behavior is toolkit-independent; the first implementation is JavaFX with AtlantaFX theming, replacing the retired Apache Pivot UI.

## Requirements

### Requirement: Source directory selection

The application SHALL let the user pick a source photos directory through a directory chooser opened from the "Choose" button, display the chosen path in a read-only field, and list the directory's files in the photo list area.

#### Scenario: User picks a photos directory
- **WHEN** the user presses "Choose" next to the source directory field and selects a directory in the dialog
- **THEN** the field shows the directory's absolute path and the photo list is refreshed with that directory's files

#### Scenario: User cancels the directory dialog
- **WHEN** the user closes the directory chooser without selecting a directory
- **THEN** the previously displayed path and photo list remain unchanged

### Requirement: Output file selection

The application SHALL let the user pick an output video file through a save dialog opened from the "Choose" button, and display the chosen path in an editable field.

#### Scenario: User picks an output file
- **WHEN** the user presses "Choose" next to the output file field and selects a save location
- **THEN** the field shows the chosen file's absolute path

### Requirement: Output format and extension handling

The application SHALL offer the output formats MPEG4 (default) and AVI, and SHALL keep the output file name consistent with the selected format's extension (`.mp4` / `.avi`).

#### Scenario: Extension appended on output selection
- **WHEN** the user selects an output file whose name does not end with the currently selected format's extension
- **THEN** the displayed output path has the selected format's extension appended

#### Scenario: Extension switched when format changes
- **WHEN** the user changes the output format after an output file has been set
- **THEN** the displayed output path's extension is replaced with the newly selected format's extension

### Requirement: Frame rate selection

The application SHALL offer the frame rates 12, 24, 30, 48, 60 and 96 images per second, defaulting to 24, with an "images / second" hint next to the selector.

#### Scenario: Default and changed frame rate
- **WHEN** the window opens
- **THEN** the frame rate selector shows 24, and selecting another offered value is used for the next assembly

### Requirement: Assembly trigger and input validation

The application SHALL start assembling the time lapse when the user presses "Make Time Lapse" with a source directory and an output file set, using the selected output format and frame rate, and SHALL reject the action with an error dialog when either field is empty.

#### Scenario: Missing source directory
- **WHEN** the user presses "Make Time Lapse" with an empty source directory field
- **THEN** an error dialog is shown and no assembly starts

#### Scenario: Missing output file
- **WHEN** the user presses "Make Time Lapse" with an empty output file field
- **THEN** an error dialog is shown and no assembly starts

### Requirement: Progress feedback and input locking

While an assembly is running, the application SHALL disable the input controls (fields, choosers, selectors, photo list and action buttons), advance the progress bar proportionally to the photos processed, and indicate the phase and outcome through the status label: running while encoding, "Done" on success, "Error" on failure. On completion or failure the input controls SHALL be re-enabled.

#### Scenario: Successful assembly
- **WHEN** the assembly of all photos finishes
- **THEN** the progress bar reads full, the status label shows "Done", and the input controls are enabled again

#### Scenario: Assembly failure
- **WHEN** the assembly aborts (for example an unreadable photo)
- **THEN** the status label shows "Error", the input controls are enabled again, and the application remains usable

### Requirement: Application exit

The application SHALL terminate when the user presses "Exit".

#### Scenario: User exits the application
- **WHEN** the user presses "Exit"
- **THEN** the application process terminates

### Requirement: Window presentation

The application SHALL present a single window titled "Time Lapse Maker" whose controls are laid out as: source directory and output file rows with "Choose" buttons, output format and frame rate selectors, a photo list, and a bottom row with status label, progress bar, "Make Time Lapse" and "Exit" buttons. The UI SHALL be rendered with the AtlantaFX theme applied to the scene.

#### Scenario: Window opens themed
- **WHEN** the application starts
- **THEN** a window titled "Time Lapse Maker" is shown with the layout above and its controls styled by the AtlantaFX theme
