package finalproject.model;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static finalproject.model.HorizontalDirection.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameModelTest {
    private static final PlayerInput MOVE_LEFT = new PlayerInput(LEFT, false);
    private static final PlayerInput MOVE_RIGHT = new PlayerInput(RIGHT, false);
    private static final PlayerInput IDLE = new PlayerInput(NONE, false);

    @Test
    void preservesProjectileSpawningCooldownAndRemovalAlongsidePlayerMovement() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        model.update(new PlayerInput(RIGHT, true), 0.125, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(325.5, 317), model.getProjectiles().getFirst().getPosition());

        model.update(new PlayerInput(LEFT, true), 0.2, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(325.5, 197), model.getProjectiles().getFirst().getPosition());
        model.update(new PlayerInput(LEFT, true), 1.0 / 120, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(325.5, 192), model.getProjectiles().getFirst().getPosition());
        model.update(new PlayerInput(LEFT, true), 1.0 / 600, playfield);
        assertEquals(1, model.getProjectiles().size());
        assertEquals(new Point2D(325.5, 191), model.getProjectiles().getFirst().getPosition());

        model.update(new PlayerInput(RIGHT, true), 0.05, playfield);
        assertEquals(2, model.getProjectiles().size());
        assertEquals(new Point2D(325.5, 161), model.getProjectiles().getFirst().getPosition());
        assertEquals(new Point2D(277.5, 362), model.getProjectiles().getLast().getPosition());

        model.update(IDLE, 0.05, playfield);
        assertEquals(2, model.getProjectiles().size());
        assertEquals(new Point2D(325.5, 131), model.getProjectiles().getFirst().getPosition());
        assertEquals(new Point2D(277.5, 332), model.getProjectiles().getLast().getPosition());
        model.update(IDLE, 1, playfield);
        assertTrue(model.getProjectiles().stream().noneMatch(projectile -> projectile.getSource() == model.getPlayer()));

        Dimension2D resized = new Dimension2D(499, 320);
        model.update(new PlayerInput(RIGHT, true), 0.125, resized);
        Projectile playerProjectile = model.getProjectiles().stream()
                .filter(projectile -> projectile.getSource() == model.getPlayer())
                .findFirst()
                .orElseThrow();
        assertEquals(new Point2D(315, 157), playerProjectile.getPosition());
    }

    @Test
    void fitsTheVisiblePlayfieldWhenTheWindowManagerChangesItsSize() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        Ship player = model.getPlayer();
        model.update(MOVE_RIGHT, 10, playfield);

        Dimension2D resized = new Dimension2D(499, 320);
        model.update(IDLE, 0, resized);
        assertEquals(resized, model.getPlayfield());
        assertEquals(499, player.getPosition().getX() + PlayerShip.SIZE.getWidth());
        assertEquals(296, player.getPosition().getY() + PlayerShip.SIZE.getHeight());
        for (int i = 0; i < 1000; i++) {
            model.update(MOVE_RIGHT, 0.05, resized);
        }
        assertEquals(499, player.getPosition().getX() + PlayerShip.SIZE.getWidth());
        model.update(MOVE_LEFT, 0.125, resized);
        assertEquals(499 - PlayerShip.SIZE.getWidth() - 37.5, player.getPosition().getX());

        Dimension2D expanded = new Dimension2D(800, 600);
        model.update(MOVE_RIGHT, 10, expanded);
        assertEquals(800, player.getPosition().getX() + PlayerShip.SIZE.getWidth());
        model.update(MOVE_LEFT, 10, expanded);
        assertEquals(0, player.getPosition().getX());
        model.update(MOVE_RIGHT, 0.125, expanded);
        assertEquals(37.5, player.getPosition().getX());
    }

    @Test
    void movesHorizontallyAndCanLeaveBothBoundariesAfterRepeatedInput() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        PlayerShip player = model.getPlayer();
        double initialX = player.getPosition().getX();
        double initialY = player.getPosition().getY();
        assertTrue(initialX > 0);
        assertTrue(initialX + PlayerShip.SIZE.getWidth() < 640);
        assertTrue(initialY >= 0);
        assertTrue(initialY + PlayerShip.SIZE.getHeight() <= 480);

        model.update(MOVE_LEFT, 0.125, playfield);
        assertEquals(initialX - 37.5, player.getPosition().getX());
        model.update(MOVE_RIGHT, 0.0625, playfield);
        assertEquals(initialX - 18.75, player.getPosition().getX());

        for (int i = 0; i < 1000; i++) {
            model.update(MOVE_LEFT, 0.05, playfield);
        }
        assertEquals(0, player.getPosition().getX());
        model.update(MOVE_RIGHT, 0.125, playfield);
        assertEquals(37.5, player.getPosition().getX());

        for (int i = 0; i < 1000; i++) {
            model.update(MOVE_RIGHT, 0.075, playfield);
        }
        assertEquals(
                640,
                player.getPosition().getX() + PlayerShip.SIZE.getWidth()
        );
        model.update(MOVE_LEFT, 0.0625, playfield);
        assertEquals(
                640 - PlayerShip.SIZE.getWidth() - 18.75,
                player.getPosition().getX()
        );
        model.update(IDLE, 0.2, playfield);
        assertEquals(
                640 - PlayerShip.SIZE.getWidth() - 18.75,
                player.getPosition().getX()
        );
        assertEquals(initialY, player.getPosition().getY());
    }
}
