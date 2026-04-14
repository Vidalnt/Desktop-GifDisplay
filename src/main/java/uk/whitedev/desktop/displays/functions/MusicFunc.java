package uk.whitedev.desktop.displays.functions;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class MusicFunc {
    private MediaPlayer mediaPlayer;

    public void playMusic(String musicPath, String defaultMusic) {
        stopMusic();
        
        String path;
        if (musicPath == null || musicPath.isEmpty()) {
            String resourcePath;
            if (defaultMusic.equals("GoBang")) {
                resourcePath = "/assets/music/GoBang Music.wav";
            } else if (defaultMusic.equals("ChibiSakuya")) {
                resourcePath = "/assets/music/danceofnights.wav";
            } else {
                resourcePath = "/assets/music/Default Song.wav";
            }
            path = getClass().getResource(resourcePath).toString();
        } else {
            path = new File(musicPath).toURI().toString();
        }

        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
        mediaPlayer.play();
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}