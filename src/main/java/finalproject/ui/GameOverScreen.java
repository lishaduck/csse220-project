package finalproject.ui;

import finalproject.app.StageManager;
import javafx.scene.control.Button;

/** Displays the final score and offers a fresh game. */
public final class GameOverScreen extends MenuScreen {
    public GameOverScreen(int score) {
        super("Game over", "Final score: " + score);
        Button retry = createButton("Try again");
        retry.setId("try-again");
        retry.setDefaultButton(true);
        retry.setOnAction(event -> StageManager.from(retry).startNewGame());
        content.getChildren().add(retry);
    }
}
