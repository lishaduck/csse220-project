package finalproject.ui;

import finalproject.app.StageManager;
import javafx.scene.control.Button;

/**
 * Displays the final score and offers a fresh game.
 */
public final class GameOverScreen extends MenuScreen {
    public GameOverScreen(int score, boolean won) {
        super(won ? "You won!" : "Game over", "Final score: " + score);
        Button retry = createButton(won ? "Play again" : "Try again");
        retry.setDefaultButton(true);
        retry.setOnAction(event -> StageManager.from(retry).startNewGame());
        content.getChildren().add(retry);
    }
}
