package finalproject.app;

import finalproject.model.GameModel;
import finalproject.ui.Battle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts the JavaFX application and creates its game model.
 */

public class GameApp extends Application {

    private final GameModel model = new GameModel();

    public void start(Stage stage) {
        stage.setTitle("Flapping finches");

        Battle menu = new Battle();
        Scene scene = menu.getView();
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
