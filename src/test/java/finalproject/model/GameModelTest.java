package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static finalproject.model.HorizontalDirection.LEFT;
import static finalproject.model.HorizontalDirection.NONE;
import static finalproject.model.HorizontalDirection.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameModelTest {
    @Test
    void preservesProjectileSpawningCooldownAndRemovalAlongsidePlayerMovement() {
        GameModel model = new GameModel(new Dimension2D(640, 480));
        model.update(RIGHT, 0.125, 640, 480);
        Projectile projectile = model.getPlayer().spawnProjectile();
        assertEquals(new Point2D(325.5, 392), projectile.getPosition());

        assertTrue(model.isRefireable());
        model.addPlayerProjectile();
        assertFalse(model.isRefireable());
        model.updateProjectiles(0.25);
        assertFalse(model.isRefireable());
        model.updateProjectiles(0.1);
        assertTrue(model.isRefireable());

        model.updateProjectiles(0.35);
        model.update(NONE, 0, 640, 64);
        assertTrue(model.isRefireable());
    }

    @Test
    void fitsTheVisiblePlayfieldWhenTheWindowManagerChangesItsSize() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        Ship player = model.getPlayer();
        model.update(RIGHT, 10, 640, 480);

        model.update(NONE, 0, 499, 320);
        assertEquals(new Dimension2D(499, 320), model.getPlayfield());
        assertEquals(499, player.getPosition().getX() + PlayerShip.WIDTH);
        assertEquals(296, player.getPosition().getY() + PlayerShip.HEIGHT);
        for (int i = 0; i < 1000; i++) {
            model.update(RIGHT, 0.05, 499, 320);
        }
        assertEquals(499, player.getPosition().getX() + PlayerShip.WIDTH);
        model.update(LEFT, 0.125, 499, 320);
        assertEquals(499 - PlayerShip.WIDTH - 37.5, player.getPosition().getX());

        model.update(RIGHT, 10, 800, 600);
        assertEquals(800, player.getPosition().getX() + PlayerShip.WIDTH);
        model.update(LEFT, 10, 800, 600);
        assertEquals(0, player.getPosition().getX());
        model.update(RIGHT, 0.125, 800, 600);
        assertEquals(37.5, player.getPosition().getX());
    }

    @Test
    void movesHorizontallyAndCanLeaveBothBoundariesAfterRepeatedInput() {
        GameModel model = new GameModel(new Dimension2D(640, 480));
        PlayerShip player = model.getPlayer();
        double initialX = player.getPosition().getX();
        double initialY = player.getPosition().getY();
        assertTrue(initialX > 0);
        assertTrue(initialX + PlayerShip.WIDTH < 640);
        assertTrue(initialY >= 0);
        assertTrue(initialY + PlayerShip.HEIGHT <= 480);

        model.update(LEFT, 0.125, 640, 480);
        assertEquals(initialX - 37.5, player.getPosition().getX());
        model.update(RIGHT, 0.0625, 640, 480);
        assertEquals(initialX - 18.75, player.getPosition().getX());

        for (int i = 0; i < 1000; i++) {
            model.update(LEFT, 0.05, 640, 480);
        }
        assertEquals(0, player.getPosition().getX());
        model.update(RIGHT, 0.125, 640, 480);
        assertEquals(37.5, player.getPosition().getX());

        for (int i = 0; i < 1000; i++) {
            model.update(RIGHT, 0.075, 640, 480);
        }
        assertEquals(
                640,
                player.getPosition().getX() + PlayerShip.WIDTH
        );
        model.update(LEFT, 0.0625, 640, 480);
        assertEquals(
                640 - PlayerShip.WIDTH - 18.75,
                player.getPosition().getX()
        );
        model.update(NONE, 0.2, 640, 480);
        assertEquals(
                640 - PlayerShip.WIDTH - 18.75,
                player.getPosition().getX()
        );
        assertEquals(initialY, player.getPosition().getY());
    }
}
