package finalproject.app;

import finalproject.model.GameModel;
import finalproject.ui.Battle;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Owns the application's stage and active screen. Use on the JavaFX thread.
 */
public final class StageManager {
    private final Stage stage;

    public StageManager(Stage stage) {
        this.stage = stage;
        stage.getProperties().put(StageManager.class, this);
    }

    /**
     * Finds the manager for a control attached to a managed window.
     */
    public static StageManager from(Node node) {
        return (StageManager) node.getScene().getWindow().getProperties().get(StageManager.class);
    }

    /**
     * Displays a screen in this manager's window.
     */
    public void show(Screen screen) {
        stage.setScene(screen.getView());
        stage.show();
    }

    /**
     * Starts a fresh battle, including a fresh model, score, and wave sequence.
     */
    public void startNewGame() {
        GameModel model;
        try {
            model = new GameModel(new Dimension2D(640, 480));
        } catch (IllegalArgumentException exception) {
            Label message = new Label("Unable to load levels:\n" + exception.getMessage());
            message.setWrapText(true);
            Scene scene = new Scene(message, 640, 480);
            show(() -> scene);
            return;
        }
        show(new Battle(model));
    }
}
