package ch.epfl.test.newPartsTests;

import ch.epfl.javelo.gui.TileManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Path;

public final class TestTileManager extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TileManager tm = new TileManager(
                Path.of("testFolderTileManager"), "tile.openstreetmap.org");
        Image tileImage = tm.imageForTileAt(
                new TileManager.TileId(19, 271725, 185422));
        Platform.exit();
    }
}
