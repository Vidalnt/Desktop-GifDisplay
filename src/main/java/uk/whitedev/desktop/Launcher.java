package uk.whitedev.desktop;

import javafx.application.Application;
import uk.whitedev.desktop.displays.GifDisplay;

public class Launcher {
    public static void main(String[] args) {
        Config.getInstance().loadConfig(null);
        Application.launch(GifDisplay.class, args);
    }
}