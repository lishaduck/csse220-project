package finalproject.ui;

import finalproject.app.StageManager;
import javafx.scene.control.Button;

/** Welcomes the player and starts a fresh game. */
public final class StartScreen extends MenuScreen {
    public StartScreen() {
        super("Flapping finches", "A / D to move · Space to fire");
        Button start = createButton("Start game");
        start.setId("start-game");
        start.setDefaultButton(true);
        start.setOnAction(event -> StageManager.from(start).startNewGame());
        content.getChildren().add(start);
    }
}
