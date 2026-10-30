package finalproject.app;

import finalproject.model.GameModel;
import finalproject.ui.GraphicsHandler;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Starts the JavaFX application and creates its game model.
 */

public class GameApp extends Application {

    private final GameModel model = new GameModel();


    public void start(Stage stage) {
        GraphicsHandler.loadStartMenu(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
