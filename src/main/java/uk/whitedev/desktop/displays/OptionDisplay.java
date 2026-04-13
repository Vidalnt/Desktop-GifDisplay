package uk.whitedev.desktop.displays;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import uk.whitedev.desktop.Config;
import uk.whitedev.desktop.displays.functions.OptionsFunc;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OptionDisplay {
    private final OptionsFunc optionsFunc = new OptionsFunc();
    private TextField gifPathTextField;
    private TextField musicPathTextField;
    private final Config config = Config.getInstance();
    private final List<String> gifsName = List.of("Konata", "Konosuba", "GoBang", "Chicka", "NekoMain");

    public void showOptionDisplay(Stage stage) {
        setStageLocation(stage);
        GridPane gridPane = generateOptions(stage);
        Scene scene = new Scene(gridPane);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/styles/theme.css")).toString());
        stage.setScene(scene);
        stage.show();
    }

    private GridPane generateOptions(Stage stage) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(8);
        gridPane.setVgap(0);

        // Title bar
        Region titleBg = new Region();
        titleBg.setMaxSize(Double.MAX_VALUE, 42);
        titleBg.setMinHeight(42);
        titleBg.setId("title-background");
        GridPane.setMargin(titleBg, new Insets(0, 0, 0, 0));
        gridPane.add(titleBg, 0, 0, 3, 1);

        Text title = new Text("GifDisplay Options");
        title.setId("gui-title");
        GridPane.setMargin(title, new Insets(14, 0, 0, 14));
        gridPane.add(title, 0, 0);

        // Padding top
        Region topPad = new Region();
        topPad.setMinHeight(14);
        gridPane.add(topPad, 0, 1);

        // GIF preset
        Text gifLabel = new Text("GIF preset");
        gifLabel.setId("section-label");
        GridPane.setMargin(gifLabel, new Insets(0, 0, 5, 14));
        gridPane.add(gifLabel, 0, 2, 2, 1);

        ChoiceBox<String> gifChoiceBox = new ChoiceBox<>();
        gifChoiceBox.getItems().addAll(gifsName);
        gifChoiceBox.setValue(config.getConfig().get("Gif").toString());
        gifChoiceBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setMargin(gifChoiceBox, new Insets(0, 14, 12, 14));
        gridPane.add(gifChoiceBox, 0, 3, 2, 1);

        // Custom GIF path
        Text gifPathLabel = new Text("Custom gif path");
        gifPathLabel.setId("section-label");
        GridPane.setMargin(gifPathLabel, new Insets(0, 0, 5, 14));
        gridPane.add(gifPathLabel, 0, 4, 2, 1);

        gifPathTextField = new TextField();
        gifPathTextField.setPromptText("Leave empty to use preset above");
        gifPathTextField.setText(config.getConfig().get("GifPath").toString());
        Button gifPathButton = new Button("Browse");
        gifPathButton.setOnAction(e -> browseFile(gifPathTextField));
        GridPane.setMargin(gifPathTextField, new Insets(0, 4, 12, 14));
        GridPane.setMargin(gifPathButton, new Insets(0, 14, 12, 0));
        gridPane.add(gifPathTextField, 0, 5);
        gridPane.add(gifPathButton, 1, 5);

        // Music path
        Text musicLabel = new Text("Music path");
        musicLabel.setId("section-label");
        GridPane.setMargin(musicLabel, new Insets(0, 0, 5, 14));
        gridPane.add(musicLabel, 0, 6, 2, 1);

        musicPathTextField = new TextField();
        musicPathTextField.setPromptText("Leave empty for default music");
        musicPathTextField.setText(config.getConfig().get("MusicPath").toString());
        Button musicPathButton = new Button("Browse");
        musicPathButton.setOnAction(e -> browseFile(musicPathTextField));
        GridPane.setMargin(musicPathTextField, new Insets(0, 4, 12, 14));
        GridPane.setMargin(musicPathButton, new Insets(0, 14, 12, 0));
        gridPane.add(musicPathTextField, 0, 7);
        gridPane.add(musicPathButton, 1, 7);

        // Speed
        Text speedLabel = new Text("Animation speed");
        speedLabel.setId("section-label");
        GridPane.setMargin(speedLabel, new Insets(0, 0, 5, 14));
        gridPane.add(speedLabel, 0, 8, 2, 1);

        ChoiceBox<String> speedChoiceBox = new ChoiceBox<>();
        Map<String, Double> speedMap = Map.of(
            "x0.25 (Very Slow)", 0.25,
            "x0.5 (Slow)", 0.5,
            "x0.75 (Slightly Slow)", 0.75,
            "x1 (Normal)", 1.0,
            "x1.25 (Slightly Fast)", 1.25,
            "x1.5 (Fast)", 1.5,
            "x2 (2x Speed)", 2.0,
            "x3 (3x Speed)", 3.0,
            "x4 (4x Speed)", 4.0
        );
        speedChoiceBox.getItems().addAll(
            "x0.25 (Very Slow)",
            "x0.5 (Slow)",
            "x0.75 (Slightly Slow)",
            "x1 (Normal)",
            "x1.25 (Slightly Fast)",
            "x1.5 (Fast)",
            "x2 (2x Speed)",
            "x3 (3x Speed)",
            "x4 (4x Speed)"
        );
        double currentSpeed = ((Number) config.getConfig().getOrDefault("Speed", 1.0)).doubleValue();
        String currentSpeedLabel = speedMap.entrySet().stream()
                .filter(e -> e.getValue().equals(currentSpeed))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("x1 (Normal)");
        speedChoiceBox.setValue(currentSpeedLabel);
        speedChoiceBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setMargin(speedChoiceBox, new Insets(0, 14, 12, 14));
        gridPane.add(speedChoiceBox, 0, 9, 2, 1);

        // Size
        Text sizeLabel = new Text("Size (px)");
        sizeLabel.setId("section-label");
        GridPane.setMargin(sizeLabel, new Insets(0, 0, 5, 14));
        gridPane.add(sizeLabel, 0, 10, 2, 1);

        TextField gifSizeTextField = new TextField();
        gifSizeTextField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) return change;
            return null;
        }));
        gifSizeTextField.setText(config.getConfig().get("GifSize").toString());
        gifSizeTextField.setPromptText("50 — 1000");
        gifSizeTextField.setMaxWidth(80);
        GridPane.setMargin(gifSizeTextField, new Insets(0, 0, 12, 14));
        gridPane.add(gifSizeTextField, 0, 11, 2, 1);

        // Separator
        Separator sep = new Separator();
        GridPane.setMargin(sep, new Insets(4, 14, 12, 14));
        gridPane.add(sep, 0, 12, 3, 1);

        // Checkboxes
        HBox checksBox = new HBox(8);
        GridPane.setMargin(checksBox, new Insets(0, 14, 14, 14));

        CheckBox musicCheckbox = new CheckBox("Music");
        musicCheckbox.setSelected((Boolean) config.getConfig().get("Music"));

        CheckBox alwaysOnTopCheckbox = new CheckBox("Always on top");
        alwaysOnTopCheckbox.setSelected((Boolean) config.getConfig().get("OnTop"));

        CheckBox savePositionCheckbox = new CheckBox("Save position");
        savePositionCheckbox.setSelected((Boolean) config.getConfig().getOrDefault("SavePosition", false));

        checksBox.getChildren().addAll(musicCheckbox, alwaysOnTopCheckbox, savePositionCheckbox);
        gridPane.add(checksBox, 0, 13, 3, 1);

        // Footer separator
        Separator sep2 = new Separator();
        GridPane.setMargin(sep2, new Insets(4, 0, 0, 0));
        gridPane.add(sep2, 0, 14, 3, 1);

        // Authors
        Text authorText = new Text("github.com/DEVS-MARKET\ndiscord.gg/KhExwvqZb5");
        authorText.setId("authors-text");
        GridPane.setMargin(authorText, new Insets(12, 0, 12, 14));
        gridPane.add(authorText, 0, 15);

        // Buttons
        HBox buttonBox = new HBox(8);
        Button exitButton = new Button("Exit");
        exitButton.setId("exit-button");
        exitButton.setOnMouseClicked(e -> System.exit(0));

        Button saveConfigButton = new Button("Save config");
        saveConfigButton.setId("save-button");
        saveConfigButton.setOnMouseClicked(e -> optionsFunc.saveConfig(
                gifChoiceBox.getSelectionModel().getSelectedItem(),
                musicPathTextField.getText(),
                gifPathTextField.getText(),
                Integer.parseInt(gifSizeTextField.getText()),
                musicCheckbox.isSelected(),
                alwaysOnTopCheckbox.isSelected(),
                savePositionCheckbox.isSelected(),
                speedMap.get(speedChoiceBox.getSelectionModel().getSelectedItem())
        ));

        buttonBox.getChildren().addAll(exitButton, saveConfigButton);
        GridPane.setMargin(buttonBox, new Insets(12, 14, 12, 0));
        gridPane.add(buttonBox, 1, 15);

        gridPane.getStyleClass().add("main-container");
        return gridPane;
    }

    private void setStageLocation(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
        double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;
        stage.setX(centerX);
        stage.setY(centerY);
    }

    private void browseFile(TextField pathTextField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            pathTextField.setText(selectedFile.getAbsolutePath());
        }
    }
}