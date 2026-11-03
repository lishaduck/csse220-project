package finalproject.app;

import finalproject.model.GameModel;
import finalproject.ui.Battle;
import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts the JavaFX application and creates its game model.
 */
public class GameApp extends Application {
    private final GameModel model = new GameModel(new Dimension2D(640, 480));

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage stage) {
        stage.setTitle("Flapping finches");

        Battle battle = new Battle(model);
        Scene scene = battle.getView();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused) {
                battle.clearInput();
            }
        });
        stage.setOnHidden(event -> battle.stop());
        stage.show();
        battle.start();
    }
}
