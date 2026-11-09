package finalproject.ui;

import finalproject.model.EnemyShip;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Loads laser artwork and draws each projectile at its model position and size.
 */
public class EnemyView {
    private final Image enemyImage;
    private final EnemyShip enemy;

    public EnemyView(EnemyShip enemy) {
        enemyImage = new Image(enemy.getImage().toExternalForm());
        this.enemy = enemy;
    }

    public void render(GraphicsContext gc) {
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
