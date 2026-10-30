package finalproject;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class GraphicsHandler {
    public static void loadStartMenu(Stage stage) {
        StartMenu menu = new StartMenu();
        Scene scene = menu.getView();
        stage.setTitle("Flapping finches");
        stage.setScene(scene);
        stage.show();
    }
}
