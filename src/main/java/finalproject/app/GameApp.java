package finalproject.app;

import finalproject.model.GameModel;
import finalproject.ui.Battle;
import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Starts the JavaFX application and creates its game model.
 */
public class GameApp extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage stage) {
        stage.setTitle("Flapping finches");

        GameModel model;
        try {
            model = new GameModel(new Dimension2D(640, 480));
        } catch (IllegalArgumentException exception) {
            Label message = new Label("Unable to load levels:\n" + exception.getMessage());
            message.setWrapText(true);
            stage.setScene(new Scene(message, 640, 480));
            stage.show();
            return;
        }
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
