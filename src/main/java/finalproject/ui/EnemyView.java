package finalproject.ui;

import finalproject.model.EnemyShip;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Objects;

/**
 * Loads laser artwork and draws each projectile at its model position and size.
 */
public class EnemyView {
    private final Image enemyImage;

    public EnemyView() {
        URL enemyResource = Objects.requireNonNull(
                EnemyView.class.getResource("/EnemyShip.gif"),
                "Missing laser resource: /EnemyShip.gif"
        );
        enemyImage = new Image(enemyResource.toExternalForm());
    }

    public void render(GraphicsContext gc, EnemyShip enemy) {
        Point2D position = enemy.getPosition();
        gc.drawImage(
                enemyImage,
                position.getX(),
                position.getY(),
                enemy.getWidth(),
                enemy.getHeight()
        );
    }
}
