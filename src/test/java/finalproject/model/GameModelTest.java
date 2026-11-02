package finalproject.model;

import javafx.geometry.Dimension2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameModelTest {
    @Test
    void fitsTheVisiblePlayfieldWhenTheWindowManagerChangesItsSize() {
        Dimension2D playfield = new Dimension2D(640, 480);
        GameModel model = new GameModel(playfield);
        Ship player = model.getPlayer();
        model.movePlayer(3000);

        model.resizePlayfield(499, 320);
        assertEquals(new Dimension2D(499, 320), model.getPlayfield());
        assertEquals(499, player.getPosition().getX() + PlayerShip.WIDTH);
        assertEquals(296, player.getPosition().getY() + PlayerShip.HEIGHT);
        for (int i = 0; i < 1000; i++) {
            model.movePlayer(15);
        }
        assertEquals(499, player.getPosition().getX() + PlayerShip.WIDTH);
        model.movePlayer(-37.5);
        assertEquals(499 - PlayerShip.WIDTH - 37.5, player.getPosition().getX());

        model.resizePlayfield(800, 600);
        model.movePlayer(3000);
        assertEquals(800, player.getPosition().getX() + PlayerShip.WIDTH);
        model.movePlayer(-3000);
        assertEquals(0, player.getPosition().getX());
        model.movePlayer(37.5);
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

        model.movePlayer(-37.5);
        assertEquals(initialX - 37.5, player.getPosition().getX());
        model.movePlayer(18.75);
        assertEquals(initialX - 18.75, player.getPosition().getX());

        for (int i = 0; i < 1000; i++) {
            model.movePlayer(-15);
        }
        assertEquals(0, player.getPosition().getX());
        model.movePlayer(37.5);
        assertEquals(37.5, player.getPosition().getX());

        for (int i = 0; i < 1000; i++) {
            model.movePlayer(22.5);
        }
        assertEquals(
                640,
                player.getPosition().getX() + PlayerShip.WIDTH
        );
        model.movePlayer(-18.75);
        assertEquals(
                640 - PlayerShip.WIDTH - 18.75,
                player.getPosition().getX()
        );
        model.movePlayer(0);
        assertEquals(
                640 - PlayerShip.WIDTH - 18.75,
                player.getPosition().getX()
        );
        assertEquals(initialY, player.getPosition().getY());
    }
}
