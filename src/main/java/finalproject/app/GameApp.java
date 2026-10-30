package finalproject.app;

import finalproject.model.PowerUp;
import finalproject.model.Projectile;
import finalproject.model.Ship;
import finalproject.ui.GraphicsHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Starter JavaFX app for CSSE220 Milestone 1.
 * Replace this simple window with the first version of your project.
 */

public class GameApp extends Application {

    private final ArrayList<Ship> ships = new ArrayList<>();
    private final ArrayList<PowerUp> powerUps = new ArrayList<>();
    private final ArrayList<Projectile> projectiles = new ArrayList<>();


    public void start(Stage stage) {
        GraphicsHandler.loadStartMenu(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
