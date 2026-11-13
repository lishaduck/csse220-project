package finalproject.app;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Starts the JavaFX application and its initial battle screen.
 */
public class GameApp extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage stage) {
        stage.setTitle("Flapping finches");
        stage.setResizable(false);
        StageManager screens = new StageManager(stage);
        screens.startNewGame();
    }
}
