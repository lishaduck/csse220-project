package finalproject.ui;

import javafx.scene.image.Image;

import java.util.Objects;

/** Shares the animated starfield at the game's display resolution. */
final class GameBackground {
    static final Image IMAGE = new Image(Objects.requireNonNull(GameBackground.class.getResource("/Background.gif"))
            .toExternalForm(), 640, 480, false, false);

    private GameBackground() {
    }
}
