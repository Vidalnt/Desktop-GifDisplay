package uk.whitedev.desktop.displays;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import uk.whitedev.desktop.Config;
import uk.whitedev.desktop.displays.functions.MusicFunc;
import uk.whitedev.desktop.displays.functions.TrayIconFunc;
import uk.whitedev.desktop.loader.LoadStandardGIf;

import java.io.File;
import java.util.List;

public class GifDisplay extends Application {
    private final TrayIconFunc trayIconFunc = new TrayIconFunc();
    private static final MusicFunc musicFunc = new MusicFunc();
    private static final LoadStandardGIf gifLoader = new LoadStandardGIf();
    private static final Config config = Config.getInstance();
    public static boolean ISLOCKED = false;
    private static double xOffset = 0;
    private static double yOffset = 0;

    private static Stage activeMainStage = null;
    private static Timeline activeTimeline = null;
    private static Stage primaryStageRef = null;
    
    private static int currentFrameIndex = 0;
    private static List<File> currentFrames = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStageRef = primaryStage;
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setOpacity(0);
        primaryStage.setHeight(0);
        primaryStage.setWidth(0);
        primaryStage.show();
        
        showGifDisplay();
    }

    private void showGifDisplay() {
        Stage mainStage = new Stage();
        mainStage.initStyle(StageStyle.TRANSPARENT);
        activeMainStage = mainStage;
        renderGifDisplay(mainStage);
        trayIconFunc.addIconToSysTray(mainStage);
    }

    private void renderGifDisplay(Stage mainStage) {
        String standardGif = (String) config.getConfig().get("Gif");
        String gifPath = (String) config.getConfig().get("GifPath");
        int gifSize = (int) config.getConfig().get("GifSize");
        String musicPath = (String) config.getConfig().get("MusicPath");
        boolean isMusic = (boolean) config.getConfig().get("Music");
        boolean isAlwaysOnTop = (boolean) config.getConfig().get("OnTop");

        Scene scene;
        if (gifPath == null || gifPath.isEmpty()) {
            scene = createScene(null, standardGif, gifSize);
        } else {
            Image gifImage = new Image(new File(gifPath).toURI().toString());
            scene = createScene(gifImage, null, gifSize);
        }

        mainStage.setAlwaysOnTop(isAlwaysOnTop);
        mainStage.setScene(scene);

        double posX = ((Number) config.getConfig().getOrDefault("PosX", -1)).doubleValue();
        double posY = ((Number) config.getConfig().getOrDefault("PosY", -1)).doubleValue();
        if (posX >= 0 && posY >= 0) {
            mainStage.setX(posX);
            mainStage.setY(posY);
        }

        mainStage.show();

        if (isMusic) musicFunc.playMusic(musicPath, standardGif);
    }

    private Scene createScene(Image gifImage, String gifName, int gifSize) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(gifSize);
        imageView.setPreserveRatio(true);

        if (gifImage == null) {
            currentFrames = gifLoader.getFrames(gifName);
            currentFrameIndex = 0;
            
            double speed = ((Number) config.getConfig().getOrDefault("Speed", 1.0)).doubleValue();
            double frameDelay = (1000.0 / 30.0) / speed;

            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            
            final List<File> frames = currentFrames;
            final ImageView iv = imageView;
            
            KeyFrame keyFrame = new KeyFrame(Duration.millis(frameDelay), e -> updateFrame(frames, iv));
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
            
            if (activeTimeline != null) activeTimeline.stop();
            activeTimeline = timeline;
            
            if (!currentFrames.isEmpty()) {
                imageView.setImage(new Image(currentFrames.get(0).toURI().toString()));
            }
        } else {
            imageView.setImage(gifImage);
            currentFrames = null;
            if (activeTimeline != null) {
                activeTimeline.stop();
                activeTimeline = null;
            }
        }

        HBox hbox = new HBox(imageView);
        hbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0);");

        StackPane root = new StackPane(hbox);
        root.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(root, Color.TRANSPARENT);

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            if (!ISLOCKED) {
                activeMainStage.setX(event.getScreenX() - xOffset);
                activeMainStage.setY(event.getScreenY() - yOffset);
            }
        });

        return scene;
    }

    /**
     * Hot-reloads the GIF display with updated configuration.
     */
    public static void reload() {
        Platform.runLater(() -> {
            if (activeMainStage == null) return;

            musicFunc.stopMusic();
            if (activeTimeline != null) {
                activeTimeline.stop();
                activeTimeline = null;
            }

            config.loadConfig(null);

            String standardGif = (String) config.getConfig().get("Gif");
            String gifPath = (String) config.getConfig().get("GifPath");
            int gifSize = (int) config.getConfig().get("GifSize");
            String musicPath = (String) config.getConfig().get("MusicPath");
            boolean isMusic = (boolean) config.getConfig().get("Music");
            boolean isAlwaysOnTop = (boolean) config.getConfig().get("OnTop");

            Scene newScene;
            if (gifPath == null || gifPath.isEmpty()) {
                newScene = createSceneStatic(null, standardGif, gifSize);
            } else {
                Image gifImage = new Image(new File(gifPath).toURI().toString());
                newScene = createSceneStatic(gifImage, null, gifSize);
            }

            activeMainStage.setScene(newScene);
            activeMainStage.setAlwaysOnTop(isAlwaysOnTop);
            activeMainStage.sizeToScene();
            activeMainStage.show();
            activeMainStage.toFront();

            if (isMusic) musicFunc.playMusic(musicPath, standardGif);
        });
    }
    
    private static Scene createSceneStatic(Image gifImage, String gifName, int gifSize) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(gifSize);
        imageView.setPreserveRatio(true);

        if (gifImage == null) {
            currentFrames = gifLoader.getFrames(gifName);
            currentFrameIndex = 0;
            
            double speed = ((Number) config.getConfig().getOrDefault("Speed", 1.0)).doubleValue();
            double frameDelay = (1000.0 / 30.0) / speed;

            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            
            final List<File> frames = currentFrames;
            final ImageView iv = imageView;
            
            KeyFrame keyFrame = new KeyFrame(Duration.millis(frameDelay), e -> updateFrameStatic(frames, iv));
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
            activeTimeline = timeline;
            
            if (!currentFrames.isEmpty()) {
                imageView.setImage(new Image(currentFrames.get(0).toURI().toString()));
            }
        } else {
            imageView.setImage(gifImage);
            currentFrames = null;
            activeTimeline = null;
        }

        HBox hbox = new HBox(imageView);
        hbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0);");

        StackPane root = new StackPane(hbox);
        root.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(root, Color.TRANSPARENT);

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            if (!ISLOCKED) {
                activeMainStage.setX(event.getScreenX() - xOffset);
                activeMainStage.setY(event.getScreenY() - yOffset);
            }
        });

        return scene;
    }

    private static void updateFrameStatic(List<File> frames, ImageView imageView) {
        if (frames != null && !frames.isEmpty()) {
            imageView.setImage(new Image(frames.get(currentFrameIndex).toURI().toString()));
            currentFrameIndex = (currentFrameIndex + 1) % frames.size();
        }
    }

    private void updateFrame(List<File> frames, ImageView imageView) {
        if (frames != null && !frames.isEmpty()) {
            imageView.setImage(new Image(frames.get(currentFrameIndex).toURI().toString()));
            currentFrameIndex = (currentFrameIndex + 1) % frames.size();
        }
    }

    public static Stage getPrimaryStageRef() {
        return primaryStageRef;
    }
}