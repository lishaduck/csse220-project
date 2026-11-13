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
    private final HitFlash hitFlash;

    public EnemyView(EnemyShip enemy) {
        hitFlash = new HitFlash(enemy);
        enemyImage = new Image(enemy.getImage().toExternalForm());
        this.enemy = enemy;
    }

    public void render(GraphicsContext gc) {
        Point2D position = enemy.getPosition();
        gc.save();
        gc.setGlobalAlpha(hitFlash.opacity());
        gc.drawImage(
                enemyImage,
                position.getX(),
                position.getY(),
                enemy.getWidth(),
                enemy.getHeight()
        );
        gc.restore();
    }

    void stopAnimations() {
        hitFlash.stop();
    }
}
