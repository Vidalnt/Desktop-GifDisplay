package uk.whitedev.desktop.displays.functions;

import javafx.stage.Stage;
import uk.whitedev.desktop.Config;
import uk.whitedev.desktop.displays.GifDisplay;

public class OptionsFunc {
    private final Config config = Config.getInstance();

    public void saveConfig(Stage optionsStage, String gif, String musicPath, String gifPath,
                           int gifSize, boolean music, boolean onTop, boolean savePosition, double speed) {
        config.updateConfig("Gif", gif);
        config.updateConfig("MusicPath", musicPath);
        config.updateConfig("GifPath", gifPath);
        config.updateConfig("GifSize", (gifSize >= 50 && gifSize <= 1000) ? gifSize : 400);
        config.updateConfig("Music", music);
        config.updateConfig("OnTop", onTop);
        config.updateConfig("SavePosition", savePosition);
        config.updateConfig("Speed", speed);
        config.saveConfig();

        if (optionsStage != null) optionsStage.close();

        // Hot-reload GIF display without restarting the application
        GifDisplay.reload();
    }
}